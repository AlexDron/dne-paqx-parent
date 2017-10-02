/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.service.delegates.exception.DneDelegateException;
import com.dell.cpsd.paqx.dne.service.delegates.model.WorkflowResult;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.FAILED;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.WORKFLOW_RESULT;

public class HandleError extends BaseWorkflowDelegate
{
    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        final String errorCode = (String) delegateExecution.getVariable("errorCode");
        final String errorMessage = (String) delegateExecution.getVariable("errorMessage");
        WorkflowResult workflowResult = (WorkflowResult) delegateExecution.getVariable(WORKFLOW_RESULT);
        DneDelegateException dneDelegateException = new DneDelegateException(errorMessage, errorCode);
        workflowResult.setResult(FAILED);
        workflowResult.setError(dneDelegateException);
        delegateExecution.setVariable(WORKFLOW_RESULT,workflowResult);
        updateDelegateStatus("Workflow failed.");
    }
}
