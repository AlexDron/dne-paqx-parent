/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.MessageProperties;
import com.dell.cpsd.NodeInventoryResponseMessage;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for NodeInventoryResponseMessageAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class NodeInventoryResponseMessageAdapterTest extends BaseResponseAdapterTest<NodeInventoryResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<NodeInventoryResponseMessage, ServiceResponse<NodeInventoryResponseMessage>> createTestable()
    {
        return new NodeInventoryResponseMessageAdapter(this.registry);
    }

    @Override
    protected NodeInventoryResponseMessage createResponseMessageSpy()
    {
        NodeInventoryResponseMessage theSpy = spy(NodeInventoryResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}
