/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.domain.vcenter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "VIRTUAL_NIC_DV_PORTGROUP")
public class VirtualNicDVPortGroup
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UUID", unique = true, nullable = false)
    private Long uuid;

    @Column(name = "PORT_KEY")
    private String port;

    @Column(name = "PORTGROUP_ID")
    private String portGroupId;

    @OneToOne
    private VirtualNic virtualNic;

    public VirtualNicDVPortGroup()
    {
        // No-arg constructor required by Hibernate
    }

    public VirtualNicDVPortGroup(final String port)
    {
        this.port = port;
    }

    public Long getUuid()
    {
        return uuid;
    }

    public void setUuid(final Long uuid)
    {
        this.uuid = uuid;
    }

    public String getPort()
    {
        return port;
    }

    public void setPort(final String port)
    {
        this.port = port;
    }

    public VirtualNic getVirtualNic()
    {
        return virtualNic;
    }

    public void setVirtualNic(final VirtualNic virtualNic)
    {
        this.virtualNic = virtualNic;
    }

    public String getPortGroupId()
    {
        return portGroupId;
    }

    public void setPortGroupId(final String portGroupId)
    {
        this.portGroupId = portGroupId;
    }

    @Override
    public String toString()
    {
        return "VirtualNicDVPortGroup{" + "uuid=" + uuid + ", port='" + port + '\'' + '}';
    }
}
