package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {
    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/submitOrder")
    public Result submitOrder(Long seckillId){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(userId)){
            return new Result(false,"用户未登录");
        }
        try {
            seckillOrderService.submitOrder(seckillId,userId);
            return new Result(true,"提交成功");
        }catch (RuntimeException e){
            e.printStackTrace();
            return new Result(false,e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"提交失败");
        }


    }
}
