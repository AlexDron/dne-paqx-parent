/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.paqx.dne.amqp.config;

import com.dell.cpsd.common.rabbitmq.context.builder.DefaultContainerErrorHandler;
import com.dell.cpsd.common.rabbitmq.retrypolicy.DefaultRetryPolicyFactory;
import com.dell.cpsd.service.common.client.rpc.DefaultMessageConsumer;
import com.dell.cpsd.service.common.client.rpc.DelegatingMessageConsumer;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

@Configuration
public class ConsumerConfig
{

    @Autowired
    @Qualifier("rabbitConnectionFactory")
    private ConnectionFactory rabbitConnectionFactory;

    @Autowired @Qualifier("nodeExpansionResponseQueue")
    private Queue responseQueue;

    @Autowired
    private Queue essResponseQueue;

    @Bean
    SimpleMessageListenerContainer requestListenerContainer(
            @Autowired DelegatingMessageConsumer delegatingMessageConsumer,
            @Autowired MessageConverter dneMessageConverter)
    {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(rabbitConnectionFactory);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setQueues(responseQueue, essResponseQueue);
        container.setAdviceChain(new Advice[]{dneListenerRetryPolicy()});
        container.setMessageConverter(dneMessageConverter);
        container.setMessageListener(new MessageListenerAdapter(delegatingMessageConsumer, dneMessageConverter));
        container.setErrorHandler(new DefaultContainerErrorHandler("dneRequestListenerContainer"));
        return container;
    }

    @Bean
    public DelegatingMessageConsumer defaultMessageConsumer()
    {
        return new DefaultMessageConsumer();
    }

    /**
     * The retry operations interceptor for the consumer.
     *
     * @return  The retry operations interceptor for the consumer.
     *
     * @since   1.0
     */
    @Bean
    RetryOperationsInterceptor dneListenerRetryPolicy()
    {
        return DefaultRetryPolicyFactory.makeContainerListenerRetryPolicy();
    }
}
