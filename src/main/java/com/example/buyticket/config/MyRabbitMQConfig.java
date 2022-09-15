package com.example.buyticket.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zr
 * @date 2022/1/10 15:27
 */
@Configuration
public class MyRabbitMQConfig {
    /**
     * 以json序列化的格式发送消息
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 普通路由
     * @return
     */
    @Bean
    public Exchange ticketEventExchange(){
        /*
         *   String name,
         *   boolean durable,
         *   boolean autoDelete,
         *   Map<String, Object> arguments
         * */
        TopicExchange topicExchange = new TopicExchange("ticket-event-exchange",true,false);

        return topicExchange;
    }


    /**
     * 抢票队列
     * 作用：削峰，创建订单
     */
    @Bean
    public Queue orderSecKillOrderQueue() {
        Queue queue = new Queue("ticket.snapUp.queue", true, false, false);
        return queue;
    }

    @Bean
    public Binding orderSecKillOrderQueueBinding() {
        //String destination, DestinationType destinationType, String exchange, String routingKey,
        // 			Map<String, Object> arguments
        Binding binding = new Binding(
                "ticket.snapUp.queue",
                Binding.DestinationType.QUEUE,
                "ticket-event-exchange",
                "ticket.snapUp",
                null);

        return binding;
    }
}
