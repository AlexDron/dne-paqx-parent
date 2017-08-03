/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.paqx.dne.amqp.config;

import com.dell.cpsd.common.rabbitmq.connectors.RabbitMQCachingConnectionFactory;
import com.dell.cpsd.common.rabbitmq.connectors.TLSConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

@Configuration
public class ProductionConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductionConfig.class);
    @Autowired
    private PropertiesConfig propertiesConfig;

    @Bean
    @Qualifier("rabbitConnectionFactory")
    public ConnectionFactory productionCachingConnectionFactory() {

        com.rabbitmq.client.ConnectionFactory connectionFactory = null;

        LOGGER.info("Rabbit connection properties : sslEnabled: [{}], host: [{}], port: [{}], tlsVersion:[{}]",
                propertiesConfig.isSslEnabled(), propertiesConfig.rabbitHostname(),
                propertiesConfig.rabbitPort(), propertiesConfig.tlsVersion());

        if (propertiesConfig.isSslEnabled())
        {
            connectionFactory = new TLSConnectionFactory(propertiesConfig);
        }
        else
        {
            connectionFactory = new com.rabbitmq.client.ConnectionFactory();
        }

        return new RabbitMQCachingConnectionFactory(connectionFactory, propertiesConfig);
    }
}
