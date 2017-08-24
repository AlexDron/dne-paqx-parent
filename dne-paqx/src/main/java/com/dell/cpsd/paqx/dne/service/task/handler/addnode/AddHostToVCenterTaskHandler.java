/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.AddHostToVCenterResponse;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.InstallEsxiTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.ListESXiCredentialDetailsTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.virtualization.capabilities.api.ClusterOperationRequest;
import com.dell.cpsd.virtualization.capabilities.api.ClusterOperationRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add Host to VCenter Task Handler
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class AddHostToVCenterTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AddHostToVCenterTaskHandler.class);

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService           nodeService;
    private final DataServiceRepository repository;

    public AddHostToVCenterTaskHandler(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute Add Host to VCenter task");

        final AddHostToVCenterResponse response = initializeResponse(job);

        try
        {
            final Validate validate = new Validate(job).invoke();
            final ComponentEndpointIds componentEndpointIds = validate.getComponentEndpointIds();
            final String hostname = validate.getHostname();
            final String clusterId = validate.getClusterId();
            final ListESXiCredentialDetailsTaskResponse esXiCredentialDetailsTaskResponse = validate
                    .getListESXiCredentialDetailsTaskResponse();

            final ClusterOperationRequestMessage requestMessage = getClusterOperationRequestMessage(componentEndpointIds, hostname,
                    clusterId, esXiCredentialDetailsTaskResponse);

            final boolean success = this.nodeService.requestAddHostToVCenter(requestMessage);

            if (!success)
            {
                throw new IllegalStateException("Request add host to VCenter failed");
            }

            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            response.setClusterId(clusterId);

            return true;
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
            response.addError(e.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }

    private ClusterOperationRequestMessage getClusterOperationRequestMessage(final ComponentEndpointIds componentEndpointIds,
            final String hostname, final String clusterId, final ListESXiCredentialDetailsTaskResponse esXiCredentialDetailsTaskResponse)
    {
        final ClusterOperationRequestMessage requestMessage = new ClusterOperationRequestMessage();

        requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
        final ClusterOperationRequest clusterOperationRequest = new ClusterOperationRequest();
        clusterOperationRequest.setHostName(hostname);
        clusterOperationRequest.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(esXiCredentialDetailsTaskResponse.getComponentUuid(),
                        esXiCredentialDetailsTaskResponse.getEndpointUuid(), esXiCredentialDetailsTaskResponse.getCredentialUuid()));
        clusterOperationRequest.setClusterOperation(ClusterOperationRequest.ClusterOperation.ADD_HOST);
        clusterOperationRequest.setClusterID(clusterId);
        requestMessage.setClusterOperationRequest(clusterOperationRequest);
        return requestMessage;
    }

    @Override
    public AddHostToVCenterResponse initializeResponse(Job job)
    {
        final AddHostToVCenterResponse response = new AddHostToVCenterResponse();
        response.setWorkFlowTaskName(job.getCurrentTask().getTaskName());
        response.setWorkFlowTaskStatus(Status.IN_PROGRESS);
        job.addTaskResponse(job.getStep(), response);

        return response;
    }

    class Validate
    {
        private final Job                                   job;
        private       ComponentEndpointIds                  componentEndpointIds;
        private       String                                hostname;
        private       String                                clusterId;
        private       ListESXiCredentialDetailsTaskResponse listESXiCredentialDetailsTaskResponse;

        public Validate(final Job job)
        {
            this.job = job;
        }

        public Validate invoke()
        {
            componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");

            if (componentEndpointIds == null)
            {
                throw new IllegalStateException("No VCenter components found.");
            }

            final InstallEsxiTaskResponse installEsxiTaskResponse = (InstallEsxiTaskResponse) job.getTaskResponseMap().get("installEsxi");

            if (installEsxiTaskResponse == null)
            {
                throw new IllegalStateException("No Install ESXi task response found");
            }

            hostname = installEsxiTaskResponse.getHostname();

            if (hostname == null)
            {
                throw new IllegalStateException("Host name is null");
            }

            final NodeExpansionRequest inputParams = job.getInputParams();

            if (inputParams == null)
            {
                throw new IllegalStateException("Job Input Params are null");
            }

            final String clusterName = inputParams.getClusterName();

            if (clusterName == null)
            {
                throw new IllegalStateException("Cluster Name is null");
            }

            clusterId = repository.getClusterId(clusterName);

            // If null, should we refactor the vcenter side to find the cluster id based on the host name?
            if (clusterId == null)
            {
                throw new IllegalStateException("Cluster ID is null");
            }

            final ListESXiCredentialDetailsTaskResponse listESXiCredentialDetailsTaskResponse = (ListESXiCredentialDetailsTaskResponse) job
                    .getTaskResponseMap().get("retrieveEsxiDefaultCredentialDetails");

            if (listESXiCredentialDetailsTaskResponse == null)
            {
                throw new IllegalStateException("Default ESXi Host Credential Details are null.");
            }

            this.listESXiCredentialDetailsTaskResponse = listESXiCredentialDetailsTaskResponse;

            return this;
        }

        ComponentEndpointIds getComponentEndpointIds()
        {
            return componentEndpointIds;
        }

        String getHostname()
        {
            return hostname;
        }

        String getClusterId()
        {
            return clusterId;
        }

        public ListESXiCredentialDetailsTaskResponse getListESXiCredentialDetailsTaskResponse()
        {
            return listESXiCredentialDetailsTaskResponse;
        }
    }
}