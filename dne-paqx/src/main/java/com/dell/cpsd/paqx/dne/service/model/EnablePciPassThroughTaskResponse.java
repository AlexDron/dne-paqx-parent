package com.dell.cpsd.paqx.dne.service.model;

/**
 * TODO: Document Usage
 * <p/>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * <p/>
 *
 * @version 1.0
 * @since 1.0
 */
public class EnablePciPassThroughTaskResponse extends TaskResponse
{
    private String hostPciDeviceId;

    public String getHostPciDeviceId()
    {
        return hostPciDeviceId;
    }

    public void setHostPciDeviceId(final String hostPciDeviceId)
    {
        this.hostPciDeviceId = hostPciDeviceId;
    }
}
