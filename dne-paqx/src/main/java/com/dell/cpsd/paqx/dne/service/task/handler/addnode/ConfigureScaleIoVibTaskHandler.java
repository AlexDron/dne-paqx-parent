package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOMdmCluster;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.ConfigureScaleIoVibTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.InstallEsxiTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBConfigureRequest;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBConfigureRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * TODO: Document Usage
 * <p/>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * <p/>
 *
 * @version 1.0
 * @since 1.0
 */
public class ConfigureScaleIoVibTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureScaleIoVibTaskHandler.class);
    private static final String IOCTL_INI_GUID_STRING = "IoctlIniGuidStr";
    private static final String IOCTL_MDM_IP_STRING   = "IoctlMdmIPStr";
    private static final String EQUALS_STRING         = "=";
    private static final String SPACE_STRING          = " ";
    private static final String COMMA_DELIMITER       = ",";
    private static final String SOFTWARE_VIB_MODULE = "scini";

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;
    private final DataServiceRepository repository;

    public ConfigureScaleIoVibTaskHandler(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute Configure ScaleIO VIB task");
        final ConfigureScaleIoVibTaskResponse response = initializeResponse(job);

        try
        {
            final ComponentEndpointIds componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");

            if (componentEndpointIds == null)
            {
                throw new IllegalStateException("No VCenter components found.");
            }

            final InstallEsxiTaskResponse installEsxiTaskResponse = (InstallEsxiTaskResponse) job.getTaskResponseMap().get("installEsxi");

            if (installEsxiTaskResponse == null)
            {
                throw new IllegalStateException("No Install ESXi task response found");
            }

            final String hostname = installEsxiTaskResponse.getHostname();

            if (hostname == null)
            {
                throw new IllegalStateException("Host name is null");
            }

            final SoftwareVIBConfigureRequestMessage requestMessage = new SoftwareVIBConfigureRequestMessage();
            requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
            requestMessage.setComponentEndpointIds(
                    new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                            componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));

            final SoftwareVIBConfigureRequest softwareVIBConfigureRequest = new SoftwareVIBConfigureRequest();
            softwareVIBConfigureRequest.setHostName(hostname);
            softwareVIBConfigureRequest.setModuleName(SOFTWARE_VIB_MODULE);
            //softwareVIBConfigureRequest.setModuleOptions(buildModuleOptions(jobId));
            requestMessage.setSoftwareVIBConfigureRequest(softwareVIBConfigureRequest);

            final boolean success = this.nodeService.requestConfigureScaleIoVib(requestMessage);

            response.setWorkFlowTaskStatus(success ? Status.SUCCEEDED : Status.FAILED);

            return success;
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
            response.addError(e.toString());
            return false;
        }
    }

    @Override
    public ConfigureScaleIoVibTaskResponse initializeResponse(Job job)
    {
        final ConfigureScaleIoVibTaskResponse response = new ConfigureScaleIoVibTaskResponse();
        response.setWorkFlowTaskName(job.getCurrentTask().getTaskName());
        response.setWorkFlowTaskStatus(Status.IN_PROGRESS);
        job.addTaskResponse(job.getStep(), response);

        return response;
    }

    //TODO: This will change
    private String buildModuleOptions(final String jobId) throws Exception
    {
        if (jobId == null)
        {
            throw new Exception("Job Id is null");
        }

        try
        {
            final ScaleIOData scaleIOData = repository.getScaleIoData(jobId);

            final Set<String> mdmIpList = new HashSet<>();

            final ScaleIOMdmCluster scaleIOMdmCluster = scaleIOData.getMdmCluster();
            if (scaleIOMdmCluster != null)
            {
                // Scan the slave element info for ips
                scaleIOMdmCluster.getSlaveElementInfo().stream().filter(Objects::nonNull).forEach(
                        scaleIOSDSElementInfo -> scaleIOSDSElementInfo.getIps().forEach(scaleIOIP -> mdmIpList.add(scaleIOIP.getIp())));

                // Scan the master element info the ips
                scaleIOMdmCluster.getMasterElementInfo().stream().filter(Objects::nonNull).forEach(
                        scaleIOSDSElementInfo -> scaleIOSDSElementInfo.getIps().forEach(scaleIOIP -> mdmIpList.add(scaleIOIP.getIp())));
            }

            final StringBuilder builder = new StringBuilder();
            builder.append(IOCTL_INI_GUID_STRING);
            builder.append(EQUALS_STRING);
            final String randomGuidString = UUID.randomUUID().toString();

            LOGGER.info("IoctlIniGuidStr for jobId [{}] is [{}]", jobId, randomGuidString);

            builder.append(randomGuidString);
            builder.append(SPACE_STRING);
            builder.append(IOCTL_MDM_IP_STRING);
            builder.append(EQUALS_STRING);
            builder.append(String.join(COMMA_DELIMITER, mdmIpList));

            return builder.toString();

        }
        catch (Exception exception)
        {
            throw new Exception("Exception occurred", exception);
        }
    }
}
