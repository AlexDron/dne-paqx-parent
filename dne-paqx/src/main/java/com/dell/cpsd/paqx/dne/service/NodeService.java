/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service;

import com.dell.cpsd.EsxiInstallationInfo;
import com.dell.cpsd.SetObmSettingsRequestMessage;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOStoragePool;
import com.dell.cpsd.paqx.dne.service.model.*;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import com.dell.cpsd.service.engineering.standards.EssValidateStoragePoolResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.*;

import java.util.List;

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
     * List the virtualisation scaleio data
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    List<ScaleIOData> listScaleIOData() throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * List the rackhd node inventory
     *
     * @return Node Inventory data
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    Object listNodeInventory(String symphonyUUID) throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * List the validated cluster names
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    ValidateVcenterClusterResponseMessage validateClusters(List<ClusterInfo> clusterInfoList)
            throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * Validated Storage pool names
     *
     * @param scaleIOStoragePools
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    EssValidateStoragePoolResponseMessage validateStoragePools(List<ScaleIOStoragePool> scaleIOStoragePools) throws ServiceTimeoutException, ServiceExecutionException;

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
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    IdracInfo idracNetworkSettings(IdracNetworkSettingsRequest idracNetworkSettingsRequest)
            throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * Change Idrac credentials
     *
     * @param nodeId - The <code>ChangeIdracCredentialsRequest</code> instance.
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    ChangeIdracCredentialsResponse changeIdracCredentials(String nodeId) throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * Configure the Boot Device Idrac.
     *
     * @param setObmSettingsRequestMessage - The <code>ConfigureBootDeviceIdracRequest</code> instance.
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */

    ObmSettingsResponse obmSettingsResponse(SetObmSettingsRequestMessage setObmSettingsRequestMessage)
            throws ServiceTimeoutException, ServiceExecutionException;

    BootDeviceIdracStatus bootDeviceIdracStatus(ConfigureBootDeviceIdracRequest configureBootDeviceIdracRequest)
            throws ServiceTimeoutException, ServiceExecutionException;


    BootDeviceIdracStatus configurePxeBoot(String uuid, String ipAddress)
            throws ServiceTimeoutException, ServiceExecutionException;

    boolean requestScaleIoComponents() throws ServiceTimeoutException, ServiceExecutionException;

    boolean requestVCenterComponents() throws ServiceTimeoutException, ServiceExecutionException;

    boolean requestDiscoverScaleIo(final ComponentEndpointIds componentEndpointIds, final String jobId)
            throws ServiceTimeoutException, ServiceExecutionException;

    boolean requestDiscoverVCenter(final ComponentEndpointIds componentEndpointIds, final String jobId)
            throws ServiceTimeoutException, ServiceExecutionException;

    boolean requestInstallEsxi(final EsxiInstallationInfo esxiInstallationInfo, final String idracIp);

    boolean requestAddHostToVCenter(final ClusterOperationRequestMessage requestMessage);

    boolean requestInstallSoftwareVib(final SoftwareVIBRequestMessage requestMessage);

    boolean requestConfigureScaleIoVib(final SoftwareVIBConfigureRequestMessage requestMessage);

    boolean requestAddHostToDvSwitch(final AddHostToDvSwitchRequestMessage requestMessage);

    boolean requestDeployScaleIoVm(final DeployVMFromTemplateRequestMessage requestMessage);

    boolean requestEnablePciPassThrough(final EnablePCIPassthroughRequestMessage requestMessage);

    boolean requestHostReboot(final HostPowerOperationRequestMessage requestMessage);

    boolean requestSetPciPassThrough(final UpdatePCIPassthruSVMRequestMessage requestMessage);

    boolean requestInstallEsxiLicense(final AddEsxiHostVSphereLicenseRequest requestMessage);

    ComponentEndpointIds listDefaultCredentials(final ListEsxiCredentialDetailsRequestMessage requestMessage);

    boolean requestExitHostMaintenanceMode(final HostMaintenanceModeRequestMessage requestMessage);

    boolean requestDatastoreRename(final DatastoreRenameRequestMessage datastoreRenameRequestMessage);

}
