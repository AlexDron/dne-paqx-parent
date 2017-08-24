/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.virtualization.capabilities.api.HostMaintenanceModeResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.MessageProperties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for HostMaintenanceModeResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class HostMaintenanceModeResponseAdapterTest extends BaseResponseAdapterTest<HostMaintenanceModeResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<HostMaintenanceModeResponseMessage, ServiceResponse<HostMaintenanceModeResponseMessage>> createTestable()
    {
        return new HostMaintenanceModeResponseAdapter(this.registry);
    }

    @Override
    protected HostMaintenanceModeResponseMessage createResponseMessageSpy()
    {
        HostMaintenanceModeResponseMessage theSpy = spy(HostMaintenanceModeResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}