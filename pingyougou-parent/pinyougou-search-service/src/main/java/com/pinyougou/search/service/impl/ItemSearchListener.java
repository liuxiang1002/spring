package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;
@Component
public class ItemSearchListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        System.out.println("监听接受的消息。。。");

        try {
            TextMessage textMessage = (TextMessage)message;
            String  text = textMessage.getText();
            List<TbItem> itemList = JSON.parseArray(text, TbItem.class);
            for (TbItem item : itemList) {
                System.out.println(item.getId()+" "+item.getTitle());
                Map jsonObject = JSON.parseObject(item.getSpec());//将spec字段中的json字符串转换为map

                item.setSpecMap(jsonObject);//给带注解的字段赋值
            }
            itemSearchService.importList(itemList);
            System.out.println("成功导入到索引库");
        } catch (JMSException e) {
            e.printStackTrace();

        }


    }
}
