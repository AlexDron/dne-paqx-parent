
/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegate;

import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.*;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.delegates.model.WorkflowResult;
import com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants;
import com.dell.cpsd.paqx.dne.service.model.BootDeviceIdracStatus;
import com.dell.cpsd.paqx.dne.service.model.DiscoveredNode;
import com.dell.cpsd.paqx.dne.service.model.IdracInfo;
import com.dell.cpsd.paqx.dne.service.model.IdracNetworkSettingsRequest;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.*;

import java.util.List;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.WORKFLOW_RESULT;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class ConfigureBootDeviceTest {

    private ConfigureBootDevice configureBootDevice;
    private NodeService nodeService;
    private DelegateExecution delegateExecution;
    private List<DiscoveredNode> discoveredNodesResponse;
    private DiscoveredNode discoveredNode;
    private NodeDetail nodeDetail;
    private IdracNetworkSettingsRequest idracNetworkSettingsRequest;
    private BootDeviceIdracStatus bootDeviceIdracStatus;
    private WorkflowResult workflowResult;
    private BaseWorkflowDelegate baseWorkflowDelegate;

    @Before
    public void setUp() throws Exception
    {
        nodeService = mock(NodeService.class);
        configureBootDevice = new ConfigureBootDevice(nodeService);
        delegateExecution = mock(DelegateExecution.class);
        nodeDetail = new NodeDetail();
        idracNetworkSettingsRequest = mock(IdracNetworkSettingsRequest.class);
        bootDeviceIdracStatus = new BootDeviceIdracStatus();
        workflowResult = mock(WorkflowResult.class);
        baseWorkflowDelegate = mock(BaseWorkflowDelegate.class);
        nodeDetail.setId("1");
        nodeDetail.setIdracIpAddress("1");
        nodeDetail.setIdracGatewayIpAddress("1");
        nodeDetail.setIdracSubnetMask("1");
        nodeDetail.setServiceTag("abc");
    }

    @Ignore @Test
    public void testFailedBootDeviceConfig() throws Exception {
        try {
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            given(nodeService.bootDeviceIdracStatus(any())).willThrow(new NullPointerException());
            when(delegateExecution.getVariable(WORKFLOW_RESULT)).thenReturn(workflowResult);
            configureBootDevice.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CONFIGURE_BOOT_DEVICE_FAILED));
        }
    }

    @Ignore @Test
    public void testSuccess() throws Exception {
        bootDeviceIdracStatus.setStatus("SUCCESS");
        when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
        when(nodeService.bootDeviceIdracStatus(any())).thenReturn(bootDeviceIdracStatus);
        final ConfigureBootDevice c = spy(new ConfigureBootDevice(nodeService));
        c.delegateExecute(delegateExecution);
        verify(c).updateDelegateStatus("Boot Device Configuration was successful on Node abc");
        }

    @Ignore @Test
    public void testException() throws Exception {
        try {
            bootDeviceIdracStatus.setStatus("FAIL");
            when(delegateExecution.getVariable(NODE_DETAIL)).thenReturn(nodeDetail);
            given(nodeService.idracNetworkSettings(any())).willThrow(new NullPointerException());
            when(delegateExecution.getVariable(WORKFLOW_RESULT)).thenReturn(workflowResult);
            configureBootDevice.delegateExecute(delegateExecution);
        } catch (BpmnError error) {
            assertTrue(error.getErrorCode().equals(DelegateConstants.CONFIGURE_BOOT_DEVICE_FAILED));
        }
    }
}
