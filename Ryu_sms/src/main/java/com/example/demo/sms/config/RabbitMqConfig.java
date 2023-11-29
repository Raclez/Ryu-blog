package com.example.demo.sms.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置文件【可用于自动生成队列和交换机】
 */
@Configuration
public class RabbitMqConfig {

    public static final String Ryu_BLOG = "Ryu.blog";
    public static final String Ryu_EMAIL = "Ryu.email";
    public static final String Ryu_SMS = "Ryu.sms";
    public static final String EXCHANGE_DIRECT = "exchange.direct";
    public static final String ROUTING_KEY_BLOG = "Ryu.blog";
    public static final String ROUTING_KEY_EMAIL = "Ryu.email";
    public static final String ROUTING_KEY_SMS = "Ryu.sms";

    public static final String DEAD_LETTER_EXCHANGE = "exchange.spider";
    public static final String DEAD_LETTER_QUEUE = "Ryu.spider";
    public static final String DEAD_LETTER_ROUTING_KEY = "Ryu.spider";

    /**
     * 声明死信交换机
     */
    @Bean(DEAD_LETTER_EXCHANGE)
    public Exchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(DEAD_LETTER_EXCHANGE).durable(true).build();
    }

    /**
     * 声明死信队列
     */
    @Bean(DEAD_LETTER_QUEUE)
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    /**
     * 死信队列绑定死信交换机
     */
    @Bean
    public Binding deadLetterBinding(@Qualifier(DEAD_LETTER_QUEUE) Queue queue, @Qualifier(DEAD_LETTER_EXCHANGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DEAD_LETTER_ROUTING_KEY).noargs();
    }


    /**
     * 声明交换机
     */
    @Bean(EXCHANGE_DIRECT)
    public Exchange EXCHANGE_DIRECT() {
        // 声明路由交换机，durable:在rabbitmq重启后，交换机还在
        return ExchangeBuilder.directExchange(EXCHANGE_DIRECT).durable(true).build();
    }

    /**
     * 声明Blog队列
     *
     * @return
     */
    @Bean(Ryu_BLOG)
    public Queue Ryu_BLOG() {
        return new Queue(Ryu_BLOG);
    }

    /**
     * 声明Email队列
     *
     * @return
     */
    @Bean(Ryu_EMAIL)
    public Queue Ryu_EMAIL() {
        return new Queue(Ryu_EMAIL);
    }

    /**
     * 声明SMS队列
     *
     * @return
     */
    @Bean(Ryu_SMS)
    public Queue Ryu_SMS() {
        return new Queue(Ryu_SMS);
    }

    /**
     * Ryu.blog 队列绑定交换机，指定routingKey
     *
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding BINDING_QUEUE_INFORM_BLOG(@Qualifier(Ryu_BLOG) Queue queue, @Qualifier(EXCHANGE_DIRECT) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_BLOG).noargs();
    }

    /**
     * Ryu.mail 队列绑定交换机，指定routingKey
     *
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding BINDING_QUEUE_INFORM_EMAIL(@Qualifier(Ryu_EMAIL) Queue queue, @Qualifier(EXCHANGE_DIRECT) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_EMAIL).noargs();
    }

    /**
     * Ryu.sms 队列绑定交换机，指定routingKey
     *
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding BINDING_QUEUE_INFORM_SMS(@Qualifier(Ryu_SMS) Queue queue, @Qualifier(EXCHANGE_DIRECT) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_SMS).noargs();
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
