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
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DV_PORTGROUP")
public class DVPortGroup
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UUID", unique = true, nullable = false)
    private Long uuid;

    @Column(name = "DV_PORTGROUP_ID", nullable = false)
    private String id;

    @Column(name = "NAME")
    private String name;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "dvPortGroupList")
    private List<VMNetwork> vmNetworkList = new ArrayList<>();

    public DVPortGroup()
    {
    }

    public DVPortGroup(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public List<VMNetwork> getVmNetworkList()
    {
        return vmNetworkList;
    }

    public void setVmNetworkList(List<VMNetwork> vmNetworkList)
    {
        this.vmNetworkList = vmNetworkList;
    }

    public Long getUuid()
    {
        return uuid;
    }

    public void setUuid(final Long uuid)
    {
        this.uuid = uuid;
    }

}
