/**
 * This contains the information for a step in the workflow.
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Step
{
    /*
     * The name of the next step.
     */
    private final String  nextStep;

    /*
     * The flag that indicates that this step is the last.
     */
    private final boolean isFinalStep;

    
    /**
     * Step constructor.
     * 
     * @param   nextStep    The name of the next step.
     * 
     * @since   1.0
     */
    public Step(final String nextStep)
    {
        this(nextStep, false);
    }

    
    /**
     * Step constructor.
     * 
     * @param   nextStep    The name of the next step.
     * @param   isFinalStep The last step flag.
     * 
     * @since   1.0
     */
    public Step(final String nextStep, final boolean isFinalStep)
    {
        this.nextStep = nextStep;
        this.isFinalStep = isFinalStep;
    }

    
    /**
     * This returns the name of the next step.
     * 
     * @return  The name of the next step.
     * 
     * @since   1.0
     */
    public String getNextStep()
    {
        return nextStep;
    }

    
    /**
     * This returns true if this step is the last.
     * 
     * @return  True if this step is the last.
     * 
     * @since   1.0
     */
    public boolean isFinalStep()
    {
        return isFinalStep;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() 
    {
        final StringBuilder builder = new StringBuilder();
        
        builder.append("Step {");
        
        builder.append("nextStep=").append(this.nextStep);
        builder.append(", isFinalStep=").append(this.isFinalStep);
        
        builder.append("}");
        
        return builder.toString();
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() 
    {
        final HashCodeBuilder builder = new HashCodeBuilder();
        
        builder.append(this.nextStep);
        builder.append(this.isFinalStep);
        
        return builder.toHashCode();
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) 
    {
        if (other == this) 
        {
            return true;
        }
        
        if (!(other instanceof Step))
        {
            return false;
        }
        
        final Step rhs = ((Step) other);
        
        final EqualsBuilder builder = new EqualsBuilder();
        
        builder.append(this.nextStep, rhs.nextStep);
        builder.append(this.isFinalStep, rhs.isFinalStep);
        
        return builder.isEquals();
    }
}
