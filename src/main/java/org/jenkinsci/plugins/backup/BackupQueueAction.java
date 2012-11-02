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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 *
 * @author lucinka
 */
@Extension
public class BackupQueueAction implements RootAction, Describable<BackupQueueAction>{
    private QueueBackUpDescriptor backup;
    
    private transient String problems;
    
    
    public BackupQueueAction(){
        backup = new QueueBackUpDescriptor();
        backup.load();
    }
    
    public QueueBackUpDescriptor getBackup(){
        return backup;
    }

    public String getIconFileName() {
        return "save.png";
    }

    public String getDisplayName() {
        return "Backup queue and running jobs";
    }

    public String getUrlName() {
        return "backup";
    }
    
    public boolean hasProblems(){
        return problems!=null;
    }
    
    public String getProblems(){
        return problems;
    }
    
    public void doRuns(StaplerRequest req, StaplerResponse res) throws ServletException, IOException{
        Jenkins.getInstance().checkPermission(Jenkins.ADMINISTER);
        backup.scheduleJobAgain();
        res.forwardToPreviousPage(req);
        
    }
    
    public void doQueue(StaplerRequest req, StaplerResponse res) throws ServletException, IOException{
        Jenkins.getInstance().checkPermission(Jenkins.ADMINISTER);
        backup.restoreQueue();
        res.forwardToPreviousPage(req);
    }
    
    public void doProblems(StaplerRequest req, StaplerResponse res) throws ServletException, IOException{
        Jenkins.getInstance().checkPermission(Jenkins.ADMINISTER);
        req.getView(this, "problem.jelly").forward(req, res);
    }
    
    public void doOverview(StaplerRequest req, StaplerResponse res) throws ServletException, IOException{
        Jenkins.getInstance().checkPermission(Jenkins.ADMINISTER);
        req.getView(this, "overview.jelly").forward(req, res);
    }
    
    public void doBackup(StaplerRequest req, StaplerResponse res) throws ServletException, IOException{
        Jenkins.getInstance().checkPermission(Jenkins.ADMINISTER);
        try{
            problems = backup.prepareOnRestart();
        }
        catch(Exception e){
            Logger.getLogger(getClass().getName()).log(Level.WARNING, null, e);
            String message = "Exception " + e.getClass().getName() + " was thrown during interrupting and saving running jobs\n" + e.getMessage();
            if(problems !=null){
                problems = problems + message;
            }
            else{
                problems = message;
            }
        }
        if(problems==null){
            System.out.println("problem null");
            res.forwardToPreviousPage(req);
        }
        else{
            req.getView(this, "problem.jelly");
        }
    }

    public Descriptor<BackupQueueAction> getDescriptor() {
        return backup;
    }
    
}
