/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates.model;

import java.io.Serializable;

public class ESXiCredentialDetails implements Serializable
{

    private String componentUuid;
    private String endpointUuid;
    private String credentialUuid;

    public String getComponentUuid()
    {
        return componentUuid;
    }

    public void setComponentUuid(final String componentUuid)
    {
        this.componentUuid = componentUuid;
    }

    public String getEndpointUuid()
    {
        return endpointUuid;
    }

    public void setEndpointUuid(final String endpointUuid)
    {
        this.endpointUuid = endpointUuid;
    }

    public String getCredentialUuid()
    {
        return credentialUuid;
    }

    public void setCredentialUuid(final String credentialUuid)
    {
        this.credentialUuid = credentialUuid;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof ESXiCredentialDetails))
        {
            return false;
        }

        final ESXiCredentialDetails that = (ESXiCredentialDetails) o;

        if (!getComponentUuid().equals(that.getComponentUuid()))
        {
            return false;
        }
        if (!getEndpointUuid().equals(that.getEndpointUuid()))
        {
            return false;
        }
        return getCredentialUuid().equals(that.getCredentialUuid());
    }

    @Override
    public int hashCode()
    {
        int result = getComponentUuid().hashCode();
        result = 31 * result + getEndpointUuid().hashCode();
        result = 31 * result + getCredentialUuid().hashCode();
        return result;
    }
}
