/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.repository;

import com.dell.cpsd.paqx.dne.domain.ComponentDetails;
import com.dell.cpsd.paqx.dne.domain.inventory.NodeInventory;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.vcenter.Host;
import com.dell.cpsd.paqx.dne.domain.vcenter.PciDevice;
import com.dell.cpsd.paqx.dne.domain.vcenter.PortGroup;
import com.dell.cpsd.paqx.dne.domain.vcenter.VCenter;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;

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
    boolean saveScaleIoComponentDetails(List<ComponentDetails> componentEndpointDetailsList);

    boolean saveVCenterComponentDetails(List<ComponentDetails> componentEndpointDetailsList);

    /**
     * This method can be used for the MVP, fetches the first
     * component, endpoint, and credential from the list.
     *
     * @param componentType VCenter/ScaleIO
     * @return ComponentEndpointIds
     */
    ComponentEndpointIds getComponentEndpointIds(final String componentType);

    /**
     * This method returns the component endpoint uuids based on
     * the endpoint type defined in the System Definition File.
     *
     * @param endpointType VCENTER-CUSTOMER/VCENTER-MANAGEMENT
     * @return ComponentEndpointIds
     */
    ComponentEndpointIds getVCenterComponentEndpointIdsByEndpointType(final String endpointType);

    boolean saveVCenterData(final String jobId, final VCenter vCenterData);

    boolean saveScaleIoData(final String jobId, final ScaleIOData scaleIOData);

    Host getVCenterHost(final String hostName) throws NoResultException;

    Host getExistingVCenterHost() throws NoResultException;

    List<PortGroup> getPortGroups();

    ScaleIOData getScaleIoDataByJobId(final String jobId);

    /**
     * The MVP Approach, later can be integrated with the Job,
     * when multiple discoveries will be supported.
     *
     * @return ScaleIO Data
     */
    ScaleIOData getScaleIoData();

    List<PciDevice> getPciDeviceList();

    String getClusterId(final String clusterName);

    String getDataCenterName(final String clusterName);

    String getVlanIdVmk0();

    boolean saveNodeInventory(final NodeInventory nodeInventory);

    NodeInventory getNodeIventory(final String symphonyUUID) throws NoResultException;
}
