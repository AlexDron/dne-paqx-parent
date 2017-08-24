/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 **/

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.domain.vcenter.Cluster;
import com.dell.cpsd.paqx.dne.domain.vcenter.DVSwitch;
import com.dell.cpsd.paqx.dne.domain.vcenter.DataCenter;
import com.dell.cpsd.paqx.dne.domain.vcenter.Datastore;
import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.domain.vcenter.HostDnsConfig;
import com.dell.cpsd.paqx.dne.domain.vcenter.HostIpRouteConfig;
import com.dell.cpsd.paqx.dne.domain.vcenter.Network;
import com.dell.cpsd.paqx.dne.domain.vcenter.PciDevice;
import com.dell.cpsd.paqx.dne.domain.vcenter.PhysicalNic;
import com.dell.cpsd.paqx.dne.domain.vcenter.PortGroup;
import com.dell.cpsd.paqx.dne.domain.vcenter.VCenter;
import com.dell.cpsd.paqx.dne.domain.vcenter.VMIP;
import com.dell.cpsd.paqx.dne.domain.vcenter.VMNetwork;
import com.dell.cpsd.paqx.dne.domain.vcenter.VSwitch;
import com.dell.cpsd.paqx.dne.domain.vcenter.VirtualMachine;
import com.dell.cpsd.paqx.dne.domain.vcenter.VirtualNic;
import com.dell.cpsd.paqx.dne.domain.vcenter.VirtualNicDVPortGroup;
import com.dell.cpsd.virtualization.capabilities.api.DiscoveryResponseInfoMessage;
import com.dell.cpsd.virtualization.capabilities.api.DistributedVirtualSwicthPortConnection;
import com.dell.cpsd.virtualization.capabilities.api.DvSwitch;
import com.dell.cpsd.virtualization.capabilities.api.GuestNicInfo;
import com.dell.cpsd.virtualization.capabilities.api.HostHardwareInfo;
import com.dell.cpsd.virtualization.capabilities.api.HostPciDevice;
import com.dell.cpsd.virtualization.capabilities.api.HostSystem;
import com.dell.cpsd.virtualization.capabilities.api.HostSystemIdentificationInfo;
import com.dell.cpsd.virtualization.capabilities.api.HostSystemInfo;
import com.dell.cpsd.virtualization.capabilities.api.HostVirtualNic;
import com.dell.cpsd.virtualization.capabilities.api.HostVirtualSwitch;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class DiscoveryInfoToVCenterDomainTransformer
{

    public VCenter transform(final DiscoveryResponseInfoMessage discoveryResponseInfoMessage)
    {
        if (discoveryResponseInfoMessage == null)
        {
            return null;
        }

        // Create the VCenter object
        // TODO: Add vcenter properties
        final VCenter returnVal = new VCenter("change-me", "change-me");

        // Transform and link datacenters
        final List<DataCenter> datacenters = discoveryResponseInfoMessage.getDatacenters().stream().filter(Objects::nonNull)
                .map(datacenter -> transformDatacenter(datacenter, returnVal)).collect(Collectors.toList());
        returnVal.setDataCenterList(datacenters);

        return returnVal;
    }

    private DataCenter transformDatacenter(com.dell.cpsd.virtualization.capabilities.api.Datacenter datacenter, VCenter vCenter)
    {
        if (datacenter == null)
        {
            return null;
        }
        final DataCenter returnVal = new DataCenter();
        returnVal.setId(datacenter.getId());
        returnVal.setName(datacenter.getName());

        final List<VirtualMachine> virtualMachines = new ArrayList<>();
        if (datacenter.getVms() != null)
        {
            if (!datacenter.getVms().values().isEmpty())
            {
                // Transform vms
                virtualMachines.addAll(datacenter.getVms().values().stream().filter(Objects::nonNull).map(this::transformVirtualMachine)
                        .collect(Collectors.toList()));

            }
        }
        if (datacenter.getDatastores() != null)
        {
            if (!datacenter.getDatastores().values().isEmpty())
            {
                // Transform and link datastores
                final List<Datastore> datastores = datacenter.getDatastores().values().stream().filter(Objects::nonNull)
                        .map(datastore -> transformDatastore(datastore, returnVal, virtualMachines)).collect(Collectors.toList());
                returnVal.setDatastoreList(datastores);
            }
        }

        // Transform and link dvswitches
        if (datacenter.getDvSwitches() != null)
        {
            if (!datacenter.getDvSwitches().values().isEmpty())
            {
                final List<com.dell.cpsd.virtualization.capabilities.api.PortGroup> portGroups = datacenter.getPortgroups().values()
                        .stream().filter(Objects::nonNull).collect(Collectors.toList());

                final List<DVSwitch> dvSwitches = datacenter.getDvSwitches().values().stream().filter(Objects::nonNull)
                        .map(dvSwitch -> transformDVSwitch(dvSwitch, returnVal, portGroups)).collect(Collectors.toList());
                returnVal.setDvSwitchList(dvSwitches);
            }
        }

        if (datacenter.getClusters() != null)
        {
            if (!datacenter.getClusters().values().isEmpty())
            {
                // Transform and link clusters
                final List<Cluster> clusters = datacenter.getClusters().values().stream().filter(Objects::nonNull)
                        .map(cluster -> transformCluster(cluster, returnVal, virtualMachines)).collect(Collectors.toList());
                returnVal.setClusterList(clusters);
            }
        }

        if (datacenter.getNetworks() != null)
        {
            if (!datacenter.getNetworks().isEmpty())
            {
                // Transform and link networks
                final List<Network> networks = datacenter.getNetworks().values().stream().filter(Objects::nonNull)
                        .map(network -> transformNetwork(network, returnVal)).collect(Collectors.toList());
                returnVal.setNetworkList(networks);
            }
        }
        // TODO: Link Host Pnics to Dvswitches
        // TODO: Link Datastores to Hosts
        // TODO: Link Virtual Nics to DVPortgroups
        // TODO: Link VMGuestNetworks to DVPortgroups

        // FK link
        returnVal.setvCenter(vCenter);

        return returnVal;
    }

    private Datastore transformDatastore(com.dell.cpsd.virtualization.capabilities.api.Datastore datastore, DataCenter datacenter,
            List<VirtualMachine> virtualMachines)
    {
        if (datastore == null)
        {
            return null;
        }

        final Datastore returnVal = new Datastore();
        returnVal.setId(datastore.getId());
        returnVal.setName(datastore.getName());
        returnVal.setType(datastore.getDatastoreSummary().getType());
        returnVal.setUrl(datastore.getDatastoreSummary().getUrl());

        // One to Many Link to VMs
        final List<VirtualMachine> vmsOnDatastore = virtualMachines.stream()
                .filter(virtualMachine -> datastore.getVmIds().contains(virtualMachine.getId())).collect(Collectors.toList());
        vmsOnDatastore.forEach(virtualMachine -> virtualMachine.setDatastore(returnVal));
        returnVal.setVirtualMachineList(vmsOnDatastore);

        // FK link
        returnVal.setDataCenter(datacenter);

        return returnVal;
    }

    private DVSwitch transformDVSwitch(DvSwitch dvSwitch, DataCenter datacenter,
            List<com.dell.cpsd.virtualization.capabilities.api.PortGroup> portGroups)
    {
        if (dvSwitch == null)
        {
            return null;
        }

        final DVSwitch returnVal = new DVSwitch();
        returnVal.setId(dvSwitch.getId());
        returnVal.setName(dvSwitch.getName());
        returnVal.setAllowPromiscuous(dvSwitch.getVMwareDVSConfigInfo().getDVPortSetting().getDVSSecurityPolicy().getAllowPromicuous());

        dvSwitch.getVMwareDVSConfigInfo().getHostMembers().stream().filter(Objects::nonNull).forEach(
                distributedVirtualSwitchHostMember -> returnVal
                        .addHostMember(distributedVirtualSwitchHostMember.getDistributedVirtualSwitchHostMemberConfigInfo().getHostId()));

        final List<com.dell.cpsd.virtualization.capabilities.api.PortGroup> dvsPortGroups = portGroups.stream()
                .filter(portGroup -> dvSwitch.getPortGroupids().contains(portGroup.getId())).collect(Collectors.toList());

        // Transform and link portgroups to dvs
        final List<PortGroup> domainPortGroups = dvsPortGroups.stream().filter(Objects::nonNull)
                .map(portGroup -> transformPortgroup(portGroup, returnVal)).collect(Collectors.toList());

        // Link dvs to portgroups
        returnVal.setPortGroupList(domainPortGroups);

        // FK link
        returnVal.setDataCenter(datacenter);

        return returnVal;
    }

    private PortGroup transformPortgroup(com.dell.cpsd.virtualization.capabilities.api.PortGroup portGroup, DVSwitch dvSwitch)
    {
        if (portGroup == null)
        {
            return null;
        }

        final PortGroup returnVal = new PortGroup();
        returnVal.setId(portGroup.getId());
        returnVal.setName(portGroup.getName());
        returnVal.setAllowPromiscuous(portGroup.getDVPortgroupConfigInfo().getDVPortSetting().getDVSSecurityPolicy().getAllowPromicuous());
        returnVal.setVlanId(portGroup.getDVPortgroupConfigInfo().getDVPortSetting().getVlanId());
        returnVal.setDvSwitch(dvSwitch);

        return returnVal;
    }

    private Network transformNetwork(com.dell.cpsd.virtualization.capabilities.api.Network network, DataCenter datacenter)
    {
        if (network == null)
        {
            return null;
        }

        final Network returnVal = new Network();
        returnVal.setId(network.getId());
        returnVal.setName(network.getName());
        returnVal.setDataCenter(datacenter);

        return returnVal;
    }

    private Cluster transformCluster(com.dell.cpsd.virtualization.capabilities.api.Cluster cluster, DataCenter datacenter,
            List<VirtualMachine> virtualMachines)
    {
        if (cluster == null)
        {
            return null;
        }

        final Cluster returnVal = new Cluster();
        returnVal.setId(cluster.getId());
        returnVal.setName(cluster.getName());

        // Transform and link hosts
        if (cluster.getHosts() != null)
        {
            if (!cluster.getHosts().values().isEmpty())
            {
                final List<Host> hosts = cluster.getHosts().values().stream().filter(Objects::nonNull)
                        .map(hostSystem -> transformHost(hostSystem, returnVal, virtualMachines)).collect(Collectors.toList());
                returnVal.setHostList(hosts);
            }
        }

        // FK link
        returnVal.setDataCenter(datacenter);

        return returnVal;
    }

    private Host transformHost(HostSystem hostSystem, Cluster cluster, List<VirtualMachine> virtualMachines)
    {
        if (hostSystem == null)
        {
            return null;
        }

        final Host returnVal = new Host();
        returnVal.setId(hostSystem.getId());
        returnVal.setName(hostSystem.getName());

        returnVal.setPowerState(hostSystem.getPowerState());
        returnVal.setConnectionState(hostSystem.getConnectionState());
        returnVal.setMaintenanceMode(hostSystem.getMaintenanceMode());

        // Transform and link HostDnsConfig
        if (hostSystem.getHostConfigInfo() != null && hostSystem.getHostConfigInfo().getHostNetworkInfo() != null)
        {
            final HostDnsConfig hostDnsConfig = transformHostDnsConfig(
                    hostSystem.getHostConfigInfo().getHostNetworkInfo().getHostDnsConfig(), returnVal);

            returnVal.setHostDnsConfig(hostDnsConfig);

            // Transform and link HostIpRouteConfig
            final HostIpRouteConfig hostIpRouteConfig = transformHostIpRouteConfig(
                    hostSystem.getHostConfigInfo().getHostNetworkInfo().getHostIpRouteConfig(), returnVal);
            returnVal.setHostIpRouteConfig(hostIpRouteConfig);

            if (hostSystem.getHostConfigInfo().getHostNetworkInfo().getVswitchs() != null)
            {
                // Transform and link HostVirtualSwitch
                final List<VSwitch> vSwitches = hostSystem.getHostConfigInfo().getHostNetworkInfo().getVswitchs().stream()
                        .filter(Objects::nonNull).map(hostVirtualSwitch -> transformVSwitch(hostVirtualSwitch, returnVal))
                        .collect(Collectors.toList());
                returnVal.setvSwitchList(vSwitches);
            }

            if (hostSystem.getHostConfigInfo().getHostNetworkInfo().getVnics() != null)
            {
                // Transform and link VirtualNics
                final List<VirtualNic> virtualNics = hostSystem.getHostConfigInfo().getHostNetworkInfo().getVnics().stream()
                        .filter(Objects::nonNull).map(hostVirtualNic -> transformHostVirtualNic(hostVirtualNic, returnVal))
                        .collect(Collectors.toList());
                returnVal.setVirtualNicList(virtualNics);
            }

            if (hostSystem.getHostConfigInfo().getHostNetworkInfo().getPnics() != null)
            {
                // Transform and link VirtualNics
                final List<PhysicalNic> physicalNics = hostSystem.getHostConfigInfo().getHostNetworkInfo().getPnics().stream()
                        .filter(Objects::nonNull).map(physicalNic -> transformHostPhysicalNic(physicalNic, returnVal))
                        .collect(Collectors.toList());
                returnVal.setPhysicalNicList(physicalNics);
            }

            if (hostSystem.getHostConfigInfo().getHostDateTimeInfo() != null)
            {
                returnVal.setNtpServers(hostSystem.getHostConfigInfo().getHostDateTimeInfo().getNtpServers());
            }

        }

        final HostHardwareInfo hostHardwareInfo = hostSystem.getHostHardwareInfo();

        if (hostHardwareInfo != null)
        {
            final List<HostPciDevice> hostPciDevices = hostHardwareInfo.getPciDevice();
            if (hostPciDevices != null)
            {
                // Transform and link VirtualNics
                hostPciDevices.stream().filter(Objects::nonNull).forEach(pciDevice -> returnVal.getPciDevices().add(transformHostPciDevice(pciDevice, returnVal)));
            }

            final HostSystemInfo hostSystemInfo = hostHardwareInfo.getHostSystemInfo();
            if (hostSystemInfo != null)
            {
                final HostSystemIdentificationInfo hostSystemVerificationInfo = hostSystemInfo.getHostSystemIdentificationInfo();

                if (hostSystemVerificationInfo != null)
                {
                    returnVal.setServiceTag(hostSystemVerificationInfo.getServiceTag());
                }
            }
        }

        // One to Many Link to VMs
        final List<VirtualMachine> vmsOnHost = virtualMachines.stream()
                .filter(virtualMachine -> hostSystem.getVmIds().contains(virtualMachine.getId())).collect(Collectors.toList());
        vmsOnHost.forEach(virtualMachine -> virtualMachine.setHost(returnVal));
        returnVal.setVirtualMachineList(vmsOnHost);

        // FK Link
        returnVal.setCluster(cluster);

        return returnVal;
    }

    private PciDevice transformHostPciDevice(final HostPciDevice pciDevice, final Host returnVal)
    {
        if (pciDevice == null)
        {
            return null;
        }
        return new PciDevice(pciDevice.getDeviceId(), pciDevice.getDeviceName(), pciDevice.getVendorId(), pciDevice.getVendorName(),
                pciDevice.getSubVendorId(), returnVal);
    }

    private HostDnsConfig transformHostDnsConfig(com.dell.cpsd.virtualization.capabilities.api.HostDnsConfig hostDnsConfig, Host host)
    {
        if (hostDnsConfig == null)
        {
            return null;
        }

        final HostDnsConfig returnVal = new HostDnsConfig();
        returnVal.setDhcp(hostDnsConfig.getDhcp());
        returnVal.setDomainName(hostDnsConfig.getDomainName());
        returnVal.setHostname(hostDnsConfig.getHostName());
        returnVal.setSearchDomains(hostDnsConfig.getSearchDomains());
        returnVal.setDnsConfigIPs(hostDnsConfig.getIpAddresses());

        // FK link
        returnVal.setHost(host);

        return returnVal;
    }

    private HostIpRouteConfig transformHostIpRouteConfig(com.dell.cpsd.virtualization.capabilities.api.HostIpRouteConfig hostIpRouteConfig,
            Host host)
    {
        if (hostIpRouteConfig == null)
        {
            return null;
        }

        final HostIpRouteConfig returnVal = new HostIpRouteConfig();
        returnVal.setDefaultGateway(hostIpRouteConfig.getDefaultGateway());
        returnVal.setDefaultGatewayDevice(hostIpRouteConfig.getDefaultGatewayDevice());
        returnVal.setIpV6defaultGateway(hostIpRouteConfig.getIpV6DefaultGateway());
        returnVal.setIpV6defaultGatewayDevice(hostIpRouteConfig.getIpV6DefaultGatewayDevice());

        // FK link
        returnVal.setHost(host);

        return returnVal;
    }

    private VSwitch transformVSwitch(HostVirtualSwitch hostVirtualSwitch, Host host)
    {
        if (hostVirtualSwitch == null)
        {
            return null;
        }

        final VSwitch returnVal = new VSwitch();
        returnVal.setId(hostVirtualSwitch.getKey());
        returnVal.setName(hostVirtualSwitch.getName());
        returnVal.setAllowPromiscuous(
                hostVirtualSwitch.getHostVirtualSwitchSpec().getHostNetworkPolicy().getHostNetworkSecurityPolicy().getAllowPromiscuous());

        // FK link
        returnVal.setHost(host);

        return returnVal;
    }

    private VirtualNic transformHostVirtualNic(HostVirtualNic virtualNic, Host host)
    {
        if (virtualNic == null)
        {
            return null;
        }

        final VirtualNic returnVal = new VirtualNic();
        returnVal.setDevice(virtualNic.getDevice());
        returnVal.setPort(virtualNic.getPort());
        returnVal.setPortGroup(virtualNic.getPortGroup());
        returnVal.setMac(virtualNic.getHostVirtualNicSpec().getMac());
        returnVal.setDhcp(virtualNic.getHostVirtualNicSpec().getHostIpConfig().getDhcp());
        returnVal.setIp(virtualNic.getHostVirtualNicSpec().getHostIpConfig().getIpAddress());
        returnVal.setSubnetMask(virtualNic.getHostVirtualNicSpec().getHostIpConfig().getSubnetMask());
        returnVal.setVirtualNicDVPortGroup(
                transformVirtualNicPortGroup(returnVal, virtualNic.getHostVirtualNicSpec().getDistributedVirtualSwicthPortConnection()));

        // FK link
        returnVal.setHost(host);

        return returnVal;
    }

    private VirtualNicDVPortGroup transformVirtualNicPortGroup(final VirtualNic virtualNic,
            final DistributedVirtualSwicthPortConnection distributedVirtualSwicthPortConnection)
    {
        if (distributedVirtualSwicthPortConnection == null)
        {
            return null;
        }

        final VirtualNicDVPortGroup virtualNicDVPortGroup = new VirtualNicDVPortGroup();
        virtualNicDVPortGroup.setPort(distributedVirtualSwicthPortConnection.getPortKey());
        virtualNicDVPortGroup.setPortGroupId(distributedVirtualSwicthPortConnection.getPortGroupId());
        virtualNicDVPortGroup.setVirtualNic(virtualNic);

        return virtualNicDVPortGroup;
    }

    private PhysicalNic transformHostPhysicalNic(com.dell.cpsd.virtualization.capabilities.api.PhysicalNic physicalNic, Host host)
    {
        if (physicalNic == null)
        {
            return null;
        }

        final PhysicalNic returnVal = new PhysicalNic();
        returnVal.setDevice(physicalNic.getDevice());
        returnVal.setDriver(physicalNic.getDriver());
        returnVal.setPci(physicalNic.getPci());
        returnVal.setMac(physicalNic.getMac());

        // FK link
        returnVal.setHost(host);

        return returnVal;
    }

    private VirtualMachine transformVirtualMachine(com.dell.cpsd.virtualization.capabilities.api.VirtualMachine virtualMachine)
    {
        if (virtualMachine == null)
        {
            return null;
        }

        final VirtualMachine returnVal = new VirtualMachine();
        returnVal.setName(virtualMachine.getName());
        returnVal.setId(virtualMachine.getId());
        returnVal.setGuestHostname(virtualMachine.getGuestInfo().getHostName());
        returnVal.setGuestOS(virtualMachine.getGuestInfo().getGuestFullName());

        final List<VMNetwork> vmNetworks = virtualMachine.getGuestInfo().getNet().stream().filter(Objects::nonNull)
                .map(guestNicInfo -> transformVMNetwork(guestNicInfo, returnVal)).collect(Collectors.toList());
        returnVal.setVmNetworkList(vmNetworks);

        // No FK link to hosts and datastores at this level
        return returnVal;
    }

    private VMNetwork transformVMNetwork(GuestNicInfo guestNicInfo, VirtualMachine virtualMachine)
    {
        if (guestNicInfo == null)
        {
            return null;
        }

        final VMNetwork returnVal = new VMNetwork();
        returnVal.setConnected(guestNicInfo.getConnected());
        returnVal.setMacAddress(guestNicInfo.getMacAddress());

        final List<VMIP> vmips = guestNicInfo.getIpAddresses().stream().filter(Objects::nonNull).map(s -> transformVMIP(s, returnVal))
                .collect(Collectors.toList());
        returnVal.setVmip(vmips);

        // FK link
        returnVal.setVirtualMachine(virtualMachine);

        return returnVal;
    }

    private VMIP transformVMIP(String ip, VMNetwork vmNetwork)
    {
        if (ip == null)
        {
            return null;
        }

        final VMIP returnVal = new VMIP(ip);

        // FK link
        returnVal.setVmNetwork(vmNetwork);

        return returnVal;
    }

}
