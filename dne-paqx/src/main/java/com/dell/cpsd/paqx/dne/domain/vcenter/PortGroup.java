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
@Table(name = "PORTGROUP")
public class PortGroup
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UUID", unique = true, nullable = false)
    private Long uuid;

    @Column(name = "PORTGROUP_ID", nullable = false)
    private String id;

    @Column(name = "PORTGROUP_NAME")
    private String name;

    @Column(name = "VLAN_ID")
    private String vlanId;

    @Column(name = "ALLOW_PROMISCUOUS")
    private boolean allowPromiscuous;

    @ManyToOne(cascade = CascadeType.ALL)
    private DVSwitch dvSwitch;

    public PortGroup()
    {
    }

    public Long getUuid()
    {
        return uuid;
    }

    public void setUuid(final Long uuid)
    {
        this.uuid = uuid;
    }

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getVlanId()
    {
        return vlanId;
    }

    public void setVlanId(final String vlanId)
    {
        this.vlanId = vlanId;
    }

    public boolean isAllowPromiscuous()
    {
        return allowPromiscuous;
    }

    public void setAllowPromiscuous(final boolean allowPromiscuous)
    {
        this.allowPromiscuous = allowPromiscuous;
    }

    public DVSwitch getDvSwitch()
    {
        return dvSwitch;
    }

    public void setDvSwitch(final DVSwitch dvSwitch)
    {
        this.dvSwitch = dvSwitch;
    }
}
