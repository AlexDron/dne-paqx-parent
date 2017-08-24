/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.TestUtil;
import com.dell.cpsd.paqx.dne.amqp.config.RabbitConfig;
import com.dell.cpsd.paqx.dne.domain.vcenter.Cluster;
import com.dell.cpsd.paqx.dne.domain.vcenter.DVSwitch;
import com.dell.cpsd.paqx.dne.domain.vcenter.DataCenter;
import com.dell.cpsd.paqx.dne.domain.vcenter.Datastore;
import com.dell.cpsd.paqx.dne.domain.vcenter.Network;
import com.dell.cpsd.paqx.dne.domain.vcenter.VCenter;
import com.dell.cpsd.virtualization.capabilities.api.Datacenter;
import com.dell.cpsd.virtualization.capabilities.api.DiscoveryResponseInfoMessage;
import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.MessageConverter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * The test class for testing the actual vcenter discovery response.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class VcenterDiscoveryTransformationTest
{
    private DiscoveryInfoToVCenterDomainTransformer transformer;

    private DiscoveryResponseInfoMessage entity;

    @Before
    public void setup() throws Exception
    {
        transformer = new DiscoveryInfoToVCenterDomainTransformer();
        final RabbitConfig config = new RabbitConfig();
        final MessageConverter converter = config.dneMessageConverter();

        final Message message = TestUtil.jsonMessage("com.dell.cpsd.vcenter.discover.response",
                "src/test/resources/vcenterResponseDiscoveryPayload.json");
        entity = (DiscoveryResponseInfoMessage) converter.fromMessage(message);
    }

    @Test
    public void testNull() throws Exception
    {
        final VCenter result = transformer.transform(null);
        assertTrue(result == null);
    }

    @Test
    public void testResponseInfoMessageWithEmptyDataCenters() throws Exception
    {
        final VCenter result = transformer.transform(new DiscoveryResponseInfoMessage());
        assertTrue(result != null);
    }

    @Test
    public void testResponseInfoMessageWithOneDataCenter() throws Exception
    {
        final DiscoveryResponseInfoMessage message = new DiscoveryResponseInfoMessage();
        final ArrayList<Datacenter> dataCenterList = new ArrayList<>();
        dataCenterList.add(new Datacenter());
        message.setDatacenters(dataCenterList);
        final VCenter result = transformer.transform(message);
        assertTrue(result != null);
    }

    @Test
    public void testRealResponseTransformation() throws Exception
    {
        assertNotNull(entity);

        final VCenter domainObject = transformer.transform(entity);

        assertTrue(domainObject != null);

        final List<DataCenter> dataCenterList = domainObject.getDataCenterList();

        assertNotNull(dataCenterList);

        assertTrue(dataCenterList.size() == 1);

        final DataCenter dataCenter = dataCenterList.get(0);

        assertNotNull(dataCenter);

        assertEquals(dataCenter.getId(), "datacenter-2");
        assertEquals(dataCenter.getName(), "Datacenter");

        final List<Cluster> clusterList = dataCenter.getClusterList();
        final List<Datastore> datastoreList = dataCenter.getDatastoreList();
        final List<Network> networkList = dataCenter.getNetworkList();
        final List<DVSwitch> dvSwitchList = dataCenter.getDvSwitchList();

        assertNotNull(clusterList);
        assertNotNull(datastoreList);
        assertNotNull(networkList);
        assertNotNull(dvSwitchList);

        assertTrue(clusterList.size() == 2);
        assertTrue(datastoreList.size() == 8);
        assertTrue(networkList.size() == 1);
        assertTrue(dvSwitchList.size() == 3);
    }
}