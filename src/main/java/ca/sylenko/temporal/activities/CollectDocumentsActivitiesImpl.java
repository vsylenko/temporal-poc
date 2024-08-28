package ca.sylenko.temporal.activities;

public class CollectDocumentsActivitiesImpl implements CollectDocumentsActivities {
    @Override
    public void collectDocuments(String caseId) {
        System.out.println("Collecting documents for case: " + caseId);
        try {
            // Simulate document collection by sleeping for 5 seconds
            Thread.sleep(5000); 
        } catch (InterruptedException e) {
            // Handle the exception as needed
            Thread.currentThread().interrupt();
            System.out.println("Document collection was interrupted for case: " + caseId);
        }
    }
}
