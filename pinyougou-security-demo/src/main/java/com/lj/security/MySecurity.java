package com.lj.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 完全使用注解来进行开发security
 * 该类是security的配置核心类，所有xml的配置都在这个类里面，
 */

@EnableWebSecurity  //这个就是xml中开启自动配置的选项
public class MySecurity extends WebSecurityConfigurerAdapter {
    //认证
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //定义了一个用户名密码和角色
        auth.inMemoryAuthentication().withUser("admin").password("{noop}admin")
                .roles("ADMIN").roles("USER");
        auth.inMemoryAuthentication().withUser("user").password("{noop}user")
                .roles("USER");
    }

    //授权
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //默认拦截所有资源，需要进行登录

        /*
        * 授权 登录和错误页面 不需要登录
        * 其他请求/admin/** 都需要拥有ADMIN的角色才可以访问
        * 其他请求/user/**  都需要拥有USER的角色的人才可以访问
        * 其他的任意请求，都只要登录了就可以访问
        * */
        http.authorizeRequests()
                //所有人都可以访问
                .antMatchers("/login.html","/error.html").permitAll()
                .antMatchers("/admin/**","a.jsp").hasRole("ADMIN")
                .antMatchers("/user/**").hasRole("USER");
                //.anyRequest().authenticated();
        //设置不使用security自带的登录，自己配置
        http.formLogin()
                //配置登录页面
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                //表示登录成功之后，跳转到该页面
                .defaultSuccessUrl("/test.html",true)
                //登录失败的页面
                .failureUrl("/error.html");
       //禁用csrf(跨站请求伪造)
        http.csrf().disable();
    }
}
