/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.domain.vcenter;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "HOST")
public class Host
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UUID", unique = true, nullable = false)
    private Long uuid;

    @Column(name = "HOST_ID", unique = true, nullable = false)
    private String id;

    @Column(name = "HOST_NAME")
    private String name;

    @Column(name = "POWER_STATE")
    private String powerState;

    @Column(name = "CONNECTION_STATE")
    private String connectionState;

    @Column(name = "SERVICE_TAG")
    private String serviceTag;

    @Column(name = "MAINTENANCE_MODE")
    private boolean maintenanceMode;

    @ManyToOne(cascade = CascadeType.ALL)
    private Cluster cluster;

    @ElementCollection
    @CollectionTable(name = "NTP_SERVERS", joinColumns = @JoinColumn(name = "UUID"))
    private List<String> ntpServers;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "host", orphanRemoval = true)
    private List<VSwitch> vSwitchList = new ArrayList<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "host", orphanRemoval = true)
    private List<VirtualNic> virtualNicList = new ArrayList<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "host", orphanRemoval = true)
    private List<PhysicalNic> physicalNicList = new ArrayList<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "host", orphanRemoval = true)
    private List<VirtualMachine> virtualMachineList = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "host", orphanRemoval = true)
    private HostIpRouteConfig hostIpRouteConfig;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "host", orphanRemoval = true)
    private HostDnsConfig hostDnsConfig;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "host", orphanRemoval = true)
    private List<VibUrl> vibUrls = new ArrayList<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "host", orphanRemoval = true)
    private List<PciDevice> pciDevices = new ArrayList<>();

    public Host()
    {
    }

    public Host(String id, String name, String powerState, String connectionState, String serviceTag, boolean maintenanceMode)
    {
        this.id = id;
        this.name = name;
        this.powerState = powerState;
        this.connectionState = connectionState;
        this.serviceTag = serviceTag;
        this.maintenanceMode = maintenanceMode;
    }

    public Long getUuid()
    {
        return uuid;
    }

    public void setUuid(Long uuid)
    {
        this.uuid = uuid;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPowerState()
    {
        return powerState;
    }

    public void setPowerState(String powerState)
    {
        this.powerState = powerState;
    }

    public String getConnectionState()
    {
        return connectionState;
    }

    public void setConnectionState(final String connectionState)
    {
        this.connectionState = connectionState;
    }

    public String getServiceTag()
    {
        return serviceTag;
    }

    public void setServiceTag(final String serviceTag)
    {
        this.serviceTag = serviceTag;
    }

    public boolean isMaintenanceMode()
    {
        return maintenanceMode;
    }

    public void setMaintenanceMode(final boolean maintenanceMode)
    {
        this.maintenanceMode = maintenanceMode;
    }

    public Cluster getCluster()
    {
        return cluster;
    }

    public void setCluster(Cluster cluster)
    {
        this.cluster = cluster;
    }

    public List<String> getNtpServers()
    {
        return ntpServers;
    }

    public void setNtpServers(final List<String> ntpServers)
    {
        this.ntpServers = ntpServers;
    }

    public List<VSwitch> getvSwitchList()
    {
        return vSwitchList;
    }

    public void addVSwitch(VSwitch vSwitch)
    {
        this.vSwitchList.add(vSwitch);
    }

    public void setvSwitchList(List<VSwitch> vSwitchList)
    {
        this.vSwitchList = vSwitchList;
    }

    public List<VirtualNic> getVirtualNicList()
    {
        return virtualNicList;
    }

    public void addVirtualNic(VirtualNic virtualNic)
    {
        this.virtualNicList.add(virtualNic);
    }

    public void setVirtualNicList(List<VirtualNic> virtualNicList)
    {
        this.virtualNicList = virtualNicList;
    }

    public HostIpRouteConfig getHostIpRouteConfig()
    {
        return hostIpRouteConfig;
    }

    public void setHostIpRouteConfig(HostIpRouteConfig hostIpRouteConfig)
    {
        this.hostIpRouteConfig = hostIpRouteConfig;
    }

    public HostDnsConfig getHostDnsConfig()
    {
        return hostDnsConfig;
    }

    public void setHostDnsConfig(HostDnsConfig hostDnsConfig)
    {
        this.hostDnsConfig = hostDnsConfig;
    }

    public List<PhysicalNic> getPhysicalNicList()
    {
        return physicalNicList;
    }

    public void setPhysicalNicList(List<PhysicalNic> physicalNicList)
    {
        this.physicalNicList = physicalNicList;
    }

    public List<VirtualMachine> getVirtualMachineList()
    {
        return virtualMachineList;
    }

    public void setVirtualMachineList(List<VirtualMachine> virtualMachineList)
    {
        this.virtualMachineList = virtualMachineList;
    }

    public List<VibUrl> getVibUrls()
    {
        return vibUrls;
    }

    public void setVibUrls(final List<VibUrl> vibUrls)
    {
        this.vibUrls = vibUrls;
    }

    public List<PciDevice> getPciDevices()
    {
        return pciDevices;
    }

    public void setPciDevices(final List<PciDevice> pciDevices)
    {
        this.pciDevices = pciDevices;
    }
}
