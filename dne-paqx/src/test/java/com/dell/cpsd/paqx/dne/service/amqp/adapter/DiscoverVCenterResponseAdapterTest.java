/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.virtualization.capabilities.api.DiscoveryResponseInfoMessage;
import com.dell.cpsd.virtualization.capabilities.api.MessageProperties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for DiscoverVCenterResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class DiscoverVCenterResponseAdapterTest extends BaseResponseAdapterTest<DiscoveryResponseInfoMessage>
{
    @Override
    protected ServiceCallbackAdapter<DiscoveryResponseInfoMessage, ServiceResponse<DiscoveryResponseInfoMessage>> createTestable()
    {
        return new DiscoverVCenterResponseAdapter(this.registry);
    }

    @Override
    protected DiscoveryResponseInfoMessage createResponseMessageSpy()
    {
        DiscoveryResponseInfoMessage theSpy = spy(DiscoveryResponseInfoMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}