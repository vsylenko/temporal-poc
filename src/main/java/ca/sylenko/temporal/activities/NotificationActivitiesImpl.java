package ca.sylenko.temporal.activities;

public class NotificationActivitiesImpl implements NotificationActivities {
    @Override
    public void notifyStakeholders(String caseId, String resolution) {
        System.out.println("Notifying stakeholders of case " + caseId + ": " + resolution);
    }
}
