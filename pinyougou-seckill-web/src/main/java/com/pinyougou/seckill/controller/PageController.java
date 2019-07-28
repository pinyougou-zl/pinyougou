package com.pinyougou.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @RequestMapping("/page/login")
    public String showPage(String url) {

        //重定向
        return "redirect:" + url;
    }
}
