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
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class DisputeResolutionWorkflowLauncher {

	private static final String TASK_QUEUE = "dispute resolution queue";
	private static final String TEMPORAL_SERVER_URL = "localhost:7233";

	public static void main(String[] args) {

		// Use the new WorkflowServiceStubsOptions to configure and create WorkflowServiceStubs
		WorkflowServiceStubsOptions options = WorkflowServiceStubsOptions.newBuilder().setTarget(TEMPORAL_SERVER_URL).build();
		WorkflowServiceStubs serviceStub = WorkflowServiceStubs.newServiceStubs(options);

		WorkflowClient client = WorkflowClient.newInstance(serviceStub);

		WorkerFactory factory = WorkerFactory.newInstance(client);
		Worker worker = factory.newWorker(TASK_QUEUE);
		worker.registerWorkflowImplementationTypes(DisputeResolutionWorkflowImpl.class);

		// Register activity implementations
		worker.registerActivitiesImplementations(new CreateDisputeCaseActivitiesImpl(),
				new CollectDocumentsActivitiesImpl(), new VerifyDocumentsActivitiesImpl(),
				new ResolveDisputeActivitiesImpl(), new NotificationActivitiesImpl(),
				new GenerateReportActivitiesImpl());

		factory.start(); // Worker is ready tosStart listening to the Task Queue

		DisputeResolutionWorkflow workflow = client.newWorkflowStub(DisputeResolutionWorkflow.class,
				WorkflowOptions.newBuilder().setTaskQueue(TASK_QUEUE).build());// Start a single workflow execution 
		WorkflowClient.start(workflow::startDisputeResolution, "case12345"); // Pass the case ID as an argument
	}
}
