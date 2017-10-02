/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates.exception;

import java.io.Serializable;

public class DneDelegateException extends Exception implements Serializable
{
    private static final long serialVersionUID = 411924233242220L;

    private String errorCode;

    public DneDelegateException(final String message, final String errorCode)
    {
        super(message);
        this.errorCode = errorCode;
    }

    public DneDelegateException(final String message, final String errorCode, final Throwable cause)
    {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public DneDelegateException(final String errorCode, final Throwable cause)
    {
        super(cause);
        this.errorCode = errorCode;
    }

    public DneDelegateException(final String message,
                                final String errorCode,
                                final Throwable cause,
                                final boolean enableSuppression,
                                final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }

    public String getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(final String errorCode)
    {
        this.errorCode = errorCode;
    }
}
