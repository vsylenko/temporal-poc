package ca.sylenko.temporal.workflows;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.sylenko.temporal.activities.CollectDocumentsActivities;
import ca.sylenko.temporal.activities.CreateDisputeCaseActivities;
import ca.sylenko.temporal.activities.GenerateReportActivities;
import ca.sylenko.temporal.activities.NotificationActivities;
import ca.sylenko.temporal.activities.ResolveDisputeActivities;
import ca.sylenko.temporal.activities.VerifyDocumentsActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

public class DisputeResolutionWorkflowImpl implements DisputeResolutionWorkflow {
	private static final Logger logger = LoggerFactory.getLogger(DisputeResolutionWorkflowImpl.class);

	/**
	 * This example illustrates how to use a retry policy in a Workflow.
	 */
	private final RetryOptions retryOptions = RetryOptions.newBuilder()
			.setInitialInterval(Duration.ofSeconds(15)) // first retry will occur after 15 seconds
			.setBackoffCoefficient(2.0) // double the delay after each retry
			.setMaximumInterval(Duration.ofSeconds(60)) // up to a maximum delay of 60 seconds
			.setMaximumAttempts(100) // fail the Activity after 100 attempts
			.build();

	/**
	 * Crucially important that activities should have Start-to-Close timeout, which is recommended to be always set. Its value should be
	 * longer than the maximum amount of time we anticipate the execution of the Activity should take. This allows the Temporal Cluster to
	 * detect to a Worker that crashed, in which case it will consider that attempt failed and will create another task that a different
	 * Worker could pick up. 
	 * Start-to-Close Timeout represents the time allowed for the execution of an Activity Task, so it should be set
	 * slightly longer than the maximum duration you'd expect for that Activity to complete successfully. A Start-to-Close Timeout of 5
	 * seconds might be appropriate for an Activity in a basic Hello World example, which should return the greeting in a fraction of a
	 * second. While specifying a Start-to-Close Timeout that is too short for the execution of your Activity is a problem, you should also
	 * avoid one that is too long. 
	 * The Start-to-Close Timeout is one way Temporal detects a Worker crash, so an excessively long value
	 * wastes time by delaying the detection and recovery, ultimately reducing throughput. The Start-to-Close Timeout should be set to
	 * slightly longer than the slowest successful execution you expect for the Activity.
	 */
	private final ActivityOptions defaultActivityOptions = ActivityOptions.newBuilder()
			.setStartToCloseTimeout(Duration.of(30L, ChronoUnit.SECONDS)).setRetryOptions(retryOptions).build();

	private final CreateDisputeCaseActivities createDisputeActivities = Workflow.newActivityStub(CreateDisputeCaseActivities.class,
			defaultActivityOptions);

	private final CollectDocumentsActivities collectDocumentActivities = Workflow.newActivityStub(CollectDocumentsActivities.class,
			defaultActivityOptions);

	private final VerifyDocumentsActivities verifyDocumentsActivities = Workflow.newActivityStub(VerifyDocumentsActivities.class,
			defaultActivityOptions);

	private final ResolveDisputeActivities resolveDisputeActivities = Workflow.newActivityStub(ResolveDisputeActivities.class,
			defaultActivityOptions);

	private final NotificationActivities notificationActivities = Workflow.newActivityStub(NotificationActivities.class,
			defaultActivityOptions);

	private final GenerateReportActivities generateReportActivities = Workflow.newActivityStub(GenerateReportActivities.class,
			defaultActivityOptions);

	@Override
	public void startDisputeResolution(String caseId) {
		// examaple of logging
		logger.info("Preparing to execute CreateDisputeCaseActivities for caseId {}", caseId); 
		
		// In this case, all activities are blocking/synchronous (executed sequentially one after another)
		createDisputeActivities.createDisputeCase(caseId);
		collectDocumentActivities.collectDocuments(caseId);
		boolean isVerified = verifyDocumentsActivities.verifyDocuments(caseId);
		if (isVerified) {
			String resolution = resolveDisputeActivities.resolveDispute(caseId);
			notificationActivities.notifyStakeholders(caseId, resolution);
			generateReportActivities.generateReport(caseId, resolution);
		} else {
			notificationActivities.notifyStakeholders(caseId, "Verification Failed");
		}
	}
}