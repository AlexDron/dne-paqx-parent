/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Qualifier("enterHostMaintenanceMode")
public class EnterHostMaintenanceMode extends AbstractHostMaintenanceMode
{
    @Autowired
    public EnterHostMaintenanceMode(final NodeService nodeService, final DataServiceRepository repository)
    {
        super(nodeService, repository, "Enter Host Maintenance Mode");
    }

    @Override
    protected boolean getMaintenanceModeEnable()
    {
        return true;
    }
}
