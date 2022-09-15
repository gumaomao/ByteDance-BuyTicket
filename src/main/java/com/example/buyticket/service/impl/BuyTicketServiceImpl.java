package com.example.buyticket.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.buyticket.dao.TicketDao;
import com.example.buyticket.entity.OrderInfo;
import com.example.buyticket.entity.Ticket;
import com.example.buyticket.service.BuyTicketService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class BuyTicketServiceImpl implements BuyTicketService {
    @Autowired
    private TicketDao ticketDao;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //存入到Redis,redission中的key
    private String redissKey = "Ticket_On";
    private String redissionKey = "Ticket_Snap";


    @Override
    public boolean buyTicket(String userId, Integer num) throws InterruptedException {

        long s1 = System.currentTimeMillis();

//        根据userId获取当前用户的信息，判断是否登录，合法性检验



        //1、当前抢购车票的详细信息从Redis中获取
        String ticketString = redisTemplate.opsForValue().get(redissKey);
        if (StringUtils.isEmpty(ticketString)) {
            return false;
        }

        Ticket ticket = JSON.parseObject(ticketString,Ticket.class);

        //2.获取信号量
        Integer count = ticket.getNum();

        //判断信号量是否大于0,并且买的数量不能超过库存
        if (count > 0 && count >= num){
            RSemaphore semaphore = redissonClient.getSemaphore(redissionKey);
            boolean semaphoreCount = semaphore.tryAcquire(num, 10, TimeUnit.MILLISECONDS);
            //保证Redis中还有商品库存
            if (semaphoreCount) {
                // 抢票成功 快速下单 发送消息到 MQ
                OrderInfo orderInfo = new OrderInfo();
                orderInfo.setTicketNum(num);
                rabbitTemplate.convertAndSend("ticket-event-exchange","ticket.snapUp",orderInfo);
                long s2 = System.currentTimeMillis();
                log.info("耗时..." + (s2 - s1));
            }else {
                return false;
            }
        }else {
            return false;
        }
        long s3 = System.currentTimeMillis();
        log.info("总耗时..." + (s3 - s1) + "ms");
        return true;
    }

    // 把车票相关信息存入redis中
    @Override
    public void onTicket() {
        Ticket ticket = ticketDao.selectById(1);
        //判断Redis中是否有该信息，如果没有才进行添加
        Boolean hasKey = redisTemplate.hasKey(redissKey);
        if (!hasKey){
            redisTemplate.opsForValue().set(redissKey, JSON.toJSONString(ticket));
        }

        //使用库存作为分布式Redisson信号量（限流）
        RSemaphore semaphore = redissonClient.getSemaphore(redissionKey);
        // 车票可以库存的数量作为信号量
        semaphore.trySetPermits(ticket.getNum());
    }
}
