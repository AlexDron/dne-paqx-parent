/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

package com.dell.cpsd.paqx.dne.dto;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Add Host to DvSwitch Dto which contains the set of
 * dvs names and pnic device names.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class AddHostDvSwitchDto
{
    private final Set<String> dvsNames   = new HashSet<>();
    private final Set<String> pNicsNames = new HashSet<>();
    private final String hostname;

    public AddHostDvSwitchDto(final String hostname)
    {
        this.hostname = hostname;
    }

    public Set<String> getDvsNames()
    {
        return Collections.unmodifiableSet(dvsNames);
    }

    public Set<String> getPnicsNames()
    {
        return Collections.unmodifiableSet(pNicsNames);
    }

    public void addDvSwitchName(final String dvSwitchName)
    {
        dvsNames.add(dvSwitchName);
    }

    public void addPNicDeviceName(final String pNicName)
    {
        pNicsNames.add(pNicName);
    }

    public String getHostname()
    {
        return hostname;
    }
}

