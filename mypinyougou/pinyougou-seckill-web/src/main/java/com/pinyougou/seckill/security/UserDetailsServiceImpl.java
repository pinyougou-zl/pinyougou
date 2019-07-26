package com.pinyougou.seckill.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.user.service.LoginService;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Reference
    private LoginService loginService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Boolean flag = loginService.login(username);
        if (flag) {
            //只做授权，认证交给CAS
            return new User(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
        }else {
            //用户已冻结
            return new User(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList("NONE"));
        }
    }
}
