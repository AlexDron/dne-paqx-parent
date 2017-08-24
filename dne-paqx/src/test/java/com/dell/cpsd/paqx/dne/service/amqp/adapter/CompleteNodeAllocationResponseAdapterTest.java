/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.converged.capabilities.compute.discovered.nodes.api.CompleteNodeAllocationResponseMessage;
import com.dell.converged.capabilities.compute.discovered.nodes.api.MessageProperties;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for CompleteNodeAllocationResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class CompleteNodeAllocationResponseAdapterTest extends BaseResponseAdapterTest<CompleteNodeAllocationResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<CompleteNodeAllocationResponseMessage, ServiceResponse<CompleteNodeAllocationResponseMessage>> createTestable()
    {
        return new CompleteNodeAllocationResponseAdapter(this.registry);
    }

    @Override
    protected CompleteNodeAllocationResponseMessage createResponseMessageSpy()
    {
        CompleteNodeAllocationResponseMessage theSpy = spy(CompleteNodeAllocationResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}