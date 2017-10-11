/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.domain.vcenter.HostDnsConfig;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.DeployScaleIoVmTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.InstallEsxiTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.DeployVMFromTemplateRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.NicSetting;
import com.dell.cpsd.virtualization.capabilities.api.VirtualMachineCloneSpec;
import com.dell.cpsd.virtualization.capabilities.api.VirtualMachineConfigSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * TODO: Document Usage
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class DeployScaleIoVmTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DeployScaleIoVmTaskHandler.class);

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    private static final String SCALEIO_VM_PREFIX        = "ScaleIO-";
    private static final String SCALEIO_TEMPLATE_VM_NAME = "EMC ScaleIO SVM Template.*";
    private static final int    SCALEIO_VM_NUM_CPU       = 8;
    private static final int    SCALEIO_VM_RAM           = 8192;
    private static final long   SLEEP_PERIOD             = 30000L;
    
    /**
     * The <code>DataServiceRepository</code> instance
     */
    private final DataServiceRepository repository;

    public DeployScaleIoVmTaskHandler(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute Deploy ScaleIO VM From Template task");

        final DeployScaleIoVmTaskResponse response = initializeResponse(job);

        try
        {
            final Validate validate = new Validate(job).invoke();
            final ComponentEndpointIds componentEndpointIds = validate.getComponentEndpointIds();
            final String hostname = validate.getHostname();
            final String dataCenterName = validate.getDataCenterName();
            final String newScaleIoVmName = validate.getNewScaleIoVmName();
            final String newScaleIoVmHostname = validate.getNewScaleIoVmHostname();
            final String domainName = validate.getNewScaleIoVmDomainName();
            final List<String> dnsServers = validate.getNewScaleIoVmDnsServers();
            final List<NicSetting> nicSettings = validate.getNewScaleIoVmNicSettings();

            final VirtualMachineCloneSpec virtualMachineCloneSpec = new VirtualMachineCloneSpec();
            virtualMachineCloneSpec.setPoweredOn(false);
            virtualMachineCloneSpec.setTemplate(false);
            virtualMachineCloneSpec.setDomain(domainName);

            final VirtualMachineConfigSpec virtualMachineConfigSpec = new VirtualMachineConfigSpec();
            virtualMachineConfigSpec.setHostName(newScaleIoVmHostname);
            virtualMachineConfigSpec.setNumCPUs(SCALEIO_VM_NUM_CPU);
            virtualMachineConfigSpec.setMemoryMB(SCALEIO_VM_RAM);
            virtualMachineConfigSpec.setDnsServerList(dnsServers);
            virtualMachineConfigSpec.setNicSettings(nicSettings);
            virtualMachineCloneSpec.setVirtualMachineConfigSpec(virtualMachineConfigSpec);

            final DeployVMFromTemplateRequestMessage requestMessage = new DeployVMFromTemplateRequestMessage();
            requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
            requestMessage.setComponentEndpointIds(
                    new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                            componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
            requestMessage.setHostName(hostname);
            requestMessage.setTemplateName(SCALEIO_TEMPLATE_VM_NAME);
            requestMessage.setDatacenterName(dataCenterName);
            requestMessage.setNewVMName(newScaleIoVmName);
            requestMessage.setVirtualMachineCloneSpec(virtualMachineCloneSpec);

            Thread.sleep(SLEEP_PERIOD);// TODO for test purpose

            final boolean succeeded = this.nodeService.requestDeployScaleIoVm(requestMessage);

            if (!succeeded)
            {
                throw new IllegalStateException("Request deploy ScaleIO VM failed");
            }

            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            response.setNewVMName(newScaleIoVmName);
            return true;
        }
        catch (Exception e)
        {
            LOGGER.error("Error deploying ScaleIO VM", e);
            response.addError(e.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }

    @Override
    public DeployScaleIoVmTaskResponse initializeResponse(Job job)
    {
        final DeployScaleIoVmTaskResponse response = new DeployScaleIoVmTaskResponse();
        response.setWorkFlowTaskName(job.getCurrentTask().getTaskName());
        response.setWorkFlowTaskStatus(Status.IN_PROGRESS);
        job.addTaskResponse(job.getStep(), response);

        return response;
    }

    private class Validate
    {
        private final InstallEsxiTaskResponse installEsxiTaskResponse;
        private final NodeExpansionRequest    inputParams;
        private       ComponentEndpointIds    componentEndpointIds;
        private       String                  hostname;
        private       String                  dataCenterName;
        private       String                  newScaleIoVmIpAddress;
        private       String                  newScaleIoVmName;
        private       String                  newScaleIoVmHostname;
        private       String                  newScaleIoVmDomainName;
        private       List<String>            newScaleIoVmDnsServers;
        private       List<NicSetting>        newScaleIoVmNicSettings;

        Validate(final Job job)
        {
            this.installEsxiTaskResponse = (InstallEsxiTaskResponse) job.getTaskResponseMap().get("installEsxi");

            if (this.installEsxiTaskResponse == null)
            {
                throw new IllegalStateException("No Install ESXi task response found");
            }

            this.inputParams = job.getInputParams();

            if (this.inputParams == null)
            {
                throw new IllegalStateException("Job Input Params are null");
            }
        }

        Validate invoke()
        {
            this.componentEndpointIds = this.queryComponentEndpointIds();
            this.hostname = this.queryHostname();
            this.dataCenterName = this.queryDatacenterName();
            this.newScaleIoVmIpAddress = this.queryScaleIOSVMManagementIpAddress();
            this.newScaleIoVmName = SCALEIO_VM_PREFIX + this.newScaleIoVmIpAddress;
            this.newScaleIoVmHostname = this.newScaleIoVmName.replace(".", "-");
            this.newScaleIoVmDomainName = this.queryDomainName();
            this.newScaleIoVmDnsServers = this.queryDnsConfigIps();
            this.newScaleIoVmNicSettings = this.buildNicSettingsList();
            return this;
        }

        ComponentEndpointIds getComponentEndpointIds()
        {
            return componentEndpointIds;
        }

        String getHostname()
        {
            return hostname;
        }

        String getDataCenterName()
        {
            return dataCenterName;
        }

        String getNewScaleIoVmName()
        {
            return newScaleIoVmName;
        }

        String getNewScaleIoVmHostname()
        {
            return newScaleIoVmHostname;
        }

        String getNewScaleIoVmDomainName()
        {
            return newScaleIoVmDomainName;
        }

        List<String> getNewScaleIoVmDnsServers()
        {
            return newScaleIoVmDnsServers;
        }

        List<NicSetting> getNewScaleIoVmNicSettings()
        {
            return newScaleIoVmNicSettings;
        }

        ComponentEndpointIds queryComponentEndpointIds()
        {
            final ComponentEndpointIds ids = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");

            if (ids == null)
            {
                throw new IllegalStateException("No VCenter components found.");
            }

            return ids;
        }

        String queryHostname()
        {
            final String hostName = this.installEsxiTaskResponse.getHostname();

            if (StringUtils.isEmpty(hostName))
            {
                throw new IllegalStateException("Hostname is null");
            }

            return hostName;
        }

        String queryScaleIOSVMManagementIpAddress()
        {
            final String scaleIOSVMManagementIpAddress = this.inputParams.getScaleIoSvmManagementIpAddress();

            if (StringUtils.isEmpty(scaleIOSVMManagementIpAddress))
            {
                throw new IllegalStateException("ScaleIO Management IP Address is null");
            }

            return scaleIOSVMManagementIpAddress;
        }

        String queryDatacenterName()
        {
            final String clusterName = this.inputParams.getClusterName();

            if (StringUtils.isEmpty(clusterName))
            {
                throw new IllegalStateException("Cluster name is null");
            }

            String dcName = repository.getDataCenterName(clusterName);

            if (StringUtils.isEmpty(dcName))
            {
                throw new IllegalStateException("DataCenter name is null");
            }

            return dcName;
        }

        String queryDomainName()
        {
            final String domainName = repository.getDomainName();

            if (StringUtils.isEmpty(domainName))
            {
                throw new IllegalStateException("Domain name is null");
            }

            return domainName;
        }

        List<String> queryDnsConfigIps()
        {
            final Host existingVCenterHost = repository.getExistingVCenterHost();
            final HostDnsConfig hostDnsConfig = existingVCenterHost.getHostDnsConfig();
            List<String> dnsConfigIps = hostDnsConfig.getDnsConfigIPs();

            if (CollectionUtils.isEmpty(dnsConfigIps))
            {
                throw new IllegalStateException("No DNS config IPs");
            }

            return dnsConfigIps;
        }

        List<NicSetting> buildNicSettingsList()
        {
            final String esxiManagementGatewayIpAddress = this.inputParams.getEsxiManagementGatewayIpAddress();

            if (StringUtils.isEmpty(esxiManagementGatewayIpAddress))
            {
                throw new IllegalStateException("ESXi Management Gateway IP Address is null");
            }

            final String scaleIoSvmManagementIpAddress = this.inputParams.getScaleIoSvmManagementIpAddress();

            if (StringUtils.isEmpty(scaleIoSvmManagementIpAddress))
            {
                throw new IllegalStateException("ScaleIO VM Management IP Address is null");
            }

            final String scaleIoSvmManagementSubnetMask = this.inputParams.getScaleIoSvmManagementSubnetMask();

            if (StringUtils.isEmpty(scaleIoSvmManagementSubnetMask))
            {
                throw new IllegalStateException("ScaleIO VM Management Subnet Mask is null");
            }

            final String scaleIoData1SvmIpAddress = this.inputParams.getScaleIoData1SvmIpAddress();

            if (StringUtils.isEmpty(scaleIoData1SvmIpAddress))
            {
                throw new IllegalStateException("ScaleIO Data1 IP Address is null");
            }

            final String scaleIoData1KernelAndSvmSubnetMask = this.inputParams.getScaleIoData1KernelAndSvmSubnetMask();

            if (StringUtils.isEmpty(scaleIoData1KernelAndSvmSubnetMask))
            {
                throw new IllegalStateException("ScaleIO VM Data1 Subnet Mask is null");
            }

            final String scaleIoData2SvmIpAddress = this.inputParams.getScaleIoData2SvmIpAddress();

            if (StringUtils.isEmpty(scaleIoData2SvmIpAddress))
            {
                throw new IllegalStateException("ScaleIO Data2 IP Address is null");
            }

            final String scaleIoData2KernelAndSvmSubnetMask = this.inputParams.getScaleIoData2KernelAndSvmSubnetMask();

            if (StringUtils.isEmpty(scaleIoData2KernelAndSvmSubnetMask))
            {
                throw new IllegalStateException("ScaleIO VM Data2 Subnet Mask is null");
            }

            final NicSetting nicSettingScaleIoMgmt = new NicSetting();
            nicSettingScaleIoMgmt.setIpAddress(this.newScaleIoVmIpAddress);
            //TODO: This needs to come from the UI
            nicSettingScaleIoMgmt.setGateway(singletonList("10.239.139.33"));
            nicSettingScaleIoMgmt.setSubnetMask(scaleIoSvmManagementSubnetMask);

            final NicSetting nicSettingScaleIoData1 = new NicSetting();
            nicSettingScaleIoData1.setIpAddress(scaleIoData1SvmIpAddress);
            nicSettingScaleIoData1.setSubnetMask(scaleIoData1KernelAndSvmSubnetMask);

            final NicSetting nicSettingScaleIoData2 = new NicSetting();
            nicSettingScaleIoData2.setIpAddress(scaleIoData2SvmIpAddress);
            nicSettingScaleIoData2.setSubnetMask(scaleIoData2KernelAndSvmSubnetMask);

            return Arrays.asList(nicSettingScaleIoMgmt, nicSettingScaleIoData1, nicSettingScaleIoData2);
        }
    }
}
