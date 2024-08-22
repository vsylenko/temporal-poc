package ca.sylenko.temporal.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface CollectDocumentsActivities {

    @ActivityMethod
    public void collectDocuments(String caseId);

}
