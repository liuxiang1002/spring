package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

/**
 * 品牌接口
 * @author Administrator
 *
 */
public interface BrandService {

	public List<TbBrand> findAll();

	//分页
	public PageResult findPage(int pageNum,int pageSize);

	//添加
	public  void findAdd(TbBrand tbBrand);

	//修改
	public TbBrand findOne(Long id);
	public void update(TbBrand tbBrand);

	//删除
	public void delects(long[] ids);

	//查询
	public PageResult findPage(TbBrand tbBrandint,int pageNum,int pageSize);

	List<Map> selectOptionList();
}
