/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.EsxiInstallationInfo;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.InstallEsxiTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.transformers.HostToInstallEsxiRequestTransformer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
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
    private DataServiceRepository repository;

    @Mock
    private EsxiInstallationInfo esxiInstallInfo;

    private InstallEsxiTaskHandler handler;
    private InstallEsxiTaskHandler spy;

    private String taskName                       = "installEsxiTask";
    private String stepName                       = "installEsxiStep";
    private String symphonyUuid                   = "symphonyUuid";
    private String esxiManagementIpAddress        = "1.2.3.4";
    private String esxiManagementHostname         = "vCenter-1-2-1-2";
    private String esxiManagementGatewayIpAddress = "1.3.5.7";
    private String esxiManagementSubnetMask       = "255.255.255.0";
    private String idracIpAddress                 = "1.2.3.4";
    private String esxiHostDomainName             = "example.com";

    /**
     * The test setup.
     *
     * @since 1.0
     */
    @Before
    public void setUp()
    {
        this.handler = new InstallEsxiTaskHandler(this.service, this.transformer, this.repository);
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
        doReturn(this.symphonyUuid).when(this.request).getSymphonyUuid();
        doReturn(this.esxiManagementIpAddress).when(this.request).getEsxiManagementIpAddress();
        doReturn(this.esxiManagementHostname).when(this.request).getEsxiManagementHostname();
        doReturn(this.esxiManagementGatewayIpAddress).when(this.request).getEsxiManagementGatewayIpAddress();
        doReturn(this.esxiManagementSubnetMask).when(this.request).getEsxiManagementSubnetMask();
        doReturn(this.esxiInstallInfo).when(this.transformer).transformInstallEsxiData(anyString(), anyString(), any());
        doReturn(this.idracIpAddress).when(this.request).getIdracIpAddress();
        doReturn(true).when(this.service).requestInstallEsxi(any(), anyString());
        doReturn(this.esxiHostDomainName).when(this.repository).getDomainName();

        boolean result = this.spy.executeTask(this.job);

        assertThat(result, is(true));
        verify(this.transformer).transformInstallEsxiData(anyString(), anyString(), any());
        verify(this.service).requestInstallEsxi(any(), anyString());
        verify(this.response).setWorkFlowTaskStatus(Status.SUCCEEDED);
        verify(this.response, never()).addError(anyString());
        ArgumentCaptor<String> setHostnameParameterCaptor = ArgumentCaptor.forClass(String.class);
        verify(this.response, times(2)).setHostname(setHostnameParameterCaptor.capture());
        assertThat(setHostnameParameterCaptor.getAllValues().get(0), endsWith("-1-2-1-2"));
        assertThat(setHostnameParameterCaptor.getAllValues().get(1), endsWith("-1-2-1-2.example.com"));
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

        boolean result = this.spy.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.transformer, never()).transformInstallEsxiData(anyString(), anyString(), any());
        verify(this.service, never()).requestInstallEsxi(any(), anyString());
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.InstallEsxiTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     */
    @Test
    public void testExecuteTask_symphonyUuid_is_null()
    {
        String nullISymphonyUuid = null;

        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(nullISymphonyUuid).when(this.request).getSymphonyUuid();

        boolean result = this.spy.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.transformer, never()).transformInstallEsxiData(anyString(), anyString(), any());
        verify(this.service, never()).requestInstallEsxi(any(), anyString());
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
        doReturn(this.symphonyUuid).when(this.request).getSymphonyUuid();
        doReturn(nullEsxiManagementIpAddress).when(this.request).getEsxiManagementIpAddress();

        boolean result = this.spy.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response, never()).setHostname(anyString());
        verify(this.transformer, never()).transformInstallEsxiData(anyString(), anyString(), any());
        verify(this.service, never()).requestInstallEsxi(any(), anyString());
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    @Test
    public void testExecuteTask_esxi_management_gateway_ip_address_is_null()
    {
        String nullEsxiManagementGatewayIpAddress = null;

        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.symphonyUuid).when(this.request).getSymphonyUuid();
        doReturn(this.esxiManagementIpAddress).when(this.request).getEsxiManagementIpAddress();
        doReturn(nullEsxiManagementGatewayIpAddress).when(this.request).getEsxiManagementGatewayIpAddress();

        boolean result = this.spy.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response, never()).setHostname(anyString());
        verify(this.transformer, never()).transformInstallEsxiData(anyString(), anyString(), any());
        verify(this.service, never()).requestInstallEsxi(any(), anyString());
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    @Test
    public void testExecuteTask_esxi_management_subnet_mask_is_null()
    {
        String nullEsxiManagementSubnetMask = null;

        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.symphonyUuid).when(this.request).getSymphonyUuid();
        doReturn(this.esxiManagementIpAddress).when(this.request).getEsxiManagementIpAddress();
        doReturn(this.esxiManagementGatewayIpAddress).when(this.request).getEsxiManagementGatewayIpAddress();
        doReturn(nullEsxiManagementSubnetMask).when(this.request).getEsxiManagementSubnetMask();

        boolean result = this.spy.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response, never()).setHostname(anyString());
        verify(this.transformer, never()).transformInstallEsxiData(anyString(), anyString(), any());
        verify(this.service, never()).requestInstallEsxi(any(), anyString());
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
        doReturn(this.symphonyUuid).when(this.request).getSymphonyUuid();
        doReturn(this.esxiManagementIpAddress).when(this.request).getEsxiManagementIpAddress();
        doReturn(this.esxiManagementGatewayIpAddress).when(this.request).getEsxiManagementGatewayIpAddress();
        doReturn(this.esxiManagementSubnetMask).when(this.request).getEsxiManagementSubnetMask();
        doReturn(nullEsxiManagementHostname).when(this.request).getEsxiManagementHostname();
        doReturn(this.esxiInstallInfo).when(this.transformer).transformInstallEsxiData(anyString(), anyString(), any());
        doReturn(this.idracIpAddress).when(this.request).getIdracIpAddress();
        doReturn(true).when(this.service).requestInstallEsxi(any(), anyString());
        doReturn(this.esxiHostDomainName).when(this.repository).getDomainName();

        boolean result = this.spy.executeTask(this.job);

        assertThat(result, is(true));
        ArgumentCaptor<String> setHostnameParameterCaptor = ArgumentCaptor.forClass(String.class);
        verify(this.response, times(2)).setHostname(setHostnameParameterCaptor.capture());
        assertThat(setHostnameParameterCaptor.getAllValues().get(0), endsWith("-1-2-3-4"));
        assertThat(setHostnameParameterCaptor.getAllValues().get(1), endsWith("-1-2-3-4.example.com"));
        verify(this.transformer).transformInstallEsxiData(anyString(), anyString(), any());
        verify(this.service).requestInstallEsxi(any(), anyString());
        verify(this.response).setWorkFlowTaskStatus(Status.SUCCEEDED);
        verify(this.response, never()).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.addnode.InstallEsxiTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     */
    @Test
    public void testExecuteTask_esxi_management_fqdn_is_null()
    {
        String nullEsxiHostDomainName = null;

        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(this.symphonyUuid).when(this.request).getSymphonyUuid();
        doReturn(this.esxiManagementIpAddress).when(this.request).getEsxiManagementIpAddress();
        doReturn(this.esxiManagementGatewayIpAddress).when(this.request).getEsxiManagementGatewayIpAddress();
        doReturn(this.esxiManagementSubnetMask).when(this.request).getEsxiManagementSubnetMask();
        doReturn(this.esxiManagementHostname).when(this.request).getEsxiManagementHostname();
        doReturn(this.esxiInstallInfo).when(this.transformer).transformInstallEsxiData(anyString(), anyString(), any());
        doReturn(this.idracIpAddress).when(this.request).getIdracIpAddress();
        doReturn(true).when(this.service).requestInstallEsxi(any(), anyString());
        doReturn(nullEsxiHostDomainName).when(this.repository).getDomainName();

        boolean result = this.spy.executeTask(this.job);

        assertThat(result, is(false));
        verify(this.response).setHostname(anyString());
        verify(this.transformer).transformInstallEsxiData(anyString(), anyString(), any());
        verify(this.service).requestInstallEsxi(any(), anyString());
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
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