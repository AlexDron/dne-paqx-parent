/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.AbstractHostMaintenanceModeTaskHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Qualifier("exitHostMaintenanceMode")
public class ExitHostMaintenanceMode extends AbstractHostMaintenanceMode
{
    @Autowired
    public ExitHostMaintenanceMode(final NodeService nodeService, final DataServiceRepository repository)
    {
        super(nodeService, repository, "Exit Host Maintenance Mode");
    }

    @Override
    protected boolean getMaintenanceModeEnable()
    {
        return false;
    }
}
