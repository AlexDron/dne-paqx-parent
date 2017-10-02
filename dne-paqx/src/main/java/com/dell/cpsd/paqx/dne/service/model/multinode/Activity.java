/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.model.multinode;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Activity
{
    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private Date startTime;

    @JsonProperty
    private Date endTime;

    @JsonProperty
    private Long durationInMillis;

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(final Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(final Date endTime)
    {
        this.endTime = endTime;
    }

    public Long getDurationInMillis()
    {
        return durationInMillis;
    }

    public void setDurationInMillis(final Long durationInMillis)
    {
        this.durationInMillis = durationInMillis;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Activity))
        {
            return false;
        }

        final Activity activity = (Activity) o;

        if (!getId().equals(activity.getId()))
        {
            return false;
        }
        return getName().equals(activity.getName());
    }

    @Override
    public int hashCode()
    {
        int result = getId().hashCode();
        result = 31 * result + getName().hashCode();
        return result;
    }
}
