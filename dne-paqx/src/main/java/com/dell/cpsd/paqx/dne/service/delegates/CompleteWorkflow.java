/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.service.delegates.model.WorkflowResult;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.COMPLETED;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.COMPLETED_WITH_ERRORS;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.WORKFLOW_RESULT;

public class CompleteWorkflow extends BaseWorkflowDelegate
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CompleteWorkflow.class);

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Entering Complete Workflow");
        updateResult(delegateExecution);
        LOGGER.info("Finished Complete Workflow");
    }

    public synchronized void updateResult(DelegateExecution delegateExecution) {
        WorkflowResult workflowResult = (WorkflowResult) delegateExecution.getVariable(WORKFLOW_RESULT);
        if (workflowResult != null)
        {
            if (workflowResult.getHasErrors())
            {
                updateDelegateStatus("Workflow completed with errors. See status for details");
                workflowResult.setResult(COMPLETED_WITH_ERRORS);
            }
            else
            {
                updateDelegateStatus("Workflow completed successfully.");
                workflowResult.setResult(COMPLETED);
            }
        }
        delegateExecution.setVariable(WORKFLOW_RESULT, workflowResult);
    }
}
