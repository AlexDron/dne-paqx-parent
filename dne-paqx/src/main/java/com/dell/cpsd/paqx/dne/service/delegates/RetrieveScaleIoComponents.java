/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.service.NodeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Qualifier("retrieveScaleIoComponents")
public class RetrieveScaleIoComponents extends BaseWorkflowDelegate
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RetrieveScaleIoComponents.class);

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    @Autowired
    public RetrieveScaleIoComponents(final NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Retrieve ScaleIO Components");

/*        boolean succeeded;
        try
        {
            succeeded = this.nodeService.requestScaleIoComponents();
        }
        catch (Exception e)
        {
            LOGGER.error("An Unexpected Exception occurred while retrieving Scale IO Components", e);
            updateDelegateStatus(
                    "An Unexpected Exception occurred while retrieving Scale IO Components.  Reason: " + e.getMessage());
            throw new BpmnError(RETRIEVE_SCALE_IO_COMPONENTS_FAILED,
                                "An Unexpected Exception occurred while retrieving Scale IO Components.  Reason: " +
                                e.getMessage());
        }
        if (!succeeded)
        {
            LOGGER.error("Scale IO Components were not retrieved.");
            updateDelegateStatus("Scale IO Components were not retrieved.");
            throw new BpmnError(RETRIEVE_SCALE_IO_COMPONENTS_FAILED, "Scale IO Components were not retrieved.");
        }*/
        LOGGER.info("Scale IO Components were retrieved successfully.");
        updateDelegateStatus("Scale IO Components were retrieved successfully.");

    }
}
