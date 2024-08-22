package ca.sylenko.temporal.activities;

import java.time.Duration;

import io.temporal.workflow.Workflow;

public class CollectDocumentsActivitiesImpl implements CollectDocumentsActivities {
    @Override
    public void collectDocuments(String caseId) {
        System.out.println("Collecting documents for case: " + caseId);
        // Simulate document collection
        Workflow.sleep(Duration.ofSeconds(5));
    }
}
