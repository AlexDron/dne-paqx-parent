/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates.model;

import java.io.Serializable;

public class NodeDetail implements Serializable
{
    private String id;
    private String serviceTag;

    private String idracIpAddress;
    private String idracGatewayIpAddress;
    private String idracSubnetMask;

    private String esxiManagementIpAddress;
    private String esxiManagementGatewayIpAddress;
    private String esxiManagementSubnetMask;
    private String esxiManagementHostname;

    private String scaleIOSVMDataIpAddress1;
    private String scaleIOSVMDataIpAddress2;
    private String scaleIOSVMManagementIpAddress;
    private String hostname;
    private String clusterName;

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public String getServiceTag()
    {
        return serviceTag;
    }

    public void setServiceTag(final String serviceTag)
    {
        this.serviceTag = serviceTag;
    }

    public String getIdracIpAddress()
    {
        return idracIpAddress;
    }

    public void setIdracIpAddress(final String idracIpAddress)
    {
        this.idracIpAddress = idracIpAddress;
    }

    public String getIdracGatewayIpAddress()
    {
        return idracGatewayIpAddress;
    }

    public void setIdracGatewayIpAddress(final String idracGatewayIpAddress)
    {
        this.idracGatewayIpAddress = idracGatewayIpAddress;
    }

    public String getIdracSubnetMask()
    {
        return idracSubnetMask;
    }

    public void setIdracSubnetMask(final String idracSubnetMask)
    {
        this.idracSubnetMask = idracSubnetMask;
    }

    public String getEsxiManagementIpAddress()
    {
        return esxiManagementIpAddress;
    }

    public void setEsxiManagementIpAddress(final String esxiManagementIpAddress)
    {
        this.esxiManagementIpAddress = esxiManagementIpAddress;
    }

    public String getEsxiManagementGatewayIpAddress()
    {
        return esxiManagementGatewayIpAddress;
    }

    public void setEsxiManagementGatewayIpAddress(final String esxiManagementGatewayIpAddress)
    {
        this.esxiManagementGatewayIpAddress = esxiManagementGatewayIpAddress;
    }

    public String getEsxiManagementSubnetMask()
    {
        return esxiManagementSubnetMask;
    }

    public void setEsxiManagementSubnetMask(final String esxiManagementSubnetMask)
    {
        this.esxiManagementSubnetMask = esxiManagementSubnetMask;
    }

    public String getEsxiManagementHostname()
    {
        return esxiManagementHostname;
    }

    public void setEsxiManagementHostname(final String esxiManagementHostname)
    {
        this.esxiManagementHostname = esxiManagementHostname;
    }

    public String getScaleIOSVMDataIpAddress1()
    {
        return scaleIOSVMDataIpAddress1;
    }

    public void setScaleIOSVMDataIpAddress1(final String scaleIOSVMDataIpAddress1)
    {
        this.scaleIOSVMDataIpAddress1 = scaleIOSVMDataIpAddress1;
    }

    public String getScaleIOSVMDataIpAddress2()
    {
        return scaleIOSVMDataIpAddress2;
    }

    public void setScaleIOSVMDataIpAddress2(final String scaleIOSVMDataIpAddress2)
    {
        this.scaleIOSVMDataIpAddress2 = scaleIOSVMDataIpAddress2;
    }

    public String getScaleIOSVMManagementIpAddress()
    {
        return scaleIOSVMManagementIpAddress;
    }

    public void setScaleIOSVMManagementIpAddress(final String scaleIOSVMManagementIpAddress)
    {
        this.scaleIOSVMManagementIpAddress = scaleIOSVMManagementIpAddress;
    }

    public String getHostname()
    {
        return hostname;
    }

    public void setHostname(final String hostname)
    {
        this.hostname = hostname;
    }

    public String getClusterName()
    {
        return clusterName;
    }

    public void setClusterName(final String clusterName)
    {
        this.clusterName = clusterName;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof NodeDetail))
        {
            return false;
        }

        final NodeDetail that = (NodeDetail) o;

        if (!getId().equals(that.getId()))
        {
            return false;
        }
        return getServiceTag().equals(that.getServiceTag());
    }

    @Override
    public int hashCode()
    {
        int result = getId().hashCode();
        result = 31 * result + getServiceTag().hashCode();
        return result;
    }
}
