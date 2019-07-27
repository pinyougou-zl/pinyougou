package com.pinyougou.manager.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 创建配置类
 */
@EnableWebSecurity
public class ManagerSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin").password("{noop}admin")
                //会自动添加ROLE_
                .roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //该静态资源所有人都可以访问
        .antMatchers("/css/**","/img/**","/js/**","/plugins/**","/login.html")
                .permitAll()
                //设置所有的其他请求都需要认证通过即可 也就是用户名和密码正确即可不需要其他的角色
                .anyRequest().authenticated();

        //配置登录信息
        http.formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/index.html",true)
                //登录失败会在url显示error字样
                .failureUrl("/login?error");
        //配置退出账号功能
        http.logout().logoutUrl("/logout").invalidateHttpSession(true);
        http.csrf().disable();
        //开启同源iframe，可以访问策略
        http.headers().frameOptions().sameOrigin();
    }
}
