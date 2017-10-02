/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.PING_IP_ADDRESS_FAILED;

@Component
@Scope("prototype")
@Qualifier("pingIdrac")
public class PingIdrac extends BaseWorkflowDelegate
{
    /**
     * The <code>Logger</code> instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PingIdrac.class);

    /**
     * Ping response wait time
     */
    private final int pingTimeoutInMilliSeconds;

    @Autowired
    public PingIdrac(int pingTimeoutInMilliSeconds)
    {
        this.pingTimeoutInMilliSeconds = pingTimeoutInMilliSeconds;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Ping iDrac");

        NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);

        if (nodeDetail != null && StringUtils.isNotEmpty(nodeDetail.getIdracIpAddress()))
        {
            InetAddress idracIP = null;
            try
            {
                String idracIpAddress = nodeDetail.getIdracIpAddress();

                idracIP = InetAddress.getByName(idracIpAddress);
            }
            catch (Exception e)
            {
                LOGGER.error("An Unexpected Exception occurred while attempting to ping Node " + nodeDetail.getServiceTag(), e);
                updateDelegateStatus("An Unexpected Exception occurred while attempting to ping Node " + nodeDetail.getServiceTag());
                updateWorkflowErrorResult(true, delegateExecution);
                throw new BpmnError(PING_IP_ADDRESS_FAILED,
                                    "An Unexpected Exception occurred while attempting to ping Node " +
                                    nodeDetail.getServiceTag() + ".  Reason: " + e.getMessage());
            }
            boolean ping = false;
            if (idracIP != null)
            {
                try
                {
                    ping = idracIP.isReachable(this.pingTimeoutInMilliSeconds);
                }
                catch (Exception e)
                {
                    LOGGER.error("Error trying to reach ip address!", e);
                }
            }
            if (idracIP == null || !ping)
            {
                LOGGER.error("Node " + nodeDetail.getServiceTag() + " IP Address was not able to be contacted.");
                updateDelegateStatus("Node " + nodeDetail.getServiceTag() + " IP Address was not able to be contacted.");
                updateWorkflowErrorResult(true, delegateExecution);
                throw new BpmnError(PING_IP_ADDRESS_FAILED,
                                    "Node " + nodeDetail.getServiceTag() + " IP Address was not able to be contacted.");
            }
        }
    }
}
