/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.amqp.producer;

import com.dell.cpsd.ChangeIdracCredentialsRequestMessage;
import com.dell.cpsd.CompleteNodeAllocationRequestMessage;
import com.dell.cpsd.ConfigureBootDeviceIdracRequestMessage;
import com.dell.cpsd.ConfigurePxeBootRequestMessage;
import com.dell.cpsd.InstallESXiRequestMessage;
import com.dell.cpsd.ListNodes;
import com.dell.cpsd.NodeInventoryRequestMessage;
import com.dell.cpsd.SetObmSettingsRequestMessage;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettingsRequestMessage;
import com.dell.cpsd.service.engineering.standards.EssValidateProtectionDomainsRequestMessage;
import com.dell.cpsd.service.engineering.standards.EssValidateStoragePoolRequestMessage;
import com.dell.cpsd.storage.capabilities.api.ListComponentRequestMessage;
import com.dell.cpsd.storage.capabilities.api.ListStorageRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.*;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

public interface DneProducer
{
    /**
     * List the discovered nodes
     *
     * @param request
     */
    void publishListNodes(ListNodes request);

    /**
     * Publish a message to discover the node inventory data
     *
     * @param nodeInventoryRequestMessage
     */
    void publishNodeInventoryDiscovery(NodeInventoryRequestMessage nodeInventoryRequestMessage);

    /**
     * list the virtualisation clusters
     *
     * @param request
     */
    void publishDiscoverClusters(DiscoverClusterRequestInfoMessage request);

    /**
     * Validate list of clusters
     *
     * @param request
     */
    void publishValidateClusters(ValidateVcenterClusterRequestMessage request);

    /**
     * Complete node allocation
     * 
     * @param request
     */
    void publishCompleteNodeAllocation(CompleteNodeAllocationRequestMessage request);

    /**
     * List the idrac network settings
     *
     * @param request
     */
    void publishIdracNetwokSettings(IdracNetworkSettingsRequestMessage request);

    /**
     * Change idrac credentials
     * 
     * @param request
     */
    void publishChangeIdracCredentials(ChangeIdracCredentialsRequestMessage request);

    /**
     * Configure boot device idrac
     *
     * @param request
     */
    void publishConfigureBootDeviceIdrac(ConfigureBootDeviceIdracRequestMessage request);

    /**
     * Configure PXE boot options
     *
     * @param request
     */
    void publishConfigurePxeBoot(ConfigurePxeBootRequestMessage request);

    /**
     * configure boot device idrac
     *
     * @param request
     */
    void publishConfigureObmSettings(SetObmSettingsRequestMessage request);

    /**
     * List ScaleIO Components
     *
     * @param request ListComponentRequestMessage
     */
    void publishListScaleIoComponents(ListComponentRequestMessage request);

    /**
     * List VCenter Components
     *
     * @param request ListComponentsRequestMessage
     */
    void publishListVCenterComponents(ListComponentsRequestMessage request);

    /**
     * Discover ScaleIO.
     *
     * @param request ListStorageRequestMessage
     */
    void publishDiscoverScaleIo(ListStorageRequestMessage request);

    /**
     * Discover VCenter.
     *
     * @param request DiscoveryRequestInfoMessage
     */
    void publishDiscoverVcenter(DiscoveryRequestInfoMessage request);

    /**
     * Install ESXi on a host
     *
     * @param request
     */
    void publishInstallEsxiRequest(InstallESXiRequestMessage request);

    /**
     * Add ESXi host to vCenter
     *
     * @param request
     */
    void publishAddHostToVCenter(ClusterOperationRequestMessage request);

    /**
     * Install ScaleIO VIB
     *
     * @param request
     */
    void publishInstallScaleIoVib(SoftwareVIBRequestMessage request);

    /**
     * Configure ScaleIO VIB
     *
     * @param request
     */
    void publishConfigureScaleIoVib(SoftwareVIBConfigureRequestMessage request);

    /**
     * Add ESXi host to virtual distributed switch
     *
     * @param request
     */
    void publishAddHostToDvSwitch(AddHostToDvSwitchRequestMessage request);

    /**
     * Deploy VM from a template
     *
     * @param request
     */
    void publishDeployVmFromTemplate(DeployVMFromTemplateRequestMessage request);

    /**
     * Enable PCI pass-through
     *
     * @param request
     */
    void publishEnablePciPassthrough(EnablePCIPassthroughRequestMessage request);

    /**
     * Reboot the host
     *
     * @param request
     */
    void publishRebootHost(HostPowerOperationRequestMessage request);

    /**
     * Set PCI pass-through
     *
     * @param request
     */
    void publishSetPciPassthrough(UpdatePCIPassthruSVMRequestMessage request);

    /**
     * Apply the ESXi host vSphere license
     *
     * @param request
     */
    void publishApplyEsxiLicense(AddEsxiHostVSphereLicenseRequest request);

    /**
     * List ESXi host credential details
     *
     * @param requestMessage
     */
    void publishListExsiCredentialDetails(ListEsxiCredentialDetailsRequestMessage requestMessage);

    /**
     * Switch host maintenance modes
     *
     * @param requestMessage
     */
    void publishHostMaintenanceMode(HostMaintenanceModeRequestMessage requestMessage);

    /**
     * Request ESS to validate storage pools
     *
     * @param requestMessage
     */
    void publishValidateStorage(EssValidateStoragePoolRequestMessage requestMessage);

    /**
     * Rename the ESXi host's datastore
     *
     * @param requestMessage
     */
    void publishDatastoreRename(DatastoreRenameRequestMessage requestMessage);

    /**
     * Update the software acceptance level on the ESXi host
     *
     * @param requestMessage
     */
    void publishUpdateSoftwareAcceptance(VCenterUpdateSoftwareAcceptanceRequestMessage requestMessage);

    void publishValidateProtectionDomain(EssValidateProtectionDomainsRequestMessage requestMessage);

}