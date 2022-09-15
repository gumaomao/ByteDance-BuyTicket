package com.example.buyticket.listener;


import com.example.buyticket.dao.TicketDao;
import com.example.buyticket.entity.OrderInfo;
import com.example.buyticket.entity.Ticket;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RabbitListener(queues = "ticket.snapUp.queue")
public class TicketSnapUpListener {

    @Autowired
    private TicketDao ticketDao;

    @RabbitHandler
    public void listener(OrderInfo orderInfo, Channel channel, Message message) throws IOException {
        log.info("准备创建抢购车票订单的详细信息...");
        try {
//           TODO 根据orderInfo 创建并保存订单 扣减库存
            Ticket ticket = ticketDao.selectById(1);
            if (ticket.getNum() == 0) {
               return;
            }
            ticketDao.debit(1,orderInfo.getTicketNum());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

}
