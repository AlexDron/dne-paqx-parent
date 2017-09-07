/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.model;

import java.util.List;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

public class BootDeviceIdracStatus  {
    private String status;
    private List<String> errors;

    public BootDeviceIdracStatus(){}

    public BootDeviceIdracStatus(String status, List<String> errors){
        this.status=status;
        this.errors= errors;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }


    public String toString(){
        StringBuilder builder = new StringBuilder();

        builder.append("BootOrderStatus{");
        builder.append("status=").append(this.status);
        builder.append("errors=").append(this.errors);
        builder.append("}");

        return builder.toString();

    }
}
