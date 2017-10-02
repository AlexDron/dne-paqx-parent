/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.model.BootDeviceIdracStatus;
import com.dell.cpsd.paqx.dne.service.model.ConfigureBootDeviceIdracRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.CONFIGURE_BOOT_DEVICE_FAILED;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

@Component
@Scope("prototype")
@Qualifier("configureBootDevice")
public class ConfigureBootDevice extends BaseWorkflowDelegate
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureBootDevice.class);

    /**
     * The <code>NodeService</code> instance
     */
    private NodeService nodeService;

    @Autowired
    public ConfigureBootDevice(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Configure Boot Device");

        NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);

        if (nodeDetail != null && StringUtils.isNotEmpty(nodeDetail.getId()) && StringUtils.isNotEmpty(
                nodeDetail.getIdracIpAddress()))
        {
            BootDeviceIdracStatus bootDeviceIdracStatus = null;
            try
            {

                String uuid = nodeDetail.getId();
                String ipAddress = nodeDetail.getIdracIpAddress();

                ConfigureBootDeviceIdracRequest configureBootDeviceIdracRequest = new ConfigureBootDeviceIdracRequest();
                configureBootDeviceIdracRequest.setUuid(uuid);
                configureBootDeviceIdracRequest.setIdracIpAddress(ipAddress);

                bootDeviceIdracStatus = nodeService.bootDeviceIdracStatus(configureBootDeviceIdracRequest);
            }
            catch (Exception e)
            {
                LOGGER.error("An Unexpected Exception Occurred attempting to Configure Boot Device on Node " +
                             nodeDetail.getServiceTag(), e);
                updateDelegateStatus("An Unexpected Exception Occurred attempting to Configure Boot Device on Node " +
                                     nodeDetail.getServiceTag());
                updateWorkflowErrorResult(true, delegateExecution);
                throw new BpmnError(CONFIGURE_BOOT_DEVICE_FAILED,
                                    "Configure Boot Device on Node " + nodeDetail.getServiceTag() +
                                    " failed!  Reason: " + e.getMessage());
            }
            if (bootDeviceIdracStatus != null && "SUCCESS".equalsIgnoreCase(bootDeviceIdracStatus.getStatus()))
            {
                LOGGER.info("Boot Device Configuration was successful on Node " + nodeDetail.getServiceTag());
                updateDelegateStatus("Boot Device Configuration was successful on Node " + nodeDetail.getServiceTag());
            }
            else
            {

                final String[] message = {
                        "Boot Device Configuration was unsuccessful on Node " + nodeDetail.getServiceTag() +
                        ". Please correct the following errors and try again.\n"};
                if (bootDeviceIdracStatus != null && CollectionUtils.isNotEmpty(bootDeviceIdracStatus.getErrors()))
                {
                    bootDeviceIdracStatus.getErrors().forEach(error -> {
                        message[0] += error + "\n";
                    });
                }
                LOGGER.error(message[0]);
                updateDelegateStatus(message[0]);
                updateWorkflowErrorResult(true, delegateExecution);
                throw new BpmnError(CONFIGURE_BOOT_DEVICE_FAILED, message[0]);
            }

        }
    }
}
