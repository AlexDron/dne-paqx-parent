/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.virtualization.capabilities.api.AddEsxiHostVSphereLicenseResponse;
import com.dell.cpsd.virtualization.capabilities.api.MessageProperties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for ApplyEsxiLicenseResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class ApplyEsxiLicenseResponseAdapterTest extends BaseResponseAdapterTest<AddEsxiHostVSphereLicenseResponse>
{
    @Override
    protected ServiceCallbackAdapter<AddEsxiHostVSphereLicenseResponse, ServiceResponse<AddEsxiHostVSphereLicenseResponse>> createTestable()
    {
        return new ApplyEsxiLicenseResponseAdapter(this.registry);
    }

    @Override
    protected AddEsxiHostVSphereLicenseResponse createResponseMessageSpy()
    {
        AddEsxiHostVSphereLicenseResponse theSpy = spy(AddEsxiHostVSphereLicenseResponse.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}