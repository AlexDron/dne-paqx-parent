/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.model;

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
    private String type;

    private ComponentEndpointIds componentEndpointIds;

    public ComponentEndpointIds getComponentEndpointIds()
    {
        return componentEndpointIds;
    }

    public void setComponentEndpointIds(final ComponentEndpointIds componentEndpointIds)
    {
        this.componentEndpointIds = componentEndpointIds;
    }

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }
}
