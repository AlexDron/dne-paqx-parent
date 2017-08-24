/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.virtualization.capabilities.api.AddHostToDvSwitchResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.MessageProperties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for AddHostToDvSwitchResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class AddHostToDvSwitchResponseAdapterTest extends BaseResponseAdapterTest<AddHostToDvSwitchResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<AddHostToDvSwitchResponseMessage, ServiceResponse<AddHostToDvSwitchResponseMessage>> createTestable()
    {
        return new AddHostToDvSwitchResponseAdapter(this.registry);
    }

    @Override
    protected AddHostToDvSwitchResponseMessage createResponseMessageSpy()
    {
        AddHostToDvSwitchResponseMessage theSpy = spy(AddHostToDvSwitchResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}