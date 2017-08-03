/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.transformers;

import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIODevice;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOFaultSet;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOIP;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOMasterElementInfo;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOMasterScaleIOIP;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOMdmCluster;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOPrimaryMDMIP;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOProtectionDomain;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIORoleIP;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOSDC;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOSDCVolume;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOSDS;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOSDSElementInfo;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOSecondaryMDMIP;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOSlaveElementInfo;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOStoragePool;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOTiebreakerElementInfo;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOTiebreakerScaleIOIP;
import com.dell.cpsd.paqx.dne.domain.scaleio.StoragePool;
import com.dell.cpsd.storage.capabilities.api.MasterDataRestRep;
import com.dell.cpsd.storage.capabilities.api.MdmClusterDataRestRep;
import com.dell.cpsd.storage.capabilities.api.ScaleIODeviceDataRestRep;
import com.dell.cpsd.storage.capabilities.api.ScaleIOFaultSetDataRestRep;
import com.dell.cpsd.storage.capabilities.api.ScaleIOProtectionDomainDataRestRep;
import com.dell.cpsd.storage.capabilities.api.ScaleIOSDCDataRestRep;
import com.dell.cpsd.storage.capabilities.api.ScaleIOSDSDataRestRep;
import com.dell.cpsd.storage.capabilities.api.ScaleIOSDSIPDataRestRep;
import com.dell.cpsd.storage.capabilities.api.ScaleIOStoragePoolDataRestRep;
import com.dell.cpsd.storage.capabilities.api.ScaleIOSystemDataRestRep;
import com.dell.cpsd.storage.capabilities.api.SlavesDataRestRep;
import com.dell.cpsd.storage.capabilities.api.TieBreakersDataRestRep;
import com.dell.cpsd.storage.capabilities.api.Volume;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Transformer that transforms the ScaleIO Rest Data representation
 * to the
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
@Component
public class ScaleIORestToScaleIODomainTransformer
{
    public ScaleIOData transform(final ScaleIOSystemDataRestRep scaleIOSystemDataRestRep)
    {
        if (scaleIOSystemDataRestRep == null)
        {
            return null;
        }
        //Create the scaleIODataObject
        final ScaleIOData returnVal = new ScaleIOData(scaleIOSystemDataRestRep.getId(), scaleIOSystemDataRestRep.getName(),
                scaleIOSystemDataRestRep.getInstallId(), scaleIOSystemDataRestRep.getMdmMode(),
                scaleIOSystemDataRestRep.getSystemVersionName(), scaleIOSystemDataRestRep.getMdmClusterState(),
                scaleIOSystemDataRestRep.getVersion());

        //Link IP lists
        linkTieBreakerMdmIPList(returnVal, scaleIOSystemDataRestRep);
        linkPrimaryMdmIPList(returnVal, scaleIOSystemDataRestRep);
        linkSecondaryMdmIPList(returnVal, scaleIOSystemDataRestRep);

        //Link SDCs
        linkSDCs(returnVal, scaleIOSystemDataRestRep);

        //Link MDMCluster
        linkMDMCluster(returnVal, scaleIOSystemDataRestRep);

        //Find protection domains and fault sets
        final Set<ScaleIOProtectionDomain> protectionDomains = findProtectionDomainsFromSDS(scaleIOSystemDataRestRep.getSdsList());
        linkProtectionDomainsToData(protectionDomains, returnVal);

        final Set<ScaleIOFaultSet> faultSets = findFaultSetsFromSDS(scaleIOSystemDataRestRep.getSdsList());
        final Set<ScaleIOStoragePool> storagePools = findStoragePoolsFromSDS(scaleIOSystemDataRestRep.getSdsList());
        //Each of these will now be linked as we go through the SDS's

        //Iterate through SDS
        linkSDSs(returnVal, scaleIOSystemDataRestRep, protectionDomains, faultSets, storagePools);

        //FINALLY
        return returnVal;
    }

    /**
     * This method transforms the scale io sdc volumes.
     *
     * @param returnVal             ScaleIO data domain
     * @param scaleIOSDCDataRestRep ScaleIo SDC Rest Rep
     * @since 1.0
     */
    private void linkSDCVolumes(final ScaleIOSDC returnVal, final ScaleIOSDCDataRestRep scaleIOSDCDataRestRep)
    {
        final List<Volume> scaleIOSDCVolumes = scaleIOSDCDataRestRep.getVolumeList();
        if (scaleIOSDCVolumes != null && !scaleIOSDCVolumes.isEmpty())
        {
            scaleIOSDCVolumes.stream().filter(Objects::nonNull).forEach(volume -> {
                final ScaleIOSDCVolume scaleIOSDCVolume = new ScaleIOSDCVolume(volume.getId(), volume.getName(), volume.getSizeInKb(),
                        volume.getStoragePoolId(), volume.getVolumeType(), volume.getVtreeId());
                scaleIOSDCVolume.setStoragePool(transformStoragePool(volume.getStoragePool(), scaleIOSDCVolume));
                scaleIOSDCVolume.addScaleIOSDC(returnVal);
                returnVal.addSDCVolume(scaleIOSDCVolume);
            });
        }
    }

    /**
     * This method transforms the storage pool rest rep to domain model.
     *
     * @param storagePool      Storage Pool Rest Rep
     * @param scaleIOSDCVolume SDC Volume Domain
     * @return Storage Pool Domain
     * @since 1.0
     */
    private StoragePool transformStoragePool(final com.dell.cpsd.storage.capabilities.api.StoragePool storagePool,
            final ScaleIOSDCVolume scaleIOSDCVolume)
    {
        final StoragePool returnVal = new StoragePool(storagePool.getId(), storagePool.getName(), storagePool.getProtectionDomainId());
        returnVal.setScaleIoSdcVolume(scaleIOSDCVolume);
        return returnVal;
    }

    private void linkSDSs(final ScaleIOData returnVal, final ScaleIOSystemDataRestRep scaleIOSystemDataRestRep,
            final Set<ScaleIOProtectionDomain> protectionDomains, final Set<ScaleIOFaultSet> faultSets,
            final Set<ScaleIOStoragePool> storagePools)
    {
        if (scaleIOSystemDataRestRep != null)
        {
            final List<ScaleIOSDSDataRestRep> sdsRepList = scaleIOSystemDataRestRep.getSdsList();
            if (sdsRepList != null)
            {
                sdsRepList.stream().filter(Objects::nonNull).forEach(sds -> {
                    final ScaleIOSDS domainSDS = new ScaleIOSDS(sds.getId(), sds.getName(), sds.getSdsState(),
                            sds.getPort() == null ? -1 : Integer.valueOf(sds.getPort()));
                    domainSDS.setScaleIOData(returnVal);
                    returnVal.addSds(domainSDS);

                    linkSDSToProtectionDomain(domainSDS, protectionDomains, sds.getProtectionDomainId());
                    linkSDSToFaultSet(domainSDS, faultSets, sds.getFaultSetId());

                    //linkProtectionDomainToFaultSet
                    linkProtectionDomainToFaultSet(protectionDomains, faultSets, sds.getProtectionDomainId(), sds.getFaultSetId());

                    linkSDSToDevices(domainSDS, sds, storagePools, protectionDomains);

                    linkIPListToSDS(domainSDS, sds);
                });
            }
        }

    }

    private void linkIPListToSDS(final ScaleIOSDS domainSDS, final ScaleIOSDSDataRestRep scaleIOSystemDataRestRep)
    {
        if (scaleIOSystemDataRestRep != null)
        {
            final List<ScaleIOSDSIPDataRestRep> restIPList = scaleIOSystemDataRestRep.getIpList();

            if (restIPList != null)
            {
                restIPList.forEach(x -> {
                    final ScaleIORoleIP roleIP = new ScaleIORoleIP(x.getRole(), x.getIp());
                    domainSDS.addRoleIP(roleIP);
                    roleIP.setSds(domainSDS);
                });
            }
        }
    }

    private void linkProtectionDomainToFaultSet(final Set<ScaleIOProtectionDomain> protectionDomains, final Set<ScaleIOFaultSet> faultSets,
            final String protectionDomainId, final String faultSetId)
    {
        final ScaleIOProtectionDomain pd = protectionDomains == null ?
                null :
                protectionDomains.stream().filter(Objects::nonNull).filter(x -> x.getId().equals(protectionDomainId)).findFirst()
                        .orElse(null);
        final ScaleIOFaultSet fs = faultSets == null ?
                null :
                faultSets.stream().filter(Objects::nonNull).filter(x -> x.getId().equals(faultSetId)).findFirst().orElse(null);

        if (pd != null && fs != null)
        {
            fs.setProtectionDomain(pd);
            pd.addFaultSet(fs);
        }
    }

    private void linkSDSToDevices(final ScaleIOSDS domainSDS, final ScaleIOSDSDataRestRep sds, final Set<ScaleIOStoragePool> storagePools,
            final Set<ScaleIOProtectionDomain> protectionDomains)
    {
        if (sds != null)
        {
            final List<ScaleIODeviceDataRestRep> deviceList = sds.getDeviceList();

            if (deviceList != null)
            {
                deviceList.stream().filter(Objects::nonNull).forEach(device -> {
                    final ScaleIODevice domainDevice = new ScaleIODevice(device.getId(), device.getName(),
                            device.getDeviceCurrentPathName());
                    domainDevice.setSds(domainSDS);
                    domainSDS.addDevice(domainDevice);

                    linkStoragePoolToDevice(domainDevice, device.getStoragePoolId(), storagePools);
                    linkStoragePoolToProtectionDomain(storagePools, device.getStoragePoolId(), protectionDomains,
                            device.getScaleIOStoragePoolDataRestRep().getProtectionDomainId());
                });
            }
        }
    }

    private void linkStoragePoolToProtectionDomain(final Set<ScaleIOStoragePool> storagePools, final String storagePoolId,
            final Set<ScaleIOProtectionDomain> protectionDomains, final String protectionDomainId)
    {
        final ScaleIOStoragePool storagePool = storagePools == null ?
                null :
                storagePools.stream().filter(Objects::nonNull).filter(x -> x.getId().equals(storagePoolId)).findFirst().orElse(null);
        final ScaleIOProtectionDomain pd = protectionDomains == null ?
                null :
                protectionDomains.stream().filter(Objects::nonNull).filter(x -> x.getId().equals(protectionDomainId)).findFirst()
                        .orElse(null);

        if (pd != null && storagePool != null)
        {
            storagePool.setProtectionDomain(pd);
            pd.addStoragePool(storagePool);
        }
    }

    private void linkStoragePoolToDevice(final ScaleIODevice domainDevice, final String storagePoolId, Set<ScaleIOStoragePool> storagePools)
    {
        final ScaleIOStoragePool storagePool = storagePools == null ?
                null :
                storagePools.stream().filter(Objects::nonNull).filter(x -> x.getId().equals(storagePoolId)).findFirst().orElse(null);
        if (storagePool != null)
        {
            storagePool.addDevice(domainDevice);
            domainDevice.setStoragePool(storagePool);
        }
    }

    private void linkSDSToFaultSet(final ScaleIOSDS domainSDS, final Set<ScaleIOFaultSet> faultSets, final String faultSetId)
    {
        final ScaleIOFaultSet fs = faultSets == null ?
                null :
                faultSets.stream().filter(Objects::nonNull).filter(x -> x.getId().equals(faultSetId)).findFirst().orElse(null);

        if (fs != null)
        {
            domainSDS.setFaultSet(fs);
            fs.addSDS(domainSDS);
        }
    }

    private void linkSDSToProtectionDomain(final ScaleIOSDS domainSDS, final Set<ScaleIOProtectionDomain> protectionDomains,
            final String protectionDomainId)
    {
        final ScaleIOProtectionDomain pd = protectionDomains == null ?
                null :
                protectionDomains.stream().filter(Objects::nonNull).filter(x -> x.getId().equals(protectionDomainId)).findFirst()
                        .orElse(null);

        if (pd != null)
        {
            domainSDS.setProtectionDomain(pd);
            pd.addSDS(domainSDS);
        }
    }

    private void linkProtectionDomainsToData(final Set<ScaleIOProtectionDomain> protectionDomains, final ScaleIOData returnVal)
    {
        if (protectionDomains != null)
        {
            protectionDomains.stream().filter(Objects::nonNull).forEach(domain -> {
                domain.setScaleIOData(returnVal);
                returnVal.addProtectionDomain(domain);
            });
        }
    }

    private Set<ScaleIOStoragePool> findStoragePoolsFromSDS(final List<ScaleIOSDSDataRestRep> sdsList)
    {
        return sdsList == null ?
                null :
                sdsList.stream().filter(Objects::nonNull).map(ScaleIOSDSDataRestRep::getDeviceList).flatMap(List::stream)
                        .map(y -> createStoragePoolFromRest(y.getScaleIOStoragePoolDataRestRep())).collect(Collectors.toSet());
    }

    private ScaleIOStoragePool createStoragePoolFromRest(final ScaleIOStoragePoolDataRestRep scaleIOStoragePoolDataRestRep)
    {
        if (scaleIOStoragePoolDataRestRep == null)
        {
            return null;
        }
        return new ScaleIOStoragePool(scaleIOStoragePoolDataRestRep.getId(), scaleIOStoragePoolDataRestRep.getName(),
                scaleIOStoragePoolDataRestRep.getCapacityAvailableForVolumeAllocationInKb() == null ?
                        -1 :
                        Integer.valueOf(scaleIOStoragePoolDataRestRep.getCapacityAvailableForVolumeAllocationInKb()),
                scaleIOStoragePoolDataRestRep.getMaxCapacityInKb() == null ?
                        -1 :
                        Integer.valueOf(scaleIOStoragePoolDataRestRep.getMaxCapacityInKb()),
                scaleIOStoragePoolDataRestRep.getNumOfVolumes() == null ?
                        -1 :
                        Integer.valueOf(scaleIOStoragePoolDataRestRep.getNumOfVolumes()));
    }

    private Set<ScaleIOProtectionDomain> findProtectionDomainsFromSDS(final List<ScaleIOSDSDataRestRep> sdsList)
    {
        return sdsList == null ?
                null :
                sdsList.stream().filter(Objects::nonNull).map(ScaleIOSDSDataRestRep::getScaleIOProtectionDomainDataRestRep)
                        .map(this::createProtectionDomainFromRest).collect(Collectors.toSet());
    }

    private Set<ScaleIOFaultSet> findFaultSetsFromSDS(final List<ScaleIOSDSDataRestRep> sdsList)
    {
        return sdsList == null ?
                null :
                sdsList.stream().filter(Objects::nonNull).map(ScaleIOSDSDataRestRep::getScaleIOFaultSetDataRestRep)
                        .map(this::createFaultSetFromRest).collect(Collectors.toSet());
    }

    private ScaleIOFaultSet createFaultSetFromRest(final ScaleIOFaultSetDataRestRep y)
    {
        if (y == null)
        {
            return null;
        }
        return new ScaleIOFaultSet(y.getId(), y.getName());
    }

    private ScaleIOProtectionDomain createProtectionDomainFromRest(final ScaleIOProtectionDomainDataRestRep y)
    {
        if (y == null)
        {
            return null;
        }
        return new ScaleIOProtectionDomain(y.getId(), y.getName(), y.getProtectionDomainState());
    }

    private void linkMDMCluster(final ScaleIOData returnVal, final ScaleIOSystemDataRestRep scaleIOSystemDataRestRep)
    {
        if (scaleIOSystemDataRestRep != null)
        {
            final MdmClusterDataRestRep cluster = scaleIOSystemDataRestRep.getMdmClusterDataRestRep();
            if (cluster != null)
            {
                final ScaleIOMdmCluster domainCluster = new ScaleIOMdmCluster(cluster.getId(), cluster.getName(), cluster.getClusterState(),
                        cluster.getClusterMode(), cluster.getGoodNodesNum() == null ? -1 : Integer.valueOf(cluster.getGoodNodesNum()),
                        cluster.getGoodReplicasNum() == null ? -1 : Integer.valueOf(cluster.getGoodReplicasNum()));
                domainCluster.setScaleIOData(returnVal);
                returnVal.setMdmCluster(domainCluster);

                //Now need to add master, slave and tiebreaker
                addMasterToCluster(domainCluster, cluster.getMasterDataRestRep());
                addSlavesToCluster(domainCluster, cluster.getSlaves());
                addTiebreakersToCluster(domainCluster, cluster.getTieBreakers());
            }
        }
    }

    private void addTiebreakersToCluster(final ScaleIOMdmCluster domainCluster, final List<TieBreakersDataRestRep> tieBreakers)
    {
        if (tieBreakers != null)
        {
            tieBreakers.stream().filter(Objects::nonNull).forEach(tieBreaker -> addTiebreakerToCluster(domainCluster, tieBreaker));
        }
    }

    private void addTiebreakerToCluster(final ScaleIOMdmCluster domainCluster, final TieBreakersDataRestRep tiebreaker)
    {
        if (tiebreaker != null)
        {
            final ScaleIOTiebreakerElementInfo domainTiebreaker = new ScaleIOTiebreakerElementInfo(tiebreaker.getId(),
                    Integer.valueOf(tiebreaker.getPort()), tiebreaker.getVersionInfo(), tiebreaker.getName(), tiebreaker.getRole(),
                    tiebreaker.getStatus());
            domainCluster.addTiebreaker(domainTiebreaker);
            domainTiebreaker.setMdmCluster(domainCluster);
            //Now add ips and management ips
            addIPs(domainTiebreaker, tiebreaker.getIps(), "tiebreaker");
            addManagementIPs(domainTiebreaker, tiebreaker.getManagementIPs());
        }
    }

    private void addSlavesToCluster(final ScaleIOMdmCluster domainCluster, final List<SlavesDataRestRep> slaves)
    {
        if (slaves != null)
        {
            slaves.stream().filter(Objects::nonNull).forEach(slave -> addSlaveToCluster(domainCluster, slave));
        }
    }

    private void addSlaveToCluster(final ScaleIOMdmCluster domainCluster, final SlavesDataRestRep slave)
    {
        if (slave != null)
        {
            final ScaleIOSlaveElementInfo domainSlave = new ScaleIOSlaveElementInfo(slave.getId(), Integer.valueOf(slave.getPort()),
                    slave.getVersionInfo(), slave.getName(), slave.getRole(), slave.getStatus());
            domainCluster.addSlave(domainSlave);
            domainSlave.setMdmCluster(domainCluster);
            //Now add ips and management ips
            addIPs(domainSlave, slave.getIps(), "slave");
            addManagementIPs(domainSlave, slave.getManagementIPs());
        }
    }

    private void addMasterToCluster(final ScaleIOMdmCluster domainCluster, final MasterDataRestRep masterDataRestRep)
    {
        if (masterDataRestRep != null)
        {
            final ScaleIOMasterElementInfo master = new ScaleIOMasterElementInfo(masterDataRestRep.getId(),
                    Integer.valueOf(masterDataRestRep.getPort()), masterDataRestRep.getVersionInfo(), masterDataRestRep.getName(),
                    masterDataRestRep.getRole());
            domainCluster.addMaster(master);
            master.setMdmCluster(domainCluster);
            //Now add ips and management ips
            addIPs(master, masterDataRestRep.getIps(), "master");
            addManagementIPs(master, masterDataRestRep.getManagementIPs());
        }
    }

    private void addIPs(final ScaleIOSDSElementInfo master, final List<String> ips, String type)
    {
        if (ips != null)
        {
            ips.stream().filter(Objects::nonNull).forEach(ip -> {
                final ScaleIOIP domainIP = new ScaleIOIP(ip);
                domainIP.setType(type);
                domainIP.setScaleIOSDSElementInfo(master);
                master.addIP(domainIP);
            });
        }
    }

    private void addManagementIPs(final ScaleIOSDSElementInfo master, final List<String> ips)
    {
        if (ips != null)
        {
            ips.stream().filter(Objects::nonNull).forEach(ip -> {
                final ScaleIOMasterScaleIOIP domainIP = new ScaleIOMasterScaleIOIP(master.getId(), ip);
                domainIP.setScaleIOSDSElementInfo(master);
                master.addManagementIP(domainIP);
            });
        }
    }

    private void linkSDCs(final ScaleIOData returnVal, final ScaleIOSystemDataRestRep scaleIOSystemDataRestRep)
    {
        if (scaleIOSystemDataRestRep != null)
        {
            final List<ScaleIOSDCDataRestRep> repSdcList = scaleIOSystemDataRestRep.getSdcList();
            if (repSdcList != null)
            {
                repSdcList.stream().filter(Objects::nonNull).forEach(sdc -> {
                    final ScaleIOSDC domainSDC = new ScaleIOSDC(sdc.getId(), sdc.getName(), sdc.getSdcIp(), sdc.getSdcGuid(),
                            sdc.getMdmConnectionState());
                    linkSDCVolumes(domainSDC, sdc);
                    domainSDC.setScaleIOData(returnVal);
                    returnVal.addSdc(domainSDC);
                });
            }
        }
    }

    private void linkSecondaryMdmIPList(final ScaleIOData returnVal, final ScaleIOSystemDataRestRep scaleIOSystemDataRestRep)
    {
        if (scaleIOSystemDataRestRep != null)
        {
            final List<String> secondaryMdmActorIpList = scaleIOSystemDataRestRep.getSecondaryMdmActorIpList();
            if (secondaryMdmActorIpList != null)
            {
                //Link secondaryMdmActorIpList
                final List<ScaleIOIP> secondaryMdmActorIps = new ArrayList<>();
                secondaryMdmActorIpList.stream().filter(Objects::nonNull).forEach(ip -> {
                    final ScaleIOIP domainIP = new ScaleIOSecondaryMDMIP(returnVal.getId(), ip);
                    domainIP.setScaleIOData(returnVal);
                    secondaryMdmActorIps.add(domainIP);
                });

                returnVal.setSecondaryMDMIPList(secondaryMdmActorIps);
            }
        }
    }

    private void linkPrimaryMdmIPList(final ScaleIOData returnVal, final ScaleIOSystemDataRestRep scaleIOSystemDataRestRep)
    {
        if (scaleIOSystemDataRestRep != null)
        {
            final List<String> primaryMdmActorIpList = scaleIOSystemDataRestRep.getPrimaryMdmActorIpList();

            if (primaryMdmActorIpList != null)
            {
                //Link primaryMdmActorIpList
                final List<ScaleIOIP> primaryMdmActorIps = new ArrayList<>();
                primaryMdmActorIpList.stream().filter(Objects::nonNull).forEach(ip -> {
                    final ScaleIOIP domainIP = new ScaleIOPrimaryMDMIP(returnVal.getId(), ip);
                    domainIP.setScaleIOData(returnVal);
                    primaryMdmActorIps.add(domainIP);
                });

                returnVal.setPrimaryMDMIPList(primaryMdmActorIps);
            }
        }
    }

    private void linkTieBreakerMdmIPList(final ScaleIOData returnVal, final ScaleIOSystemDataRestRep scaleIOSystemDataRestRep)
    {
        if (scaleIOSystemDataRestRep != null)
        {
            final List<String> tiebreakerMdmIpList = scaleIOSystemDataRestRep.getTiebreakerMdmIpList();
            if (tiebreakerMdmIpList != null)
            {
                //Link tiebreakerMdmIPList
                final List<ScaleIOIP> tiebreakerDomainIps = new ArrayList<>();
                tiebreakerMdmIpList.stream().filter(Objects::nonNull).forEach(ip -> {
                    final ScaleIOIP domainIP = new ScaleIOTiebreakerScaleIOIP(returnVal.getId(), ip);
                    domainIP.setScaleIOData(returnVal);
                    tiebreakerDomainIps.add(domainIP);
                });
                returnVal.setTiebreakerScaleIOList(tiebreakerDomainIps);
            }
        }
    }
}
