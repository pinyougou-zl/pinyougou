package com.pinyougou.es;

import com.pinyougou.es.service.ItemService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestImport {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/spring-es.xml");
        //获取对象
        ItemService bean = context.getBean(ItemService.class);
        bean.ImportDataToes();
    }
}
