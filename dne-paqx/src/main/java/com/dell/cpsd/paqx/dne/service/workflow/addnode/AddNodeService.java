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

import com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddHostToDvSwitchTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.AddHostToVCenterTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ApplyEsxiLicenseTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ConfigureScaleIoVibTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.DeployScaleIoVmTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.DiscoverScaleIoTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.DiscoverVCenterTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.EnablePciPassthroughTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.InstallEsxiTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.InstallScaleIoVibTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ListScaleIoComponentsTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ListVCenterComponentsTaskHandler;
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
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.FindDiscoveredNodesTaskHandler;
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
		//TODO: Uncomment this out when integration is done
        //It's working till discover vcenter step
		/*workflowTasks.put("listScaleIoComponents", listScaleIoComponentsTask());
		workflowTasks.put("listVCenterComponents", listVCenterComponentsTask());
        workflowTasks.put("discoverScaleIo", discoverScaleIoTask());
        workflowTasks.put("discoverVCenter", discoverVCenterTask());
        workflowTasks.put("installEsxi", null);
        workflowTasks.put("addHostToVcenter", null);
        workflowTasks.put("installScaleIoVib", null);
        workflowTasks.put("configureScaleIoVib", null);
        workflowTasks.put("addHostToDvSwitch", null);
        workflowTasks.put("deploySVM", null);
        workflowTasks.put("enablePciPassthroughHost", null);
        workflowTasks.put("rebootHost", null);
        workflowTasks.put("setPciPassthroughSioVm", null);
        workflowTasks.put("applyEsxiLicense", null);
        workflowTasks.put("installScaleIOSDC", null);
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

    @Bean("listScaleIoComponentsTask")
    private WorkflowTask listScaleIoComponentsTask()
    {
        return createTask("List ScaleIO Components", new ListScaleIoComponentsTaskHandler(this.nodeService));
    }

    @Bean("listVCenterComponentsTask")
    private WorkflowTask listVCenterComponentsTask()
    {
        return createTask("List VCenter Components", new ListVCenterComponentsTaskHandler(this.nodeService));
    }

    @Bean("discoverScaleIoTask")
    private WorkflowTask discoverScaleIoTask()
    {
        return createTask("Discover ScaleIO", new DiscoverScaleIoTaskHandler(this.nodeService));
    }

    @Bean("discoverVCenterTask")
    private WorkflowTask discoverVCenterTask()
    {
        return createTask("Discover VCenter", new DiscoverVCenterTaskHandler(this.nodeService));
    }

    @Bean("installEsxiTask")
    private WorkflowTask installEsxiTask()
    {
        return createTask("Install ESXi", new InstallEsxiTaskHandler(this.nodeService, hostToInstallEsxiRequestTransformer));
    }

    @Bean("addHostToVcenterTask")
    private WorkflowTask addHostToVcenterTask()
    {
        return createTask("Add Host to VCenter", new AddHostToVCenterTaskHandler(this.nodeService));
    }

    @Bean("installScaleIoVibTask")
    private WorkflowTask installScaleIoVibTask()
    {
        return createTask("Install ScaleIO VIB", new InstallScaleIoVibTaskHandler(this.nodeService));
    }

    @Bean("configureScaleIoVibTask")
    private WorkflowTask configureScaleIoVibTask()
    {
        return createTask("Configure ScaleIO VIB", new ConfigureScaleIoVibTaskHandler(this.nodeService));
    }

    @Bean("addHostToDvSwitchTask")
    private WorkflowTask addHostToDvSwitchTask()
    {
        return createTask("Add Host to DV Switch", new AddHostToDvSwitchTaskHandler(this.nodeService));
    }

    @Bean("deploySVMTask")
    private WorkflowTask deploySVMTask()
    {
        return createTask("Deploy ScaleIO VM", new DeployScaleIoVmTaskHandler(this.nodeService));
    }

    @Bean("enablePciPassthroughHostTask")
    private WorkflowTask enablePciPassthroughHostTask()
    {
        return createTask("Enable PCI pass through", new EnablePciPassthroughTaskHandler(this.nodeService));
    }

    @Bean("rebootHostTask")
    private WorkflowTask rebootHostTask()
    {
        return createTask("Reboot Host", new RebootHostTaskHandler(this.nodeService));
    }

    @Bean("setPciPassthroughSioVmTask")
    private WorkflowTask setPciPassthroughSioVmTask()
    {
        return createTask("Set PCI Pass through ScaleIO VM", new UpdatePciPassthroughTaskHandler(this.nodeService));
    }

    @Bean("applyEsxiLicenseTask")
    private WorkflowTask applyEsxiLicenseTask()
    {
        return createTask("Apply ESXi License", new ApplyEsxiLicenseTaskHandler(this.nodeService));
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
