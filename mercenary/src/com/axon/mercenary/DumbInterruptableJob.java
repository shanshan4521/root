package com.axon.mercenary;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;



public class DumbInterruptableJob implements InterruptableJob {
    
    // logging services
    private Logger log = LoggerFactory.getLogger(DumbInterruptableJob.class);
    
    // has the job been interrupted?
    private boolean interrupted = false;

    // job name 
    private JobKey jobKey = null;
    
    /**
     * <p>
     * Empty constructor for job initialization
     * </p>
     */
    public DumbInterruptableJob() {
    }

    public void execute(JobExecutionContext context)
        throws JobExecutionException {

        jobKey = context.getJobDetail().getKey();
        log.info("---- " + jobKey + " executing at " + new Date());

        try {
            // main job loop... see the JavaDOC for InterruptableJob for discussion...
            // do some work... in this example we are 'simulating' work by sleeping... :)

            for (int i = 0; i < 4; i++) {
                try {
                    Thread.sleep(1000L);
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
                
                // periodically check if we've been interrupted...
                if(interrupted) {
                    log.info("--- " + jobKey + "  -- Interrupted... bailing out!");
                    return; // could also choose to throw a JobExecutionException 
                             // if that made for sense based on the particular  
                             // job's responsibilities/behaviors
                }
            }
            
        } finally {
            log.info("---- " + jobKey + " completed at " + new Date());
        }
    }
    
    public void interrupt() throws UnableToInterruptJobException {
        log.info("---" + jobKey + "  -- INTERRUPTING --");
        interrupted = true;
    }

}
