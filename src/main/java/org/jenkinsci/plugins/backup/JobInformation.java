/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.backup;

import hudson.matrix.MatrixConfiguration;
import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.Item;
import hudson.model.ParametersAction;

import jenkins.model.Jenkins;

/**
 *
 * @author lucinka
 */
public class JobInformation {
    
    private String name;
    private ParametersAction parameters;
    
    public JobInformation(String name, ParametersAction parameters){
        this.name= name;
        this.parameters = parameters;
    }
    
    public boolean hasParameters(){
        return parameters==null;
    }
    
    public String getName(){
        return name;
    }
        
    public Item getJob(){
        return Jenkins.getInstance().getItem(name);
    }
    
    public ParametersAction getParameters(){
        return parameters;
    }
    
    public void scheduleJobAgain(){
        Item item = getJob();
        if(item instanceof AbstractProject && !(item instanceof MatrixConfiguration)){ //matrix configuration will be shechuled by matrix job
            Cause cause = new Cause() {

                @Override
                public String getShortDescription() {
                    return "rescheduling after restart";
                }
            };
            if(parameters==null){
                ((AbstractProject)item).scheduleBuild(cause);
            }
            else{
               ((AbstractProject)item).scheduleBuild(0, cause, parameters);
            }
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public boolean equals(Object object){
        if(object instanceof JobInformation)
            return ((JobInformation)object).getName().equals(name);
        return false;
    }
}
