package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.dao.ItemSearchDao;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ItemSearchDao dao;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        Map<String,Object> resultMap = new HashMap<>();

        //1、获取关键字
        String keywords = (String) searchMap.get("keywords");
        //2、创建搜索查询的对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //3、创建条件查询，匹配查询，因为要进行高亮，不能只查找一个关键字
        queryBuilder.withQuery(QueryBuilders.multiMatchQuery(keywords,"seller","category","brand","title"));
        //3.0、增加一个聚合查询,参数一：设置聚合函数的名称（别名），参数二：设置分组的字段
        queryBuilder.addAggregation(AggregationBuilders.terms("category_group").field("category").size(50));


        //3.1、设置高亮，就是在关键字加上css样式
        queryBuilder
                //选择查询的高亮的字段
                .withHighlightFields(new HighlightBuilder.Field("title"))
                //设置关键字的前缀和后缀
                .withHighlightBuilder(new HighlightBuilder()
                        .preTags("<em style=\"color:red\">")
                        .postTags("</em>"));
//**********************************************************************************************************************
        /**
         * 进行一系列的查询,过滤查询
         */
        //获得bool查询对象,多个条件搜索的查询，封装到布尔查询即可
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //过滤查询，商品分类的查询
        String category = (String) searchMap.get("category");  //根据传来的key获取对应的值
        if(StringUtils.isNotBlank(category)) {
            //不为空设置查询
            boolQueryBuilder.filter(QueryBuilders.termQuery("category",category));
        }
        //过滤查询，品牌的查询
        String brand = (String) searchMap.get("brand");
        if(StringUtils.isNotBlank(brand)) {
            //不为空设置查询  termQuery该方法为词条查询，也就是不分词进行查询
            boolQueryBuilder.filter(QueryBuilders.termQuery("brand",brand));
        }
        //过滤查询，规格的过滤
        Map<String,Object> spec = (Map<String, Object>) searchMap.get("spec");
        if(spec != null) {
            //不为空设置查询
            for (String key : spec.keySet()) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("specMap."+key+".keyword",spec.get(key)));
            }
        }
        //过滤查询，价格的查询
        String price = (String) searchMap.get("price");
        //需要区分字符串
        if(StringUtils.isNotBlank(price)) {
            String[] strings = price.split("-");
            if("*".equals(strings[1])) {
                //也就是3000以上
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(strings[0]));
            }else {
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").from(strings[0],true).to(strings[1],true));
            }
        }


        //最后把bool查询放入查询对象中
        queryBuilder.withFilter(boolQueryBuilder);

//**********************************************************************************************************************
        //4、构建查询对象
        NativeSearchQuery build = queryBuilder.build();

        /**
         * 添加分页的代码，就是在执行完过滤查询后进行分页
         */
        Integer pageNo = (Integer) searchMap.get("pageNo");
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if(pageNo ==null) {
            pageNo = 1;  //默认
        }
        if(pageSize ==null) {
            pageSize=40;  //默认显示40条数据
        }
        //在es中0是带表第一页
        build.setPageable(PageRequest.of(pageNo-1,pageSize));

        /**
         * 进行排序
         */
        String sortField = (String) searchMap.get("sortField");  //获取需要排序的字段
        String sortType = (String) searchMap.get("sortType");  //回去排序的排序的方式，需要升序还是降序
        if(StringUtils.isNotBlank(sortField) && StringUtils.isNotBlank(sortType)) {
            if(sortType.equals("ASC")) {
                //进行升序
                Sort sort = new Sort(Sort.Direction.ASC, sortField);
                build.addSort(sort);
            }else if(sortType.equals("DESC")) {
                //进行降序
                Sort sort = new Sort(Sort.Direction.DESC, sortField);
                build.addSort(sort);
            }else {
                //不排序
            }
        }

        //5、执行查询
        AggregatedPage<TbItem> items = elasticsearchTemplate.queryForPage(build, TbItem.class, new SearchResultMapper() {
            //获取高亮，匿名内部类，搜索结果映射
            @Override  //进行自定义映射的封装
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                //hits中就是搜索到的结果
                SearchHits hits = response.getHits();
                List<T> content = new ArrayList<>();
                //需要对hits进行判定
                if(hits == null || hits.getHits().length<=0) {
                    //没有搜索到记录,不能返回一个null，返回一个空的集合
                    return new AggregatedPageImpl(content);
                }
                //如果有，进行遍历获取高亮
                for (SearchHit hit : hits) {
                    String sourceAsString = hit.getSourceAsString(); //就是每个文档对应的json数据
                    //转换成我们所需要的pojo
                    TbItem tbItem = JSON.parseObject(sourceAsString, TbItem.class);
                    //获取高亮
                    //为什么返回的是一个map集合呢，因为搜索的时候会有多个条件查询，比如手机、规格、价钱
                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                    //获取高亮域为title的高亮对象
                    HighlightField title = highlightFields.get("title");
                    //对高亮对象也要进行判断，集合是否存在这个这个高亮信息
                    if(title != null) {
                        //获取高亮碎片
                        Text[] fragments = title.getFragments();
                        StringBuffer sb = new StringBuffer();
                        if(fragments  != null) {
                            for (Text fragment : fragments) {
                                //获取高亮碎片的值<em styple="colore:red">
                                sb.append(fragment.string());
                            }
                        }
                        //有高亮值，进行存储
                        if( StringUtils.isNotBlank(sb.toString()) ) {
                            tbItem.setTitle(sb.toString());
                        }
                    }
                    //存入集合
                    content.add((T) tbItem);
                }

                AggregatedPageImpl aggregatedPage = new AggregatedPageImpl<>(content, pageable, hits.getTotalHits()
                        , response.getAggregations(), response.getScrollId());
                return aggregatedPage;
            }
        });
        /**
         * 获取分组结果集
         */
        Aggregation category_group = items.getAggregation("category_group");
        StringTerms stringTerms = (StringTerms) category_group;
        List<String> categoryList = new ArrayList<>();
        if(stringTerms != null) {
            List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
            for (StringTerms.Bucket bucket : buckets) {
                String keyAsString = bucket.getKeyAsString();  //获取到的就是分类的名称
                categoryList.add(keyAsString);
            }
        }

        //搜索之后 默认 展示第一个商品分类的品牌和规格的列表
        if(StringUtils.isNotBlank(category)) {
            Map map = searchBrandAndSpecList(category);//{ "brandList",[],"specList":[]}
            resultMap.putAll(map);
        }else {
            //获取第一个分类下所有的品牌跟规格
            if ( categoryList != null && categoryList.size() > 0 ) {
                Map map = searchBrandAndSpecList(categoryList.get(0));//{ "brandList",[],"specList":[]}
                resultMap.putAll(map);
            } else {
                resultMap.put("specList", new HashMap<>());
                resultMap.put("brandList", new HashMap<>());
            }
        }


        //获取结果集
        List<TbItem> content = items.getContent();
        long totalElements = items.getTotalElements();
        int totalPages = items.getTotalPages();
        resultMap.put("rows",content);
        resultMap.put("total",totalElements);
        resultMap.put("totalPages",totalPages);
        resultMap.put("categoryList",categoryList);//商品分类的列表数据
        return resultMap;
    }

    /**
     * 更新索引库
     * @param items
     */
    @Override
    public void updateIndex(List<TbItem> items) {
        //先设置map，再一次性插入
        for (TbItem tbItem : items) {
            String spec = tbItem.getSpec();
            Map map = JSON.parseObject(spec, Map.class);
            tbItem.setSpecMap(map);
        }
        //进行更新索引
        dao.saveAll(items);
    }

    /**
     * 删除索引的方法
     * @param ids
     */
    @Override
    public void deleteByIds(Long[] ids) {
        DeleteQuery query = new DeleteQuery();
        //删除多个goodsid
        query.setQuery(QueryBuilders.termsQuery("goodsId",ids));
        //根据删除条件索引名和类型
        elasticsearchTemplate.delete(query,TbItem.class);
    }

    /**
     * 查询品牌和规格列表
     * @return
     */
    private Map searchBrandAndSpecList(String category) {
        Map map = new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        //根据模板id查询 品牌列表
        if(typeId!=null){
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList",brandList); //存到Map
            //根据模板id查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList",specList);
        }
        return map;
    }
}
