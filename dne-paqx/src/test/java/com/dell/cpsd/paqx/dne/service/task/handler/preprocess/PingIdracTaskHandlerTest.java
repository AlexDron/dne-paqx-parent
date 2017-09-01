/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved. Dell EMC Confidential/Proprietary Information
 *
 */

package com.dell.cpsd.paqx.dne.service.task.handler.preprocess;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * The tests for PingIdracTaskHandler.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class PingIdracTaskHandlerTest
{
    @Mock
    private Job job;

    @Mock
    private TaskResponse response;

    @Mock
    private NodeExpansionRequest request;

    private PingIdracTaskHandler handler;
    private PingIdracTaskHandler spy;

    /**
     * The test setup.
     *
     * @since 1.0
     */
    @Before
    public void setUp()
    {
        this.handler = new PingIdracTaskHandler(1000);
        this.spy = spy(this.handler);
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.preprocess.PingIdracTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     *
     * @since 1.0
     */
    @Ignore
    @Test
    public void executeTask_successful_case() throws Exception
    {
        String idracIpAddress = "127.0.0.1";// ping the loopback address

        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(idracIpAddress).when(this.request).getIdracIpAddress();

        assertTrue(this.spy.executeTask(this.job));
        verify(this.response).setWorkFlowTaskStatus(Status.SUCCEEDED);
        verify(this.response, never()).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.preprocess.PingIdracTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     *
     * @since 1.0
     */
    @Test
    public void executeTask_no_input_params() throws Exception
    {
        NodeExpansionRequest nullRequest = null;

        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        doReturn(nullRequest).when(this.job).getInputParams();

        assertFalse(this.spy.executeTask(this.job));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.preprocess.PingIdracTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     *
     * @since 1.0
     */
    @Test
    public void executeTask_no_idrac_ip_address() throws Exception
    {
        String nullIdracIpAddress = null;

        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(nullIdracIpAddress).when(this.request).getIdracIpAddress();

        assertFalse(this.spy.executeTask(this.job));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

    /**
     * {@link com.dell.cpsd.paqx.dne.service.task.handler.preprocess.PingIdracTaskHandler#executeTask(com.dell.cpsd.paqx.dne.domain.Job)}.
     *
     * @throws Exception
     *
     * @since 1.0
     */
    @Test
    public void executeTask_ip_address_unreachable() throws Exception
    {
        String bogusIdracIpAddress = "1.0.0.0";

        doReturn(this.response).when(this.spy).initializeResponse(this.job);
        doReturn(this.request).when(this.job).getInputParams();
        doReturn(bogusIdracIpAddress).when(this.request).getIdracIpAddress();

        assertFalse(this.spy.executeTask(this.job));
        verify(this.response).setWorkFlowTaskStatus(Status.FAILED);
        verify(this.response).addError(anyString());
    }

}