package com.example.ats_score_checker.data;

public class AnalysisRequest {
    private String resume_text;
    private String job_description;

    public AnalysisRequest(String resume_text, String job_description) {
        this.resume_text = resume_text;
        this.job_description = job_description;
    }

    public String getResumeText() {
        return resume_text;
    }

    public void setResumeText(String resume_text) {
        this.resume_text = resume_text;
    }

    public String getJobDescription() {
        return job_description;
    }

    public void setJobDescription(String job_description) {
        this.job_description = job_description;
    }
}
