package com.dell.cpsd.paqx.dne.service.model;

/**
 * TODO: Document Usage
 * <p/>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * <p/>
 *
 * @version 1.0
 * @since 1.0
 */
public class DiscoverScaleIoTaskResponse extends TaskResponse
{
    private String jobId;

    private String message;

    private String status;

    public String getJobId()
    {
        return jobId;
    }

    public void setJobId(final String jobId)
    {
        this.jobId = jobId;
    }

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
}
