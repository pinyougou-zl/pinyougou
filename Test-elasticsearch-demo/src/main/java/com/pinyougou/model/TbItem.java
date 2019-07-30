package com.pinyougou.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Map;

//代表着名称为pinyougou，类型item
@Document(indexName = "pinyougou",type = "item")  //标识一个文档
public class TbItem implements Serializable {
    /**
     * 商品id，同时也是商品编号
     */
    @Id  //文档的唯一的Id
    @Field(type = FieldType.Long)  //标识 该数据也要作为字段进行展示
    private Long id;

    /**
     * 商品标题
     */
    //标识分词器的类型（最小分词）搜索也进行分词（尽量一样）类型为text也即是String
            //store:是否存储，默认是false,但是数据存储在了ES的_store中了。
            //如果设置了 copy_to 为 keyword 那么要求域的 值不能为空。
            // 字段值copy 一份 存储奥keyword中
    @Field(analyzer = "ik_smart", searchAnalyzer = "ik_smart", type = FieldType.Text,copyTo="keyword")
    private String title;


    @Field(type = FieldType.Long)
    private Long goodsId;

    /**
     * 冗余字段 存放三级分类名称  关键字 只能按照确切的词来搜索
     */
    @Field(type = FieldType.Keyword)
    private String category;

    /**
     * 冗余字段 存放品牌名称
     */
    @Field(type = FieldType.Keyword)
    private String brand;

    /**
     * 冗余字段，用于存放商家的店铺名称
     */
    @Field(type = FieldType.Keyword)
    private String seller;

    //要索引 对象类型
    @Field(index = true,type=FieldType.Object)
    private Map<String,String> specMap;

    public Map<String, String> getSpecMap() {
        return specMap;
    }

    public void setSpecMap(Map<String, String> specMap) {
        this.specMap = specMap;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }
}