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
public class ComponentEndpointIds
{
    private final String componentUuid;
    private final String endpointUuid;
    private final String endpointUrl;
    private final String credentialUuid;

    public ComponentEndpointIds(final String componentUuid, final String endpointUuid, final String endpointUrl,
            final String credentialUuid)
    {
        this.componentUuid = componentUuid;
        this.endpointUuid = endpointUuid;
        this.endpointUrl = endpointUrl;
        this.credentialUuid = credentialUuid;
    }

    public String getComponentUuid()
    {
        return componentUuid;
    }

    public String getEndpointUuid()
    {
        return endpointUuid;
    }

    public String getEndpointUrl()
    {
        return endpointUrl;
    }

    public String getCredentialUuid()
    {
        return credentialUuid;
    }
}
