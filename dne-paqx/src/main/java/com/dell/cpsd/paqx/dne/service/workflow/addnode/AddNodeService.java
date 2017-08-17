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
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.ListESXiCredentialDetailsTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.RebootHostTaskHandler;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.UpdatePciPassThroughTaskHandler;
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
        workflowTasks.put("installEsxi", installEsxiTask());//WIP
        workflowTasks.put("esxi-credential-details", esxiCredentialDetailsTask());
        workflowTasks.put("addHostToVcenter", addHostToVcenterTask());//CODING DONE, TESTING PENDING
        workflowTasks.put("installScaleIoVib", installScaleIoVibTask());//VIB URL's SOURCE IS NOT KNOWN
        workflowTasks.put("configureScaleIoVib", configureScaleIoVibTask());//CODING DONE, TESTING PENDING
        workflowTasks.put("addHostToDvSwitch", addHostToDvSwitchTask());//WIP
        workflowTasks.put("deploySVM", deploySVMTask());//CODING DONE, TESTING PENDING
        workflowTasks.put("enablePciPassthroughHost", enablePciPassthroughHostTask());//CODING DONE, TESTING PENDING
        workflowTasks.put("rebootHost", rebootHostTask());//CODING DONE, TESTING DONE
        workflowTasks.put("setPciPassthroughSioVm", setPciPassthroughSioVmTask());//CODING DONE, TESTING PENDING
        workflowTasks.put("applyEsxiLicense", applyEsxiLicenseTask());//CODING DONE, TESTING DONE
        /*workflowTasks.put("installScaleIOSDC", null);
        workflowTasks.put("addNewHostToScaleIO", null);*/
        workflowTasks.put("updateSystemDefinition", updateSystemDefinitionTask());
        workflowTasks.put("notifyNodeDiscoveryToUpdateStatus", notifyNodeDiscoveryToUpdateStatusTask());

        return workflowTasks;
    }

    @Bean("esxiCredentialDetailsTask")
    private WorkflowTask esxiCredentialDetailsTask()
    {
        return createTask("Retrieve default ESXi host credential details", new ListESXiCredentialDetailsTaskHandler(this.nodeService));
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
        return createTask("Set PCI Pass through ScaleIO VM", new UpdatePciPassThroughTaskHandler(this.nodeService, repository));
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
