package com.entity;

import com.pinyougou.pojo.TbItemCat;

import java.io.Serializable;
import java.util.List;

public class ItemQuery implements Serializable {

    private Long id;

    private Long parentId;

    private String name;

    private Long typeId;

    private String status;

    private List<ItemQuery> itemQueries;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ItemQuery> getItemQueries() {
        return itemQueries;
    }

    public void setItemQueries(List<ItemQuery> itemQueries) {
        this.itemQueries = itemQueries;
    }
}
