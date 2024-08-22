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
import io.temporal.workflow.Workflow;

public class DisputeResolutionWorkflowImpl implements DisputeResolutionWorkflow {
	private static final Logger log = LoggerFactory.getLogger(DisputeResolutionWorkflowImpl.class);

	private final ActivityOptions defaultActivityOptions = ActivityOptions.newBuilder()
			.setStartToCloseTimeout(Duration.of(30L, ChronoUnit.SECONDS)).build();

	private final CreateDisputeCaseActivities createDisputeActivities = Workflow
			.newActivityStub(CreateDisputeCaseActivities.class, defaultActivityOptions);

	private final CollectDocumentsActivities collectDocumentActivities = Workflow
			.newActivityStub(CollectDocumentsActivities.class, defaultActivityOptions);

	private final VerifyDocumentsActivities verifyDocumentsActivities = Workflow
			.newActivityStub(VerifyDocumentsActivities.class, defaultActivityOptions);

	private final ResolveDisputeActivities resolveDisputeActivities = Workflow
			.newActivityStub(ResolveDisputeActivities.class, defaultActivityOptions);

	private final NotificationActivities notificationActivities = Workflow.newActivityStub(NotificationActivities.class,
			defaultActivityOptions);

	private final GenerateReportActivities generateReportActivities = Workflow
			.newActivityStub(GenerateReportActivities.class, defaultActivityOptions);

	@Override
	public void startDisputeResolution(String caseId) {
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