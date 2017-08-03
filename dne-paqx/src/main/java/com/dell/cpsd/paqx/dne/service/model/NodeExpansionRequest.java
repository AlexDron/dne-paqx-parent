/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.model;

public class NodeExpansionRequest
{
    private String idracIpAddress;
    private String idracGatewayIpAddress;
    private String idracSubnetMask;

    private String esxiManagementIpAddress;
    private String esxiManagementGatewayIpAddress;
    private String esxiManagementSubnetMask;

    private String scaleIOSVMDataIpAddress1;
    private String scaleIOSVMDataIpAddress2;
    private String scaleIOSVMManagementIpAddress;
    private String hostname;

    public NodeExpansionRequest()
    {
    }

    public NodeExpansionRequest(String idracIpAddress, String idracGatewayIpAddress, String idracSubnetMask,
                                String esxiManagementIpAddress, String esxiManagementGatewayIpAddress, String esxiManagementSubnetMask,
                                String scaleIOSVMDataIpAddress1, String scaleIOSVMDataIpAddress2,
                                String scaleIOSVMManagementIpAddress) {
        this.idracIpAddress = idracIpAddress;
        this.idracGatewayIpAddress = idracGatewayIpAddress;
        this.idracSubnetMask = idracSubnetMask;
        this.esxiManagementIpAddress = esxiManagementIpAddress;
        this.esxiManagementGatewayIpAddress = esxiManagementGatewayIpAddress;
        this.esxiManagementSubnetMask = esxiManagementSubnetMask;
        this.scaleIOSVMDataIpAddress1 = scaleIOSVMDataIpAddress1;
        this.scaleIOSVMDataIpAddress2 = scaleIOSVMDataIpAddress2;
        this.scaleIOSVMManagementIpAddress = scaleIOSVMManagementIpAddress;
    }

    public String getIdracIpAddress()
    {
        return idracIpAddress;
    }

    public void setIdracIpAddress(String idracIpAddress)
    {
        this.idracIpAddress = idracIpAddress;
    }

    public String getIdracSubnetMask()
    {
        return idracSubnetMask;
    }

    public void setIdracSubnetMask(String idracSubnetMask)
    {
        this.idracSubnetMask = idracSubnetMask;
    }

    public String getIdracGatewayIpAddress()
    {
        return idracGatewayIpAddress;
    }

    public void setIdracGatewayIpAddress(String idracGatewayIpAddress)
    {
        this.idracGatewayIpAddress = idracGatewayIpAddress;
    }

    public String getEsxiManagementIpAddress()
    {
        return esxiManagementIpAddress;
    }

    public void setEsxiManagementIpAddress(String esxiManagementIpAddress)
    {
        this.esxiManagementIpAddress = esxiManagementIpAddress;
    }

    public String getEsxiManagementGatewayIpAddress()
    {
        return esxiManagementGatewayIpAddress;
    }

    public void setEsxiManagementGatewayIpAddress(String esxiManagementGatewayIpAddress)
    {
        this.esxiManagementGatewayIpAddress = esxiManagementGatewayIpAddress;
    }

    public String getEsxiManagementSubnetMask()
    {
        return esxiManagementSubnetMask;
    }

    public void setEsxiManagementSubnetMask(String esxiManagementSubnetMask) {
        this.esxiManagementSubnetMask = esxiManagementSubnetMask;
    }

    public String getScaleIOSVMDataIpAddress1()
    {
        return scaleIOSVMDataIpAddress1;
    }

    public void setScaleIOSVMDataIpAddress1(String scaleIOSVMDataIpAddress)
    {
        this.scaleIOSVMDataIpAddress1 = scaleIOSVMDataIpAddress;
    }

    public String getScaleIOSVMDataIpAddress2()
    {
        return scaleIOSVMDataIpAddress2;
    }

    public void setScaleIOSVMDataIpAddress2(String scaleIOSVMDataIpAddress)
    {
        this.scaleIOSVMDataIpAddress2 = scaleIOSVMDataIpAddress;
    }

    public String getScaleIOSVMManagementIpAddress()
    {
        return scaleIOSVMManagementIpAddress;
    }

    public void setScaleIOSVMManagementIpAddress(String scaleIOSVMManagementIpAddress)
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

    @Override
    public String toString()
    {
        return "NodeExpansionRequest{"
                + "idracIpAddress='" + idracIpAddress + '\''
                + ", idracGatewayIpAddress='" + idracGatewayIpAddress + '\''
                + ", idracSubnetMask='" + idracSubnetMask + '\''
                + ", esxiManagementIpAddress='" + esxiManagementIpAddress + '\''
                + ", esxiManagementGatewayIpAddress='" + esxiManagementGatewayIpAddress + '\''
                + ", esxiManagementSubnetMask='" + esxiManagementSubnetMask + '\''
                + ", scaleIOSVMDataIpAddress1='" + scaleIOSVMDataIpAddress1 + '\''
                + ", scaleIOSVMDataIpAddress2='" + scaleIOSVMDataIpAddress2 + '\''
                + ", scaleIOSVMManagementIpAddress='" + scaleIOSVMManagementIpAddress + '\''
                + ", hostname='" + hostname + '\'' + '}';
    }
}
