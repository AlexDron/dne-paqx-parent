/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.repository.InMemoryJobRepository;
import com.dell.cpsd.paqx.dne.service.model.Step;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class WorkflowServiceImplTest
{
    /*
     * The workflow service to test
     */
    private WorkflowServiceImpl workflowServiceUnderTest;
    

    /**
     * This sets up the test
     * 
     * @since   1.0
     */
    @Before
    public void setUp() throws Exception
    {
        final InMemoryJobRepository inMemoryJobRepository = new InMemoryJobRepository();

        final Map<String, Step> workflowSteps = new HashMap<>();

        workflowSteps.put("startAddNodeWorkflow", new Step("completed", true));
        workflowSteps.put("completed", null);
        workflowSteps.put("listScaleIoComponents", new Step("listVCenterComponents", false));
        workflowSteps.put("listVCenterComponents", new Step("listRackHdComponents", false));

        this.workflowServiceUnderTest =
            new WorkflowServiceImpl(inMemoryJobRepository, workflowSteps);
    }
    
 
    /**
     * This tests the workflow constructor with a null repository.
     * 
     * @since   1.0
     */
    @Test(expected = IllegalArgumentException.class)
    public void firstConstructorNullRepository()
    {
        final Map<String, Step> workflowSteps = new HashMap<>();
        
        new WorkflowServiceImpl(null, workflowSteps);
    }
    
   
    /**
     * This tests the workflow constructor with a null set of steps.
     * 
     * @since   1.0
     */
    @Test(expected = IllegalArgumentException.class)
    public void firstConstructorNullSteps()
    {
        final InMemoryJobRepository inMemoryJobRepository = new InMemoryJobRepository();
        
        new WorkflowServiceImpl(inMemoryJobRepository, null);
    }
    
    
    /**
     * This tests the workflow <code>createWorkflow</code> with valid arguments.
     * 
     * @since   1.0
     */
    @Test
    public void testCreateWorkflow() 
    {
        Job initialJob = workflowServiceUnderTest.createWorkflow(
                                "addNode", "startAddNodeWorkflow", "status1", new HashMap<>());

        assertNotNull(initialJob);
        assertNotNull(initialJob.getId());
    }
    
    
    /**
     * This tests the workflow <code>createWorkflow</code> with a null workflow.
     * 
     * @since   1.0
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateWorkflowNullWorkflow() 
    {
        Job initialJob = workflowServiceUnderTest.createWorkflow(
                                    null, "startAddNodeWorkflow", "status1", new HashMap<>());

        assertNotNull(initialJob);
        assertNotNull(initialJob.getId());
    }

   
    /**
     * This tests the <code>findNextStep</code>
     * 
     * @since   1.0
     */
    @Test
    public void testFindNextStep() 
    {
        Job initialJob = workflowServiceUnderTest.createWorkflow(
                                "addNode", "startAddNodeWorkflow", "status1", new HashMap<>());

        Step nextStep = workflowServiceUnderTest.findNextStep(
                                      "addNode", initialJob.getStep());

        assertNotNull(nextStep);
        assertEquals(nextStep.isFinalStep(), true);
    }
    

    /**
     * This tests the <code>advanceToNextStep</code>
     * 
     * @since   1.0
     */
    @Test
    public void testAdvanceToNextStep() 
    {
        final Job initialJob = workflowServiceUnderTest.createWorkflow(
                                "addNode", "startAddNodeWorkflow", "status1", new HashMap<>());

        final Job advancedJob = workflowServiceUnderTest.advanceToNextStep(
                          initialJob, initialJob.getStep(), "status2");

        assertNotNull(advancedJob);
        assertEquals(advancedJob.getStep(), "completed");
    }
    
    
    /**
     * This tests the <code>advanceToNextStep</code> for a null next step.
     * 
     * @since   1.0
     */
    @Test
    public void testAdvanceToNextStepForNullNextStep() 
    {
        final Job initialJob = workflowServiceUnderTest.createWorkflow(
                                "addNode", "startAddNodeWorkflow", "status1", new HashMap<>());
        
        final Job advancedJob = workflowServiceUnderTest.advanceToNextStep(
                          initialJob, initialJob.getStep(), "status2");

        assertNotNull(advancedJob);
        assertEquals(advancedJob.getStep(), "completed");
        
        final Job completedJob = workflowServiceUnderTest.advanceToNextStep(
                initialJob, initialJob.getStep(), "status3");
        
        assertNotNull(completedJob);
        assertEquals(completedJob.getStep(), "completed");
    }
    
    
    /**
     * This tests the <code>findActiveJobs</code>
     * 
     * @since   1.0
     */
    @Test
    public void testFindActiveJobs() 
    {
        final Job initialJob = workflowServiceUnderTest.createWorkflow(
                                "addNode", "startAddNodeWorkflow", "status1", new HashMap<>());

        final Job[] jobs = workflowServiceUnderTest.findActiveJobs();

        assertNotNull(jobs);
        assertEquals(jobs.length, 1);
    }
    
    
    /**
     * This tests the <code>findJob</code>, where the job exists.
     * 
     * @since   1.0
     */
    @Test
    public void testFindJob() 
    {
        final Job initialJob = workflowServiceUnderTest.createWorkflow(
                                "addNode", "startAddNodeWorkflow", "status1", new HashMap<>());

        final UUID jobId = initialJob.getId();

        final Job foundJob = workflowServiceUnderTest.findJob(jobId);

        assertNotNull(foundJob);
        assertEquals(initialJob, foundJob);
    }
    
    
    /**
     * This tests the <code>findJob</code> where the job does not exist.
     * 
     * @since   1.0
     */
    @Test
    public void testFindJobNonExistant() 
    {
        final UUID jobId = UUID.randomUUID();
        
        final Job foundJob = workflowServiceUnderTest.findJob(jobId);

        assertNull(foundJob);
    }

    @Test
    public void testMultipleJobs() throws Exception
    {


        final Job captureScaleIojob = workflowServiceUnderTest
                .createWorkflow("addNode", "listScaleIoComponents", "Capture VCenter component request submitted successfully",
                        new HashMap<>());
        final Job captureVCenterJob = workflowServiceUnderTest
                .createWorkflow("addNode", "listVCenterComponents", "Capture VCenter component request submitted successfully",
            new HashMap<>());

        assertNotNull(captureScaleIojob);
        assertNotNull(captureVCenterJob);
    }
}
