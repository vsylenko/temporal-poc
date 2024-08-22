package ca.sylenko.temporal.activities;

public class GenerateReportActivitiesImpl implements GenerateReportActivities {

    @Override
    public void generateReport(String caseId, String resolution) {
        System.out.println("Generating report for case " + caseId + ": " + resolution);
    }
}