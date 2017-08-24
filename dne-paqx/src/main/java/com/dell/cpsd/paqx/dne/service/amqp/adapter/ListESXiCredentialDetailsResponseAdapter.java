/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 */

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;
import com.dell.cpsd.virtualization.capabilities.api.ListEsxiCredentialDetailsResponseMessage;

/**
 * List ESXi Credential Details Response Adapter
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class ListESXiCredentialDetailsResponseAdapter implements
        ServiceCallbackAdapter<ListEsxiCredentialDetailsResponseMessage, ServiceResponse<ListEsxiCredentialDetailsResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public ListESXiCredentialDetailsResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<ListEsxiCredentialDetailsResponseMessage> transform(
            final ListEsxiCredentialDetailsResponseMessage responseMessage)
    {
        return new ServiceResponse<>(responseMessage.getMessageProperties().getCorrelationId(), responseMessage, null);
    }

    @Override
    public void consume(final IServiceCallback callback, final ServiceResponse<ListEsxiCredentialDetailsResponseMessage> responseMessage)
    {
        callback.handleServiceResponse(responseMessage);
    }

    @Override
    public IServiceCallback take(final ListEsxiCredentialDetailsResponseMessage listEsxiCredentialDetailsResponseMessage)
    {
        return serviceCallbackRegistry
                .removeServiceCallback(listEsxiCredentialDetailsResponseMessage.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<ListEsxiCredentialDetailsResponseMessage> getSourceClass()
    {
        return ListEsxiCredentialDetailsResponseMessage.class;
    }
}