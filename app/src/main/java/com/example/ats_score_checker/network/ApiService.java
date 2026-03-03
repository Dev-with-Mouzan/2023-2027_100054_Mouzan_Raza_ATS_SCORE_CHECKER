package com.example.ats_score_checker.network;

import com.example.ats_score_checker.data.AnalysisResponse;
import com.example.ats_score_checker.data.InterviewRequest;
import com.example.ats_score_checker.data.InterviewResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("/analyze-resume")
    Call<AnalysisResponse> analyzeResume(
            @Part MultipartBody.Part resume_file,
            @Part("job_description") RequestBody jobDescription
    );

    @POST("/interview")
    Call<InterviewResponse> mockInterview(@Body InterviewRequest request);
}
