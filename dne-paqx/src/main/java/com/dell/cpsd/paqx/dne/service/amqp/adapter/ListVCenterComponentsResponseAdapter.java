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
import com.dell.cpsd.virtualization.capabilities.api.ListComponentsResponseMessage;

/**
 * List VCenter Components Response Adapter
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class ListVCenterComponentsResponseAdapter
        implements ServiceCallbackAdapter<ListComponentsResponseMessage, ServiceResponse<ListComponentsResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public ListVCenterComponentsResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<ListComponentsResponseMessage> transform(final ListComponentsResponseMessage responseMessage)
    {
        return new ServiceResponse<>(responseMessage.getMessageProperties().getCorrelationId(), responseMessage, null);
    }

    @Override
    public void consume(final IServiceCallback callback, final ServiceResponse<ListComponentsResponseMessage> responseMessage)
    {
        callback.handleServiceResponse(responseMessage);
    }

    @Override
    public IServiceCallback take(final ListComponentsResponseMessage listComponentsResponseMessage)
    {
        return serviceCallbackRegistry.removeServiceCallback(listComponentsResponseMessage.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<ListComponentsResponseMessage> getSourceClass()
    {
        return ListComponentsResponseMessage.class;
    }
}