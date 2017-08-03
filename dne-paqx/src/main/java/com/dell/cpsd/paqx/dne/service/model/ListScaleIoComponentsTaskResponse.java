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
public class ListScaleIoComponentsTaskResponse extends TaskResponse
{
    private String message;

    private String type;

    private List<ComponentEndpointDetails> componentEndpointDetails = new ArrayList<>();

    public String getMessage()
    {
        return message;
    }

    public void setMessage(final String message)
    {
        this.message = message;
    }

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }

    public List<ComponentEndpointDetails> getComponentEndpointDetails()
    {
        return componentEndpointDetails;
    }

    public void setComponentEndpointDetails(final List<ComponentEndpointDetails> componentEndpointDetails)
    {
        this.componentEndpointDetails = componentEndpointDetails;
    }
}
