package com.dell.cpsd.paqx.dne.service.model;

/**
 * TODO: Document Usage
 * <p/>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * <p/>
 *
 * @version 1.0
 * @since 1.0
 */
public class DeployScaleIoVmTaskResponse extends TaskResponse
{
    private String message;

    private String status;

    private String newVMName;

    public String getMessage()
    {
        return message;
    }

    public void setMessage(final String message)
    {
        this.message = message;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(final String status)
    {
        this.status = status;
    }

    public String getNewVMName()
    {
        return newVMName;
    }

    public void setNewVMName(final String newVMName)
    {
        this.newVMName = newVMName;
    }
}
