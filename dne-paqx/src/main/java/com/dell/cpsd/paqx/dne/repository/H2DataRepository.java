package com.dell.cpsd.paqx.dne.repository;

import com.dell.cpsd.paqx.dne.domain.DneJob;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.domain.vcenter.PortGroup;
import com.dell.cpsd.paqx.dne.domain.vcenter.VCenter;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public boolean saveVCenterData(final String jobId, final VCenter vCenterData)
    {
        TypedQuery<DneJob> query = entityManager.createQuery("select dne from DneJob as dne where dne.id = :jobId", DneJob.class);

        DneJob dneJob = null;

        try
        {
            dneJob = query.setParameter("jobId", jobId).getSingleResult();
        }
        catch (NoResultException e)
        {
            LOG.error("No job found with id: {}", jobId);
            LOG.error("No result", e.getMessage());
        }

        if (dneJob == null)
        {
            dneJob = new DneJob(jobId, null, vCenterData);
            entityManager.persist(dneJob);
        }
        else
        {
            LOG.info("DneJob != null for vcenter save");
            vCenterData.setJob(dneJob);
            dneJob.setVcenter(vCenterData);
            entityManager.merge(dneJob);
        }
        entityManager.flush();

        if (dneJob.getUuid() == null)
        {
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public boolean saveScaleIoData(final String jobId, final ScaleIOData scaleIOData)
    {
        TypedQuery<DneJob> query = entityManager.createQuery("select dne from DneJob as dne where dne.id = :jobId", DneJob.class);

        DneJob dneJob = null;

        try
        {
            dneJob = query.setParameter("jobId", jobId).getSingleResult();
        }
        catch (NoResultException e)
        {
            LOG.error("No job found with id: {}", jobId);
            LOG.error("No result", e.getMessage());
        }

        if (dneJob == null)
        {
            dneJob = new DneJob(jobId, scaleIOData, null);
            entityManager.persist(dneJob);
        }
        else
        {
            LOG.info("DneJob != null for scaleio save");
            scaleIOData.setJob(dneJob);
            dneJob.setScaleIO(scaleIOData);
            entityManager.merge(dneJob);
        }
        entityManager.flush();

        if (dneJob.getUuid() == null)
        {
            return false;
        }

        return true;
    }

    @Override
    public Host getVCenterHost(final String hostName) throws NoResultException
    {
        final TypedQuery<Host> query = entityManager.createQuery("SELECT h FROM Host as h where h.name=:hostName", Host.class);
        query.setParameter("hostName", hostName);
        return query.getSingleResult();
    }

    @Override
    public List<PortGroup> getPortGroups()
    {
        final TypedQuery<PortGroup> query = entityManager.createQuery("SELECT p FROM PortGroup as p", PortGroup.class);
        return query.getResultList();
    }
}
