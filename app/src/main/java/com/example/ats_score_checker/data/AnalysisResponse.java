package com.example.ats_score_checker.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * This class models the JSON response from the backend API.
 * The @SerializedName annotation maps the JSON keys (snake_case) to our Java fields (camelCase).
 */
public class AnalysisResponse {

    @SerializedName("match_percentage")
    private int matchPercentage;

    @SerializedName("missing_keywords")
    private List<String> missingKeywords;

    @SerializedName("profile_summary")
    private String profileSummary;

    @SerializedName("improvement_tips")
    private List<String> improvementTips;

    @SerializedName("application_success_rate")
    private String applicationSuccessRate;

    // --- Getter methods ---

    public int getMatchPercentage() {
        return matchPercentage;
    }

    public List<String> getMissingKeywords() {
        return missingKeywords;
    }

    public String getProfileSummary() {
        return profileSummary;
    }

    public List<String> getImprovementTips() {
        return improvementTips;
    }

    public String getApplicationSuccessRate() {
        return applicationSuccessRate;
    }
}
