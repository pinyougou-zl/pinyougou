package com.pinyougou;

import com.pinyougou.es.service.ItemService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @param
 * @Description : 向ES库中导入mysql数据库中的数据
 * @Return :
 * @Author : yang
 */
public class App {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
        ItemService itemService = context.getBean(ItemService.class);
        itemService.importDataToEs();
    }
}
