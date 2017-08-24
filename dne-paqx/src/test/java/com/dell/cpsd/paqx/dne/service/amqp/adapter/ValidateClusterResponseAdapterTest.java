/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.virtualization.capabilities.api.MessageProperties;
import com.dell.cpsd.virtualization.capabilities.api.ValidateVcenterClusterResponseMessage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for ValidateClusterResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class ValidateClusterResponseAdapterTest extends BaseResponseAdapterTest<ValidateVcenterClusterResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<ValidateVcenterClusterResponseMessage, ServiceResponse<ValidateVcenterClusterResponseMessage>> createTestable()
    {
        return new ValidateClusterResponseAdapter(this.registry);
    }

    @Override
    protected ValidateVcenterClusterResponseMessage createResponseMessageSpy()
    {
        ValidateVcenterClusterResponseMessage theSpy = spy(ValidateVcenterClusterResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}