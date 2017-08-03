/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.domain.vcenter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "VM_IP")
public class VMIP
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UUID", unique = true, nullable = false)
    private Long uuid;

    @Column(name = "IP_ADDRESS")
    private String ipAddress;

    @ManyToOne(cascade = CascadeType.ALL)
    private VMNetwork vmNetwork;

    public VMIP()
    {
    }

    public VMIP(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public Long getUuid()
    {
        return uuid;
    }

    public void setUuid(Long uuid)
    {
        this.uuid = uuid;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public VMNetwork getVmNetwork()
    {
        return vmNetwork;
    }

    public void setVmNetwork(VMNetwork vmNetwork)
    {
        this.vmNetwork = vmNetwork;
    }
}
