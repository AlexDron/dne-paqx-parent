package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;
import com.dell.cpsd.service.engineering.standards.EssValidateProtectionDomainsResponseMessage;

public class ValidateProtectionDomainResponseAdapter implements ServiceCallbackAdapter<EssValidateProtectionDomainsResponseMessage, ServiceResponse<EssValidateProtectionDomainsResponseMessage>> {

    private ServiceCallbackRegistry serviceCallbackRegistry;

    public ValidateProtectionDomainResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<EssValidateProtectionDomainsResponseMessage> transform(EssValidateProtectionDomainsResponseMessage essValidateProtectionDomainsResponseMessage)
    {
        return new ServiceResponse<>(essValidateProtectionDomainsResponseMessage.getMessageProperties().getCorrelationId(), essValidateProtectionDomainsResponseMessage, null);

    }

    @Override
    public void consume(IServiceCallback callback, ServiceResponse<EssValidateProtectionDomainsResponseMessage> essValidateProtectionDomainsResponse)
    {
        callback.handleServiceResponse(essValidateProtectionDomainsResponse);
    }

    @Override
    public IServiceCallback take(EssValidateProtectionDomainsResponseMessage essValidateProtectionDomainsResponse)
    {
        return serviceCallbackRegistry.removeServiceCallback(essValidateProtectionDomainsResponse.getMessageProperties().getCorrelationId());

    }

    @Override
    public Class<EssValidateProtectionDomainsResponseMessage> getSourceClass()
    {
        return EssValidateProtectionDomainsResponseMessage.class;
    }

}
