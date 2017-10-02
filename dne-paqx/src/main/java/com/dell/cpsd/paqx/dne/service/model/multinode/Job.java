/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.model.multinode;

public class Job
{
    private String id;

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Job))
        {
            return false;
        }

        final Job job = (Job) o;

        return getId().equals(job.getId());
    }

    @Override
    public int hashCode()
    {
        return getId().hashCode();
    }
}
