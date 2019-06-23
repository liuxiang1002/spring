package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.io.Serializable;
@Component  
public class ItemPageDeleteListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {

        try {
            ObjectMessage objectMessage =(ObjectMessage)message;
            Long[] goodids = (Long[])objectMessage.getObject();
            System.out.println("ItemDeleteListener监听接收到消息..."+goodids);
            boolean b = itemPageService.deleteItemHtml(goodids);
            System.out.println("网页删除结果："+b);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
