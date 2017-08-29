/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOStoragePool;
import com.dell.cpsd.service.engineering.standards.EssValidateStoragePoolRequestMessage;
import com.dell.cpsd.service.engineering.standards.StoragePool;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Transform message from ESS format to DNE format.
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

@Component
public class StoragePoolEssRequestTransformer {

    public EssValidateStoragePoolRequestMessage transform(List<ScaleIOStoragePool> scaleIOStoragePools) {
        EssValidateStoragePoolRequestMessage requestMessage = new EssValidateStoragePoolRequestMessage();
        List<StoragePool> storagePools = new ArrayList<>();
        for (ScaleIOStoragePool scaleIOStoragePool : scaleIOStoragePools) {
            StoragePool storagePool = new StoragePool();
            storagePool.setId(scaleIOStoragePool.getId());
            storagePool.setName(scaleIOStoragePool.getName());
            storagePool.setNumberOfDevices("" + scaleIOStoragePool.getDevices().size());
            storagePools.add(storagePool);
        }
        requestMessage.setStoragePools(storagePools);
        return requestMessage;
    }
}
