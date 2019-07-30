package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.entity.Error;
import com.entity.Result;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.PhoneFormatCheckUtils;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){			
		return userService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return userService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add/{smscode}")
	public Result add(@Valid @RequestBody TbUser user, BindingResult bindingResult, @PathVariable(value = "smscode") String smscode){
		//注册用户的
		try {
			//先进行校验
			if(bindingResult.hasErrors()) {  //有错误
				Result result = new Result(false, "失败");
				List<FieldError> fieldErrors = bindingResult.getFieldErrors();
				for (FieldError fieldError : fieldErrors) {
					result.getErrorsList().add(new Error(fieldError.getField(),fieldError.getDefaultMessage()));
				}
				return result;
			}

			//进行验证码的判定
			boolean b = userService.checkSmsCode(user.getPhone(), smscode);
			if(b==false) {
				Result result = new Result(false, "验证码输入错误");
				result.getErrorsList().add(new Error("smsCode","验证码输入错误"));
				return result;
			}

			//我们还需要添加一些信息
			user.setCreated(new Date());  //创建日期
			user.setUpdated(new Date());  //更新日期
			//对注册进来的密码进行加密
			String s = DigestUtils.md5Hex(user.getPassword());  //使用md5加密
			user.setPassword(s);
			//进行增加
			userService.add(user);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}

	/**
	 * 我们需要对手机号码进行一个判断
	 */
	@RequestMapping("/sendCode")
	public Result sendCode(String phone) {
		//判断手机的格式
		if(!PhoneFormatCheckUtils.isChinaPhoneLegal(phone) ) {
			return new Result(false, "手机号格式不正确");
		}
		try {
			userService.createSmsCode(phone);  //生成验证码
			return new Result(false, "验证码发送成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "验证码发送失败");
		}
	}
	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user){
		try {
			userService.update(user);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne/{id}")
	public TbUser findOne(@PathVariable(value = "id") Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			userService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbUser user) {
        return userService.findPage(pageNo, pageSize, user);
    }
	
}
