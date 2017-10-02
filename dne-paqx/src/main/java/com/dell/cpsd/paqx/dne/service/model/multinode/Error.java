/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.model.multinode;

public class Error
{
    private String errorCode;
    private String errorMessage;

    public String getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(final String errorCode)
    {
        this.errorCode = errorCode;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(final String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Error))
        {
            return false;
        }

        final Error error = (Error) o;

        if (getErrorCode() != null ? !getErrorCode().equals(error.getErrorCode()) : error.getErrorCode() != null)
        {
            return false;
        }
        return getErrorMessage() != null ?
                getErrorMessage().equals(error.getErrorMessage()) :
                error.getErrorMessage() == null;
    }

    @Override
    public int hashCode()
    {
        int result = getErrorCode() != null ? getErrorCode().hashCode() : 0;
        result = 31 * result + (getErrorMessage() != null ? getErrorMessage().hashCode() : 0);
        return result;
    }
}
