/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.preprocess;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.InetAddress;

/**
 * Task responsible for pinging the iDRAC IP.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class PingIdracTaskHandler  extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The <code>Logger</code> instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PingIdracTaskHandler.class);

    /**
     * Ping response wait time
     */
    private final int pingTimeoutInMilliSeconds;

    /**
     * PingIdracTaskHandler constructor.
     *
     * @param pingTimeoutInMilliSeconds
     *            - The time in milliseconds to wait for a response before timing out.
     *
     * @since 1.0
     */
    public PingIdracTaskHandler(int pingTimeoutInMilliSeconds)
    {
        this.pingTimeoutInMilliSeconds = pingTimeoutInMilliSeconds;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute ping iDRAC task");

        TaskResponse response = initializeResponse(job);

        try
        {
            final NodeExpansionRequest inputParams = job.getInputParams();

            if (inputParams == null)
            {
                throw new IllegalStateException("Job input parameters are null");
            }

            String idracIpAddress = inputParams.getIdracIpAddress();

            if (StringUtils.isEmpty(idracIpAddress))
            {
                throw new IllegalStateException("No iDRAC IP address specified");
            }

            InetAddress idracIP = InetAddress.getByName(idracIpAddress);

            if (!idracIP.isReachable(this.pingTimeoutInMilliSeconds))
            {
                throw new IllegalStateException("iDRAC IP address was not reachable");
            }

            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            return true;
        }
        catch (Exception e)
        {
            LOGGER.error("Error while pinging iDRAC IP", e);
            response.addError(e.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }
}
