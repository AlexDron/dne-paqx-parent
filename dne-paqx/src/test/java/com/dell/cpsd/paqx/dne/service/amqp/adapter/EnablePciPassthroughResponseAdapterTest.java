/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.virtualization.capabilities.api.EnablePCIPassthroughResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.MessageProperties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for EnablePciPassthroughResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class EnablePciPassthroughResponseAdapterTest extends BaseResponseAdapterTest<EnablePCIPassthroughResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<EnablePCIPassthroughResponseMessage, ServiceResponse<EnablePCIPassthroughResponseMessage>> createTestable()
    {
        return new EnablePciPassthroughResponseAdapter(this.registry);
    }

    @Override
    protected EnablePCIPassthroughResponseMessage createResponseMessageSpy()
    {
        EnablePCIPassthroughResponseMessage theSpy = spy(EnablePCIPassthroughResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}