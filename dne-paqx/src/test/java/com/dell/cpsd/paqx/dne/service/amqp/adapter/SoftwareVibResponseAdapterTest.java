/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.virtualization.capabilities.api.MessageProperties;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBResponseMessage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for SoftwareVibResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class SoftwareVibResponseAdapterTest extends BaseResponseAdapterTest<SoftwareVIBResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<SoftwareVIBResponseMessage, ServiceResponse<SoftwareVIBResponseMessage>> createTestable()
    {
        return new SoftwareVibResponseAdapter(this.registry);
    }

    @Override
    protected SoftwareVIBResponseMessage createResponseMessageSpy()
    {
        SoftwareVIBResponseMessage theSpy = spy(SoftwareVIBResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}