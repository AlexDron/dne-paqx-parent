/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOProtectionDomain;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import com.dell.cpsd.service.engineering.standards.EssValidateStoragePoolResponseMessage;
import org.apache.commons.collections.CollectionUtils;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.FIND_SCALE_IO_FAILED;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.FIND_VCLUSTER_FAILED;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.STORAGE_POOL;

@Component
@Scope("prototype")
@Qualifier("findScaleIO")
public class FindScaleIO extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FindScaleIO.class);

    /*
     * Node Service instance used to find and validate Storage pool entries
     */
    private NodeService nodeService;

    /**
     * Construct an instance based on nodeservice reference
     *
     * @param nodeService
     */
    @Autowired
    public FindScaleIO(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute FindScaleIO");
/*
        List<ScaleIOData> scaleIODataList = nodeService.listScaleIOData();

        if (CollectionUtils.isNotEmpty(scaleIODataList))
        {
            ScaleIOData scaleIOData = scaleIODataList.get(0);
            List<ScaleIOProtectionDomain> protectionDomains = scaleIOData.getProtectionDomains();
            EssValidateStoragePoolResponseMessage storageResponseMessage = null;
            if (CollectionUtils.isNotEmpty(protectionDomains))
            {
                for (ScaleIOProtectionDomain protectionDomain : protectionDomains)
                {
                    try
                    {
                        storageResponseMessage = nodeService.validateStoragePools(protectionDomain.getStoragePools());
                    }
                    catch (ServiceTimeoutException | ServiceExecutionException exception)
                    {
                        LOGGER.error("An unexpected Exception occurred while attempting to validate Storage Pools.",
                                     exception);
                        updateDelegateStatus(
                                "An Unexpected exception occurred trying to retrieve the validate Storage Pools.  Reason: " +
                                exception.getMessage());
                        throw new BpmnError(FIND_VCLUSTER_FAILED,
                                            "An Unexpected exception occurred trying to retrieve the validate Storage Pools.  Reason: " +
                                            exception.getMessage());
                    }
                    if (storageResponseMessage != null)
                    {
                        if (CollectionUtils.isNotEmpty(storageResponseMessage.getValidStorage()))
                        {
                            delegateExecution.setVariable(STORAGE_POOL,
                                                          storageResponseMessage.getValidStorage().get(0));

                            LOGGER.info("Storage pool validated successfully.");
                            updateDelegateStatus("Storage pool validated successfully.");
                            break;
                        }
                        else if (CollectionUtils.isNotEmpty(storageResponseMessage.getInvalidStorage()))
                        {
                            final String message[] = {"Find Storage Pool Failed. Reason: "};
                            storageResponseMessage.getInvalidStorage().forEach(failed -> {
                                message[0] += failed + " ";
                            });
                            LOGGER.error(message[0]);
                            updateDelegateStatus(message[0]);
                            updateWorkflowErrorResult(true, delegateExecution);
                            throw new BpmnError(FIND_SCALE_IO_FAILED, message[0]);
                        }
                    }
                }
            }
        }*/
    }
}
