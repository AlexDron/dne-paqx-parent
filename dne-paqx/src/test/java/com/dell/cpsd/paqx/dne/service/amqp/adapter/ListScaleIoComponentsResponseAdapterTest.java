/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.storage.capabilities.api.ListComponentResponseMessage;
import com.dell.cpsd.storage.capabilities.api.MessageProperties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for ListScaleIoComponentsResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class ListScaleIoComponentsResponseAdapterTest extends BaseResponseAdapterTest<ListComponentResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<ListComponentResponseMessage, ServiceResponse<ListComponentResponseMessage>> createTestable()
    {
        return new ListScaleIoComponentsResponseAdapter(this.registry);
    }

    @Override
    protected ListComponentResponseMessage createResponseMessageSpy()
    {
        ListComponentResponseMessage theSpy = spy(ListComponentResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}