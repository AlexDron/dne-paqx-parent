/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.repository.InMemoryJobRepository;
import com.dell.cpsd.paqx.dne.service.model.Step;
import com.dell.cpsd.paqx.dne.service.workflow.preprocess.PreProcessService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PreProcessServiceImplTest {

    private WorkflowServiceImpl workflowServiceUnderTest;
    private PreProcessService   preProcessServiceUnderTest;
    private Map<String, WorkflowTask> workFlowTasks;
    @Before
    public void setUp() throws Exception
    {
        final InMemoryJobRepository inMemoryJobRepository = new InMemoryJobRepository();

        final Map<String, Step> workflowSteps = new HashMap<>();

        workflowSteps.put("startPreProcessWorkflow", new Step("completed", true));
        workflowSteps.put("completed", null);

        this.workflowServiceUnderTest =
                new WorkflowServiceImpl(inMemoryJobRepository, workflowSteps);

        this.preProcessServiceUnderTest = new PreProcessService();
        preProcessServiceUnderTest.setWorkflowService(workflowServiceUnderTest);

        workFlowTasks = preProcessServiceUnderTest.preProcessWorkflowTasks();
    }

    @Test
    public void testCreateWorkflow()
    {

        Job initialJob = preProcessServiceUnderTest.createWorkflow(
                "PreProcess", "startPreProcessWorkflow", "status1");

        assertNotNull(initialJob);
        assertNotNull(initialJob.getId());
    }

    @Test
    public void testFindJob()
    {
        Job initialJob = preProcessServiceUnderTest.createWorkflow(
                "PreProcess", "startPreProcessWorkflow", "status1");

        Job foundJob = preProcessServiceUnderTest.findJob(initialJob.getId());
        assertNotNull(initialJob);
        assertNotNull(foundJob);
        assertEquals(initialJob, foundJob);
    }

    @Test
    public void testPreProcessWorkFlowTask_setup()
    {
        Assert.assertNotNull(workFlowTasks);
        Assert.assertEquals(12, workFlowTasks.size());
    }

    @Test
    public void testTaskName_findAvailableNodes()
    {
        Assert.assertEquals("Finding discovered Nodes", workFlowTasks.get("findAvailableNodes").getTaskName());
    }

    @Test
    public void testTaskName_configIdrac()
    {
    	Assert.assertEquals("Configuring Out of Band Management", workFlowTasks.get("configIdrac").getTaskName());
    }

    @Test
    public void testTaskName_findConfigurePxeBoot() {
        Assert.assertEquals("Configure Pxe boot", workFlowTasks.get("configurePxeBoot").getTaskName());
    }
  
    @Test
    public void testTaskName_configureObmSettings()
    {
        Assert.assertEquals("Configuring Obm Settings", workFlowTasks.get("configureObmSettings").getTaskName());
    }

    @Test
    public void testTaskName_findVCluster() {
    	Assert.assertEquals("Find VCluster", workFlowTasks.get("findVCluster").getTaskName());
    }

    @Test
    public void testTaskName_findScaleIO() {
        Assert.assertEquals("Find ScaleIO", workFlowTasks.get("findScaleIO").getTaskName());
    }

    @Ignore
    @Test
    public void testTaskName_findProtectionDomain() {
    	Assert.assertEquals("Find ProtectionDomain", workFlowTasks.get("findProtectionDomain").getTaskName());
    }

    @Ignore
    @Test
    public void testTaskName_findSystemData() {
    	Assert.assertEquals("Find SystemData", workFlowTasks.get("findSystemData").getTaskName());
    }

    @Ignore
    @Test
    public void testTaskName_assignDefaultHostName() {
    	Assert.assertEquals("Assign Default HostName", workFlowTasks.get("assignDefaultHostName").getTaskName());
    }

    @Ignore
    @Test
    public void testTaskName_assignDefaultCredentials() {
    	Assert.assertEquals("Assign Default Credentials", workFlowTasks.get("assignDefaultCredentials").getTaskName());
    }

}
