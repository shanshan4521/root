package com.axon.mercenary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
			Process p = null;
			BufferedReader br = null;

			String exec = String.valueOf(context.getJobDetail().getJobDataMap()
							.get("url"));
			try {
				p = Runtime.getRuntime().exec(exec);
				log.info("测试代码");
				br = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				String line = null;
				StringBuilder sb = new StringBuilder();
				while ((line = br.readLine()) != null) {
					;
				}

				br = new BufferedReader(new InputStreamReader(
						p.getErrorStream()));
				while ((line = br.readLine()) != null) {
					sb.append(line).append("\n");
				}

				if (p.waitFor() == 1) {
					;
				} else {
					;
				}
			} catch (IOException e1) {
				;
			} catch (InterruptedException e) {
				;
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						;
					}
				}
				if (p != null) {
					p.destroy();
				}
			}

			// periodically check if we've been interrupted...
			if (interrupted) {
				log.info("--- " + jobKey + "  -- Interrupted... bailing out!");
				JobExecutionException e = new JobExecutionException("测试程序结束。");
				throw e;
			}
		} finally {
			log.info("---- " + jobKey + " 结束在 " + new Date());
		}
	}

	public void interrupt() throws UnableToInterruptJobException {
		log.info("---" + jobKey + "  -- INTERRUPTING --");
		interrupted = true;
	}

}
