/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.storage.capabilities.api.ListStorageResponseMessage;
import com.dell.cpsd.storage.capabilities.api.MessageProperties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for DiscoverScaleIoResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class DiscoverScaleIoResponseAdapterTest extends BaseResponseAdapterTest<ListStorageResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<ListStorageResponseMessage, ServiceResponse<ListStorageResponseMessage>> createTestable()
    {
        return new DiscoverScaleIoResponseAdapter(this.registry);
    }

    @Override
    protected ListStorageResponseMessage createResponseMessageSpy()
    {
        ListStorageResponseMessage theSpy = spy(ListStorageResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}