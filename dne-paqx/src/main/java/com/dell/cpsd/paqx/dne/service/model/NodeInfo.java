/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.model;

import java.util.ArrayList;
import java.util.List;

import com.dell.cpsd.service.system.definition.api.Definition;
import com.dell.cpsd.service.system.definition.api.Identity;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
*/
public class NodeInfo
{
    private String       symphonyUuid;
    private NodeStatus   nodeStatus;
    private Identity     identity;
    private Definition   definition;
    private List<String> parentGroups;
    private List<String> endpoints;

    public NodeInfo(String symphonyUuid, NodeStatus nodeStatus)
    {
        this.nodeStatus = nodeStatus;
        this.symphonyUuid = symphonyUuid;

        // TODO: need to decide how we actually get all this info...
        // Specifically, need to get the MAC address from somewhere...
        // for now will use the symphonyUuid.
        // What about the IP address and the serial number??
        this.identity = new Identity("SERVER", this.symphonyUuid, null, null, null/* businessKeys */);
        this.definition = new Definition("POWEREDGE", "POWEREDGE", "630", "R630");
        this.parentGroups = new ArrayList<>();
        this.parentGroups.add("SystemCompute");
        this.endpoints = new ArrayList<>();
        this.endpoints.add("RACKHD-EP");
        // Omitting these endpoints for now...
        //this.endpoints.add("COMMON-DELL-POWEREDGE-IDRAC-EP");
        //this.endpoints.add("COMMON-DELL-POWEREDGE-ESXI-HOST-EP");
        //this.endpoints.add("COMMON-DELL-POWEREDGE-SVM-EP");
    }

    public String getSymphonyUuid()
    {
        return symphonyUuid;
    }

    public NodeStatus getNodeStatus()
    {
        return nodeStatus;
    }

    public Identity getIdentity()
    {
        return this.identity;
    }

    public Definition getDefinition()
    {
        return this.definition;
    }

    public List<String> getParentGroups()
    {
        return this.parentGroups;
    }

    public List<String> getEndpoints()
    {
        return this.endpoints;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();

        builder.append("NodeInfo {");

        builder.append("symphonyUuid=").append(this.symphonyUuid);
        builder.append(", nodeStatus=").append(this.nodeStatus);

        builder.append("}");

        return builder.toString();
    }
}
