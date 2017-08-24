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
import com.dell.cpsd.virtualization.capabilities.api.AddEsxiHostVSphereLicenseResponse;

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
public class ApplyEsxiLicenseResponseAdapter
        implements ServiceCallbackAdapter<AddEsxiHostVSphereLicenseResponse, ServiceResponse<AddEsxiHostVSphereLicenseResponse>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public ApplyEsxiLicenseResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<AddEsxiHostVSphereLicenseResponse> transform(final AddEsxiHostVSphereLicenseResponse responseMessage)
    {
        return new ServiceResponse<>(responseMessage.getMessageProperties().getCorrelationId(), responseMessage, null);
    }

    @Override
    public void consume(final IServiceCallback callback, final ServiceResponse<AddEsxiHostVSphereLicenseResponse> responseMessage)
    {
        callback.handleServiceResponse(responseMessage);
    }

    @Override
    public IServiceCallback take(final AddEsxiHostVSphereLicenseResponse addEsxiHostVSphereLicenseResponse)
    {
        return serviceCallbackRegistry.removeServiceCallback(addEsxiHostVSphereLicenseResponse.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<AddEsxiHostVSphereLicenseResponse> getSourceClass()
    {
        return AddEsxiHostVSphereLicenseResponse.class;
    }
}
