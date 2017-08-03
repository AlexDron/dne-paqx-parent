/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
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
 * Vib URL for host
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "VIB_URL")
public class VibUrl
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UUID", unique = true, nullable = false)
    private Long uuid;

    @Column(name = "VIB_URL", unique = true, nullable = false)
    private String vibUrl;

    @ManyToOne(cascade = CascadeType.ALL)
    private Host host;

    public VibUrl()
    {
        // No-arg constructor required for entity classes
    }

    public VibUrl(final String vibUrl)
    {
        this.vibUrl = vibUrl;
    }

    public Long getUuid()
    {
        return uuid;
    }

    public void setUuid(final Long uuid)
    {
        this.uuid = uuid;
    }

    public String getVibUrl()
    {
        return vibUrl;
    }

    public void setVibUrl(final String vibUrl)
    {
        this.vibUrl = vibUrl;
    }

    public Host getHost()
    {
        return host;
    }

    public void setHost(final Host host)
    {
        this.host = host;
    }

    @Override
    public String toString()
    {
        return "VibUrl{" + "uuid=" + uuid + ", vibUrl='" + vibUrl + '\'' + '}';
    }
}

