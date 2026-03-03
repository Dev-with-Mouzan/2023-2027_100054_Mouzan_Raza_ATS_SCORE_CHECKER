package com.example.ats_score_checker;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.ats_score_checker.data.AnalysisResponse;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<Uri> resumeUri = new MutableLiveData<>();
    private final MutableLiveData<String> jobDescription = new MutableLiveData<>();
    private final MutableLiveData<AnalysisResponse> analysisResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<Uri> getResumeUri() { return resumeUri; }
    public void setResumeUri(Uri uri) { resumeUri.setValue(uri); }

    public LiveData<String> getJobDescription() { return jobDescription; }
    public void setJobDescription(String jd) { jobDescription.setValue(jd); }

    public LiveData<AnalysisResponse> getAnalysisResult() { return analysisResult; }
    public void setAnalysisResult(AnalysisResponse result) { analysisResult.setValue(result); }

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public void setIsLoading(boolean loading) { isLoading.setValue(loading); }

    public LiveData<String> getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String message) { errorMessage.setValue(message); }

    public void reset() {
        resumeUri.setValue(null);
        jobDescription.setValue("");
        analysisResult.setValue(null);
        isLoading.setValue(false);
        errorMessage.setValue(null);
    }
}
