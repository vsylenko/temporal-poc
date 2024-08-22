package ca.sylenko.temporal.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface NotificationActivities {

    @ActivityMethod
    public void notifyStakeholders(String caseId, String resolution);
    
}
