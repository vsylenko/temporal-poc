package ca.sylenko.temporal.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface GenerateReportActivities {
	
	@ActivityMethod
	void generateReport(String caseId, String resolution);

}
