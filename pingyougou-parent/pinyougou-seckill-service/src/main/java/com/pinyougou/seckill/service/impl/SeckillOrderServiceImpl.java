package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbSeckillGoodsMapper;

import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import util.IdWorker;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;
    @Autowired
    private IdWorker idWorker;
    @Override
    public void submitOrder(Long seckillId, String userId) {

        //从缓存中查询秒杀商品
        TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
        if (seckillGoods==null){
            throw new RuntimeException("商品不存在");
        }
        if (seckillGoods.getStockCount()<0){
            throw new RuntimeException("商品已抢购一空");
        }
        //扣减（redis）库存
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        redisTemplate.boundHashOps("seckillGoods").put(seckillId,seckillGoods);//放回缓存
        if (seckillGoods.getStockCount()==0){//如果库存被抢光
            seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
            redisTemplate.boundHashOps("seckillGoods").delete(seckillId);

        }
        //保存（redis）订单
        long orderId = idWorker.nextId();
        TbSeckillOrder seckillOrder=new TbSeckillOrder();
        seckillOrder.setId(orderId);
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价格
        seckillOrder.setSeckillId(seckillId);
        seckillOrder.setSellerId(seckillGoods.getSellerId());
        seckillOrder.setUserId(userId);//设置用户ID
        seckillOrder.setStatus("0");//状态
        redisTemplate.boundHashOps("seckillOrder").put(userId, seckillOrder);

    }

    @Override
    public TbSeckillOrder searchOrderFromRedisByUserId(String userId) {

        return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);

    }

    @Override
    public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {
        System.out.println("saveOrderFromRedisToDb:"+userId);
        //根据用户ID查询日志
        TbSeckillOrder  seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
        if (seckillOrder == null){
            throw new RuntimeException("订单不存在");
        }
        //如果与传递过来的订单号不符
        if (seckillOrder.getId().longValue()!=orderId.longValue()){
            throw new RuntimeException("订单不符");
        }
        seckillOrder.setTransactionId(transactionId);//交易流水号
        seckillOrder.setCreateTime(new Date());//支付时间
        seckillOrder.setStatus("1");//交易状态
        seckillOrderMapper.insert(seckillOrder);
        redisTemplate.boundHashOps("seckillOrder").delete(userId);
    }

    @Override
    public void deleteOrderFromRedis(String userId, Long orderId) {
        //根据用户ID查询日志
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
        if (seckillOrder!=null && seckillOrder.getId().longValue()==orderId.longValue()){
            redisTemplate.boundHashOps("seckillOrder").delete(userId);//删除缓存中的订单
            //恢复库存
            //1从库存中提取秒杀商品
            TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSeckillId());
            if (seckillGoods!=null){
                seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
                redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(),seckillGoods);
            }else {
                seckillGoods=new TbSeckillGoods();
                seckillGoods.setId(seckillOrder.getSeckillId());
                //设置属性。。如果seckillOrder中没有，需从数据库查询并赋值
                seckillGoods.setStockCount(1);//库存数为1
                //存入缓存
                redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(),seckillGoods);
            }

        }

    }

}
