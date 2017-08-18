/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.model;


public class DiscoveredNode
{
    private final String convergedUuid;
    private final com.dell.cpsd.DiscoveredNode.AllocationStatus nodeStatus;

    public DiscoveredNode(String convergedUuid, com.dell.cpsd.DiscoveredNode.AllocationStatus nodeStatus)
    {
        this.convergedUuid = convergedUuid;
        this.nodeStatus = nodeStatus;
    }

    public String getConvergedUuid()
    {
        return convergedUuid;
    }

    public com.dell.cpsd.DiscoveredNode.AllocationStatus getNodeStatus() {
        return nodeStatus;
    }
}
