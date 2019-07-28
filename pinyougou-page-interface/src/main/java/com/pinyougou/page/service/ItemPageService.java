package com.pinyougou.page.service;

/**
 * 商品详细页接口
 */
public interface ItemPageService {
    /**
     * 生成商品详情页
     */
    public void genItemHtml(Long goodsId);

    /**
     * 删除商品页
     */
    public void deleteById(Long[] ids);
}
