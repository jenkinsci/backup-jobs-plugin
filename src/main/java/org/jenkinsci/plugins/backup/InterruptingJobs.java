/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.backup;


import hudson.model.AbstractProject;
import hudson.model.Executor;
import hudson.model.ParametersAction;
import hudson.model.Run;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;

/**
 *
 * @author Lucie Votypkova
 */
public class InterruptingJobs {
    
    private String message;
    
    public String getErrorMessage(){
        return message;
    }
    
    public boolean hasErrorMessage(){
        return message!=null;
    }
    
    public boolean interruptBuild(Run run) {
        Executor executor = run.getExecutor();
        if(executor==null)
            return true;
        executor.interrupt();
        for(Thread t:Thread.getAllStackTraces().keySet()){
            if(t.getName().contains("executing " + run.getParent().getDisplayName())){
                t.interrupt();
                return t.getName().contains("executing " + run.getParent().getDisplayName());
            }
        }
        return true;
    }
    
    public List<JobInformation> interruptAllRuns(){
        StringBuilder builder = new StringBuilder();
        List<JobInformation> projects = new ArrayList<JobInformation>();
        for(AbstractProject project: Jenkins.getInstance().getAllItems(AbstractProject.class)){
            Iterator<Run> iterator = project.getBuilds().iterator();
            while(iterator.hasNext()){
                Run run = iterator.next();
                if(run.isBuilding()){
                    ParametersAction action = run.getAction(ParametersAction.class);
                    JobInformation info = new JobInformation(run.getParent(),action);
                    projects.add(info);
                    if(!interruptBuild(run)){
                        String error = "Build " + run.getNumber() + " of job " + run.getParent().getDisplayName() + " is not able interrupt\n";
                        builder.append(error);
                        Logger.getLogger(getClass().getName()).log(Level.WARNING, error);
                    }
                }
            }
        }
        if(builder.length()>0)
          message = builder.toString();  
        return projects;
    }
}
