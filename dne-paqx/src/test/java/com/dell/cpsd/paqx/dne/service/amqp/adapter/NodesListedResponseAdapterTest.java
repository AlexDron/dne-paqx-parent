/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.MessageProperties;
import com.dell.cpsd.NodesListed;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for NodesListedResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class NodesListedResponseAdapterTest extends BaseResponseAdapterTest<NodesListed>
{
    @Override
    protected ServiceCallbackAdapter<NodesListed, ServiceResponse<NodesListed>> createTestable()
    {
        return new NodesListedResponseAdapter(this.registry);
    }

    @Override
    protected NodesListed createResponseMessageSpy()
    {
        NodesListed theSpy = spy(NodesListed.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}