/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIODevice;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOStoragePool;
import com.dell.cpsd.service.engineering.standards.EssValidateStoragePoolRequestMessage;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for storage transformer.
 *
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

public class StoragePoolEssRequestTransformerTest
{

    private StoragePoolEssRequestTransformer transformer;
    private List<ScaleIOStoragePool>         listScaleIOStoragePool;
    private List<ScaleIODevice>              listDevices;

    @Before
    public void setUp() throws Exception
    {
        transformer = new StoragePoolEssRequestTransformer();
        buildScaleIowithStoragePool();
    }

    @Test
    public void testDomainToEssStoragepoolTransform()
    {
        EssValidateStoragePoolRequestMessage requestMessage = transformer.transform(listScaleIOStoragePool);

        assertNotNull(requestMessage);
        assertEquals(String.valueOf(listScaleIOStoragePool.get(0).getDevices().size()),
                requestMessage.getStoragePools().get(0).getNumberOfDevices());
    }

    private void buildScaleIowithStoragePool()
    {
        listScaleIOStoragePool = new ArrayList<>();
        listDevices = new ArrayList<>();
        ScaleIOStoragePool storagePool = new ScaleIOStoragePool();
        storagePool.setId("1");
        storagePool.setName("SP-1");
        for (int i = 0; i <= 3; i++)
        {
            ScaleIODevice device = new ScaleIODevice();
            device.setId("" + i);
            device.setName("Dv-" + i);
            device.setDeviceState("Normal");

            listDevices.add(device);
        }
        listScaleIOStoragePool.add(storagePool);
    }
}
