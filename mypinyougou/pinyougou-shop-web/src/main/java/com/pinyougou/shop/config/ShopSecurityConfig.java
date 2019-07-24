package com.pinyougou.shop.config;

import com.pinyougou.shop.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class ShopSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //放行静态资源
        http.authorizeRequests()
                .antMatchers("/*.html","/css/**","/img/**","/js/**","/plugins/**","/seller/add")
                .permitAll()  //所有人都可以访问
        //拦截请求 配置权限
                .anyRequest().authenticated();
        //自定义登录的页面
        http.formLogin()
                .loginPage("/shoplogin.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/index.html",true)
                .failureUrl("/shoplogin.html?error");
        //禁用csrf
        http.csrf().disable();
        //设置同源访问策略
        http.headers().frameOptions().sameOrigin();
        //注销 并销毁session
        http.logout().logoutUrl("/logout").invalidateHttpSession(true);
    }
}
