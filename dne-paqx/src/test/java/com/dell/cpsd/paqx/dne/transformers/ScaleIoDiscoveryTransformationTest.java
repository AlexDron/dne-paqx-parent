/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.transformers;


import com.dell.cpsd.paqx.dne.TestUtil;
import com.dell.cpsd.paqx.dne.amqp.config.RabbitConfig;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOSDC;
import com.dell.cpsd.storage.capabilities.api.ListStorageResponseMessage;
import com.dell.cpsd.storage.capabilities.api.MdmClusterDataRestRep;
import com.dell.cpsd.storage.capabilities.api.ScaleIOSystemDataRestRep;
import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.MessageConverter;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The test class for testing the actual scaleio discovery response.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class ScaleIoDiscoveryTransformationTest
{
    private ScaleIORestToScaleIODomainTransformer transformer;

    private ListStorageResponseMessage entity;

    @Before
    public void setUp() throws Exception
    {
        final RabbitConfig config = new RabbitConfig();
        final MessageConverter converter = config.dneMessageConverter();
        transformer = new ScaleIORestToScaleIODomainTransformer();

        final Message message = TestUtil.jsonMessage("com.dell.cpsd.list.storage.response",
                "src/test/resources/scaleIODiscoveryResponsePayload.json");
        entity = (ListStorageResponseMessage) converter.fromMessage(message);
    }
    @Test
    public void testNullCase() throws Exception
    {
        assertNull(transformer.transform(null));
    }

    @Test
    public void testEmptyRep() throws Exception
    {
        final ScaleIOSystemDataRestRep rep = new ScaleIOSystemDataRestRep();
        assertTrue(transformer.transform(rep) != null);
    }

    @Test
    public void testEmptyMDMCluster() throws Exception
    {
        final ScaleIOSystemDataRestRep rep = new ScaleIOSystemDataRestRep();
        rep.setMdmClusterDataRestRep(new MdmClusterDataRestRep());
        transformer.transform(rep);
    }

    @Test
    public void testRealScaleIoDiscoveryResponse() throws Exception
    {
        assertNotNull(entity);

        final ScaleIOSystemDataRestRep rep = entity.getScaleIOSystemDataRestRep();

        final ScaleIOData domainObject = transformer.transform(rep);

        assertTrue(domainObject != null);

        assertEquals(domainObject.getId(), "1116c4e3676c415d");
        assertEquals(domainObject.getName(), "mcr1-sio2");

        final List<ScaleIOSDC> scaleIOSDCList = domainObject.getSdcList();

        assertNotNull(domainObject.getSdcList());
        assertEquals(entity.getScaleIOSystemDataRestRep().getSdcList().size(), scaleIOSDCList.size());

        final ScaleIOSDC scaleIOSDC_1 = scaleIOSDCList.get(0);

        assertNotNull(scaleIOSDC_1);
        assertNotNull(scaleIOSDC_1.getScaleIOSDCVolumes());
        assertEquals(entity.getScaleIOSystemDataRestRep().getSdcList().get(0).getVolumeList().size(),
                scaleIOSDC_1.getScaleIOSDCVolumes().size());

        final ScaleIOSDC scaleIOSDC_2 = scaleIOSDCList.get(1);

        assertNotNull(scaleIOSDC_2);
        assertNotNull(scaleIOSDC_2.getScaleIOSDCVolumes());
        assertEquals(entity.getScaleIOSystemDataRestRep().getSdcList().get(1).getVolumeList().size(),
                scaleIOSDC_2.getScaleIOSDCVolumes().size());
    }
}