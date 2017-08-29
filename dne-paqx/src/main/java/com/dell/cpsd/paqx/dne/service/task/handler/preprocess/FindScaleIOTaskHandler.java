/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.preprocess;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOProtectionDomain;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import com.dell.cpsd.service.engineering.standards.EssValidateStoragePoolResponseMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements the logic to find Storage pool entries
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

public class FindScaleIOTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FindScaleIOTaskHandler.class);

    /*
     * Node Service instance used to find and validate Storage pool entries
     */
    private NodeService nodeService;

    /**
     * Construct an instance based on nodeservice reference
     *
     * @param nodeService
     */
    public FindScaleIOTaskHandler(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute FindScaleIOTaskHandler task");
        TaskResponse response = initializeResponse(job);

        try
        {
            List<ScaleIOData> scaleIODataList = nodeService.listScaleIOData();

            if (scaleIODataList != null && scaleIODataList.size() > 0)
            {
                ScaleIOData scaleIOData = scaleIODataList.get(0);
                List<ScaleIOProtectionDomain> protectionDomains = scaleIOData.getProtectionDomains();
                EssValidateStoragePoolResponseMessage storageResponseMessage = null;
                if (protectionDomains != null)
                {
                    for (ScaleIOProtectionDomain protectionDomain : protectionDomains)
                    {
                        storageResponseMessage = nodeService.validateStoragePools(protectionDomain.getStoragePools());
                        if (storageResponseMessage.getInvalidStorage().size() > 0)
                        {
                            response.setWorkFlowTaskStatus(Status.FAILED);
                            storageResponseMessage.getInvalidStorage().stream().forEach(f -> {
                                response.addError(f);
                            });
                            return false;
                        } else if (storageResponseMessage.getValidStorage().size() > 0) {
                            Map<String, String> result = new HashMap<>();
                            result.put("storagePool", storageResponseMessage.getValidStorage().get(0));
                            response.setResults(result);

                            LOGGER.info("Storage pool validated successfully.");
                            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
                        }
                    }
                }

                return true;
            }
        }
        catch (ServiceTimeoutException | ServiceExecutionException exception)
        {
            LOGGER.error("Error listing scaleIO data.");
            LOGGER.error(exception.getMessage());
        }

        return false;
    }

}
