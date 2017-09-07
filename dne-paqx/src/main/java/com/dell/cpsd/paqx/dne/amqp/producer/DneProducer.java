/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.amqp.producer;

import com.dell.cpsd.*;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettingsRequestMessage;
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
     * list the virtualisation clusters
     *
     * @param request
     */
    void publishDiscoverClusters(DiscoverClusterRequestInfoMessage request);

    /**
     * validate list of clusters
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
     * list the idrac network settings
     *
     * @param request
     */
    void publishIdracNetwokSettings(IdracNetworkSettingsRequestMessage request);
    
    /**
     * change idrac credentials
     * 
     * @param request
     */
    void publishChangeIdracCredentials(ChangeIdracCredentialsRequestMessage request);

    /**
     * configure boot device idrac
     *
     * @param request
     */
    void publishConfigureBootDeviceIdrac(ConfigureBootDeviceIdracRequestMessage request);

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
    void publishListScaleIoComponents(final ListComponentRequestMessage request);

    /**
     * List VCenter Components
     *
     * @param request ListComponentsRequestMessage
     */
    void publishListVCenterComponents(final ListComponentsRequestMessage request);

    /**
     * Discover ScaleIO.
     *
     * @param request ListStorageRequestMessage
     */
    void publishDiscoverScaleIo(final ListStorageRequestMessage request);

    /**
     * Discover VCenter.
     *
     * @param request DiscoveryRequestInfoMessage
     */
    void publishDiscoverVcenter(final DiscoveryRequestInfoMessage request);

    void publishInstallEsxiRequest(final InstallESXiRequestMessage request);

    void publishAddHostToVCenter(final ClusterOperationRequestMessage request);

    void publishInstallScaleIoVib(final SoftwareVIBRequestMessage request);

    void publishConfigureScaleIoVib(final SoftwareVIBConfigureRequestMessage request);

    void publishAddHostToDvSwitch(final AddHostToDvSwitchRequestMessage request);

    void publishDeployVmFromTemplate(final DeployVMFromTemplateRequestMessage request);

    void publishEnablePciPassthrough(final EnablePCIPassthroughRequestMessage request);

    void publishRebootHost(final HostPowerOperationRequestMessage request);

    void publishSetPciPassthrough(final UpdatePCIPassthruSVMRequestMessage request);

    void publishApplyEsxiLicense(final AddEsxiHostVSphereLicenseRequest request);

    void publishListExsiCredentialDetails(final ListEsxiCredentialDetailsRequestMessage requestMessage);

    void publishEsxiHostExitMaintenanceMode(final HostMaintenanceModeRequestMessage requestMessage);

    void publishValidateStorage(EssValidateStoragePoolRequestMessage requestMessage);
}