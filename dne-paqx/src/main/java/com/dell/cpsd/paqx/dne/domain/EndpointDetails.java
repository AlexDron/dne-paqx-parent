package com.dell.cpsd.paqx.dne.domain;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Document Usage
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "ENDPOINT_DETAILS")
public class EndpointDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UUID", unique = true, nullable = false)
    private Long uuid;

    @Column(name = "ELEMENT_TYPE", nullable = false)
    private String type;

    @Column(name = "IDENTIFIER", nullable = false)
    private String identifier;

    @Column(name = "ENDPOINT_UUID", unique = true, nullable = false)
    private String endpointUuid;

    @Column(name = "ENDPOINT_URL", nullable = false)
    private String endpointUrl;

    @ManyToOne(cascade = CascadeType.ALL)
    private ComponentDetails componentDetails;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "endpointDetails", orphanRemoval = true)
    private List<CredentialDetails> credentialDetailsList = new ArrayList<>();

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

    public String getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(final String identifier)
    {
        this.identifier = identifier;
    }

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

    public ComponentDetails getComponentDetails()
    {
        return componentDetails;
    }

    public void setComponentDetails(final ComponentDetails componentDetails)
    {
        this.componentDetails = componentDetails;
    }

    public List<CredentialDetails> getCredentialDetailsList()
    {
        return credentialDetailsList;
    }

    public void setCredentialDetailsList(final List<CredentialDetails> credentialDetailsList)
    {
        this.credentialDetailsList = credentialDetailsList;
    }

    @Override
    public String toString()
    {
        return "EndpointDetails{" + "uuid=" + uuid + ", type='" + type + '\'' + ", identifier='" + identifier + '\'' + ", endpointUuid='"
                + endpointUuid + '\'' + ", endpointUrl='" + endpointUrl + '\'' + '}';
    }
}
