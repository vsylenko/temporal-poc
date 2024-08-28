package ca.sylenko.temporal;

import java.util.UUID;

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

/**
 * This class is the main entry point for the Dispute Resolution Workflow. It
 * launches the Workflow and registers the Activities and Workflow
 * implementations.
 */
public class DisputeResolutionWorkflowLauncher {

	private static final String TASK_QUEUE = "dispute resolution queue";
	private static final String TEMPORAL_SERVER_URL = "localhost:7233";
	private static final String WORKDLOW_ID = "dispute-resolution-workflow-case-";

	public static void main(String[] args) {
		// Generate a random case ID if it's not provided as an argument
		String caseId = args.length < 1 ? UUID.randomUUID().toString().substring(0, 8) : args[0];

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
				.setWorkflowId(WORKDLOW_ID + caseId) // Append the case ID to the Workflow ID
				.setTaskQueue(TASK_QUEUE)
				.setWorkflowIdReusePolicy(
						WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_ALLOW_DUPLICATE_FAILED_ONLY)  // Allow duplicate Workflow IDs only if the previous Workflow execution failed
				.build();

		// Start the single Workflow execution
		DisputeResolutionWorkflow workflow = client.newWorkflowStub(DisputeResolutionWorkflow.class, workflowOptions);
		// Blocking start of Workflow (execution will be waiting until it's completed)
		WorkflowClient.start(workflow::startDisputeResolution, caseId); // Pass the case ID as an argument

		// Example: Workflow will be started at this point but the call doesn't block.
		// WorkflowClient.execute(workflow::startDisputeResolution, "NS-12346");
	}
}
