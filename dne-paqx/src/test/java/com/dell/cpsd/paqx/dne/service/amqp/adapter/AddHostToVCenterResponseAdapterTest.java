/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.virtualization.capabilities.api.ClusterOperationResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.MessageProperties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for AddHostToVCenterResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class AddHostToVCenterResponseAdapterTest extends BaseResponseAdapterTest<ClusterOperationResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<ClusterOperationResponseMessage, ServiceResponse<ClusterOperationResponseMessage>> createTestable()
    {
        return new AddHostToVCenterResponseAdapter(this.registry);
    }

    @Override
    protected ClusterOperationResponseMessage createResponseMessageSpy()
    {
        ClusterOperationResponseMessage theSpy = spy(ClusterOperationResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}