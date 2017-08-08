/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service;

import java.util.List;

import com.dell.converged.capabilities.compute.discovered.nodes.api.EsxiInstallationInfo;
import com.dell.cpsd.paqx.dne.service.model.*;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import com.dell.cpsd.virtualization.capabilities.api.ClusterInfo;
import com.dell.cpsd.virtualization.capabilities.api.ValidateVcenterClusterResponseMessage;

public interface NodeService
{
    /**
     * List the discovered nodes
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    List<DiscoveredNode> listDiscoveredNodes() throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * List the virtualisation clusters
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    List<ClusterInfo> listClusters() throws ServiceTimeoutException, ServiceExecutionException;


    /**
     * List the validated cluster names
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    ValidateVcenterClusterResponseMessage validateClusters(List<ClusterInfo> clusterInfoList ) throws ServiceTimeoutException, ServiceExecutionException;


    /**
     * Notify the Node Discovery Service that node allocation is complete
     * 
     * @param elementIdentifier - The node identifier
     * @return true if the node allocation completed successfully, false otherwise.
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    boolean notifyNodeAllocationComplete(String elementIdentifier) throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * Configure the iDRAC network settings.
     * 
     * @param idracNetworkSettingsRequest - The <code>IdracNetworkSettingsRequest</code> instance.
     * 
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    IdracInfo idracNetworkSettings(IdracNetworkSettingsRequest idracNetworkSettingsRequest)
            throws ServiceTimeoutException, ServiceExecutionException;
    
    /**
     * Change Idrac credentials
     * 
     * @param configureBootDeviceIdracRequest - The <code>ChangeIdracCredentialsRequest</code> instance.
     * 
     * @return
     * 
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    ChangeIdracCredentialsResponse changeIdracCredentials(String nodeId) throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * Configure the Boot Device Idrac.
     *
     * @param configureBootDeviceIdracRequest - The <code>ConfigureBootDeviceIdracRequest</code> instance.
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */

    BootDeviceIdracStatus bootDeviceIdracStatus (ConfigureBootDeviceIdracRequest configureBootDeviceIdracRequest)
            throws ServiceTimeoutException, ServiceExecutionException;

    List<ComponentEndpointDetails> requestScaleIoComponents() throws ServiceTimeoutException, ServiceExecutionException;

    List<ComponentEndpointDetails> requestVCenterComponents() throws ServiceTimeoutException, ServiceExecutionException;

    boolean requestDiscoverScaleIo(final ComponentEndpointIds componentEndpointIds, final String jobId)
            throws ServiceTimeoutException, ServiceExecutionException;

    boolean requestDiscoverVCenter(final ComponentEndpointIds componentEndpointIds, final String jobId)
            throws ServiceTimeoutException, ServiceExecutionException;

    boolean requestInstallEsxi(final EsxiInstallationInfo esxiInstallationInfo);
}
