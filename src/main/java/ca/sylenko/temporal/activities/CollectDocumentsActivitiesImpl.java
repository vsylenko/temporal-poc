package ca.sylenko.temporal.activities;

import java.time.Duration;

import io.temporal.workflow.Workflow;

public class CollectDocumentsActivitiesImpl implements CollectDocumentsActivities {
    @Override
    public void collectDocuments(String caseId) {
        System.out.println("Collecting documents for case: " + caseId);
        try {
            // Simulate document collection
            Thread.sleep(5000);  // Sleep for 5 seconds
        } catch (InterruptedException e) {
            // Handle the exception as needed
            Thread.currentThread().interrupt();
            System.out.println("Document collection was interrupted for case: " + caseId);
        }
    }
}
