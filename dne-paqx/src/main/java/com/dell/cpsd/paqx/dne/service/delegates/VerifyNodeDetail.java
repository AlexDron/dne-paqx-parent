/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;

public class VerifyNodeDetail extends BaseWorkflowDelegate
{
    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        //TODO ADD VALIDATION FOR Required Fields to fail subprocess if missing.
    }
}
