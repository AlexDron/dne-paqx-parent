package com.dell.cpsd.paqx.dne.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * TODO: Document Usage
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "CREDENTIAL_DETAILS")
public class CredentialDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UUID", unique = true, nullable = false)
    private Long uuid;

    @Column(name = "CREDENTIAL_UUID", unique = true, nullable = false)
    private String credentialUuid;

    @Column(name = "CREDENTIAL_NAME", nullable = false)
    private String credentialName;

    @ManyToOne(cascade = CascadeType.ALL)
    private EndpointDetails endpointDetails;

    public Long getUuid()
    {
        return uuid;
    }

    public void setUuid(final Long uuid)
    {
        this.uuid = uuid;
    }

    public String getCredentialUuid()
    {
        return credentialUuid;
    }

    public void setCredentialUuid(final String credentialUuid)
    {
        this.credentialUuid = credentialUuid;
    }

    public String getCredentialName()
    {
        return credentialName;
    }

    public void setCredentialName(final String credentialName)
    {
        this.credentialName = credentialName;
    }

    public EndpointDetails getEndpointDetails()
    {
        return endpointDetails;
    }

    public void setEndpointDetails(final EndpointDetails endpointDetails)
    {
        this.endpointDetails = endpointDetails;
    }

    @Override
    public String toString()
    {
        return "CredentialDetails{" + "uuid=" + uuid + ", credentialUuid='" + credentialUuid + '\'' + ", credentialName='" + credentialName
                + '\'' + '}';
    }
}
