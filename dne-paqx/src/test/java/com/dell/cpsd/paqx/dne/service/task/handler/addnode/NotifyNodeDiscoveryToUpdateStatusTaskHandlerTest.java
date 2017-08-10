/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * The tests for the NotifyNodeDiscoveryToUpdateStatusTaskHandler.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class NotifyNodeDiscoveryToUpdateStatusTaskHandlerTest
{
    @Mock
    private WorkflowTask task;

    @Mock
    private NodeService  service;

    @Mock
    private Job job;

    @Mock
    private TaskResponse response;

    @Mock
    private NodeExpansionRequest request;

    private NotifyNodeDiscoveryToUpdateStatusTaskHandler handler;
    private NotifyNodeDiscoveryToUpdateStatusTaskHandler spy;

    private String taskName = "notifyNodeDiscoveryToUpdateStatusTask";
    private String stepName = "notifyNodeDiscoveryToUpdateStatusStep";
    private String symphonyUuid = "symphonyUuid";

    /**
     * The test setup.
     * 
     * @since 1.0
     */
    @Before
    public void setUp()
    {
        this.handler = new NotifyNodeDiscoveryToUpdateStatusTaskHandler(this.service);
        this.spy = spy(this.handler);
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.NotifyNodeDiscoveryToUpdateStatusTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     * 
     * @throws ServiceExecutionException
     * @throws ServiceTimeoutException
     * 
     * @since 1.0
     */
    @Test
    public void testExecuteTask_successful_case() throws ServiceTimeoutException, ServiceExecutionException
    {
        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.symphonyUuid).when(this.request).getSymphonyUuid();
        doReturn(true).when(this.service).notifyNodeAllocationComplete(anyString());

        assertEquals(true, this.spy.executeTask(this.job));
        verify(this.service).notifyNodeAllocationComplete(any());
        verify(this.response).setWorkFlowTaskStatus(Status.SUCCEEDED);
        verify(this.response, never()).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.NotifyNodeDiscoveryToUpdateStatusTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     * 
     * @throws ServiceExecutionException
     * @throws ServiceTimeoutException
     * 
     * @since 1.0
     */
    @Test
    public void testExecuteTask_no_input_params() throws ServiceTimeoutException, ServiceExecutionException
    {
        NodeExpansionRequest nullRequest = null;

        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        doReturn(nullRequest).when(this.job).getInputParams();

        assertEquals(false, this.spy.executeTask(this.job));
        verify(this.service, never()).notifyNodeAllocationComplete(any());
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.NotifyNodeDiscoveryToUpdateStatusTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws ServiceExecutionException
     * @throws ServiceTimeoutException
     *
     * @since 1.0
     */
    @Test
    public void testExecuteTask_no_symphonyuuid() throws ServiceTimeoutException, ServiceExecutionException
    {
        String emptySymphonyUuid = null;

        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(emptySymphonyUuid).when(this.request).getSymphonyUuid();

        assertEquals(false, this.spy.executeTask(this.job));
        verify(this.service, never()).notifyNodeAllocationComplete(any());
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.NotifyNodeDiscoveryToUpdateStatusTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     * 
     * @throws ServiceExecutionException
     * @throws ServiceTimeoutException
     * 
     * @since 1.0
     */
    @Test
    public void testExecuteTask_node_discovery_service_error() throws ServiceTimeoutException, ServiceExecutionException
    {
        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.symphonyUuid).when(this.request).getSymphonyUuid();
        doReturn(false).when(this.service).notifyNodeAllocationComplete(anyString());

        assertEquals(false, this.spy.executeTask(this.job));
        verify(this.service).notifyNodeAllocationComplete(any());
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.NotifyNodeDiscoveryToUpdateStatusTaskHandler#initializeResponse(com.dell.cpsd.paqx.dne.domain.Job)}.
     */
    @Test
    public void testInitializeResponse()
    {
        doReturn(this.task).when(this.job).getCurrentTask();
        doReturn(this.taskName).when(this.task).getTaskName();
        doReturn(this.stepName).when(this.job).getStep();

        TaskResponse response = this.handler.initializeResponse(this.job);
        assertNotNull(response);
        assertEquals(this.taskName, response.getWorkFlowTaskName());
        assertEquals(Status.IN_PROGRESS, response.getWorkFlowTaskStatus());
    }
}
