package ca.sylenko.temporal;

import ca.sylenko.temporal.activities.CollectDocumentsActivitiesImpl;
import ca.sylenko.temporal.activities.CreateDisputeCaseActivitiesImpl;
import ca.sylenko.temporal.activities.GenerateReportActivitiesImpl;
import ca.sylenko.temporal.activities.NotificationActivitiesImpl;
import ca.sylenko.temporal.activities.ResolveDisputeActivitiesImpl;
import ca.sylenko.temporal.activities.VerifyDocumentsActivitiesImpl;
import ca.sylenko.temporal.workflows.DisputeResolutionWorkflow;
import ca.sylenko.temporal.workflows.DisputeResolutionWorkflowImpl;
import io.temporal.api.enums.v1.WorkflowIdReusePolicy;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class DisputeResolutionWorkflowLauncher {

	private static final String TASK_QUEUE = "dispute resolution queue";
	private static final String TEMPORAL_SERVER_URL = "localhost:7233";
	private static final String WORKDLOW_ID = "dispute-resolution-workflow-case-";

	public static void main(String[] args) {

		// Use the new WorkflowServiceStubsOptions to configure and create
		// WorkflowServiceStubs
		WorkflowServiceStubsOptions options = WorkflowServiceStubsOptions.newBuilder().setTarget(TEMPORAL_SERVER_URL)
				.build();
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

		// A dispute resolution processing Workflow might include case number in the Workflow ID
		WorkflowOptions workflowOptions = WorkflowOptions.newBuilder()
				.setWorkflowId(WORKDLOW_ID + "NS-12346") // Append the case ID to the Workflow ID
				.setTaskQueue(TASK_QUEUE)
				.setWorkflowIdReusePolicy(
						WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_ALLOW_DUPLICATE_FAILED_ONLY)  // Allow duplicate Workflow IDs only if the previous Workflow execution failed
				.build();

		// Start the single Workflow execution
		DisputeResolutionWorkflow workflow = client.newWorkflowStub(DisputeResolutionWorkflow.class, workflowOptions);
		WorkflowClient.start(workflow::startDisputeResolution, "NS-12346"); // Pass the case ID as an argument
	}
}
