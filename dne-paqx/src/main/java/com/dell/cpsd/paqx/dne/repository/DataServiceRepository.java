package com.dell.cpsd.paqx.dne.repository;

import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.domain.vcenter.PortGroup;
import com.dell.cpsd.paqx.dne.domain.vcenter.VCenter;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointDetails;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    void saveScaleIoComponentDetails(List<ComponentEndpointDetails> componentEndpointDetailsList);

    @Transactional
    void saveVCenterComponentDetails(List<ComponentEndpointDetails> componentEndpointDetailsList);

    boolean saveVCenterData(final String jobId, final VCenter vCenterData);

    boolean saveScaleIoData(final String jobId, final ScaleIOData scaleIOData);

    Host getVCenterHost(final String hostName) throws NoResultException;

    List<PortGroup> getPortGroups();
}
