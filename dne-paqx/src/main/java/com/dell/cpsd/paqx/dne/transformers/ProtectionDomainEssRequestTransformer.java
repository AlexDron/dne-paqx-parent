package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOProtectionDomain;
import com.dell.cpsd.service.engineering.standards.EssValidateProtectionDomainsRequestMessage;
import com.dell.cpsd.service.engineering.standards.ProtectionDomain;

import java.util.ArrayList;
import java.util.List;

public class ProtectionDomainEssRequestTransformer {

    public EssValidateProtectionDomainsRequestMessage transform(List<ScaleIOProtectionDomain> scaleIOProtectionDomains) {
        EssValidateProtectionDomainsRequestMessage requestMessage = new EssValidateProtectionDomainsRequestMessage();
        List<ProtectionDomain> protectionDomains = new ArrayList<>();
        for (ScaleIOProtectionDomain scaleIOStoragePool : scaleIOProtectionDomains) {
            ProtectionDomain protectionDomain = new ProtectionDomain();
            protectionDomain.setId(scaleIOStoragePool.getId());
            protectionDomain.setName(scaleIOStoragePool.getName());
            protectionDomain.setState(scaleIOStoragePool.getProtectionDomainState());
            protectionDomains.add(protectionDomain);
        }
        requestMessage.setProtectionDomains(protectionDomains);
        return requestMessage;
    }
}
