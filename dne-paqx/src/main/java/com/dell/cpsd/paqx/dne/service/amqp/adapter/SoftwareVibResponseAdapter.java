/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBResponseMessage;

/**
 * TODO: Document Usage
 *
 * <p/>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * <p/>
 *
 * @version 1.0
 * @since 1.0
 */
public class SoftwareVibResponseAdapter
        implements ServiceCallbackAdapter<SoftwareVIBResponseMessage, ServiceResponse<SoftwareVIBResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public SoftwareVibResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<SoftwareVIBResponseMessage> transform(final SoftwareVIBResponseMessage responseMessage)
    {
        return new ServiceResponse<>(responseMessage.getMessageProperties().getCorrelationId(), responseMessage, null);
    }

    @Override
    public void consume(final IServiceCallback callback, final ServiceResponse<SoftwareVIBResponseMessage> responseMessage)
    {
        callback.handleServiceResponse(responseMessage);
    }

    @Override
    public IServiceCallback take(final SoftwareVIBResponseMessage softwareVIBResponseMessage)
    {
        return serviceCallbackRegistry.removeServiceCallback(softwareVIBResponseMessage.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<SoftwareVIBResponseMessage> getSourceClass()
    {
        return SoftwareVIBResponseMessage.class;
    }
}