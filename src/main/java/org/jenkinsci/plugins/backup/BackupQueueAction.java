/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.backup;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.RootAction;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 *
 * @author Lucie Votypkova
 */
@Extension
public class BackupQueueAction implements RootAction{
    
    private transient String problems;      

    public String getIconFileName() {
        return "save.png";
    }

    public String getDisplayName() {
        return "Interrupt and reschedule running jobs";
    }

    public String getUrlName() {
        return "reschedule";
    }
    
    public boolean hasProblems(){
        return problems!=null;
    }
    
    public String getProblems(){
        return problems;
    }
    
    public void doIndex(StaplerRequest req, StaplerResponse res) throws ServletException, IOException{
        Jenkins.getInstance().checkPermission(Jenkins.ADMINISTER);
        if(prepareOnRestart()){
            res.forwardToPreviousPage(req);
        }
        else{
            req.getView(this, "problem.jelly").forward(req, res);
        }
    }

    public boolean prepareOnRestart(){
            Jenkins.getInstance().checkPermission(Jenkins.ADMINISTER);
            List<JobInformation> runningJobs = new ArrayList<JobInformation>();
            InterruptingJobs interruption = new InterruptingJobs();
            runningJobs.addAll(interruption.interruptAllRuns());
            for(JobInformation job: runningJobs)
                job.scheduleJobAgain();
             problems = interruption.getErrorMessage();
             return problems==null;
        }
    
}
