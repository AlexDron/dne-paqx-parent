/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates.model;

import java.io.Serializable;

public class WorkflowResult implements Serializable
{
    private String businessKey;

    private String result;

    private Exception error;

    private boolean hasErrors;

    public String getBusinessKey()
    {
        return businessKey;
    }

    public void setBusinessKey(String businessKey)
    {
        this.businessKey = businessKey;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public Exception getError()
    {
        return error;
    }

    public void setError(Exception error)
    {
        this.error = error;
    }

    public boolean getHasErrors() {  return hasErrors; }

    public void setHasErrors(boolean hasErrors) { this.hasErrors = hasErrors; }
}
