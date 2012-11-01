/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.backup;

import hudson.Extension;
import hudson.model.RootAction;
import hudson.model.View;
import java.io.IOException;
import javax.servlet.ServletException;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.backup.QueueBackUp.QueueBackUpDescriptor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 *
 * @author lucinka
 */
@Extension
public class BackupQueueAction implements RootAction{
    
    private QueueBackUpDescriptor backup;
    
    private transient String problems;
    
    
    public BackupQueueAction(){
        backup = new QueueBackUpDescriptor();
        backup.load();
        backup.scheduleJobAgain();
    }

    public String getIconFileName() {
        return "lock.png";
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
    
    public void doIndex(StaplerRequest req, StaplerResponse res) throws ServletException, IOException{
        try{
            problems = backup.prepareOnRestart();
            backup.save();
        }
        catch(Exception e){
            String message = "Exception " + e.getClass().getName() + " was thrown during interrupting and saving running jobs\n" + e.getMessage();
            if(problems !=null){
                problems = problems + message;
            }
            else{
                problems = message;
            }
        }
        if(problems==null){
            res.forwardToPreviousPage(req);
        }
        else{
            req.getView(this, "index.jelly");
        }
    }
    
}
