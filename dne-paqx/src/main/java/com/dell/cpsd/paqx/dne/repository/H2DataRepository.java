package com.dell.cpsd.paqx.dne.repository;

import com.dell.cpsd.paqx.dne.domain.ComponentEndpoint;
import com.dell.cpsd.paqx.dne.domain.DneJob;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.domain.vcenter.PortGroup;
import com.dell.cpsd.paqx.dne.domain.vcenter.VCenter;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointDetails;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.EndpointCredentials;
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
    @Transactional
    public boolean saveScaleIoComponentDetails(final List<ComponentEndpointDetails> componentEndpointDetailsList)
    {
        LOG.info("Persisting ScaleIO Component, Endpoint and Credential UUID");

        if (componentEndpointDetailsList.isEmpty())
        {
            LOG.error("No Components Found");
            return false;
        }

        //For MVP Getting the first component - this can be selected from UI later on
        final ComponentEndpointDetails componentEndpointDetails = componentEndpointDetailsList.get(0);
        final List<EndpointCredentials> endpointCredentialList = componentEndpointDetails.getEndpointCredentials();

        if (endpointCredentialList.isEmpty())
        {
            LOG.error("No Endpoints Found");
            return false;
        }

        //For MVP Getting the first endpoint - this can be selected from UI later on
        final EndpointCredentials endpointCredentials = endpointCredentialList.get(0);
        final List<String> credentialUuids = endpointCredentials.getCredentialUuids();

        if (credentialUuids.isEmpty())
        {
            LOG.error("No Credentials Found");
            return false;
        }

        //For MVP Getting the first credential - this can be selected from UI later on
        final String credentialUuid = credentialUuids.get(0);
        final String endpointUuid = endpointCredentials.getEndpointUuid();
        final String endpointUrl = endpointCredentials.getEndpointUrl();
        final String componentUuid = componentEndpointDetails.getComponentUuid();

        final ComponentEndpoint componentEndpoint = new ComponentEndpoint();
        componentEndpoint.setComponentUuid(componentUuid);
        componentEndpoint.setEndpointUuid(endpointUuid);
        componentEndpoint.setCredentialUuid(credentialUuid);
        componentEndpoint.setEndpointUrl(endpointUrl);
        componentEndpoint.setType("SCALEIO");

        entityManager.persist(componentEndpoint);

        return componentEndpoint.getUuid() != null;
    }

    @Override
    @Transactional
    public boolean saveVCenterComponentDetails(final List<ComponentEndpointDetails> componentEndpointDetailsList)
    {
        LOG.info("Persisting VCenter Component, Endpoint and Credential UUID");

        if (componentEndpointDetailsList.isEmpty())
        {
            LOG.error("No Components Found");
            return false;
        }

        //For MVP Getting the first component - this can be selected from UI later on
        final ComponentEndpointDetails componentEndpointDetails = componentEndpointDetailsList.get(0);
        final List<EndpointCredentials> endpointCredentialList = componentEndpointDetails.getEndpointCredentials();

        if (endpointCredentialList.isEmpty())
        {
            LOG.error("No Endpoints Found");
            return false;
        }

        //For MVP Getting the first endpoint - this can be selected from UI later on
        final EndpointCredentials endpointCredentials = endpointCredentialList.get(0);
        final List<String> credentialUuids = endpointCredentials.getCredentialUuids();

        if (credentialUuids.isEmpty())
        {
            LOG.error("No Credentials Found");
            return false;
        }

        //For MVP Getting the first credential - this can be selected from UI later on
        final String credentialUuid = credentialUuids.get(0);
        final String endpointUuid = endpointCredentials.getEndpointUuid();
        final String endpointUrl = endpointCredentials.getEndpointUrl();
        final String componentUuid = componentEndpointDetails.getComponentUuid();

        final ComponentEndpoint componentEndpoint = new ComponentEndpoint();
        componentEndpoint.setComponentUuid(componentUuid);
        componentEndpoint.setEndpointUuid(endpointUuid);
        componentEndpoint.setCredentialUuid(credentialUuid);
        componentEndpoint.setEndpointUrl(endpointUrl);
        componentEndpoint.setType("VCENTER");

        entityManager.persist(componentEndpoint);

        return componentEndpoint.getUuid() != null;
    }

    @Override
    public ComponentEndpointIds getComponentEndpointIds(final String componentType)
    {
        final TypedQuery<ComponentEndpoint> typedQuery = entityManager
                .createQuery("select ceids from ComponentEndpoint as ceids where ceids.type = :componentType", ComponentEndpoint.class);
        typedQuery.setParameter("componentType", componentType);

        final List<ComponentEndpoint> componentEndpointList = typedQuery.getResultList();

        if (componentEndpointList != null && !componentEndpointList.isEmpty())
        {
            //For MVP, fetching the first credential
            final ComponentEndpoint componentEndpoint = componentEndpointList.get(0);

            return new ComponentEndpointIds(componentEndpoint.getComponentUuid(), componentEndpoint.getEndpointUuid(),
                    componentEndpoint.getEndpointUrl(), componentEndpoint.getCredentialUuid());
        }

        LOG.error("No Component Endpoints found in the database");

        return null;
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

    @Override
    public ScaleIOData getScaleIoData(final String jobId)
    {
        final TypedQuery<ScaleIOData> query = entityManager
                .createQuery("SELECT dneJob.scaleIOData FROM DneJob as dneJob WHERE dneJob.id = :jobId", ScaleIOData.class);
        query.setParameter("jobId", jobId);

        final List<ScaleIOData> scaleIODataList = query.getResultList();

        if (scaleIODataList != null && !scaleIODataList.isEmpty())
        {
            return scaleIODataList.stream().findFirst().orElseGet(null);
        }

        return null;
    }
}
