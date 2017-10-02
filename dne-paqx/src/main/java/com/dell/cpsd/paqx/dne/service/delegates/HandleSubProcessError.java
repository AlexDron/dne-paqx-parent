/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;

public class HandleSubProcessError extends BaseWorkflowDelegate
{
    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        final String errorCode = (String) delegateExecution.getVariable("errorCode");
        final String errorMessage = (String) delegateExecution.getVariable("errorMessage");
        //TODO Update to set errors for sub process
    }
}
