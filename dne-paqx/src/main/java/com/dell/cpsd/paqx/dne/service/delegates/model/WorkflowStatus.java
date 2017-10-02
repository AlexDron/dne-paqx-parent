/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service.delegates.model;

import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.TransitionInstance;

import java.util.List;

public class WorkflowStatus {

    private String state;

    private String activityName;

    private ActivityInstance[] activityInstances;

    private String[] executionIds;

    private String businessKey;

    private List<String> completedSteps;

    private TransitionInstance[] transitionInstances;

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public ActivityInstance[] getActivityInstances() {
        return activityInstances;
    }

    public void setActivityInstances(ActivityInstance[] activityInstances) {
        this.activityInstances = activityInstances;
    }

    public String[] getExecutionIds() {
        return executionIds;
    }

    public void setExecutionIds(String[] executionIds) {
        this.executionIds = executionIds;
    }

    public TransitionInstance[] getTransitionInstances() {
        return transitionInstances;
    }

    public void setTransitionInstances(TransitionInstance[] transitionInstances) {
        this.transitionInstances = transitionInstances;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<String> getCompletedSteps() {
        return completedSteps;
    }

    public void setCompletedSteps(List<String> completedSteps) {
        this.completedSteps = completedSteps;
    }

}
