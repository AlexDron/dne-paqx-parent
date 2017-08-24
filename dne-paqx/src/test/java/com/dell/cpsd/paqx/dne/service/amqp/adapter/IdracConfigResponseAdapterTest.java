/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettingsResponseMessage;
import com.dell.cpsd.rackhd.adapter.rabbitmq.MessageProperties;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for IdracConfigResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class IdracConfigResponseAdapterTest extends BaseResponseAdapterTest<IdracNetworkSettingsResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<IdracNetworkSettingsResponseMessage, ServiceResponse<IdracNetworkSettingsResponseMessage>> createTestable()
    {
        return new IdracConfigResponseAdapter(this.registry);
    }

    @Override
    protected IdracNetworkSettingsResponseMessage createResponseMessageSpy()
    {
        IdracNetworkSettingsResponseMessage theSpy = spy(IdracNetworkSettingsResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}