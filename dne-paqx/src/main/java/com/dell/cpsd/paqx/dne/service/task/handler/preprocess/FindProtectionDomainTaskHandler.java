/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.preprocess;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOProtectionDomain;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOSDS;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import com.dell.cpsd.paqx.dne.service.model.ValidateProtectionDomainResponse;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.service.engineering.standards.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
public class FindProtectionDomainTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler {
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FindProtectionDomainTaskHandler.class);

    /**
     * The <code>NodeService</code> instance
     */
    private NodeService nodeService;

    private final DataServiceRepository repository;

    public FindProtectionDomainTaskHandler(final NodeService nodeService, final DataServiceRepository repository) {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public boolean executeTask(Job job) {

        LOGGER.info("Execute FindProtectionDomain task");

        TaskResponse response = initializeResponse(job);

        try {
            final ScaleIOProtectionDomain scaleIOProtectionDomain = repository.getScaleIoProtectionDomain();

            if (scaleIOProtectionDomain == null) {
                throw new IllegalStateException("No ScaleIO protection domains found.");
            }

            final ScaleIOSDS scaleIOSDS = repository.getScaleIoSds();
            if (scaleIOSDS == null) {
                throw new IllegalStateException("No ScaleIO SDS found.");
            }

            /**
             * Finding uuid
             */
            Map<String, TaskResponse> responseMap = job.getTaskResponseMap();
            TaskResponse findNodeTask = responseMap.get("findAvailableNodes");
            String uuid = findNodeTask.getResults().get("symphonyUUID");

            /**
             * Setting up ScaleIODataServer request
             */
            ScaleIODataServer scaleIODataServer = new ScaleIODataServer();
            scaleIODataServer.setId(scaleIOSDS.getId());
            scaleIODataServer.setName(scaleIOSDS.getName());
            scaleIODataServer.setType(repository.getNodeType(scaleIOSDS.getUuid().toString()));
            List<ScaleIODataServer> scaleIODataServerList = new ArrayList<>();

            /**
             * Setting up ProtectionDomain request
             */
            ProtectionDomain protectionDomain = new ProtectionDomain();
            protectionDomain.setId(scaleIOProtectionDomain.getId());
            protectionDomain.setName(scaleIOProtectionDomain.getName());
            protectionDomain.setState(scaleIOProtectionDomain.getProtectionDomainState());
            protectionDomain.setScaleIODataServers(scaleIODataServerList);
            List<ProtectionDomain> protectionDomainList = new ArrayList<>();

            /**
             * Setting up NodeData request
             */
            NodeData nodeData = new NodeData();
            nodeData.setSymphonyUuid(uuid);
            nodeData.setProtectionDomainId(scaleIOProtectionDomain.getId());
            nodeData.setType(repository.getNodeType(uuid));

            /**
             * Setting up EssValidateProtectionDomainsRequestMessage
             */
            EssValidateProtectionDomainsRequestMessage requestMessage = new EssValidateProtectionDomainsRequestMessage();
            requestMessage.setNodeData(nodeData);
            requestMessage.setProtectionDomains(protectionDomainList);

            /**
             * Creating the response.
             */
            EssValidateProtectionDomainsResponseMessage protectionDomainResponse = nodeService.validateProtectionDomains(requestMessage);

            /**
             * Mapping the actual response to local response
             */
            ValidateProtectionDomainResponse validateProtectionDomainResponse = new ValidateProtectionDomainResponse();
            String protectionDomainId = protectionDomainResponse.getValidProtectionDomains().get(0).getProtectionDomainID();
            if (protectionDomainId == null){

                throw new IllegalStateException("No valid protection domain found");
//                response.addError(protectionDomainResponse.getErrorMessage());
            }

            validateProtectionDomainResponse.setProtectionDomains(protectionDomainId);
            response.setResults(buildResponseResult(validateProtectionDomainResponse));
            response.addWarning(protectionDomainResponse.getValidProtectionDomains().get(0).getWarningMessages().toString());
            response.setWorkFlowTaskStatus(Status.SUCCEEDED);

            return true;

        }
        catch (Exception e) {
            LOGGER.info("", e);
            response.setWorkFlowTaskStatus(Status.FAILED);
            response.addError(e.toString());
        }
        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }

    private Map<String, String> buildResponseResult(ValidateProtectionDomainResponse validateProtectionDomainResponse)
    {
        Map<String, String> result = new HashMap<>();

        if (validateProtectionDomainResponse == null)
        {
            return result;
        }

        if (validateProtectionDomainResponse.getProtectionDomains() != null)
        {
            result.put("protectionDomain",validateProtectionDomainResponse.getProtectionDomains() );
        }
        return result;
    }

}