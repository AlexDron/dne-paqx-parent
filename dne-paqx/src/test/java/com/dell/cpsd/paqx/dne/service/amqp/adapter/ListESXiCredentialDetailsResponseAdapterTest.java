/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.virtualization.capabilities.api.ListEsxiCredentialDetailsResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.MessageProperties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for ListESXiCredentialDetailsResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class ListESXiCredentialDetailsResponseAdapterTest extends BaseResponseAdapterTest<ListEsxiCredentialDetailsResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<ListEsxiCredentialDetailsResponseMessage, ServiceResponse<ListEsxiCredentialDetailsResponseMessage>> createTestable()
    {
        return new ListESXiCredentialDetailsResponseAdapter(this.registry);
    }

    @Override
    protected ListEsxiCredentialDetailsResponseMessage createResponseMessageSpy()
    {
        ListEsxiCredentialDetailsResponseMessage theSpy = spy(ListEsxiCredentialDetailsResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}