/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.backup;

import hudson.matrix.MatrixConfiguration;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.ParametersAction;
import hudson.model.Queue.Item;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jenkins.model.Jenkins;

/**
 *
 * @author lucinka
 */
public class QueueBackUpDescriptor extends Descriptor<BackupQueueAction>{
    
        private Set<JobInformation> runningJobs = new HashSet<JobInformation>();
        private List<JobInformation> jobsInQueue = new ArrayList<JobInformation>();
        
         private transient String message;

        public QueueBackUpDescriptor(){
            super(BackupQueueAction.class);
            load();
        }
        
        public Set<JobInformation> getRunningJobs(){
            return runningJobs;
        }
        
        public List<JobInformation> getJobsInQueue(){
            return jobsInQueue;
        }

        @Override
        public String getDisplayName() {
            return "back up";
        }
    
        public void restoreQueue(){
            for(JobInformation job:jobsInQueue){
                job.scheduleJobAgain();
            }
            jobsInQueue.clear();
            save();
        }
        
        public String getMessage(){
            return message;
        }
        
        public boolean hasMessage(){
            return message!=null;
        }
    
        public String prepareOnRestart(){
            if(!Jenkins.getInstance().hasPermission(Jenkins.ADMINISTER)){
                return null;
            }
            runningJobs.clear();
            jobsInQueue.clear();
            InterruptingJobs interruption = new InterruptingJobs();
            runningJobs.addAll(interruption.interruptAllRuns());
            if(interruption.hasErrorMessage())
                message = interruption.getErrorMessage();
            for(Item item : Jenkins.getInstance().getQueue().getItems()){
                if(item.task instanceof AbstractProject && !(item.task instanceof MatrixConfiguration)){
                    ParametersAction action = item.getAction(ParametersAction.class);            
                        JobInformation job = new JobInformation(item.task.getDisplayName(), action);
                        if(!jobsInQueue.contains(job))
                            jobsInQueue.add(job);
                        //Jenkins.getInstance().getQueue().cancel(item.task);
                }
            }
            save();
            return message;
        }
        
        public void scheduleJobAgain(){
            for(JobInformation inf:runningJobs){
                inf.scheduleJobAgain();
            }
            runningJobs.clear();
            save();
        }
       
    }

