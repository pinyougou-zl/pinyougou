package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Service
public class LoginServiceImpl extends CoreServiceImpl<TbUser> implements LoginService {

    private TbUserMapper userMapper;

    @Autowired
    public LoginServiceImpl(TbUserMapper userMapper) {
        super(userMapper, TbUser.class);
        this.userMapper=userMapper;
    }


    /**
     * 登录前对用户进行检查
     * @param username
     * @return
     */
    @Override
    public Boolean login(String username){
        TbUser condition = new TbUser();
        condition.setUsername(username);
        TbUser tbUser = userMapper.selectOne(condition);

        //优先判断状态，节约系统资源
        if ("N".equals(tbUser.getStatus())){
            return false;
        }

        //获取用户最后登录的时间
        Date lastLoginTime = tbUser.getLastLoginTime();
        if (lastLoginTime == null) {
            //第一次登录的时候，这个值肯定是null，所以赋值为当前时间
            lastLoginTime = new Date();
            tbUser.setLastLoginTime(lastLoginTime);
        }

        //获取当前时间
        Date nowTime = new Date();

        //两个时间做对比，超过三个月就冻结用户
        long now = nowTime.getTime();
        long last = lastLoginTime.getTime();

        int days = (int) ((now-last) / (1000*3600*24));
        if (days >= 90) {
            //修改状态
            tbUser.setStatus("N");
            userMapper.updateByPrimaryKeySelective(tbUser);
            return false;
        }
        //登录成功,更新上次登录时间,并增加登录次数
        tbUser.setLoginCount(tbUser.getLoginCount()+1);
        userMapper.updateByPrimaryKeySelective(tbUser);
        return true;
    }
}
