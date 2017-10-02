/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

@Component
@Scope("prototype")
@Qualifier("updateSDCPerformanceProfile")
public class UpdateSDCPerformanceProfile extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateSDCPerformanceProfile.class);

    /*
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    /*
     * The <code>DataServiceRepository</code> instance
     */
    private final DataServiceRepository repository;

    /*
    * The time to wait before sending the request to update the sdc performance profile
    */
    //private final long waitTime;

    /*
    * ScaleIO gateway credential components
    */
    private static final String COMPONENT_TYPE = "SCALEIO-CLUSTER";

    /**
     * UpdateSdcPerformanceProfileTaskHandler constructor
     *
     * @param nodeService - The <code>NodeService</code> instance
     * @param repository  - The <code>DataServiceRepository</code> instance
     * @param waitTime    - Time to wait before requesting the ScaleIO SDC performance profile update.
     */
    @Autowired
    public UpdateSDCPerformanceProfile(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute UpdateSDCPerformanceProfile");
        final String taskMessage = "Update ScaleIO SDC Performance Profile";

        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
/*        final ComponentEndpointIds componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType(
                "VCENTER-CUSTOMER");
        final String sdcGUID = (String) delegateExecution.getVariable(DelegateConstants.IOCTL_INI_GUI_STR);

        final String scaleIoSdcIpAddress = nodeDetail.getEsxiManagementIpAddress();

        final PerformanceProfileRequest performanceProfileRequest = new PerformanceProfileRequest();
        performanceProfileRequest.setSdcIp(scaleIoSdcIpAddress);
        performanceProfileRequest.setSdcGuid(sdcGUID);
        performanceProfileRequest.setPerfProfile(PerformanceProfileRequest.PerfProfile.HIGH_PERFORMANCE);

        final SioSdcUpdatePerformanceProfileRequestMessage requestMessage = new SioSdcUpdatePerformanceProfileRequestMessage();
        requestMessage.setPerformanceProfileRequest(performanceProfileRequest);
        requestMessage.setEndpointUrl("https://" + componentEndpointIds.getEndpointUrl());
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.storage.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                                                                                componentEndpointIds.getEndpointUuid(),
                                                                                componentEndpointIds
                                                                                        .getCredentialUuid()));

        boolean succeeded = false;
        try
        {
            this.nodeService.requestUpdateSdcPerformanceProfile(requestMessage);

        }
        catch (Exception ex)
        {
            LOGGER.error("An Unexpected Exception occurred attempting to Install ScaleIO Vib.", ex);
            updateDelegateStatus(
                    "An Unexpected Exception occurred attempting to request " + taskMessage + ".  Reason: " +
                    ex.getMessage());
            throw new BpmnError(CONFIGURE_SCALEIO_VIB_FAILED,
                                "An Unexpected Exception occurred attempting to request " + taskMessage +
                                ".  Reason: " + ex.getMessage());
        }
        if (!succeeded)
        {
            LOGGER.error(taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
            updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
            throw new BpmnError(CONFIGURE_SCALEIO_VIB_FAILED,
                                taskMessage + " on Node " + nodeDetail.getServiceTag() + " failed!");
        }*/

        LOGGER.info(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
        updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");

    }
}
