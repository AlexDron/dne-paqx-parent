/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * The tests for the AddNodeToSystemDefinitionTaskHandler class.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class DatastoreRenameTaskHandlerTest
{
    @Mock
    private Job job;

    @Mock
    private TaskResponse response;

    @Mock
    private NodeService service;

    @Mock
    private DataServiceRepository repository;

    @Mock
    private NodeExpansionRequest request;

    @Mock
    private ComponentEndpointIds componentEndpointIds;

    private DatastoreRenameTaskHandler handler;
    private final String esxiManagementHostname = "fpr1-h17";

    @Before
    public void setUp() throws Exception
    {
        this.handler = spy(new DatastoreRenameTaskHandler(this.service, repository));
    }

    @Test
    public void executeTask_should_successfully_request_a_datastore_rename() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(any());
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.componentEndpointIds).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());
        doReturn(this.esxiManagementHostname).when(this.request).getEsxiManagementHostname();
        doReturn(true).when(this.service).requestDatastoreRename(any());

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(true));
        verify(this.response).setWorkFlowTaskStatus(Status.SUCCEEDED);
        verify(this.response, never()).addError(anyString());
    }

    @Test
    public void executeTask_should_fail_the_workflow_when_the_datastore_rename_request_fails() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(any());
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.componentEndpointIds).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());
        doReturn(this.esxiManagementHostname).when(this.request).getEsxiManagementHostname();
        doReturn(false).when(this.service).requestDatastoreRename(any());

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    @Test
    public void executeTask_should_fail_the_workflow_when_the_job_input_params_is_null() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(any());
        doReturn(null).when(this.job).getInputParams();

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    @Test
    public void executeTask_should_fail_the_workflow_when_the_component_endpoint_ids_are_null() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(any());
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(null).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    @Test
    public void executeTask_should_fail_the_workflow_when_the_esxi_management_hostname_is_null() throws Exception
    {
        doReturn(this.response).when(this.handler).initializeResponse(any());
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.componentEndpointIds).when(this.repository).getVCenterComponentEndpointIdsByEndpointType(anyString());
        doReturn(null).when(this.request).getEsxiManagementHostname();

        boolean result = this.handler.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }
}
