package ca.sylenko.temporal.activities;

public class CreateDisputeCaseActivitiesImpl implements CreateDisputeCaseActivities {

	@Override
	public void createDisputeCase(String caseId) {
		System.out.println("Dispute Case Created: " + caseId);
	}
}
