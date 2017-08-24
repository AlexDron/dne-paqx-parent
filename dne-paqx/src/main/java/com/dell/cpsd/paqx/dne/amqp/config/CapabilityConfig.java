/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.amqp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;

import com.dell.cpsd.hdp.capability.registry.client.CapabilityRegistryException;
import com.dell.cpsd.hdp.capability.registry.client.ICapabilityRegistryLookupManager;
import com.dell.cpsd.hdp.capability.registry.client.binder.CapabilityBinder;
import com.dell.cpsd.hdp.capability.registry.client.binder.CapabilityBindingService;
import com.dell.cpsd.hdp.capability.registry.client.binder.CapabilityMatcher;
import com.dell.cpsd.hdp.capability.registry.client.binder.rpc.AmqpRpcCapabilityBindingService;
import com.dell.cpsd.hdp.capability.registry.client.lookup.config.CapabilityRegistryLookupManagerConfig;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

@Configuration
@Import({CapabilityRegistryLookupManagerConfig.class, ContextConfig.class})
public class CapabilityConfig
{
    /*
     * The logger for this class
     */
    private static final Logger  LOGGER    = LoggerFactory.getLogger(CapabilityConfig.class);

    @Bean
    public CapabilityBinder dneCapabilityBinder(
            @Autowired ICapabilityRegistryLookupManager capabilityRegistryLookupManager,
            @Autowired @Qualifier("nodeExpansionAmqpAdmin") AmqpAdmin amqpAdmin,
            @Autowired @Qualifier("nodeExpansionResponseQueue") Queue queue,
            @Autowired String replyTo)
    {
        CapabilityBindingService bindingService = new AmqpRpcCapabilityBindingService(capabilityRegistryLookupManager, amqpAdmin, queue,
                replyTo);

        CapabilityBinder binder = new CapabilityBinder(bindingService,
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("list-discovered-nodes"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("vcenter-discover-cluster"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("rackhd-configure-idrac-network"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("manage-node-allocation"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("rackhd-set-idrac-credentials"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("rackhd-configure-boot-device-idrac"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("scaleio-list-components"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("vcenter-list-components"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("vcenter-discover"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("scaleio-discover"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("rackhd-install-esxi"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("vcenter-addhostvcenter"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("vcenter-install-software-vib"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("vcenter-configure-software-vib"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("vcenter-addhostdvswitch"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("vcenter-deployvmfromtemplate"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("vcenter-enablePCIpassthroughHost"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("vcenter-powercommand"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("vcenter-setPCIpassthrough"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("vcenter-addhostlicense"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("esxi-credential-details"),
               new CapabilityMatcher().withCardinalReduction(CapabilityMatcher.CardinalReduction.ANY)
                        .withProfile("vcenter-enterMaintenance")

        );

        LOGGER.info("Capability Binder registers with capability registry lookup manager");
        binder.register(capabilityRegistryLookupManager);

        return binder;
    }

    @Bean
    public ApplicationListener<ContextRefreshedEvent> contextRefreshedEventListener(
            @Autowired CapabilityBinder capabilityBinder)
    {
        return new ApplicationListener<ContextRefreshedEvent>()
        {
            /**
             * When spring context is fully loaded, do the binding
             *
             * @param contextRefreshedEvent
             */
            @Override
            public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent)
            {
                try
                {
                    capabilityBinder.bind();
                    LOGGER.info("found list of capablities with size {} after bind", capabilityBinder.getCurrentCapabilities().size());
                }
                catch (ServiceTimeoutException | CapabilityRegistryException e)
                {
                    LOGGER.error("Unable to bind for capability", e);
                }
            }
        };
    }
}
