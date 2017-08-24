/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.virtualization.capabilities.api.DeployVMFromTemplateResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.MessageProperties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for DeployScaleIoVmResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class DeployScaleIoVmResponseAdapterTest extends BaseResponseAdapterTest<DeployVMFromTemplateResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<DeployVMFromTemplateResponseMessage, ServiceResponse<DeployVMFromTemplateResponseMessage>> createTestable()
    {
        return new DeployScaleIoVmResponseAdapter(this.registry);
    }

    @Override
    protected DeployVMFromTemplateResponseMessage createResponseMessageSpy()
    {
        DeployVMFromTemplateResponseMessage theSpy = spy(DeployVMFromTemplateResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}