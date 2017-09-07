package com.dell.cpsd.paqx.dne.rest.config;

import org.junit.Test;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * API Documentation Config Test
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class ApiDocumentationConfigTest
{
    @Test
    public void documentation() throws Exception
    {
        final Docket docket = new ApiDocumentationConfig().documentation();

        assertNotNull(docket);
        assertEquals(docket.getGroupName(), "Node Expansion");
        assertEquals(docket.getDocumentationType(), DocumentationType.SWAGGER_2);
    }
}