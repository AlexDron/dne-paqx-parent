/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.preprocess;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.DiscoverScaleIoTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.Status;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Discover ScaleIO Task Handler Test
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class DiscoverScaleIoTaskHandlerTest
{
    @Mock
    private WorkflowTask task;

    @Mock
    private Job job;

    @Mock
    private DiscoverScaleIoTaskResponse response;

    @Mock
    private NodeService service;

    @Mock
    private DataServiceRepository repository;

    @Mock
    private ComponentEndpointIds componentEndpointIds;

    private DiscoverScaleIoTaskHandler handler;

    private DiscoverScaleIoTaskHandler spy;
    
    private final String taskName = "discoverScaleIo";

    private final String stepName = "discoverScaleIoStep";

    @Before
    public void setup() throws Exception
    {
        this.handler = new DiscoverScaleIoTaskHandler(this.service, this.repository);
        this.spy = spy(this.handler);
    }

    @Test
    public void executeTaskSuccessful() throws Exception
    {
        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        when(this.job.getId()).thenReturn(UUID.randomUUID());
        doReturn(this.componentEndpointIds).when(this.repository).getComponentEndpointIds(anyString());
        doReturn(true).when(this.service).requestDiscoverScaleIo(any(), anyString());

        assertEquals(true, this.spy.executeTask(this.job));
        verify(this.response).setWorkFlowTaskStatus(Status.SUCCEEDED);
        verify(this.response, never()).addError(anyString());
    }

    @Test
    public void executeTaskWitEmptyScaleIoComponents() throws Exception
    {
        final ComponentEndpointIds emptyComponentEndpointIds = null;

        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        doReturn(emptyComponentEndpointIds).when(this.repository).getComponentEndpointIds(anyString());

        assertEquals(false, this.spy.executeTask(this.job));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    @Test
    public void executeTaskFailure() throws Exception
    {
        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        when(this.job.getId()).thenReturn(UUID.randomUUID());
        doReturn(this.componentEndpointIds).when(this.repository).getComponentEndpointIds(anyString());
        doReturn(false).when(this.service).requestDiscoverScaleIo(any(), anyString());

        assertEquals(false, this.spy.executeTask(this.job));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    @Test
    public void initializeResponse() throws Exception
    {
        doReturn(this.task).when(this.job).getCurrentTask();
        doReturn(this.taskName).when(this.task).getTaskName();
        doReturn(this.stepName).when(this.job).getStep();

        final DiscoverScaleIoTaskResponse response = this.handler.initializeResponse(this.job);
        assertNotNull(response);
        assertEquals(this.taskName, response.getWorkFlowTaskName());
        assertEquals(Status.IN_PROGRESS, response.getWorkFlowTaskStatus());
    }
}
