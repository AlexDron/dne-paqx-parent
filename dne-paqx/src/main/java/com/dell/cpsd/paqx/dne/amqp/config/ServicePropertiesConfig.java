package com.dell.cpsd.paqx.dne.amqp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

@Configuration
@PropertySources({@PropertySource(value = "file:/opt/dell/cpsd/rackhd-adapter-service/conf/service.properties", ignoreResourceNotFound = true)})
@Qualifier("servicePropertiesConfig")
public class ServicePropertiesConfig {

    @Autowired
    private Environment environment;

}
