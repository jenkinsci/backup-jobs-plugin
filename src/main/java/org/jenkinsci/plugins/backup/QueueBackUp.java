/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.backup;

import hudson.matrix.MatrixConfiguration;
import hudson.model.AbstractProject;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.Queue.Item;
import java.util.ArrayList;
import java.util.List;
import jenkins.model.Jenkins;

/**
 *
 * @author lucinka
 */
public class QueueBackUp implements Describable<QueueBackUp>{
    

    public Descriptor<QueueBackUp> getDescriptor() {
        return new QueueBackUpDescriptor();
    }

    public static class QueueBackUpDescriptor extends Descriptor{
    
        private List<JobInformation> jobs = new ArrayList<JobInformation>();
        
         private transient String message;

        public QueueBackUpDescriptor(){
            load();
        }

        @Override
        public String getDisplayName() {
            return "back up";
        }
    
        public void restoreQueue(){
            for(JobInformation job:jobs){
                job.scheduleJobAgain();
            }
            jobs.clear();
        }
        
        public String getMessage(){
            return message;
        }
        
        public boolean hasMessage(){
            return message!=null;
        }
    
        public String prepareOnRestart(){
            jobs.clear();
            InterruptingJobs interruption = new InterruptingJobs();
            jobs.addAll(interruption.interruptAllRuns());
            if(interruption.hasErrorMessage())
                message = interruption.getErrorMessage();
            for(Item item : Jenkins.getInstance().getQueue().getItems()){
                if(item.task instanceof AbstractProject && !(item.task instanceof MatrixConfiguration)){
                    ParametersAction action = item.getAction(ParametersAction.class);            
                        JobInformation job = new JobInformation(item.task.getDisplayName(), action);
                        jobs.add(job);
                        Jenkins.getInstance().getQueue().cancel(item.task);
                }
            }
            return message;
        }
        
        public void scheduleJobAgain(){
            for(JobInformation inf:jobs){
                inf.scheduleJobAgain();
            }
        }
       
    }
}
