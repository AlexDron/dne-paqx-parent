/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.rest.exception;


public class WorkflowNotFoundException extends Exception {
    /*
    * The serial version uuid
    */
    private static final long serialVersionUID = 965046523232760658L;

    /*
     * The error code.
     */
    private int code;


    /**
     * WorkflowNotFoundException constructor.
     *
     * @param code    The error code
     * @param message The message
     * @since 1.0
     */
    public WorkflowNotFoundException(final int code, final String message) {
        super(message);
        this.code = code;
    }


    /**
     * WorkflowNotFoundException constructor.
     *
     * @param code    The error code
     * @param message The message
     * @param cause   The cause
     * @since 1.0
     */
    public WorkflowNotFoundException(final int code, final String message, final Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * This returns the error code.
     *
     * @return The error code.
     * @since 1.0
     */
    public int getCode() {
        return code;
    }


    /**
     * This sets the error code.
     *
     * @param code The error code.
     * @since 1.0
     */
    public void setCode(final int code) {
        this.code = code;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        builder.append("WorkflowNotFoundException [").append(code).append("]");
        builder.append(" : ").append(this.getMessage());

        return builder.toString();
    }
}
