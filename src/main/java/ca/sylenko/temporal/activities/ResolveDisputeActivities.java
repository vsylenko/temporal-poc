package ca.sylenko.temporal.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ResolveDisputeActivities {

    @ActivityMethod
    public String resolveDispute(String caseId);
    
}
