package ca.sylenko.temporal.workflows;

import io.temporal.workflow.WorkflowMethod;

public interface DisputeResolutionWorkflow {
	@WorkflowMethod
	void startDisputeResolution(String caseId);
}
