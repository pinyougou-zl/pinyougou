package com.entity;

import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbSpecification;

import java.util.List;

/**
 * 这是模板管理的pojo
 */
public class TypeTemplate {
    private Integer id;
    private String name;
    private List<TbBrand> brandbrandIds;
    private List<TbSpecification> specIds;
    private List customAttributeItems;

    @Override
    public String toString() {
        return "TypeTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", brandbrandIds=" + brandbrandIds +
                ", specIds=" + specIds +
                ", customAttributeItems=" + customAttributeItems +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TbBrand> getBrandbrandIds() {
        return brandbrandIds;
    }

    public void setBrandbrandIds(List<TbBrand> brandbrandIds) {
        this.brandbrandIds = brandbrandIds;
    }

    public List<TbSpecification> getSpecIds() {
        return specIds;
    }

    public void setSpecIds(List<TbSpecification> specIds) {
        this.specIds = specIds;
    }

    public List getCustomAttributeItems() {
        return customAttributeItems;
    }

    public void setCustomAttributeItems(List customAttributeItems) {
        this.customAttributeItems = customAttributeItems;
    }
}
