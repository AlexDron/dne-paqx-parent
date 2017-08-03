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
public class ComponentEndpointDetails
{
    private String componentUuid;

    //TODO: Once it is defined what we need, we can define the model for that
    private Object location;

    private String elementType;

    private List<EndpointCredentials> endpointCredentials = new ArrayList<>();

    public ComponentEndpointDetails()
    {
    }

    public ComponentEndpointDetails(final String componentUuid, final Object location, final String elementType)
    {
        this.componentUuid = componentUuid;
        this.location = location;
        this.elementType = elementType;
    }

    public String getComponentUuid()
    {
        return componentUuid;
    }

    public void setComponentUuid(final String componentUuid)
    {
        this.componentUuid = componentUuid;
    }

    public Object getLocation()
    {
        return location;
    }

    public void setLocation(final Object location)
    {
        this.location = location;
    }

    public String getElementType()
    {
        return elementType;
    }

    public void setElementType(final String elementType)
    {
        this.elementType = elementType;
    }

    public List<EndpointCredentials> getEndpointCredentials()
    {
        return endpointCredentials;
    }

    public void setEndpointCredentials(final List<EndpointCredentials> endpointCredentials)
    {
        this.endpointCredentials = endpointCredentials;
    }
}
