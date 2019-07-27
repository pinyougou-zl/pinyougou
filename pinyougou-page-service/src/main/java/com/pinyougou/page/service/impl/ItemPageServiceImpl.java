package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {
    @Autowired
    private TbGoodsMapper tbGoodsMapper;

    //这个是读取配置文件的信息
    @Value("${pageDir}")
    private String pageDir;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private FreeMarkerConfigurer configurer;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper tbItemMapper;


    @Override
    public void genItemHtml(Long goodsId) {
        //查询数据库的商品的数据 生成静态页面

        //1、根据SPU的ID 查询商品的信息 （good goodsDesc）
        TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        //使用freemarker创建模板，使用数据集 生成静态页面（数据集和模板）
        genHTML("item.ftl",tbGoods,tbGoodsDesc);
    }

    /**
     * 根据传入的id，删除html
     * @param ids
     */
    @Override
    public void deleteById(Long[] ids) {
        try {
            for (Long id : ids) {
                FileUtils.forceDelete(new File(pageDir+id+".html"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void genHTML(String template,TbGoods tbGoods,TbGoodsDesc tbGoodsDesc) {
        FileWriter writer = null;
        try {
            //1、创建一个configuration对象，
            //2、设置字符编码，和 模板加载的目录,这两个步骤已经在spring配置好了
            Configuration configuration = configurer.getConfiguration();
            //3、加载一个模板，创建一个模板对象。
            Template template1 = configuration.getTemplate(template);
            //4、全部存到map集合
            Map<String,Object> model = new HashMap<>();
            model.put("tbGoods",tbGoods);
            model.put("tbGoodsDesc",tbGoodsDesc);

            //查询商品分类的信息，展示面包屑
            TbItemCat tbItemCat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id());
            TbItemCat tbItemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id());
            TbItemCat tbItemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
            model.put("tbItemCat1",tbItemCat1.getName());
            model.put("tbItemCat2",tbItemCat2.getName());
            model.put("tbItemCat3",tbItemCat3.getName());

            //读取SKU信息
            Example example = new Example(TbItem.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("goodsId",tbGoods.getId());
            criteria.andEqualTo("status","1");
            example.setOrderByClause("is_default desc");
            List<TbItem> tbItems = tbItemMapper.selectByExample(example);
            model.put("skuList",tbItems);

            //5、创建一个 Writer 对象，一般创建一 FileWriter 对象，指定生成的文件名。
            //这里需要注意生成的html不能重复，所以我们会用主键的值来命名
            writer = new FileWriter(new File(pageDir+tbGoods.getId()+".html"));
            //6、调用模板对象的 process 方法输出文件。
            template1.process(model,writer);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
