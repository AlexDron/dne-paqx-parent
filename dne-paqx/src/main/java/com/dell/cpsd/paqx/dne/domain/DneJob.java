package com.dell.cpsd.paqx.dne.domain;

import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.vcenter.VCenter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * TODO: Document Usage
 * <p/>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * <p/>
 *
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "DNE_JOB")
public class DneJob
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "JOB_UUID", unique = true, nullable = false)
    private Long uuid;

    @Column(name = "JOB_ID")
    private String id;

    @OneToOne(cascade = CascadeType.ALL)
    private ScaleIOData scaleIOData;

    @OneToOne(cascade = CascadeType.ALL)
    private VCenter vcenter;

    public DneJob()
    {
    }

    public DneJob(final String id, final ScaleIOData scaleIO, final VCenter vcenter)
    {
        this.id = id;
        this.scaleIOData = scaleIO;
        this.vcenter = vcenter;
    }

    public Long getUuid()
    {
        return uuid;
    }

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public ScaleIOData getScaleIO()
    {
        return scaleIOData;
    }

    public void setScaleIO(final ScaleIOData scaleIO)
    {
        this.scaleIOData = scaleIO;
    }

    public VCenter getVcenter()
    {
        return vcenter;
    }

    public void setVcenter(final VCenter vcenter)
    {
        this.vcenter = vcenter;
    }

    @Override
    public String toString()
    {
        return "DneJob{" + "uuid=" + uuid + ", id='" + id + '\'' + '}';
    }
}
