/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.domain.vcenter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */
@Entity
@Table(name = "PCI_DEVICE")
public class PciDevice
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UUID", unique = true, nullable = false)
    private Long uuid;

    @Column(name = "DEVICE_ID")
    private String deviceId;

    @Column(name = "DEVICE_NAME")
    private String deviceName;

    @Column(name = "VENDOR_ID")
    private String vendorId;

    @Column(name = "VENDOR_NAME")
    private String vendorName;

    @Column(name = "SUBVENDOR_ID")
    private String subVendorId;

    @ManyToOne(cascade = CascadeType.ALL)
    private Host host;

    public PciDevice()
    {
    }

    public PciDevice(final String deviceId, final String deviceName, final String vendorId, final String vendorName,
            final String subVendorId, final Host host)
    {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.subVendorId = subVendorId;
        this.host = host;
    }

    public Long getUuid()
    {
        return uuid;
    }

    public void setUuid(final Long uuid)
    {
        this.uuid = uuid;
    }

    public String getDeviceId()
    {
        return deviceId;
    }

    public void setDeviceId(final String deviceId)
    {
        this.deviceId = deviceId;
    }

    public String getDeviceName()
    {
        return deviceName;
    }

    public void setDeviceName(final String deviceName)
    {
        this.deviceName = deviceName;
    }

    public String getVendorId()
    {
        return vendorId;
    }

    public void setVendorId(final String vendorId)
    {
        this.vendorId = vendorId;
    }

    public String getVendorName()
    {
        return vendorName;
    }

    public void setVendorName(final String vendorName)
    {
        this.vendorName = vendorName;
    }

    public String getSubVendorId()
    {
        return subVendorId;
    }

    public void setSubVendorId(final String subVendorId)
    {
        this.subVendorId = subVendorId;
    }

    public Host getHost()
    {
        return host;
    }

    public void setHost(final Host host)
    {
        this.host = host;
    }
}
