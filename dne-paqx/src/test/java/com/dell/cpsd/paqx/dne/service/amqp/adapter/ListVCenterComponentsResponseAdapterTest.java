/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.virtualization.capabilities.api.ListComponentsResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.MessageProperties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for ListVCenterComponentsResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class ListVCenterComponentsResponseAdapterTest extends BaseResponseAdapterTest<ListComponentsResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<ListComponentsResponseMessage, ServiceResponse<ListComponentsResponseMessage>> createTestable()
    {
        return new ListVCenterComponentsResponseAdapter(this.registry);
    }

    @Override
    protected ListComponentsResponseMessage createResponseMessageSpy()
    {
        ListComponentsResponseMessage theSpy = spy(ListComponentsResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}