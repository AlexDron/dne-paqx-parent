/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.EsxiInstallationInfo;
import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.InstallEsxiTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.IpV4Configuration;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.paqx.dne.transformers.HostToInstallEsxiRequestTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

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
public class InstallEsxiTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(InstallEsxiTaskHandler.class);

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    private final HostToInstallEsxiRequestTransformer hostToInstallEsxiRequestTransformer;

    private final DataServiceRepository repository;

    public InstallEsxiTaskHandler(final NodeService nodeService,
            final HostToInstallEsxiRequestTransformer hostToInstallEsxiRequestTransformer, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.hostToInstallEsxiRequestTransformer = hostToInstallEsxiRequestTransformer;
        this.repository = repository;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute Install ESXi task");

        final InstallEsxiTaskResponse response = initializeResponse(job);

        try
        {
            final NodeExpansionRequest inputParams = job.getInputParams();

            if (inputParams == null)
            {
                throw new IllegalStateException("Job input parameters are null");
            }

            final String symphonyUuid = inputParams.getSymphonyUuid();

            if (StringUtils.isEmpty(symphonyUuid))
            {
                throw new IllegalStateException("Symphony Id is null");
            }

            final String esxiManagementIpAddress = inputParams.getEsxiManagementIpAddress();

            if (StringUtils.isEmpty(esxiManagementIpAddress))
            {
                throw new IllegalStateException("ESXi Management IP Address is null");
            }

            final String esxiManagementGatewayIpAddress = inputParams.getEsxiManagementGatewayIpAddress();

            if (StringUtils.isEmpty(esxiManagementGatewayIpAddress))
            {
                throw new IllegalStateException("ESXi Management Gateway IP Address is null");
            }

            final String esxiManagementSubnetMask = inputParams.getEsxiManagementSubnetMask();

            if (StringUtils.isEmpty(esxiManagementSubnetMask))
            {
                throw new IllegalStateException("ESXi Management Subnet Mask is null");
            }

            String esxiManagementHostname = inputParams.getEsxiManagementHostname();

            if (StringUtils.isEmpty(esxiManagementHostname))
            {
                LOGGER.warn("ESXi Management hostname is null, will auto generate hostname");

                esxiManagementHostname = this.generateHostname(esxiManagementIpAddress);

                LOGGER.info("Auto generated ESXi Management hostname is " + esxiManagementHostname);
            }

            response.setHostname(esxiManagementHostname);

            final IpV4Configuration ipV4Configuration = new IpV4Configuration();
            ipV4Configuration.setEsxiManagementIpAddress(esxiManagementIpAddress);
            ipV4Configuration.setEsxiManagementGateway(esxiManagementGatewayIpAddress);
            ipV4Configuration.setEsxiManagementNetworkMask(esxiManagementSubnetMask);

            final EsxiInstallationInfo esxiInstallationInfo = hostToInstallEsxiRequestTransformer
                    .transformInstallEsxiData(esxiManagementHostname, symphonyUuid, ipV4Configuration);

            final String idracIpAddress = inputParams.getIdracIpAddress();

            if (idracIpAddress == null)
            {
                throw new IllegalStateException("Idrac IP is null");
            }

            final boolean succeeded = this.nodeService.requestInstallEsxi(esxiInstallationInfo, idracIpAddress);

            if(!succeeded)
            {
                throw new IllegalStateException("Request ESXi install failed");
            }

            String fqdn = this.generateHostFqdn(esxiManagementHostname);

            if(StringUtils.isEmpty(fqdn))
            {
                throw new IllegalStateException("Host domain name is not configured");
            }

            response.setHostname(fqdn);
            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            return true;

        }
        catch (Exception e)
        {
            LOGGER.error("Error installing ESXi", e);
            response.addError(e.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }

    @Override
    public InstallEsxiTaskResponse initializeResponse(Job job)
    {
        final InstallEsxiTaskResponse response = new InstallEsxiTaskResponse();
        response.setWorkFlowTaskName(job.getCurrentTask().getTaskName());
        response.setWorkFlowTaskStatus(Status.IN_PROGRESS);
        job.addTaskResponse(job.getStep(), response);

        return response;
    }

    /*
    * Auto generates the hostname using the ESXI Management IP Address.
    */
    private String generateHostname(final String esxiManagementIpAddress)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("vCenterHost");
        builder.append("-");
        builder.append(esxiManagementIpAddress.replaceAll("\\.", "-"));
        return builder.toString();
    }

    /*
    * Builds the host fully qualified domain name (FQDN) from
    * the hostname and domain name, where the domain name is
    * retrieved from the database.
    */
    private String generateHostFqdn(String hostName)
    {
        String domainName = this.repository.getDomainName();
        if(StringUtils.isEmpty(domainName))
        {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(hostName);
        builder.append(".");
        builder.append(domainName);
        return builder.toString();
    }
}
