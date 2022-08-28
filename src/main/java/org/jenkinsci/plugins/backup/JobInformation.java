/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.backup;

import hudson.matrix.MatrixConfiguration;
import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.Job;
import hudson.model.ParametersAction;

/**
 *
 * @author Lucie Votypkova
 */
public class JobInformation {
    
    private Job job;
    private ParametersAction parameters;
    
    public JobInformation(Job job, ParametersAction parameters){
        this.job= job;
        this.parameters = parameters;
    }
    
    public boolean hasParameters(){
        return parameters==null;
    }
    
    public Job getJob(){
        return job;
    }
        
    public ParametersAction getParameters(){
        return parameters;
    }
    
    public void scheduleJobAgain(){
        if(job instanceof AbstractProject && !(job instanceof MatrixConfiguration)){ //matrix configuration will be shechuled by matrix job
            Cause cause = new Cause() {

                @Override
                public String getShortDescription() {
                    return "rescheduling after restart";
                }
            };
            if(parameters==null){
                ((AbstractProject)job).scheduleBuild(cause);
            }
            else{
               ((AbstractProject)job).scheduleBuild(0, cause, parameters);
            }
        }
    }

    @Override
    public int hashCode() {
        return job.hashCode();
    }
    
    @Override
    public boolean equals(Object object){
        if(object instanceof JobInformation)
            return ((JobInformation)object).getJob().getDisplayName().equals(job.getDisplayName());
        return false;
    }
}
