/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.converged.capabilities.compute.discovered.nodes.api.ConfigureBootDeviceIdracResponseMessage;
import com.dell.converged.capabilities.compute.discovered.nodes.api.MessageProperties;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for ConfigureBootDeviceIdracResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class ConfigureBootDeviceIdracResponseAdapterTest extends BaseResponseAdapterTest<ConfigureBootDeviceIdracResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<ConfigureBootDeviceIdracResponseMessage, ServiceResponse<ConfigureBootDeviceIdracResponseMessage>> createTestable()
    {
        return new ConfigureBootDeviceIdracResponseAdapter(this.registry);
    }

    @Override
    protected ConfigureBootDeviceIdracResponseMessage createResponseMessageSpy()
    {
        ConfigureBootDeviceIdracResponseMessage theSpy = spy(ConfigureBootDeviceIdracResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}