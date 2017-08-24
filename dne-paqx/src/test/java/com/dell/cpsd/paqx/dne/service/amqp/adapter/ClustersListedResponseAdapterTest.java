/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.virtualization.capabilities.api.DiscoverClusterResponseInfoMessage;
import com.dell.cpsd.virtualization.capabilities.api.MessageProperties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for ClustersListedResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class ClustersListedResponseAdapterTest extends BaseResponseAdapterTest<DiscoverClusterResponseInfoMessage>
{
    @Override
    protected ServiceCallbackAdapter<DiscoverClusterResponseInfoMessage, ServiceResponse<DiscoverClusterResponseInfoMessage>> createTestable()
    {
        return new ClustersListedResponseAdapter(this.registry);
    }

    @Override
    protected DiscoverClusterResponseInfoMessage createResponseMessageSpy()
    {
        DiscoverClusterResponseInfoMessage theSpy = spy(DiscoverClusterResponseInfoMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}
