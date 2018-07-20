package hello;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationJobExecutionListener implements JobExecutionListener {

	@Override
	public void afterJob(JobExecution arg0) {
		System.out.println("PeopleJobExecutionListener.afterJob()");

	}

	@Override
	public void beforeJob(JobExecution arg0) {
		System.out.println("PeopleJobExecutionListener.beforeJob()");

	}

}
