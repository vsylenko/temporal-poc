package ca.sylenko.temporal.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface VerifyDocumentsActivities {
	@ActivityMethod
	boolean verifyDocuments(String caseId);

}
