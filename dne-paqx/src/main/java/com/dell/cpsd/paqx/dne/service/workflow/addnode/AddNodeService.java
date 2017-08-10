/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.workflow.addnode;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddHostToDvSwitchTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddHostToVCenterTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ApplyEsxiLicenseTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ConfigureScaleIoVibTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.DeployScaleIoVmTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.EnablePciPassthroughTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.InstallEsxiTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.InstallScaleIoVibTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.RebootHostTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.UpdatePciPassthroughTaskHandler;
import com.dell.cpsd.paqx.dne.transformers.HostToInstallEsxiRequestTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.service.BaseService;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.WorkflowService;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionResponse;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddNodeToSystemDefinitionTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ChangeIdracCredentialsTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.NotifyNodeDiscoveryToUpdateStatusTaskHandler;
import com.dell.cpsd.sdk.AMQPClient;

@Service
public class AddNodeService extends BaseService implements IAddNodeService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AddNodeService.class);

//    @Value("#{PropertySplitter.map('${addnode.map.step.to.map}')}")
//    private Map<String, String> propertyAsMap;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private AMQPClient sdkAMQPClient;

    @Autowired
    @Qualifier("addNodeWorkflowService")
    private WorkflowService workflowService;

    @Autowired
    private HostToInstallEsxiRequestTransformer hostToInstallEsxiRequestTransformer;

    @Autowired
    private DataServiceRepository repository;

    @Override
    public Job createWorkflow(final String workflowType, final String startingStep, final String currentStatus)
    {
        Job job = workflowService.createWorkflow(workflowType, startingStep, currentStatus, addNodeWorkflowTasks());
        return job;
    }

    @Bean("addNodesWorkflowTasks")
    public Map<String, WorkflowTask> addNodeWorkflowTasks()
    {
        final Map<String, WorkflowTask> workflowTasks = new HashMap<>();

		workflowTasks.put("changeIdracCredentials", changeIdracCredentialsTask());
        workflowTasks.put("installEsxi", installEsxiTask());//Should work fine, hostname not an issue
        workflowTasks.put("addHostToVcenter", addHostToVcenterTask());//Issues due to invalid hostname, we have hostname, vcenter needs cluster id as of now
        workflowTasks.put("installScaleIoVib", installScaleIoVibTask());//If above step works, this works fine
        workflowTasks.put("configureScaleIoVib", configureScaleIoVibTask());//Module options can change
        workflowTasks.put("addHostToDvSwitch", addHostToDvSwitchTask());//Requires dvs names and pnic names - from host (don't have yet)
        workflowTasks.put("deploySVM", deploySVMTask());//Datacenter name not required to be sent from the paqx, update the adapter, Get the vm name from the UI or hardcoded. New vm name add to the task response, required by the setPciPassthroughSioVm, update the vcenter adapter
        workflowTasks.put("enablePciPassthroughHost", enablePciPassthroughHostTask());//Requires hostname, hostPciDeviceId figure it out
        workflowTasks.put("rebootHost", rebootHostTask());//Simply requires the hostname
        workflowTasks.put("setPciPassthroughSioVm", setPciPassthroughSioVmTask());//PCI device Id and vmid required (can be vmname itself in the deploySVM task response)
        workflowTasks.put("applyEsxiLicense", applyEsxiLicenseTask());// hostname is required, we have it
        /*workflowTasks.put("installScaleIOSDC", null);
        workflowTasks.put("addNewHostToScaleIO", null);*/
        workflowTasks.put("updateSystemDefinition", updateSystemDefinitionTask());
        workflowTasks.put("notifyNodeDiscoveryToUpdateStatus", notifyNodeDiscoveryToUpdateStatusTask());
        return workflowTasks;
    }

    @Bean("updateSystemDefinitionTask")
    private WorkflowTask updateSystemDefinitionTask()
    {
        return createTask("Update System Definition", new AddNodeToSystemDefinitionTaskHandler(this.sdkAMQPClient));
    }
    
    @Bean("changeIdracCredentialsTask")
    private WorkflowTask changeIdracCredentialsTask()
    {
        return createTask("Change Out of Band Management Credentials", new ChangeIdracCredentialsTaskHandler(this.nodeService));
    }

    @Bean("installEsxiTask")
    private WorkflowTask installEsxiTask()
    {
        return createTask("Install ESXi", new InstallEsxiTaskHandler(this.nodeService, hostToInstallEsxiRequestTransformer));
    }

    @Bean("addHostToVcenterTask")
    private WorkflowTask addHostToVcenterTask()
    {
        return createTask("Add Host to VCenter", new AddHostToVCenterTaskHandler(this.nodeService, repository));
    }

    @Bean("installScaleIoVibTask")
    private WorkflowTask installScaleIoVibTask()
    {
        return createTask("Install ScaleIO VIB", new InstallScaleIoVibTaskHandler(this.nodeService, repository));
    }

    @Bean("configureScaleIoVibTask")
    private WorkflowTask configureScaleIoVibTask()
    {
        return createTask("Configure ScaleIO VIB", new ConfigureScaleIoVibTaskHandler(this.nodeService, repository));
    }

    @Bean("addHostToDvSwitchTask")
    private WorkflowTask addHostToDvSwitchTask()
    {
        return createTask("Add Host to DV Switch", new AddHostToDvSwitchTaskHandler(this.nodeService, repository));
    }

    @Bean("deploySVMTask")
    private WorkflowTask deploySVMTask()
    {
        return createTask("Deploy ScaleIO VM", new DeployScaleIoVmTaskHandler(this.nodeService, repository));
    }

    @Bean("enablePciPassthroughHostTask")
    private WorkflowTask enablePciPassthroughHostTask()
    {
        return createTask("Enable PCI pass through", new EnablePciPassthroughTaskHandler(this.nodeService, repository));
    }

    @Bean("rebootHostTask")
    private WorkflowTask rebootHostTask()
    {
        return createTask("Reboot Host", new RebootHostTaskHandler(this.nodeService, repository));
    }

    @Bean("setPciPassthroughSioVmTask")
    private WorkflowTask setPciPassthroughSioVmTask()
    {
        return createTask("Set PCI Pass through ScaleIO VM", new UpdatePciPassthroughTaskHandler(this.nodeService, repository));
    }

    @Bean("applyEsxiLicenseTask")
    private WorkflowTask applyEsxiLicenseTask()
    {
        return createTask("Apply ESXi License", new ApplyEsxiLicenseTaskHandler(this.nodeService, repository));
    }

    @Bean("notifyNodeDiscoveryToUpdateStatusTask")
    private WorkflowTask notifyNodeDiscoveryToUpdateStatusTask()
    {
        return createTask("Notify Node Discovery To Update Status",
                new NotifyNodeDiscoveryToUpdateStatusTaskHandler(this.nodeService));
    }

    public Job findJob(UUID jobId)
    {
        final Job job = workflowService.findJob(jobId);

        return job;
    }

    public NodeExpansionResponse makeNodeExpansionResponse(final Job job)
    {
        return makeNodeExpansionResponse(job, workflowService);
    }

//    public String findPathFromStep(final String step)
//    {
//        return propertyAsMap.get(step);
//    }

    public WorkflowService getWorkflowService()
    {
        return workflowService;
    }

    public void setWorkflowService(WorkflowService workflowService)
    {
        this.workflowService = workflowService;
    }
}
