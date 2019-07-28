package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl extends CoreServiceImpl<TbUser>  implements UserService {

	@Autowired
	private RedisTemplate redisTemplate;
	//注入一个生产者
	@Autowired
	private DefaultMQProducer producer;
	@Value("${template_code}")
	private String templateCode;
	@Value("${sign_name}")
	private String signName;
	
	private TbUserMapper userMapper;

	@Autowired
	public UserServiceImpl(TbUserMapper userMapper) {
		super(userMapper, TbUser.class);
		this.userMapper=userMapper;
	}

	/**
	 * 这是验证验证码的
	 * @param phone
	 * @param Code
	 * @return
	 */
	@Override
	public boolean checkSmsCode(String phone, String Code) {
		if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(Code)) {
			return false;
		}
		//不为空进行判断
		//1、从redis中取出缓存中的验证码
		String o = (String) redisTemplate.boundValueOps("ZHUCE_" + phone).get();
		if(!Code.equals(o)) {
			return false;
		}
		return true;
	}

	/**
	 * 根据手机号码，发送验证码
	 * 把验证码存入到redis中
	 * @param phone
	 */
	@Override
	public void createSmsCode(String phone) {
		try {
			System.out.println(phone);
			//1、生成6位的随机数
			String code = (long)((Math.random()*9+1)*100000)+"";
			//2、把验证码存入到redis
			redisTemplate.boundValueOps("ZHUCE_"+phone).set(code);
			//在redis能存24小时
			redisTemplate.boundValueOps("ZHUCE_"+phone).expire(24L, TimeUnit.HOURS);
			//组装对象，手机号 签名 验证码  发送消息
			Map<String,Object> map = new HashMap<>();
			map.put("mobile",phone);  //手机号
			map.put("sign_name",signName);  //标签
			map.put("template_code",templateCode);   //模板
			map.put("param","{\"code\":\""+code+"\"}");  //信息体
			//发送到消息服务器
			Message message = new Message("SMS_TOPIC","SEND_MESSAGE_TAG","createSmsCode",JSON.toJSONString(map).getBytes());
			producer.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbUser> all = userMapper.selectAll();
        PageInfo<TbUser> info = new PageInfo<TbUser>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbUser> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize, TbUser user) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbUser.class);
        Example.Criteria criteria = example.createCriteria();

        if(user!=null){			
						if(StringUtils.isNotBlank(user.getUsername())){
				criteria.andLike("username","%"+user.getUsername()+"%");
				//criteria.andUsernameLike("%"+user.getUsername()+"%");
			}
			if(StringUtils.isNotBlank(user.getPassword())){
				criteria.andLike("password","%"+user.getPassword()+"%");
				//criteria.andPasswordLike("%"+user.getPassword()+"%");
			}
			if(StringUtils.isNotBlank(user.getPhone())){
				criteria.andLike("phone","%"+user.getPhone()+"%");
				//criteria.andPhoneLike("%"+user.getPhone()+"%");
			}
			if(StringUtils.isNotBlank(user.getEmail())){
				criteria.andLike("email","%"+user.getEmail()+"%");
				//criteria.andEmailLike("%"+user.getEmail()+"%");
			}
			if(StringUtils.isNotBlank(user.getSourceType())){
				criteria.andLike("sourceType","%"+user.getSourceType()+"%");
				//criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
			}
			if(StringUtils.isNotBlank(user.getNickName())){
				criteria.andLike("nickName","%"+user.getNickName()+"%");
				//criteria.andNickNameLike("%"+user.getNickName()+"%");
			}
			if(StringUtils.isNotBlank(user.getName())){
				criteria.andLike("name","%"+user.getName()+"%");
				//criteria.andNameLike("%"+user.getName()+"%");
			}
			if(StringUtils.isNotBlank(user.getStatus())){
				criteria.andLike("status","%"+user.getStatus()+"%");
				//criteria.andStatusLike("%"+user.getStatus()+"%");
			}
			if(StringUtils.isNotBlank(user.getHeadPic())){
				criteria.andLike("headPic","%"+user.getHeadPic()+"%");
				//criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
			}
			if(StringUtils.isNotBlank(user.getQq())){
				criteria.andLike("qq","%"+user.getQq()+"%");
				//criteria.andQqLike("%"+user.getQq()+"%");
			}
			if(StringUtils.isNotBlank(user.getIsMobileCheck())){
				criteria.andLike("isMobileCheck","%"+user.getIsMobileCheck()+"%");
				//criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
			}
			if(StringUtils.isNotBlank(user.getIsEmailCheck())){
				criteria.andLike("isEmailCheck","%"+user.getIsEmailCheck()+"%");
				//criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
			}
			if(StringUtils.isNotBlank(user.getSex())){
				criteria.andLike("sex","%"+user.getSex()+"%");
				//criteria.andSexLike("%"+user.getSex()+"%");
			}
	
		}
        List<TbUser> all = userMapper.selectByExample(example);
        PageInfo<TbUser> info = new PageInfo<TbUser>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbUser> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }
	
}
