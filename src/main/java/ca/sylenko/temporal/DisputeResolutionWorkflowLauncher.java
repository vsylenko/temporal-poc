package ca.sylenko.temporal;

import ca.sylenko.temporal.activities.CollectDocumentsActivitiesImpl;
import ca.sylenko.temporal.activities.CreateDisputeCaseActivitiesImpl;
import ca.sylenko.temporal.activities.GenerateReportActivitiesImpl;
import ca.sylenko.temporal.activities.NotificationActivitiesImpl;
import ca.sylenko.temporal.activities.ResolveDisputeActivitiesImpl;
import ca.sylenko.temporal.activities.VerifyDocumentsActivitiesImpl;
import ca.sylenko.temporal.workflows.DisputeResolutionWorkflow;
import ca.sylenko.temporal.workflows.DisputeResolutionWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class DisputeResolutionWorkflowLauncher {

	private static final String TASK_QUEUE = "dispute resolution queue";

	public static void main(String[] args) {

		WorkflowServiceStubs serviceStub = WorkflowServiceStubs.newInstance();
		WorkflowClient client = WorkflowClient.newInstance(serviceStub);

		WorkerFactory factory = WorkerFactory.newInstance(client);
		Worker worker = factory.newWorker(TASK_QUEUE);
		worker.registerWorkflowImplementationTypes(DisputeResolutionWorkflowImpl.class);

		// Supported activities
		worker.registerActivitiesImplementations(new CreateDisputeCaseActivitiesImpl(),
				new CollectDocumentsActivitiesImpl(), new VerifyDocumentsActivitiesImpl(),
				new ResolveDisputeActivitiesImpl(), new NotificationActivitiesImpl(),
				new GenerateReportActivitiesImpl());

		factory.start();

		DisputeResolutionWorkflow workflow = client.newWorkflowStub(DisputeResolutionWorkflow.class,
				WorkflowOptions.newBuilder().setTaskQueue(TASK_QUEUE).build());
		WorkflowClient.start(workflow::startDisputeResolution, "case12345");
	}

}
