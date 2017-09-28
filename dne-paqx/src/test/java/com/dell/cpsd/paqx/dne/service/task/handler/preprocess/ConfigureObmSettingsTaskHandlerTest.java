/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.task.handler.preprocess;

import com.dell.cpsd.SetObmSettingsRequestMessage;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * The tests for ConfigureObmSettingsTaskHandler.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

@RunWith(MockitoJUnitRunner.class)
public class ConfigureObmSettingsTaskHandlerTest {

    /*
     * The <code>NodeService</code> instance.
     * @since 1.0
     */
    @Mock
    private NodeService nodeService = null;

    /*
     * The job running the add node to system definition task handler.
     */
    private Job job         = null;
    /**
     * The test setup.
     *
     * @since 1.0
     */
    @Before
    public void setUp(){
        PreProcessTaskConfig preProcessTaskConfig = new PreProcessTaskConfig();
        WorkflowService workflowService = new WorkflowServiceImpl(new InMemoryJobRepository(), preProcessTaskConfig.preProcessWorkflowSteps());

        PreProcessService preProcessService = new PreProcessService();
        preProcessService.setWorkflowService(workflowService);

        this.job = preProcessService.createWorkflow("preProcessWorkflow", "startPreProcessWorkflow", "submitted");
        NodeExpansionRequest nodeExpansionRequest = new NodeExpansionRequest("idracIpAddress", "idracGatewayIpAddress", "idracSubnetMask",
                "managementIpAddress", "esxiKernelIpAddress1", "esxiKernelIpAddress2", "esxiManagementHostname", "scaleIoData1SvmIpAddress",
                "scaleIoData1KernelIpAddress", "scaleIoData1KernelAndSvmSubnetMask", "scaleIOSVMDataIpAddress2", "scaleIoData2KernelIpAddress",
                "scaleIoData2KernelAndSvmSubnetMask", "scaleIOSVMManagementIpAddress", "scaleIoSvmManagementSubnetMask","nodeId", "symphonyUuid", "clausterName");
        this.job.setInputParams(nodeExpansionRequest);

        TaskResponse response = new TaskResponse();
        NodeInfo nodeInfo = new NodeInfo("symphonyUuid", NodeStatus.DISCOVERED);

        Map<String, String> results = new HashMap<>();

        results.put("symphonyUUID", nodeInfo.getSymphonyUuid());
        results.put("nodeStatus", nodeInfo.getNodeStatus().toString());
        response.setResults(results);

        this.job.addTaskResponse("findAvailableNodes", response);
        this.job.changeToNextStep("configureObmSettings");
    }
    /**
     * Test successful execution of ConfigureObmSettingsTaskHandlerTest.executeTask() method
     *
     * @throws ServiceExecutionException
     * @throws ServiceTimeoutException
     *
     * @since 1.0
     */
    @Test
    public void testExecuteTask_successful_case() throws ServiceTimeoutException, ServiceExecutionException
    {
        ObmSettingsResponse obmSettingsResponse = new ObmSettingsResponse("SUCCESS",null);
        ArgumentCaptor<SetObmSettingsRequestMessage> requestCaptor = ArgumentCaptor.forClass(SetObmSettingsRequestMessage.class);
        when(this.nodeService.obmSettingsResponse(requestCaptor.capture())).thenReturn(obmSettingsResponse);

        ConfigureObmSettingsTaskHandler instance = new ConfigureObmSettingsTaskHandler(this.nodeService);
        boolean expectedResult = true;
        boolean actualResult = instance.executeTask(this.job);

        assertEquals(expectedResult, actualResult);
        verify(this.nodeService, times(1)).obmSettingsResponse(requestCaptor.capture());
    }

    /**
     * Test unsuccessful execution of ConfigureObmSettingsTaskHandlerTest.executeTask() method
     *
     * @throws ServiceExecutionException
     * @throws ServiceTimeoutException
     *
     * @since 1.0
     */
    @Test
    public void testExecuteTask_unsuccessful_case() throws ServiceTimeoutException, ServiceExecutionException
    {
        ObmSettingsResponse obmSettingsResponse = new ObmSettingsResponse("FAILED",null);
        ArgumentCaptor<SetObmSettingsRequestMessage> requestCaptor = ArgumentCaptor.forClass(SetObmSettingsRequestMessage.class);
        when(this.nodeService.obmSettingsResponse(requestCaptor.capture())).thenReturn(obmSettingsResponse);

        ConfigureObmSettingsTaskHandler instance = new ConfigureObmSettingsTaskHandler(this.nodeService);
        boolean expectedResult = false;
        boolean actualResult = instance.executeTask(this.job);

        assertEquals(expectedResult, actualResult);
        verify(this.nodeService, times(1)).obmSettingsResponse(requestCaptor.capture());
    }
}
