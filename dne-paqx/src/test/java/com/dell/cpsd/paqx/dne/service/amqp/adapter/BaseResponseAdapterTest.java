/*
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.common.rabbitmq.message.HasMessageProperties;
import com.dell.cpsd.common.rabbitmq.message.MessagePropertiesContainer;
import com.dell.cpsd.service.common.client.callback.ServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseResponseAdapterTest<R extends HasMessageProperties<?>>
{
    @Mock
    protected ServiceCallbackRegistry registry;

    @Mock
    private ServiceCallback<ServiceResponse<R>> serviceCallback;

    @Mock
    private ServiceResponse<R> serviceResponse;

    private ServiceCallbackAdapter<R, ServiceResponse<R>> adapter;
    private MessagePropertiesContainer properties;
    private String correlationId = "correlationId";
    private R responseMessage;

    protected abstract ServiceCallbackAdapter<R, ServiceResponse<R>> createTestable();
    protected abstract <R> R createResponseMessageSpy();

    /**
     * The test setup.
     *
     * @since 1.0
     */
    @Before
    public void setUp() throws Exception
    {
        this.adapter = this.createTestable();
        this.responseMessage = this.createResponseMessageSpy();
        this.properties = this.responseMessage.getMessageProperties();
    }

    @Test
    public void transform() throws Exception
    {
        doReturn(this.properties).when(this.responseMessage).getMessageProperties();
        doReturn(this.correlationId).when(this.properties).getCorrelationId();

        assertNotNull(this.adapter.transform(this.responseMessage));
    }

    @Test
    public void consume() throws Exception
    {
        this.adapter.consume(this.serviceCallback, this.serviceResponse);

        verify(this.serviceCallback).handleServiceResponse(this.serviceResponse);
    }

    @Test
    public void take() throws Exception
    {
        doReturn(this.properties).when(this.responseMessage).getMessageProperties();
        doReturn(this.correlationId).when(this.properties).getCorrelationId();

        this.adapter.take(this.responseMessage);

        verify(this.registry).removeServiceCallback(this.correlationId);
    }

    @Test
    public void getSourceClass() throws Exception
    {
        assertEquals(this.responseMessage.getClass().getSuperclass(), this.adapter.getSourceClass());
    }
}