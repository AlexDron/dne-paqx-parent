/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * <p/>
 */

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
public class ListESXiCredentialDetailsTaskResponse extends TaskResponse
{
    private String credentialUuid;
    private String componentUuid;
    private String endpointUuid;

    public String getCredentialUuid()
    {
        return credentialUuid;
    }

    public void setCredentialUuid(final String credentialUuid)
    {
        this.credentialUuid = credentialUuid;
    }

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
}