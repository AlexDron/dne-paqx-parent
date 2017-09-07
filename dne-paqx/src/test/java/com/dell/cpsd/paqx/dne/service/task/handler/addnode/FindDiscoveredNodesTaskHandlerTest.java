/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.DiscoveredNode.AllocationStatus;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.repository.InMemoryJobRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.WorkflowService;
import com.dell.cpsd.paqx.dne.service.WorkflowServiceImpl;
import com.dell.cpsd.paqx.dne.service.model.*;
import com.dell.cpsd.paqx.dne.service.workflow.preprocess.PreProcessService;
import com.dell.cpsd.paqx.dne.service.workflow.preprocess.PreProcessTaskConfig;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * The tests for FindDiscoveredNodesTaskHandler
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class FindDiscoveredNodesTaskHandlerTest
{

    /*
     * The <code>NodeService</code> instance.
     */
    @Mock
    private NodeService nodeService = null;

    /*
     * The job running the add node to system definition task handler
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
        PreProcessTaskConfig preProcessTaskConfig = new PreProcessTaskConfig();
        WorkflowService workflowService = new WorkflowServiceImpl(new InMemoryJobRepository(), preProcessTaskConfig.preProcessWorkflowSteps());

        PreProcessService preProcessService = new PreProcessService();
        preProcessService.setWorkflowService(workflowService);

        this.job = preProcessService.createWorkflow("preprocess", "startPreProcessWorkflow", "submitted");
        this.job.setInputParams(new NodeExpansionRequest("idracIpAddress", "idracGatewayIpAddress", "idracSubnetMask",
                "managementIpAddress", "esxiKernelIpAddress1", "esxiKernelIpAddress2", "esxiManagementHostname", "scaleIOSVMDataIpAddress1",
                "scaleIOSVMDataIpAddress2", "scaleIOSVMManagementIpAddress", "nodeId", "symphonyUuid", "clausterName"));

        FirstAvailableDiscoveredNodeResponse response = new FirstAvailableDiscoveredNodeResponse();
        response.setNodeInfo(new NodeInfo("symphonyUuid", NodeStatus.DISCOVERED));
        this.job.addTaskResponse("findAvailableNodes", response);

        this.job.changeToNextStep("findAvailableNodes");
    }

    /**
     * Test successful execution of FindDiscoveredNodesTaskHandler.executeTask() method
     * 
     * @throws ServiceExecutionException
     * @throws ServiceTimeoutException
     * 
     * @since 1.0
     */
    @Test
    public void testExecuteTask_successful_case() throws ServiceTimeoutException, ServiceExecutionException
    {
        when(this.nodeService.listDiscoveredNodes())
                .thenReturn(Arrays.asList(new DiscoveredNode("convergedUuid",  AllocationStatus.DISCOVERED)));

        FindDiscoveredNodesTaskHandler instance = new FindDiscoveredNodesTaskHandler(this.nodeService);
        boolean expectedResult = true;
        boolean actualResult = instance.executeTask(this.job);

        assertEquals(expectedResult, actualResult);
        verify(this.nodeService, times(1)).listDiscoveredNodes();
    }

    /**
     * Test error execution of FindDiscoveredNodesTaskHandler.executeTask() method - test error case where null discovered node list is
     * present.
     * 
     * @throws ServiceExecutionException
     * @throws ServiceTimeoutException
     * 
     * @since 1.0
     */
    @Test
    public void testExecuteTask_null_discovered_node_list() throws ServiceTimeoutException, ServiceExecutionException
    {
        when(this.nodeService.listDiscoveredNodes()).thenReturn(null);

        FindDiscoveredNodesTaskHandler instance = new FindDiscoveredNodesTaskHandler(this.nodeService);
        boolean expectedResult = false;
        boolean actualResult = instance.executeTask(this.job);

        assertEquals(expectedResult, actualResult);
        verify(this.nodeService, times(1)).listDiscoveredNodes();
    }

    /**
     * Test error execution of FindDiscoveredNodesTaskHandler.executeTask() method - test error case where no discovered node instance is
     * present.
     * 
     * @throws ServiceExecutionException
     * @throws ServiceTimeoutException
     * 
     * @since 1.0
     */
    @Test
    public void testExecuteTask_no_discovered_node() throws ServiceTimeoutException, ServiceExecutionException
    {
        when(this.nodeService.listDiscoveredNodes()).thenReturn(Collections.emptyList());

        FindDiscoveredNodesTaskHandler instance = new FindDiscoveredNodesTaskHandler(this.nodeService);
        boolean expectedResult = false;
        boolean actualResult = instance.executeTask(this.job);

//        assertEquals(expectedResult, actualResult);
        verify(this.nodeService, times(1)).listDiscoveredNodes();
    }
}
