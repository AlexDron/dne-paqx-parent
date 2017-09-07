package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.BootImageNetworkAddressV4;
import com.dell.cpsd.BootImageNetworkDevice;
import com.dell.cpsd.EsxiInstallationInfo;
import com.dell.cpsd.NodeWorkflowSwitchDevice;
import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.domain.vcenter.HostDnsConfig;
import com.dell.cpsd.paqx.dne.domain.vcenter.HostIpRouteConfig;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.model.IpV4Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Install ESXi Request Info Transformer class.
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
    private static final String VERSION               = "6.0";
    private static final String DELL_NODE_KARGS       = "netdevice=vmnic2";
    private final DataServiceRepository dataServiceRepository;

    public HostToInstallEsxiRequestTransformer(final DataServiceRepository dataServiceRepository)
    {
        this.dataServiceRepository = dataServiceRepository;
    }

    public EsxiInstallationInfo transformInstallEsxiData(final String hostname, final String nodeId,
            final IpV4Configuration ipv4Configuration) throws IllegalArgumentException
    {
        if (hostname == null)
        {
            LOG.error("Hostname is null");
            throw new IllegalArgumentException("Hostname is null");
        }

        if (nodeId == null)
        {
            LOG.error("Node Id is null");
            throw new IllegalArgumentException("Node Id is null");
        }

        Host host = null;

        try
        {
            host = dataServiceRepository.getVCenterHost(hostname);
        }
        catch (NoResultException e)
        {
            LOG.error("No Host found for the host with host name [{}]", hostname);
            throw new IllegalArgumentException("No Host found for the host with host name" + hostname);
        }

        return buildEsxiInstallData(host, nodeId, ipv4Configuration);
    }

    protected EsxiInstallationInfo buildEsxiInstallData(final Host host, final String nodeId, final IpV4Configuration ipv4Configuration)
    {
        final EsxiInstallationInfo esxiInstallationInfo = new EsxiInstallationInfo();

        esxiInstallationInfo.setIdentifier(nodeId);

        //Specific to Dell Node
        esxiInstallationInfo.setKargs(DELL_NODE_KARGS);
        esxiInstallationInfo.setVersion(VERSION);

        // Based on any existing host in the vcenter
        transformHostDnsConfig(esxiInstallationInfo, host.getHostDnsConfig());
        // Based on any existing host in the vcenter
        transformNetworkDevices(esxiInstallationInfo, host.getHostIpRouteConfig(), ipv4Configuration);
        // Based on any existing host in the vcenter
        esxiInstallationInfo.setNtpServers(host.getNtpServers());

        //As per the documentation
        transformSwitchDevices(esxiInstallationInfo);

        esxiInstallationInfo.setNtpServers(host.getNtpServers());

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

    protected void transformNetworkDevices(final EsxiInstallationInfo esxiInstallationInfo, final HostIpRouteConfig hostIpRouteConfig,
            final IpV4Configuration ipv4Configuration)

    {
        if (hostIpRouteConfig != null)
        {

            final BootImageNetworkDevice bootImageNetworkDevice1 = new BootImageNetworkDevice();
            bootImageNetworkDevice1.setDevice("vmnic0");
            bootImageNetworkDevice1.setEsxSwitchName("vSwitch0");

            final BootImageNetworkAddressV4 bootImageNetworkAddressV4 = new BootImageNetworkAddressV4();
            bootImageNetworkAddressV4.setIpAddr(ipv4Configuration.getEsxiManagementIpAddress());
            bootImageNetworkAddressV4.setGateway(ipv4Configuration.getEsxiManagementGateway());
            bootImageNetworkAddressV4.setNetmask(ipv4Configuration.getEsxiManagementNetworkMask());
            bootImageNetworkAddressV4.setVlanIds(transformNetworkDeviceVlanId());
            bootImageNetworkDevice1.setBootImageNetworkAddressV4(bootImageNetworkAddressV4);

            final BootImageNetworkDevice bootImageNetworkDevice2 = new BootImageNetworkDevice();
            bootImageNetworkDevice2.setDevice("vmnic1");
            bootImageNetworkDevice2.setEsxSwitchName("vSwitch1");

            final BootImageNetworkDevice bootImageNetworkDevice3 = new BootImageNetworkDevice();
            bootImageNetworkDevice3.setDevice("vmnic2");
            bootImageNetworkDevice3.setEsxSwitchName("vSwitch2");

            esxiInstallationInfo
                    .setNetworkDevices(Arrays.asList(bootImageNetworkDevice1, bootImageNetworkDevice3, bootImageNetworkDevice3));
        }
    }

    protected List<BigDecimal> transformNetworkDeviceVlanId()
    {

        final String vlanIdVmk0 = dataServiceRepository.getVlanIdVmk0();

        if (vlanIdVmk0 != null)
        {
            return new ArrayList<>(Collections.singletonList(new BigDecimal(vlanIdVmk0)));
        }

        return null;
    }

    protected void transformSwitchDevices(final EsxiInstallationInfo esxiInstallationInfo)
    {
        final NodeWorkflowSwitchDevice switchDevice1 = new NodeWorkflowSwitchDevice("vSwitch0", "iphash",
                Collections.singletonList("vmnic0"));
        final NodeWorkflowSwitchDevice switchDevice2 = new NodeWorkflowSwitchDevice("vSwitch1", "", Collections.singletonList("vmnic1"));
        final NodeWorkflowSwitchDevice switchDevice3 = new NodeWorkflowSwitchDevice("vSwitch2", "", Collections.singletonList("vmnic2"));
        esxiInstallationInfo.setSwitchDevices(Arrays.asList(switchDevice1, switchDevice2, switchDevice3));
    }
}
