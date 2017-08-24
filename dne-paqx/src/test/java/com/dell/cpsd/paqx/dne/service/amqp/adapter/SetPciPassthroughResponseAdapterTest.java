/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.virtualization.capabilities.api.MessageProperties;
import com.dell.cpsd.virtualization.capabilities.api.UpdatePCIPassthruSVMResponseMessage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for SetPciPassthroughResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class SetPciPassthroughResponseAdapterTest extends BaseResponseAdapterTest<UpdatePCIPassthruSVMResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<UpdatePCIPassthruSVMResponseMessage, ServiceResponse<UpdatePCIPassthruSVMResponseMessage>> createTestable()
    {
        return new SetPciPassthroughResponseAdapter(this.registry);
    }

    @Override
    protected UpdatePCIPassthruSVMResponseMessage createResponseMessageSpy()
    {
        UpdatePCIPassthruSVMResponseMessage theSpy = spy(UpdatePCIPassthruSVMResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}