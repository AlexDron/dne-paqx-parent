/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service;

import com.dell.cpsd.EsxiInstallationInfo;
import com.dell.cpsd.SetObmSettingsRequestMessage;
import com.dell.cpsd.paqx.dne.domain.node.DiscoveredNodeInfo;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOStoragePool;
import com.dell.cpsd.paqx.dne.service.model.*;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import com.dell.cpsd.service.engineering.standards.EssValidateProtectionDomainsRequestMessage;
import com.dell.cpsd.service.engineering.standards.EssValidateProtectionDomainsResponseMessage;
import com.dell.cpsd.service.engineering.standards.EssValidateStoragePoolResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.*;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

/**
 * Node service interface.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
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
     * List the discovered nodes with node info
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    List<DiscoveredNodeInfo> listDiscoveredNodeInfo() throws ServiceTimeoutException, ServiceExecutionException, JsonProcessingException;

    // before we have new landing page to list all discovered node, UI will call this api to get only first node.
    DiscoveredNodeInfo getFirstDiscoveredNodeInfo() throws ServiceTimeoutException, ServiceExecutionException, JsonProcessingException;

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
     * Validate protection domains
     *
     * @param essValidateProtectionDomainsRequestMessage
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    EssValidateProtectionDomainsResponseMessage validateProtectionDomains(EssValidateProtectionDomainsRequestMessage essValidateProtectionDomainsRequestMessage) throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * Notify the Node Discovery Service that node allocation is complete
     *
     * @param elementIdentifier - The node identifier
     *
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
     * @param nodeId - The <code>ChangeIdracCredentialsRequest</code> instance.
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    ChangeIdracCredentialsResponse changeIdracCredentials(String nodeId) throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * Configure OBM settings.
     *
     * @param setObmSettingsRequestMessage - The <code>SetObmSettingsRequestMessage</code> instance.
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    ObmSettingsResponse obmSettingsResponse(SetObmSettingsRequestMessage setObmSettingsRequestMessage)
            throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * Configure the iDRAC boot device.
     *
     * @param configureBootDeviceIdracRequest - The <code>ConfigureBootDeviceIdracRequest</code> instance.
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    BootDeviceIdracStatus bootDeviceIdracStatus(ConfigureBootDeviceIdracRequest configureBootDeviceIdracRequest)
            throws ServiceTimeoutException, ServiceExecutionException;

    /**
     *
     *
     * @param uuid - The request message UUID
     * @param ipAddress - The IP address of the iDRAC
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    BootDeviceIdracStatus configurePxeBoot(String uuid, String ipAddress)
            throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * Request a list of ScaleIO components.
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    boolean requestScaleIoComponents() throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * Request a list of VCenter components.
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    boolean requestVCenterComponents() throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * Request a discovery of ScaleIO data.
     *
     * @param componentEndpointIds - The ScaleIO <code>ComponentEndpointIds</code>
     * @param jobId - The job id
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    boolean requestDiscoverScaleIo(ComponentEndpointIds componentEndpointIds, String jobId)
            throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * Request a discovery of VCenter data.
     *
     * @param componentEndpointIds - The VCenter <code>ComponentEndpointIds</code>
     * @param jobId - The job id
     *
     * @return
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    boolean requestDiscoverVCenter(ComponentEndpointIds componentEndpointIds, String jobId)
            throws ServiceTimeoutException, ServiceExecutionException;

    /**
     * Request an installation of ESXi.
     *
     * @param esxiInstallationInfo - The <code>EsxiInstallationInfo</code> instance
     * @param idracIp - The iDRAC IP address
     * @return
     */
    boolean requestInstallEsxi(EsxiInstallationInfo esxiInstallationInfo, String idracIp);

    /**
     * Request a host to be added to VCenter.
     *
     * @param requestMessage - The <code>ClusterOperationRequestMessage</code> instance
     * @return
     */
    boolean requestAddHostToVCenter(ClusterOperationRequestMessage requestMessage);

    /**
     * Request installation of a ScaleIO SDC.
     *
     * @param requestMessage - The <code>SoftwareVIBRequestMessage</code> instance
     * @return
     */
    boolean requestInstallSoftwareVib(SoftwareVIBRequestMessage requestMessage);

    /**
     * Request a configuration of the ScaleIO SDC.
     *
     * @param requestMessage - The <code>SoftwareVIBConfigureRequestMessage</code> instance
     * @return
     */
    boolean requestConfigureScaleIoVib(SoftwareVIBConfigureRequestMessage requestMessage);

    /**
     * Request a host to be added to a virtual distributed switch.
     *
     * @param requestMessage - The <code>AddHostToDvSwitchRequestMessage</code> instance
     * @return
     */
    boolean requestAddHostToDvSwitch(AddHostToDvSwitchRequestMessage requestMessage);

    /**
     * Request the deployment of a ScaleIO SDS virtual machine.
     *
     * @param requestMessage - The <code>DeployVMFromTemplateRequestMessage</code> instance
     * @return
     */
    boolean requestDeployScaleIoVm(DeployVMFromTemplateRequestMessage requestMessage);

    /**
     * Request PCI pass-through for the ScaleIO SDS.
     *
     * @param requestMessage - The <code>EnablePCIPassthroughRequestMessage</code> instance
     * @return
     */
    boolean requestEnablePciPassThrough(EnablePCIPassthroughRequestMessage requestMessage);

    /**
     * Request a host reboot.
     *
     * @param requestMessage - The <code>HostPowerOperationRequestMessage</code>
     * @return
     */
    boolean requestHostReboot(HostPowerOperationRequestMessage requestMessage);

    /**
     * Update the PCI pass-through for the ScaleIO virtual machine.
     *
     * @param requestMessage - The <code>UpdatePCIPassthruSVMRequestMessage</code> instance
     * @return
     */
    boolean requestSetPciPassThrough(UpdatePCIPassthruSVMRequestMessage requestMessage);

    /**
     * Apply the ESXi license.
     *
     * @param requestMessage - The <code>AddEsxiHostVSphereLicenseRequest</code> instance
     * @return
     */
    boolean requestInstallEsxiLicense(AddEsxiHostVSphereLicenseRequest requestMessage);

    /**
     * List the ESXi host credentials.
     *
     * @param requestMessage - The <code>ListEsxiCredentialDetailsRequestMessage</code> instance
     * @return
     */
    ComponentEndpointIds listDefaultCredentials(ListEsxiCredentialDetailsRequestMessage requestMessage);

    /**
     * Request a host to enter or exit maintenance mode.
     *
     * @param requestMessage - The <code>HostMaintenanceModeRequestMessage</code> instance
     * @return
     */
    boolean requestHostMaintenanceMode(HostMaintenanceModeRequestMessage requestMessage);

    /**
     * Rename the datastore assigned to an ESXi host to a specified name.
     *
     * @param requestMessage - The <code>DatastoreRenameRequestMessage</code> instance
     * @return
     */
    boolean requestDatastoreRename(DatastoreRenameRequestMessage requestMessage);

    /**
     * Update the VCenter software acceptance level, e.g. partner, community, vmware accepted, vmware certified, etc.
     *
     * @param requestMessage - The <code>VCenterUpdateSoftwareAcceptanceRequestMessage</code> instance
     * @return
     */
    boolean requestUpdateSoftwareAcceptance(VCenterUpdateSoftwareAcceptanceRequestMessage requestMessage);

    /**
     * Request a host/vm power operation, e.g. power on or off or suspend.
     *
     * @param requestMessage - The <code>VmPowerOperationsRequestMessage</code> instance
     * @return
     */
    boolean requestVmPowerOperation(VmPowerOperationsRequestMessage requestMessage);
}
