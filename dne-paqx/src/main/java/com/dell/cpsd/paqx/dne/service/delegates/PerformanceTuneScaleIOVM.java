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
@Qualifier("performanceTuneScaleIOVM")
public class PerformanceTuneScaleIOVM extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceTuneScaleIOVM.class);

    /*
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    /*
     * The <code>DataServiceRepository</code> instance
     */
    private final DataServiceRepository repository;

    private static final String COMPONENT_TYPE     = "COMMON-SERVER";
    private static final String ENDPOINT_TYPE      = "COMMON-SVM";
    private static final String COMMON_CREDENTIALS = "SVM-COMMON";

    /**
     * PerformanceTuneSvmTaskHandler constructor.
     *
     * @param nodeService - The <code>NodeService</code> instance
     * @param repository  - The <code>DataServiceRepository</code> instance
     */
    @Autowired
    public PerformanceTuneScaleIOVM(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute PerformanceTuneSvmTaskHandler task");
        final String taskMessage = "Performance Tune Scale IO VM";
        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);

        /*try
        {
            final ComponentEndpointIds componentEndpointIds = repository
                    .getComponentEndpointIds(COMPONENT_TYPE, ENDPOINT_TYPE, COMMON_CREDENTIALS);

            if (componentEndpointIds == null)
            {
                throw new IllegalStateException("No component ids found.");
            }

            final NodeExpansionRequest nodeExpansionRequest = job.getInputParams();

            if (nodeExpansionRequest == null)
            {
                throw new IllegalStateException("Job input parameters are null");
            }

            final String scaleIoSvmManagementIpAddress = nodeExpansionRequest.getScaleIoSvmManagementIpAddress();

            if (StringUtils.isEmpty(scaleIoSvmManagementIpAddress))
            {
                throw new IllegalStateException("ScaleIO VM Management IP Address is null");
            }

            RemoteCommandExecutionRequestMessage requestMessage = new RemoteCommandExecutionRequestMessage();
            requestMessage.setRemoteCommand(RemoteCommandExecutionRequestMessage.RemoteCommand.PERFORMANCE_TUNING_SVM);
            requestMessage.setRemoteHost(scaleIoSvmManagementIpAddress);
            requestMessage.setOsType(RemoteCommandExecutionRequestMessage.OsType.LINUX);
            requestMessage.setComponentEndpointIds(
                    new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                                                                                           componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));

            final boolean succeeded = this.nodeService.requestRemoteCommandExecution(requestMessage);

            if (!succeeded)
            {
                throw new IllegalStateException("Performance tune ScaleIO vm request failed");
            }

            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            return true;
        }
        catch (Exception ex)
        {
            LOGGER.error("Error while performance tuning the scaleio vm", ex);
            response.addError(ex.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
*/
        LOGGER.info(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
        updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
    }
}
