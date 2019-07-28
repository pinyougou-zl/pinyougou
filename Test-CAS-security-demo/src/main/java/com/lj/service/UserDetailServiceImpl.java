package com.lj.service;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 自定义认证类
 */
public class UserDetailServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        System.out.println("====================username");
        //方法之前我们用来从数据库查询 用户的信息进行认证权限的控制
        //现在和CAS集成，该方法就只做一件事，就是授权，认证交给CAS
        return new User(s,"", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
    }
}
