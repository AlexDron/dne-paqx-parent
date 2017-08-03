/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */
package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;
import com.dell.cpsd.virtualization.capabilities.api.ValidateVcenterClusterResponseMessage;

public class ValidateClusterResponseAdapter implements ServiceCallbackAdapter<ValidateVcenterClusterResponseMessage, ServiceResponse<ValidateVcenterClusterResponseMessage>>
{
    private ServiceCallbackRegistry serviceCallbackRegistry;

    public ValidateClusterResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<ValidateVcenterClusterResponseMessage> transform(ValidateVcenterClusterResponseMessage validateVcenterClusterResponseMessage) {
        return new ServiceResponse<>(validateVcenterClusterResponseMessage.getMessageProperties().getCorrelationId(), validateVcenterClusterResponseMessage, null);

    }

    @Override
    public void consume(IServiceCallback callback, ServiceResponse<ValidateVcenterClusterResponseMessage> validateClusterResponse)
    {
        callback.handleServiceResponse(validateClusterResponse);
    }

    @Override
    public IServiceCallback take(ValidateVcenterClusterResponseMessage validateVcenterClusterResponseMessage) {
        return serviceCallbackRegistry.removeServiceCallback(validateVcenterClusterResponseMessage.getMessageProperties().getCorrelationId());

    }

    @Override
    public Class<ValidateVcenterClusterResponseMessage> getSourceClass() {
        return ValidateVcenterClusterResponseMessage.class;
    }

}
