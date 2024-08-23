package ca.sylenko.temporal.workflows;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface DisputeResolutionWorkflow {
	@WorkflowMethod
	void startDisputeResolution(String caseId);
}
