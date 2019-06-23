package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import com.pinyougou.pojo.TbBrandExample;
import entity.PageResult;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

	@Autowired
	private TbBrandMapper brandMapper;
	
	@Override
	public List<TbBrand> findAll() {

		return brandMapper.selectByExample(null);
	}

	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum,pageSize);

        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);

        return new PageResult(page.getTotal(),page.getResult());
	}

    @Override
    public void findAdd(TbBrand tbBrand) {
        brandMapper.insert(tbBrand);
    }

	@Override
	public TbBrand findOne(Long id) {

		return brandMapper.selectByPrimaryKey(id);
	}

	@Override
	public void update(TbBrand tbBrand) {
		brandMapper.updateByPrimaryKey(tbBrand);
	}

	@Override
	public void delects(long[] ids) {
		for (long id : ids) {
			brandMapper.deleteByPrimaryKey(id);
		}

	}

	@Override
	public PageResult findPage(TbBrand tbBrandint, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum,pageSize);
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        if(tbBrandint!=null){
            if(tbBrandint.getName()!=null && tbBrandint.getName().length()>0){
                criteria.andNameLike("%"+tbBrandint.getName()+"%");
            }
            if(tbBrandint.getFirstChar()!=null && tbBrandint.getFirstChar().length()>0){
                criteria.andFirstCharEqualTo(tbBrandint.getFirstChar());
            }
        }

        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);

		return new PageResult(page.getTotal(),page.getResult());
	}

    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }


}
