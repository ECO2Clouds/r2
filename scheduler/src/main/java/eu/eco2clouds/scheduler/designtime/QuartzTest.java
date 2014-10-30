package eu.eco2clouds.scheduler.designtime;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 
 * Copyright 2014 University of Manchester 
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class QuartzTest implements Job
{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException
	{
		FileOutput.outputToFile(new Date(System.currentTimeMillis()).toString());
	}
	
	private static Scheduler scheduler;
	public static void startQuartzJob()
	{
		JobDetail job = JobBuilder.newJob(QuartzTest.class)
				.withIdentity("dummyJobName", "group1").build();
		
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity("dummyTriggerName", "group1")
				.withSchedule(
					SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInSeconds(5).repeatForever())
				.build();
		
		try
		{
			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			scheduler.scheduleJob(job, trigger);
		}
		
		catch (SchedulerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void stopQuartzJob()
	{
		try
		{
			scheduler.shutdown();
		}
		
		catch (SchedulerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
