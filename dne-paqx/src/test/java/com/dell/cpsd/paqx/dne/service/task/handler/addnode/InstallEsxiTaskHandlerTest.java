/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.EsxiInstallationInfo;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.InstallEsxiTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.transformers.HostToInstallEsxiRequestTransformer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * The tests for the InstallEsxiTaskHandler class.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class InstallEsxiTaskHandlerTest
{
    @Mock
    private WorkflowTask task;

    @Mock
    private Job job;

    @Mock
    private InstallEsxiTaskResponse response;

    @Mock
    private NodeExpansionRequest request;

    @Mock
    private NodeService service;

    @Mock
    private HostToInstallEsxiRequestTransformer transformer;

    @Mock
    private EsxiInstallationInfo esxiInstallInfo;

    private InstallEsxiTaskHandler handler;
    private InstallEsxiTaskHandler spy;

    private String taskName = "installEsxiTask";
    private String stepName = "installEsxiStep";
    private String nodeId   = "nodeId";
    private String esxiManagementIpAddress = "1.2.3.4";
    private String esxiManagementHostname  = "vCenter_1_2_3_4";

    /**
     * The test setup.
     *
     * @since 1.0
     */
    @Before
    public void setUp()
    {
        this.handler = new InstallEsxiTaskHandler(this.service, this.transformer);
        this.spy = spy(this.handler);
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.InstallEsxiTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     */
    @Test
    public void testExecuteTask()
    {
        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.nodeId).when(this.request).getComponentUuid();
        doReturn(this.esxiManagementIpAddress).when(this.request).getEsxiManagementIpAddress();
        doReturn(this.esxiManagementHostname).when(this.request).getEsxiManagementHostname();
        doReturn(this.esxiInstallInfo).when(this.transformer).transformInstallEsxiData(anyString(), anyString());
        doReturn(true).when(this.service).requestInstallEsxi(any());

        assertEquals(true, this.spy.executeTask(this.job));
        verify(this.response).setHostname(argThat(value -> value.contains("_1_2_3_4")));
        verify(this.transformer).transformInstallEsxiData(anyString(), anyString());
        verify(this.service).requestInstallEsxi(any());
        verify(this.response).setWorkFlowTaskStatus(Status.SUCCEEDED);
        verify(this.response, never()).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.InstallEsxiTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     */
    @Test
    public void testExecuteTask_node_expansion_request_is_null()
    {
        NodeExpansionRequest nullRequest = null;

        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        doReturn(nullRequest).when(this.job).getInputParams();

        assertEquals(false, this.spy.executeTask(this.job));
        verify(this.transformer, never()).transformInstallEsxiData(anyString(), anyString());
        verify(this.service, never()).requestInstallEsxi(any());
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.InstallEsxiTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     */
    @Test
    public void testExecuteTask_nodeId_is_null()
    {
        String nullINodeId = null;

        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(nullINodeId).when(this.request).getComponentUuid();

        assertEquals(false, this.spy.executeTask(this.job));
        verify(this.transformer, never()).transformInstallEsxiData(anyString(), anyString());
        verify(this.service, never()).requestInstallEsxi(any());
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.InstallEsxiTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     */
    @Test
    public void testExecuteTask_esxi_management_ip_address_is_null()
    {
        String nullEsxiManagementIpAddress = null;

        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.nodeId).when(this.request).getComponentUuid();
        doReturn(nullEsxiManagementIpAddress).when(this.request).getEsxiManagementIpAddress();

        assertEquals(false, this.spy.executeTask(this.job));
        verify(this.response, never()).setHostname(anyString());
        verify(this.transformer, never()).transformInstallEsxiData(anyString(), anyString());
        verify(this.service, never()).requestInstallEsxi(any());
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.InstallEsxiTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     */
    @Test
    public void testExecuteTask_esxi_management_hostname_is_null_it_should_be_auto_generated()
    {
        String nullEsxiManagementHostname = null;

        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.nodeId).when(this.request).getComponentUuid();
        doReturn(this.esxiManagementIpAddress).when(this.request).getEsxiManagementIpAddress();
        doReturn(nullEsxiManagementHostname).when(this.request).getEsxiManagementHostname();
        doReturn(this.esxiInstallInfo).when(this.transformer).transformInstallEsxiData(anyString(), anyString());
        doReturn(true).when(this.service).requestInstallEsxi(any());

        assertEquals(true, this.spy.executeTask(this.job));
        verify(this.response).setHostname(argThat(value -> value.contains("_1_2_3_4")));
        verify(this.transformer).transformInstallEsxiData(anyString(), anyString());
        verify(this.service).requestInstallEsxi(any());
        verify(this.response).setWorkFlowTaskStatus(Status.SUCCEEDED);
        verify(this.response, never()).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.InstallEsxiTaskHandler#initializeResponse(com.dell.cpsd.paqx.dne.domain.Job)}.
     */
    @Test
    public void testInitializeResponse()
    {
        doReturn(this.task).when(this.job).getCurrentTask();
        doReturn(this.taskName).when(this.task).getTaskName();
        doReturn(this.stepName).when(this.job).getStep();

        InstallEsxiTaskResponse response = this.handler.initializeResponse(this.job);
        assertNotNull(response);
        assertEquals(this.taskName, response.getWorkFlowTaskName());
        assertEquals(Status.IN_PROGRESS, response.getWorkFlowTaskStatus());
    }
}