/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.virtualization.capabilities.api.DatastoreRenameResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.DeployVMFromTemplateResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.MessageProperties;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * The tests for DatastoreRenameResponseAdapter class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class DatastoreRenameResponseAdapterTest extends BaseResponseAdapterTest<DatastoreRenameResponseMessage>
{
    @Override
    protected ServiceCallbackAdapter<DatastoreRenameResponseMessage, ServiceResponse<DatastoreRenameResponseMessage>> createTestable()
    {
        return new DatastoreRenameResponseAdapter(this.registry);
    }

    @Override
    protected DatastoreRenameResponseMessage createResponseMessageSpy()
    {
        DatastoreRenameResponseMessage theSpy = spy(DatastoreRenameResponseMessage.class);
        theSpy.setMessageProperties(mock(MessageProperties.class));
        return theSpy;
    }
}