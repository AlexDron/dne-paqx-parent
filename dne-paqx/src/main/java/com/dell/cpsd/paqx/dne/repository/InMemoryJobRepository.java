/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.repository;

import com.dell.cpsd.paqx.dne.domain.Job;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

@Repository
public class InMemoryJobRepository implements JobRepository
{
    /*
     * The map of jobs
     */
    private final Map<UUID, Job> jobs = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override 
    public void save(final Job job)
    {
        if (job == null)
        {
            throw new IllegalArgumentException("The job to save is null.");
        }
        
        jobs.put(job.getId(), job);
    }
    

    /**
     * {@inheritDoc}
     */
    @Override 
    public Job[] findAll()
    {
        Job[] results = new Job[jobs.size()];
        return jobs.values().toArray(results);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override 
    public Job find(final UUID jobId)
    {
        return jobs.get(jobId);
    }
}
