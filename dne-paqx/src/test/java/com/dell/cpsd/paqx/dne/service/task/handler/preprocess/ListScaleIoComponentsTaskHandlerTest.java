/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.task.handler.preprocess;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.WorkflowTask;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ListScaleIoComponentsTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.Status;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * The tests for the ListScaleIoComponentsTaskHandler class.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class ListScaleIoComponentsTaskHandlerTest
{
    @Mock
    private WorkflowTask task;

    @Mock
    private Job job;

    @Mock
    private ListScaleIoComponentsTaskResponse response;

    @Mock
    private NodeService service;

    private String taskName = "listScaleIOComponentsTask";
    private String stepName = "listScaleIOComponentsStep";

    private ListScaleIoComponentsTaskHandler handler;
    private ListScaleIoComponentsTaskHandler spy;

    /**
     * The test setup.
     *
     * @since 1.0
     */
    @Before
    public void setUp() throws Exception
    {
        this.handler = new ListScaleIoComponentsTaskHandler(this.service);
        this.spy = spy(handler);
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.preprocess.ListScaleIoComponentsTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_successful_case() throws Exception
    {
        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        doReturn(true).when(this.service).requestScaleIoComponents();

        assertEquals(true, this.spy.executeTask(this.job));
        verify(this.response).setWorkFlowTaskStatus(Status.SUCCEEDED);
        verify(this.response, never()).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.preprocess.ListScaleIoComponentsTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void executeTask_unsuccessful_case() throws Exception
    {
        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        doReturn(false).when(this.service).requestScaleIoComponents();

        assertEquals(false, this.spy.executeTask(this.job));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.preprocess.ListScaleIoComponentsTaskHandler#initializeResponse(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     */
    @Test
    public void initializeResponse() throws Exception
    {
        doReturn(this.task).when(this.job).getCurrentTask();
        doReturn(this.taskName).when(this.task).getTaskName();
        doReturn(this.stepName).when(this.job).getStep();

        ListScaleIoComponentsTaskResponse response = this.handler.initializeResponse(this.job);
        assertNotNull(response);
        assertEquals(this.taskName, response.getWorkFlowTaskName());
        assertEquals(Status.IN_PROGRESS, response.getWorkFlowTaskStatus());
    }

}