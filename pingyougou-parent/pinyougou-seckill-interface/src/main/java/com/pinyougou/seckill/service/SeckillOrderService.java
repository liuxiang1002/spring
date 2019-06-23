package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillOrder;

import java.util.Map;

public interface SeckillOrderService {
    /**
     * 提交订单
     * @param seckillId
     * @param userId
     */
    public void submitOrder(Long seckillId,String userId);

    /**
     * 根据用户名查询秒杀订单
     * @param userId
     */
    public TbSeckillOrder searchOrderFromRedisByUserId(String userId);
    /**
     * 支付成功保存订单
     * @param userId
     * @param orderId
     */
    public void saveOrderFromRedisToDb(String userId,Long orderId,String transactionId);
    /**
     * 从缓存中删除订单
     * @param userId
     * @param orderId
     */
    public void deleteOrderFromRedis(String userId,Long orderId);



}
