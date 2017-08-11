package com.dell.cpsd.paqx.dne.transformers;

import com.dell.converged.capabilities.compute.discovered.nodes.api.BootImageNetworkAddressV4;
import com.dell.converged.capabilities.compute.discovered.nodes.api.BootImageNetworkAddressV6;
import com.dell.converged.capabilities.compute.discovered.nodes.api.BootImageNetworkDevice;
import com.dell.converged.capabilities.compute.discovered.nodes.api.EsxiInstallationInfo;
import com.dell.converged.capabilities.compute.discovered.nodes.api.NodeWorkflowSwitchDevice;
import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.domain.vcenter.HostDnsConfig;
import com.dell.cpsd.paqx.dne.domain.vcenter.HostIpRouteConfig;
import com.dell.cpsd.paqx.dne.domain.vcenter.PortGroup;
import com.dell.cpsd.paqx.dne.domain.vcenter.VirtualNic;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * TODO: Document Usage
 * <p/>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * <p/>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
public class HostToInstallEsxiRequestTransformer
{
    private static final Logger LOG                   = LoggerFactory.getLogger(HostToInstallEsxiRequestTransformer.class);
    private static final String OS_NAME_ESXI          = "ESXi";
    private static final String REPO_SUFFIX           = "/esxi/6.5/";
    private static final String EMPTY_STRING          = "";
    private static final String VERSION               = "6.0";
    private static final String DEFAULT_ROOT_PASSWORD = "";
    private static final String DELL_NODE_KARGS       = "netdevice=vmnic0";
    private final DataServiceRepository dataServiceRepository;

    public HostToInstallEsxiRequestTransformer(final DataServiceRepository dataServiceRepository)
    {
        this.dataServiceRepository = dataServiceRepository;
    }

    public EsxiInstallationInfo transformInstallEsxiData(final String hostname, final String nodeId) throws IllegalArgumentException
    {
        if (hostname == null)
        {
            throw new IllegalArgumentException("Hostname is null");
        }

        if (nodeId == null)
        {
            throw new IllegalArgumentException("Node Id is null");
        }

        Host host = null;

        try
        {
            host = dataServiceRepository.getVCenterHost(hostname);
        }
        catch (NoResultException e)
        {
            throw new IllegalArgumentException("No Host found for the host with host name" + hostname);
        }

        return buildEsxiInstallData(host, nodeId);
    }

    protected EsxiInstallationInfo buildEsxiInstallData(final Host host, final String nodeId)
    {
        final EsxiInstallationInfo esxiInstallationInfo = new EsxiInstallationInfo();

        esxiInstallationInfo.setIdentifier(nodeId);

        //Specific to Dell Node
        esxiInstallationInfo.setKargs(DELL_NODE_KARGS);
        esxiInstallationInfo.setVersion(VERSION);
        //The default root password as per the document
        esxiInstallationInfo.setRootPassword(DEFAULT_ROOT_PASSWORD);

        // Based on any existing host in the vcenter
        transformHostDnsConfig(esxiInstallationInfo, host.getHostDnsConfig());
        // Based on any existing host in the vcenter
        transformNetworkDevices(esxiInstallationInfo, host.getHostIpRouteConfig());
        // Based on any existing host in the vcenter
        esxiInstallationInfo.setNtpServers(host.getNtpServers());

        //As per the documentation
        transformSwitchDevices(esxiInstallationInfo);

        return esxiInstallationInfo;
    }

    protected void transformHostDnsConfig(final EsxiInstallationInfo esxiInstallationInfo, final HostDnsConfig hostDnsConfig)

    {
        if (hostDnsConfig != null)
        {
            esxiInstallationInfo.setHostname(hostDnsConfig.getHostname());

            final List<String> searchDomains = hostDnsConfig.getSearchDomains();

            if (searchDomains != null && !searchDomains.isEmpty())
            {
                esxiInstallationInfo.setDomain(searchDomains.get(0));
            }
            else
            {
                esxiInstallationInfo.setDomain(hostDnsConfig.getDomainName());
            }

            esxiInstallationInfo.setDnsServers(hostDnsConfig.getDnsConfigIPs());
        }
    }

    //TODO: Check if this is correct
    protected void transformNetworkDevices(final EsxiInstallationInfo esxiInstallationInfo, final HostIpRouteConfig hostIpRouteConfig)

    {
        if (hostIpRouteConfig != null)
        {
            // Create the first device with static info
            final BootImageNetworkDevice bootImageNetworkDevice = new BootImageNetworkDevice();
            bootImageNetworkDevice.setDevice("vmnic0");
            bootImageNetworkDevice.setEsxSwitchName("vSwitch0");

            final BootImageNetworkAddressV4 bootImageNetworkAddressV4 = new BootImageNetworkAddressV4();

            bootImageNetworkAddressV4.setGateway(hostIpRouteConfig.getDefaultGateway());

            final VirtualNic hostVirtualNic = hostIpRouteConfig.getHost().getVirtualNicList().stream().filter(Objects::nonNull)
                    .filter(virtualNic -> virtualNic.getDevice().equals("vmk0")).findFirst().orElse(null);

            if (hostVirtualNic != null)
            {
                // Get the ip of the nic on the management network
                bootImageNetworkAddressV4.setIpAddr(hostVirtualNic.getIp());
                bootImageNetworkAddressV4.setNetmask(hostVirtualNic.getSubnetMask());

                if (hostVirtualNic.getVirtualNicDVPortGroup() != null)
                {
                    transformNetworkDeviceVlanId(bootImageNetworkAddressV4, hostVirtualNic.getVirtualNicDVPortGroup().getPortGroupId());
                }
            }

            esxiInstallationInfo.setNetworkDevices(Collections.singletonList(bootImageNetworkDevice));
        }
    }

    protected void transformNetworkDeviceVlanId(final BootImageNetworkAddressV4 ipV4Network, String portGroupId)
    {
        final List<PortGroup> portGroups = dataServiceRepository.getPortGroups();
        final PortGroup portGroup = portGroups.stream().filter(Objects::nonNull)
                .filter(portGroup1 -> portGroup1.getId().equals(portGroupId)).findFirst().orElse(null);

        if (portGroup != null)
        {
            final BigDecimal vlanId = new BigDecimal(portGroup.getVlanId());
            ipV4Network.setVlanIds(Collections.singletonList(vlanId));
        }
    }

    protected void transformSwitchDevices(final EsxiInstallationInfo esxiInstallationInfo)
    {
        // This is all static as well for now
        final NodeWorkflowSwitchDevice switchDevice = new NodeWorkflowSwitchDevice("vSwitch0", "iphash", Arrays.asList("vmnic1", "vmnic5"));

        esxiInstallationInfo.setSwitchDevices(Collections.singletonList(switchDevice));
    }
}
