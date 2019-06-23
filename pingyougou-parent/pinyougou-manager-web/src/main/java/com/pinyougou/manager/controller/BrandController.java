package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<TbBrand> findAll() {
        List<TbBrand> tbBrands = brandService.findAll();
        return tbBrands;
    }

    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        PageResult page1 = brandService.findPage(page, rows);
        return page1;

    }

    //增加
    @RequestMapping("/findAdd")
    public Result findAdd(@RequestBody TbBrand tbBrand) {

        try {
            brandService.findAdd(tbBrand);
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }

    }

    //修改
    @RequestMapping("/findOne")
    public TbBrand findOne(Long id) {
        return brandService.findOne(id);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand tbBrand) {
        try {
            brandService.update(tbBrand);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    @RequestMapping("/delects")
    public Result delects(long[] id) {
        try {
            brandService.delects(id);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand tbBrand, int page, int rows) {

        PageResult page1 = brandService.findPage(tbBrand, page, rows);
        return page1;

    }

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList() {

        return brandService.selectOptionList();
    }

}