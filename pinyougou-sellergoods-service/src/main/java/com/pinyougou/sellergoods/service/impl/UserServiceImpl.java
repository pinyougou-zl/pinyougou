package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.sellergoods.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl extends CoreServiceImpl<TbUser> implements UserService {

	
	private TbUserMapper userMapper;

	@Autowired
	public UserServiceImpl(TbUserMapper userMapper) {
		super(userMapper, TbUser.class);
		this.userMapper=userMapper;
	}


	@Autowired
	private TbGoodsMapper goodsMapper;
	

	
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


	/**
	 * 用户统计
	 * 作者：房靖滔
	 */
	@Override
	public List<TbGoods> userCount(){
		//查询当前有效数据
		Example example = new Example(TbGoods.class);
		example.createCriteria().andEqualTo("auditStatus", "1");
		example.setOrderByClause("sellerNumber DESC");
		List<TbGoods> tbGoodsList = goodsMapper.selectByExample(example);
		//获取最受欢迎的前五个商品数据,不足就直接返回
		if (tbGoodsList.size() > 6) {
			return tbGoodsList.subList(0, 5);
		}else {
			return tbGoodsList;
		}
	}

	/**
	 * 活跃用户和非活跃用户的统计
	 * @return
	 */
	@Override
	public Map<String,Object> userActive(){
		//先查询有效用户
		Example example = new Example(TbUser.class);
		example.createCriteria().andEqualTo("status", "Y");
		List<TbUser> userList = userMapper.selectByExample(example);
		//建立两个集合，分别存储活跃用户和非活跃用户
		List<TbUser> activeUser = new ArrayList<>();
		List<TbUser> inActiveUser = new ArrayList<>();
		//遍历用户数据，进行垃圾分类
		for (TbUser tbUser : userList) {
			//一周登录十次以上的是活跃用户，其他都是不活跃的
			if (tbUser.getLoginCount() >= 10) {
				activeUser.add(tbUser);
			}else {
				inActiveUser.add(tbUser);
			}
		}
		//建立一个map集合用于返回数据
		Map<String,Object> map = new HashMap<>();
		map.put("activeUser", activeUser);
		map.put("inActiveUser", inActiveUser);
		return map;
	}


	@Override
	public void userExport(List<TbUser> allUser) {
		FileOutputStream fos = null;
		try {
			// 创建工作簿
			XSSFWorkbook wb = new XSSFWorkbook();
			// 工作表
			XSSFSheet sheet = wb.createSheet("用户信息表");
			// 标头行，代表第一行
			XSSFRow header = sheet.createRow(0);
			// 创建单元格，0代表第一行第一列
			XSSFCell cell = header.createCell(0);
			cell.setCellValue("用户名");
			header.createCell(1).setCellValue("注册手机号");
			header.createCell(2).setCellValue("注册邮箱");
			header.createCell(3).setCellValue("创建时间");
			header.createCell(4).setCellValue("使用状态");
			header.createCell(5).setCellValue("最后登录时间");
			header.createCell(6).setCellValue("一周登录次数统计");
			// 设置列的宽度
			// getPhysicalNumberOfCells()代表这行有多少包含数据的列
			for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
				// POI设置列宽度时比较特殊，它的基本单位是1/255个字符大小，
				// 因此我们要想让列能够盛的下20个字符的话，就需要用255*20
				sheet.setColumnWidth(i, 255 * 20);
			}
			// 设置行高，30像素
			header.setHeightInPoints(30);


			//导出数据库中的数据
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (int i = 0; i < allUser.size(); i++) {
				XSSFRow row = sheet.createRow(i+1);
				TbUser tbUser = allUser.get(i);
				row.createCell(0).setCellValue(tbUser.getUsername());
				row.createCell(1).setCellValue(tbUser.getPhone());
				row.createCell(2).setCellValue(tbUser.getEmail());
				row.createCell(3).setCellValue(sdf.format(tbUser.getCreated()));
				row.createCell(4).setCellValue(tbUser.getStatus());
				row.createCell(5).setCellValue(sdf.format(tbUser.getLastLoginTime()));
				row.createCell(6).setCellValue(tbUser.getLoginCount());
			}

			//输出文件要么是 \\要么/否则会报错
			fos = new FileOutputStream("e:/user.xlsx");
			// 向指定文件写入内容
			wb.write(fos);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("导出Excel文件异常");
		} finally {
			//关闭流
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
