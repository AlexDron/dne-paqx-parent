package com.dell.cpsd.paqx.dne.service.model;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Document Usage
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class EndpointCredentials
{
    private String endpointUuid;
    private String endpointUrl;
    private List<String> credentialUuids = new ArrayList<>();

    public String getEndpointUuid()
    {
        return endpointUuid;
    }

    public void setEndpointUuid(final String endpointUuid)
    {
        this.endpointUuid = endpointUuid;
    }

    public String getEndpointUrl()
    {
        return endpointUrl;
    }

    public void setEndpointUrl(final String endpointUrl)
    {
        this.endpointUrl = endpointUrl;
    }

    public List<String> getCredentialUuids()
    {
        return credentialUuids;
    }

    public void setCredentialUuids(final List<String> credentialUuids)
    {
        this.credentialUuids = credentialUuids;
    }
}
