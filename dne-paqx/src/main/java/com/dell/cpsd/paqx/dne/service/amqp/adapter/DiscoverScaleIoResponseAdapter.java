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
import com.dell.cpsd.storage.capabilities.api.ListStorageResponseMessage;

/**
 * TODO: Document Usage
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class DiscoverScaleIoResponseAdapter
        implements ServiceCallbackAdapter<ListStorageResponseMessage, ServiceResponse<ListStorageResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public DiscoverScaleIoResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<ListStorageResponseMessage> transform(final ListStorageResponseMessage responseMessage)
    {
        return new ServiceResponse<>(responseMessage.getMessageProperties().getCorrelationId(), responseMessage, null);
    }

    @Override
    public void consume(final IServiceCallback callback, final ServiceResponse<ListStorageResponseMessage> responseMessage)
    {
        callback.handleServiceResponse(responseMessage);
    }

    @Override
    public IServiceCallback take(final ListStorageResponseMessage listComponentResponseMessage)
    {
        return serviceCallbackRegistry.removeServiceCallback(listComponentResponseMessage.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<ListStorageResponseMessage> getSourceClass()
    {
        return ListStorageResponseMessage.class;
    }
}
