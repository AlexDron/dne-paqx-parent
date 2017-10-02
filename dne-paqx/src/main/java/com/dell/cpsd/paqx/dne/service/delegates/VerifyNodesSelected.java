/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;

public class VerifyNodesSelected extends BaseWorkflowDelegate
{
    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        //TODO Add functionality to confirm selected nodes are still available and to prune list.
    }
}
