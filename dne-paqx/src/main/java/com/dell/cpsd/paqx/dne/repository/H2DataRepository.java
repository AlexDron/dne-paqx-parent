package com.dell.cpsd.paqx.dne.repository;

import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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
public class H2DataRepository implements DataServiceRepository
{
    private static final Logger LOG = LoggerFactory.getLogger(H2DataRepository.class);

    @PersistenceContext
    public EntityManager entityManager;

    @Override
    public void saveScaleIoComponentDetails(final List<ComponentEndpointDetails> componentEndpointDetailsList)
    {
        //TODO: Complete this
    }

    @Override
    public void saveVCenterComponentDetails(final List<ComponentEndpointDetails> componentEndpointDetailsList)
    {
        //TODO: Complete this
    }

    @Override
    public void saveVCenterData()
    {
        //TODO: Complete this
    }

    @Override
    public void saveScaleIoData()
    {
        //TODO: Complete this
    }

    @Override
    public Host getVCenterHost(final String hostName) throws NoResultException
    {
        final TypedQuery<Host> query = entityManager.createQuery("SELECT h FROM Host as h where h.name=:hostName", Host.class);
        query.setParameter("hostName", hostName);
        return query.getSingleResult();
    }
}
