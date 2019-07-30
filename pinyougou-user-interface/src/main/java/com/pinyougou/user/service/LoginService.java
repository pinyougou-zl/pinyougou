package com.pinyougou.user.service;

import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbUser;

public interface LoginService extends CoreService<TbUser> {
    /**
     * 登录前对用户进行检查
     * @param username
     * @return
     */
    Boolean login(String username);
}
