package com.pinyougou.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    /**
     * 登录时获取登录名
     * @return
     */
    @RequestMapping("/name")
    public String getName() {

        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
