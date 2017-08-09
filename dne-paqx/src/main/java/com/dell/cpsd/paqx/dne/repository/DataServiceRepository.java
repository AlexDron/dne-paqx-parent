package com.dell.cpsd.paqx.dne.repository;

import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.domain.vcenter.PortGroup;
import com.dell.cpsd.paqx.dne.domain.vcenter.VCenter;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointDetails;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;

import javax.persistence.NoResultException;
import java.util.List;
/**
 * TODO: Document Usage
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public interface DataServiceRepository
{
    boolean saveScaleIoComponentDetails(List<ComponentEndpointDetails> componentEndpointDetailsList);

    boolean saveVCenterComponentDetails(List<ComponentEndpointDetails> componentEndpointDetailsList);

    ComponentEndpointIds getComponentEndpointIds(final String componentType);

    boolean saveVCenterData(final String jobId, final VCenter vCenterData);

    boolean saveScaleIoData(final String jobId, final ScaleIOData scaleIOData);

    Host getVCenterHost(final String hostName) throws NoResultException;

    List<PortGroup> getPortGroups();

    ScaleIOData getScaleIoData(final String jobId);
}
