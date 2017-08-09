package com.dell.cpsd.paqx.dne.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * TODO: Document Usage
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "COMPONENT_ENDPOINT")
public class ComponentEndpoint
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UUID", unique = true, nullable = false)
    private Long uuid;

    //For MVP, simply fetch one component
    @Column(name = "COMPONENT_TYPE", nullable = false, unique = true)
    private String type;

    @Column(name = "COMPONENT_UUID", nullable = false)
    private String componentUuid;

    @Column(name = "ENDPOINT_UUID", nullable = false)
    private String endpointUuid;

    @Column(name = "CREDENTIAL_UUID", nullable = false)
    private String credentialUuid;

    @Column(name = "ENDPOINT_URL", nullable = false)
    private String endpointUrl;

    public ComponentEndpoint()
    {
        // Default Constructor required by Hibernate
    }

    public Long getUuid()
    {
        return uuid;
    }

    public void setUuid(final Long uuid)
    {
        this.uuid = uuid;
    }

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
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

    public String getCredentialUuid()
    {
        return credentialUuid;
    }

    public void setCredentialUuid(final String credentialUuid)
    {
        this.credentialUuid = credentialUuid;
    }

    public String getEndpointUrl()
    {
        return endpointUrl;
    }

    public void setEndpointUrl(final String endpointUrl)
    {
        this.endpointUrl = endpointUrl;
    }

    @Override
    public String toString()
    {
        return "ComponentEndpoint{" + "uuid=" + uuid + ", type='" + type + '\'' + ", componentUuid='" + componentUuid + '\''
                + ", endpointUuid='" + endpointUuid + '\'' + ", credentialUuid='" + credentialUuid + '\'' + ", endpointUrl='" + endpointUrl
                + '\'' + '}';
    }
}
