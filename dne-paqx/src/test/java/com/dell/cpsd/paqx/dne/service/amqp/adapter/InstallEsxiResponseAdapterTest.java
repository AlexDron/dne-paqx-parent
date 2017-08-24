/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.converged.capabilities.compute.discovered.nodes.api.InstallESXiResponseMessage;
import com.dell.converged.capabilities.compute.discovered.nodes.api.MessageProperties;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for InstallEsxiResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class InstallEsxiResponseAdapterTest extends BaseResponseAdapterTest<InstallESXiResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<InstallESXiResponseMessage, ServiceResponse<InstallESXiResponseMessage>> createTestable()
    {
        return new InstallEsxiResponseAdapter(this.registry);
    }

    @Override
    protected InstallESXiResponseMessage createResponseMessageSpy()
    {
        InstallESXiResponseMessage theSpy = spy(InstallESXiResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}