/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

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
@Qualifier("notifyNodeDiscoveryToUpdateStatus")
public class NotifyNodeDiscoveryToUpdateStatus extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(NotifyNodeDiscoveryToUpdateStatus.class);

    /*
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    /**
     * NotifyNodeDiscoveryToUpdateStatusTaskHandler constructor.
     *
     * @param nodeService
     *            - The <code>NodeService</code> instance.
     *
     * @since 1.0
     */
    @Autowired
    public NotifyNodeDiscoveryToUpdateStatus(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute NotifyNodeDiscoveryToUpdateStatusTaskHandler task");
        final String taskMessage = "Update Node Status";
        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);

 /*       try
        {
            final NodeExpansionRequest inputParams = job.getInputParams();

            if (inputParams == null)
            {
                throw new IllegalStateException("Job input parameters are null");
            }

            final String symphonyUuid = inputParams.getSymphonyUuid();

            if (StringUtils.isEmpty(symphonyUuid))
            {
                throw new IllegalStateException("Symphony uuid is null");
            }

            boolean succeeded = this.nodeService.notifyNodeAllocationComplete(symphonyUuid);

            if (!succeeded)
            {
                throw new IllegalStateException("Node allocation completion failed");
            }

            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            return true;
        }
        catch (Exception e)
        {
            LOGGER.error("Error notifying node discovery service", e);
            response.addError(e.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
*/
        LOGGER.info(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
        updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
    }
}
