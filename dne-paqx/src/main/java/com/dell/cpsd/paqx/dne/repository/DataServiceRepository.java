package com.dell.cpsd.paqx.dne.repository;

import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
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

    @Transactional
    //TODO: Define this
    void saveVCenterData();

    @Transactional
    //TODO: Define this
    void saveScaleIoData();

    @Transactional
    Host getVCenterHost(final String hostName) throws NoResultException;
}
