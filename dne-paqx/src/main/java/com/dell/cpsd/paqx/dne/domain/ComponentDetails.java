package com.dell.cpsd.paqx.dne.domain;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
@Entity
@Table(name = "COMPONENT_DETAILS")
public class ComponentDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UUID", unique = true, nullable = false)
    private Long uuid;

    @Column(name = "ELEMENT_TYPE", nullable = false)
    private String elementType;

    @Column(name = "COMPONENT_TYPE", nullable = false)
    private String componentType;

    @Column(name = "IDENTIFIER", nullable = false)
    private String identifier;

    @Column(name = "COMPONENT_UUID", nullable = false)
    private String componentUuid;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentDetails", orphanRemoval = true)
    private List<EndpointDetails> endpointDetails = new ArrayList<>();

    public ComponentDetails()
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

    public String getElementType()
    {
        return elementType;
    }

    public void setElementType(final String type)
    {
        this.elementType = type;
    }

    public String getComponentType()
    {
        return componentType;
    }

    public void setComponentType(final String componentType)
    {
        this.componentType = componentType;
    }

    public String getComponentUuid()
    {
        return componentUuid;
    }

    public void setComponentUuid(final String componentUuid)
    {
        this.componentUuid = componentUuid;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(final String identifier)
    {
        this.identifier = identifier;
    }

    public List<EndpointDetails> getEndpointDetails()
    {
        return endpointDetails;
    }

    public void setEndpointDetails(final List<EndpointDetails> endpointDetails)
    {
        this.endpointDetails = endpointDetails;
    }

    @Override
    public String toString()
    {
        return "ComponentDetails{" + "uuid=" + uuid + ", type='" + elementType + '\'' + ", identifier='" + identifier + '\'' + ", componentUuid='"
                + componentUuid + '\'' + '}';
    }
}
