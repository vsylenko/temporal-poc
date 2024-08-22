package ca.sylenko.temporal.activities;

import java.time.Duration;

import io.temporal.workflow.Workflow;

public class ResolveDisputeActivitiesImpl implements ResolveDisputeActivities {

	@Override
	public String resolveDispute(String caseId) {
		System.out.println("Resolving dispute for case: " + caseId);
		return "Resolved in favor of Cardholder"; // Placeholder resolution
	}

}
