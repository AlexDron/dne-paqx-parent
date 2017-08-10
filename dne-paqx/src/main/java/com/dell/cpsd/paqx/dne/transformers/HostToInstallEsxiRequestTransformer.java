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
    private static final String DEFAULT_ROOT_PASSWORD = "OurRandomString1!";
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

        transformHostDnsConfig(esxiInstallationInfo, host.getHostDnsConfig());

        // THIS IS ALL STATIC CONFIG DATA FOR NOW ---- WILL CHANGE
        esxiInstallationInfo.setKargs("netdevice=vmnic2");
        esxiInstallationInfo.setVersion(VERSION);
        esxiInstallationInfo.setRootPassword(DEFAULT_ROOT_PASSWORD);

        transformNetworkDevices(esxiInstallationInfo, host.getHostIpRouteConfig());
        transformSwitchDevices(esxiInstallationInfo);

        esxiInstallationInfo.setNtpServers(host.getNtpServers());

        return esxiInstallationInfo;
    }

    protected void transformHostDnsConfig(final EsxiInstallationInfo esxiInstallationInfo, final HostDnsConfig hostDnsConfig)

    {
        if (hostDnsConfig != null)
        {
            esxiInstallationInfo.setHostname(hostDnsConfig.getHostname());

            if (hostDnsConfig.getDomainName().isEmpty())
            {
                //TODO: validate logic about search domains (grab first one for now)
                esxiInstallationInfo.setDomain(hostDnsConfig.getSearchDomains().get(0));
            }
            else
            {
                esxiInstallationInfo.setDomain(hostDnsConfig.getDomainName());
            }

            esxiInstallationInfo.setDnsServers(hostDnsConfig.getDnsConfigIPs());
        }
    }

    protected void transformNetworkDevices(final EsxiInstallationInfo esxiInstallationInfo, final HostIpRouteConfig hostIpRouteConfig)

    {
        if (hostIpRouteConfig != null)
        {
            /* Example based off of test -> Still flushing out details
            "networkDevices": [{
				"device": "vmnic0",
				"ipv4": {
					"ipAddr": "10.239.140.82",
					"gateway": "10.239.140.65",
					"netmask": "255.255.255.192",
					"vlanIds": [1639]
				},
				"esxSwitchName": "vSwitch0"
			}, {
				"device": "vmnic1",
				"esxSwitchName": "vSwitch1"
			}, {
				"device": "vmnic2",
				"esxSwitchName": "vSwitch2"
			}],
             */

            // Create the first device with static info
            final BootImageNetworkDevice bootImageNetworkDevice = new BootImageNetworkDevice();
            bootImageNetworkDevice.setDevice("vmnic0"); // Specific to how RHD parses
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

            // Create the 2nd and 3rd
            final BootImageNetworkDevice bootImageNetworkDevice1 = new BootImageNetworkDevice();
            bootImageNetworkDevice1.setDevice("vmnic1");
            bootImageNetworkDevice1.setEsxSwitchName("vSwitch1");

            final BootImageNetworkDevice bootImageNetworkDevice2 = new BootImageNetworkDevice();
            bootImageNetworkDevice2.setDevice("vmnic2");
            bootImageNetworkDevice2.setEsxSwitchName("vSwitch2");

            esxiInstallationInfo.setNetworkDevices(Arrays.asList(bootImageNetworkDevice, bootImageNetworkDevice1, bootImageNetworkDevice2));
        }
    }

    protected void transformNetworkDeviceVlanId(final BootImageNetworkAddressV4 ipV4Network, String portGroupId)
    {
        List<PortGroup> portGroups = dataServiceRepository.getPortGroups();
        PortGroup portGroup = portGroups.stream().filter(Objects::nonNull).filter(portGroup1 -> portGroup1.getId().equals(portGroupId))
                .findFirst().orElse(null);

        if (portGroup != null)
        {
            BigDecimal vlanId = new BigDecimal(portGroup.getVlanId());
            ipV4Network.setVlanIds(Arrays.asList(vlanId));
        }
    }

    protected void transformSwitchDevices(final EsxiInstallationInfo esxiInstallationInfo)
    {
        // This is all static as well for now
        NodeWorkflowSwitchDevice switchDevice = new NodeWorkflowSwitchDevice("vSwitch0", "iphash", Arrays.asList("vmnic0", "vmnic3"));
        NodeWorkflowSwitchDevice switchDevice1 = new NodeWorkflowSwitchDevice().withSwitchName("vSwitch1")
                .withUplinks(Arrays.asList("vmnic1"));
        NodeWorkflowSwitchDevice switchDevice2 = new NodeWorkflowSwitchDevice().withSwitchName("vSwitch2")
                .withUplinks(Arrays.asList("vmnic2"));

        esxiInstallationInfo.setSwitchDevices(Arrays.asList(switchDevice, switchDevice1, switchDevice2));
    }

    protected List<BootImageNetworkDevice> transformNetworkDevices(final HostIpRouteConfig hostIpRouteConfig)
    {
        if (hostIpRouteConfig != null)
        {
            final BootImageNetworkDevice bootImageNetworkDevice = new BootImageNetworkDevice();
            bootImageNetworkDevice.setDevice(hostIpRouteConfig.getDefaultGatewayDevice());

            final BootImageNetworkAddressV4 bootImageNetworkAddressV4 = new BootImageNetworkAddressV4();
            bootImageNetworkAddressV4.setGateway(hostIpRouteConfig.getDefaultGateway());

            final BootImageNetworkAddressV6 bootImageNetworkAddressV6 = new BootImageNetworkAddressV6();
            bootImageNetworkAddressV6.setGateway(hostIpRouteConfig.getIpV6defaultGateway());

            final VirtualNic virtualNicDomain = hostIpRouteConfig.getHost().getVirtualNicList().stream().filter(Objects::nonNull)
                    .filter(virtualNic -> {
                        if (hostIpRouteConfig.getDefaultGatewayDevice() != null)
                        {
                            return hostIpRouteConfig.getDefaultGatewayDevice().equalsIgnoreCase(virtualNic.getDevice());
                        }
                        return false;
                    }).findFirst().orElse(null);
            if (virtualNicDomain != null)
            {
                bootImageNetworkAddressV4.setNetmask(virtualNicDomain.getSubnetMask());
                bootImageNetworkAddressV6.setNetmask(virtualNicDomain.getSubnetMask());
            }

            bootImageNetworkDevice.setBootImageNetworkAddressV4(bootImageNetworkAddressV4);
            bootImageNetworkDevice.setBootImageNetworkAddressV6(bootImageNetworkAddressV6);

            return new ArrayList<>(Arrays.asList(bootImageNetworkDevice));
        }

        return null;
    }
}
