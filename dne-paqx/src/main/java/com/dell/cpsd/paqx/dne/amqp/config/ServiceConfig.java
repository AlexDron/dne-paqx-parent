/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.amqp.config;

import java.util.Map;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.repository.H2DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.dell.cpsd.common.logging.ILogger;
import com.dell.cpsd.paqx.dne.amqp.producer.DneProducer;
import com.dell.cpsd.paqx.dne.log.DneLoggingManager;
import com.dell.cpsd.paqx.dne.repository.InMemoryJobRepository;
import com.dell.cpsd.paqx.dne.repository.JobRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.WorkflowService;
import com.dell.cpsd.paqx.dne.service.WorkflowServiceImpl;
import com.dell.cpsd.paqx.dne.service.amqp.AmqpNodeService;
import com.dell.cpsd.paqx.dne.service.model.Step;
import com.dell.cpsd.paqx.dne.service.orchestration.IOrchestrationService;
import com.dell.cpsd.paqx.dne.service.orchestration.OrchestrationService;
import com.dell.cpsd.paqx.dne.service.workflow.addnode.AddNodeService;
import com.dell.cpsd.paqx.dne.service.workflow.addnode.AddNodeTaskConfig;
import com.dell.cpsd.paqx.dne.service.workflow.addnode.IAddNodeService;
import com.dell.cpsd.paqx.dne.service.workflow.preprocess.IPreProcessService;
import com.dell.cpsd.paqx.dne.service.workflow.preprocess.PreProcessService;
import com.dell.cpsd.paqx.dne.service.workflow.preprocess.PreProcessTaskConfig;
import com.dell.cpsd.paqx.dne.util.PropertySplitter;
import com.dell.cpsd.sdk.AMQPClient;
import com.dell.cpsd.sdk.SystemDefinitionMessenger;
import com.dell.cpsd.sdk.config.SDKConfiguration;
import com.dell.cpsd.service.common.client.rpc.DelegatingMessageConsumer;
import com.google.common.base.Splitter;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

@Configuration
@Import({
    RabbitConfig.class, 
    ConsumerConfig.class, 
    ProducerConfig.class, 
    AddNodeTaskConfig.class, 
    PreProcessTaskConfig.class, 
    PropertySplitter.class,
    SDKConfiguration.class,
    SystemDefinitionMessenger.class, 
    AMQPClient.class,
    PersistencePropertiesConfig.class,
    PersistenceConfig.class
})
public class ServiceConfig
{
    private static ILogger LOGGER = DneLoggingManager.getLogger(ServiceConfig.class);

    @Bean
    public NodeService nodeServiceClient(@Autowired DelegatingMessageConsumer delegatingMessageConsumer,
            @Autowired DneProducer dneProducer,
            @Autowired String replyTo)
    {
        return new AmqpNodeService(LOGGER, delegatingMessageConsumer, dneProducer, replyTo, repository());
    }

    @Bean
    DataServiceRepository repository()
    {
        return new H2DataRepository();
    }
    
    
    /**
     * This returns the workflow service that is used to control the add node.
     * 
     * @return  The workflow service that is used to control the add node.
     * 
     * @since   1.0
     */
    @Bean("addNodeWorkflowService")
    public WorkflowService addNodeWorkflowService(@Qualifier("addNodeWorkflowSteps") Map<String, Step> workflowSteps)
    {
        return new WorkflowServiceImpl(jobRepository(), workflowSteps);
    }

    @Bean("preProcessWorkflowService")
    public WorkflowService preProcessWorkflowService(@Qualifier("preProcessWorkflowSteps") Map<String, Step> workflowSteps)
    {
        return new WorkflowServiceImpl(jobRepository(), workflowSteps);
    }

    @Bean
    public IAddNodeService addNodeService(){
        return new AddNodeService();
    }

    @Bean
    public IPreProcessService preProcessService(){
        return new PreProcessService();
    }

    @Bean
    public IOrchestrationService orchestrationService(){
        return new OrchestrationService();
    }


    /**
     * This returns the repository for jobs, such as add node.
     * 
     * @return  The repository for jobs, such as add node.
     * 
     * @since   1.0
     */
    @Bean
    public JobRepository jobRepository()
    {
        return new InMemoryJobRepository();
    }


    public Map<String, String> map(String property) {
        return this.map(property, ",");
    }

    private Map<String, String> map(String property, String splitter) {
        return Splitter.on(splitter).omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(property);
    }
}
