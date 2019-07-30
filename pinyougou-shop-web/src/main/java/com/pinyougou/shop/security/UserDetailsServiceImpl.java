package com.pinyougou.shop.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public class UserDetailsServiceImpl implements UserDetailsService {

    @Reference
    private SellerService sellerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(username);
        //return new User(s,"{noop}123", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_SELLER"));
    //引入数据库认证
        //跟据用户查出对象
        TbSeller tb = sellerService.findOne(username);
        System.out.println(tb.getStatus());
        if(tb==null) {
            return null;
        }
        //不为空，获取状态
        String status = tb.getStatus();
        //判断状态是否为1
        if(!"1".equals(status)) {
            //未审核，账号不能用
            return null;
        }
        System.out.println("验证成功了");

        //交给框架自动匹配
        return new User(username,tb.getPassword(),
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_SELLER"));
    }

}
