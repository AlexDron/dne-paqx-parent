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
import com.dell.cpsd.virtualization.capabilities.api.ClusterOperationResponseMessage;

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
public class AddHostToVCenterResponseAdapter
        implements ServiceCallbackAdapter<ClusterOperationResponseMessage, ServiceResponse<ClusterOperationResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public AddHostToVCenterResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<ClusterOperationResponseMessage> transform(final ClusterOperationResponseMessage responseMessage)
    {
        return new ServiceResponse<>(responseMessage.getMessageProperties().getCorrelationId(), responseMessage, null);
    }

    @Override
    public void consume(final IServiceCallback callback, final ServiceResponse<ClusterOperationResponseMessage> responseMessage)
    {
        callback.handleServiceResponse(responseMessage);
    }

    @Override
    public IServiceCallback take(final ClusterOperationResponseMessage clusterOperationResponseMessage)
    {
        return serviceCallbackRegistry.removeServiceCallback(clusterOperationResponseMessage.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<ClusterOperationResponseMessage> getSourceClass()
    {
        return ClusterOperationResponseMessage.class;
    }
}

