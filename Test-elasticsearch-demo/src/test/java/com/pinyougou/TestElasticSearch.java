package com.pinyougou;

import com.pinyougou.dao.ItemDao;
import com.pinyougou.model.TbItem;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;


@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring/spring-es.xml")
public class TestElasticSearch {
    //注入ElasticsearchTemplate对象
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private ItemDao itemDao;

    /**
     * 创建索引和映射
     */
    @Test
    public void testCreate() {
        //创建索引
        elasticsearchTemplate.createIndex(TbItem.class);
        //创建映射
        elasticsearchTemplate.putMapping(TbItem.class);
    }

    /**
     * 添加文档测试
     */
    @Test
    public void testAdd() {
        TbItem tbItem = new TbItem();
        tbItem.setId(2000L);
        tbItem.setTitle("三星手机");
        Map<String,String> map = new HashMap<>();
        map.put("网络指示","移动4G");
        map.put("机身内存","16G");
        tbItem.setSpecMap(map);
        itemDao.save(tbItem);
    }

    /**
     * 删除文档测试
     */
    @Test
    public void testDelete() {
        //根据id删除
        itemDao.deleteById(2000L);
    }

    /**
     *更新文档测试
     * 修改数据和保存数据一样，有着相同的主键Id就更新（覆盖）
     */
    @Test
    public void testUpdate() {
        for (long i = 0; i < 100; i++) {
            TbItem tbItem = new TbItem();
            tbItem.setId(i);
            tbItem.setTitle("华为手机"+i);
            tbItem.setBrand("国产"+i);
            itemDao.save(tbItem);
        }

    }

    /**
     * 查询
     */
    @Test
    public void testSelect() {
        //查询所有
        /*Iterable<TbItem> all = itemDao.findAll();
        for (TbItem tbItem : all) {
            System.out.println(tbItem.getTitle());
        }*/

        //根据id查询
        TbItem tbItem = itemDao.findById(2000L).get();
        System.out.println(tbItem.getTitle());
    }

    /**
     * 分页查询
     */
    @Test
    public void testPage() {
        //在es中要注意，0代表第一页，10代表每页显示的条数
        Pageable pageanle = PageRequest.of(0,10);
        Page<TbItem> all = itemDao.findAll(pageanle);
        System.out.println("总数为："+all.getTotalElements());
        System.out.println("总页数为："+all.getTotalPages());
        for (TbItem tbItem : all) {
            System.out.println(tbItem.getTitle());
        }
    }

    //==================使用Spring Data elasticsearch查询

    /**
     * 通配符查询，索引的时候分词了，但是查询的时候不分词 *表示匹配所有 ？表示匹配一个，会占用一个字符空间
     */
    @Test
    public void testFind() {
        //使用elasticsearchTemplate对进行查询
        //添加查询条件
        SearchQuery query = new NativeSearchQuery(QueryBuilders.wildcardQuery("title","手*"));
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(query, TbItem.class);
        for (TbItem tbItem : tbItems) {
            System.out.println(tbItem.getTitle());
        }
    }

    /**
     * 分词匹配查询 通过boolean查询 默认or 进行连接
     * 索引的时候分词了，查询的时候先分词，在进行查询匹配，并通过or进行连接，并集显示，所有数据
     */
    @Test
    public void testAnalyzer() {
        //创建查询条件
        SearchQuery query = new NativeSearchQuery(QueryBuilders.matchQuery("title","手机1111"));
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(query, TbItem.class);
        for (TbItem tbItem : tbItems) {
            System.out.println(tbItem.getTitle());
        }
    }

    /**
     * 组合域。
     * 使用组合域的时候速度要更快。但是如果是高亮查询，要求不能使用该组合域字段进行匹配。
     */
    @Test
    public void testFindBy() {
        //创建查询条件
        SearchQuery query = new NativeSearchQuery(QueryBuilders.matchQuery("keyword","手机"));
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(query, TbItem.class);
        for (TbItem tbItem : tbItems) {
            System.out.println(tbItem.getTitle());
        }
    }

    /**
     * 对象域查询
     */
    @Test
    public void queryObject() {
        //specMap.网络制式.keyword  ===> fieldName.属性名.keyword
        //specMap :指定的就是要查询的字段名 和POJO中的字段一致

        // 网络制式 :指定的就是 指定 网络制式 字段
        // keyword :固定的写法 表示 搜索的时候不分词。
        SearchQuery query = new NativeSearchQuery(QueryBuilders.matchQuery("specMap.网络指示.keyword","移动4G"));
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(query, TbItem.class);
        for (TbItem tbItem : tbItems) {
            System.out.println(tbItem.getSpecMap()+"---"+tbItem.getTitle());
        }
    }

    /**
     * 过滤查询
     */
    @Test
    public void queryFilter() {
        //1、构建查询对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //2、创建查询条件
        queryBuilder.withIndices("pinyougou");// 设置从哪一个索引查询
        queryBuilder.withTypes("item");  //设置从哪个类型中查询
        //从文档中查询title为手机的数据
        queryBuilder.withQuery(QueryBuilders.matchQuery("title", "手机"));
        //3、创建过滤查询（规格的过滤查询 多个过滤使用boolean查询）
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //进行过滤
        boolQuery.filter(QueryBuilders.termQuery("specMap.网络指示.keyword","移动4G"));
        boolQuery.filter(QueryBuilders.termQuery("specMap.机身内存.keyword","16G"));

        queryBuilder.withFilter(boolQuery);

        //4、构建查询条件
        NativeSearchQuery searchQuery = queryBuilder.build();
        //执行查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class);
        for (TbItem tbItem : tbItems) {
            System.out.println(tbItem.getSpecMap()+"---"+tbItem.getTitle());
        }
    }
}

