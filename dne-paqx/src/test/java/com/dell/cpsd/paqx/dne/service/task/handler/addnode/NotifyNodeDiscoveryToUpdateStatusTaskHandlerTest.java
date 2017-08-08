/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import com.dell.cpsd.paqx.dne.service.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.repository.InMemoryJobRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.WorkflowService;
import com.dell.cpsd.paqx.dne.service.WorkflowServiceImpl;
import com.dell.cpsd.paqx.dne.service.workflow.addnode.AddNodeService;
import com.dell.cpsd.paqx.dne.service.workflow.addnode.AddNodeTaskConfig;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;

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

    /*
     * The <code>NodeService</code> instance.
     */
    @Mock
    private NodeService nodeService = null;

    /*
     * The job running the notify node discovery service node allocation complete task handler.
     */
    private Job         job         = null;

    /**
     * The test setup.
     * 
     * @since 1.0
     */
    @Before
    public void setUp()
    {
        AddNodeTaskConfig addNodeConfig = new AddNodeTaskConfig();
        WorkflowService workflowService = new WorkflowServiceImpl(new InMemoryJobRepository(), addNodeConfig.addNodeWorkflowSteps());

        AddNodeService addNodeService = new AddNodeService();
        addNodeService.setWorkflowService(workflowService);

        this.job = addNodeService.createWorkflow("addNode", "startAddNodeWorkflow", "submitted");

//        FirstAvailableDiscoveredNodeResponse response = new FirstAvailableDiscoveredNodeResponse();
//        response.setNodeInfo(new NodeInfo("symphonyUuid", "nodeId", NodeStatus.DISCOVERED));
//        this.job.addTaskResponse("findAvailableNodes", response);

        NodeExpansionRequest request = new NodeExpansionRequest();
        request.setNodeId("node-1234");
        request.setSymphonyUuid("symphony-1234");
        this.job.setInputParams(request);

        this.job.changeToNextStep("notifyNodeDiscoveryToUpdateStatus");
    }

    /**
     * Test successful execution of NotifyNodeDiscoveryToUpdateStatusTaskHandler.executeTask() method
     * 
     * @throws ServiceExecutionException
     * @throws ServiceTimeoutException
     * 
     * @since 1.0
     */
    @Test
    public void testExecuteTask_successful_case() throws ServiceTimeoutException, ServiceExecutionException
    {
        ArgumentCaptor<String> elementIdentifierCaptor = ArgumentCaptor.forClass(String.class);
        when(this.nodeService.notifyNodeAllocationComplete(elementIdentifierCaptor.capture())).thenReturn(true);

        NotifyNodeDiscoveryToUpdateStatusTaskHandler instance = new NotifyNodeDiscoveryToUpdateStatusTaskHandler(this.nodeService);
        boolean expectedResult = true;
        boolean actualResult = instance.executeTask(this.job);

        assertEquals(expectedResult, actualResult);
        verify(this.nodeService, times(1)).notifyNodeAllocationComplete(elementIdentifierCaptor.capture());
    }

    /**
     * Test error execution of NotifyNodeDiscoveryToUpdateStatusTaskHandler.executeTask() method - test error case where no 
     * findAvailableNodes task response was set.
     * 
     * @throws ServiceExecutionException
     * @throws ServiceTimeoutException
     * 
     * @since 1.0
     */
    @Test
    public void testExecuteTask_no_find_nodes_response() throws ServiceTimeoutException, ServiceExecutionException
    {
        this.job.getInputParams().setSymphonyUuid(null);

        NotifyNodeDiscoveryToUpdateStatusTaskHandler instance = new NotifyNodeDiscoveryToUpdateStatusTaskHandler(this.nodeService);
        boolean expectedResult = false;
        boolean actualResult = instance.executeTask(this.job);

        assertEquals(expectedResult, actualResult);
        verify(this.nodeService, times(0)).notifyNodeAllocationComplete(any());
    }

    /**
     * Test error execution of NotifyNodeDiscoveryToUpdateStatusTaskHandler.executeTask() method - test error case where no discovered node
     * instance is present.
     * 
     * @throws ServiceExecutionException
     * @throws ServiceTimeoutException
     * 
     * @since 1.0
     */
    @Test
    public void testExecuteTask_no_input_params() throws ServiceTimeoutException, ServiceExecutionException
    {
        this.job.setInputParams(null);

        NotifyNodeDiscoveryToUpdateStatusTaskHandler instance = new NotifyNodeDiscoveryToUpdateStatusTaskHandler(this.nodeService);
        boolean expectedResult = false;
        boolean actualResult = instance.executeTask(this.job);

        assertEquals(expectedResult, actualResult);
        verify(this.nodeService, times(0)).notifyNodeAllocationComplete(any());
    }

    /**
     * Test error execution of NotifyNodeDiscoveryToUpdateStatusTaskHandler.executeTask() method - test error case where the node 
     * discovery service encounters a problem during the node allocation completion request.
     * 
     * @throws ServiceExecutionException
     * @throws ServiceTimeoutException
     * 
     * @since 1.0
     */
    @Test
    public void testExecuteTask_node_discovery_service_error() throws ServiceTimeoutException, ServiceExecutionException
    {
        when(this.nodeService.notifyNodeAllocationComplete(any())).thenReturn(false);

        NotifyNodeDiscoveryToUpdateStatusTaskHandler instance = new NotifyNodeDiscoveryToUpdateStatusTaskHandler(this.nodeService);
        boolean expectedResult = false;
        boolean actualResult = instance.executeTask(this.job);

        assertEquals(expectedResult, actualResult);
        verify(this.nodeService, times(1)).notifyNodeAllocationComplete(any());
    }
}
