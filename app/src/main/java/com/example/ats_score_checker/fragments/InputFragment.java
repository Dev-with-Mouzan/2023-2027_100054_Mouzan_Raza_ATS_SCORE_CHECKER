package com.example.ats_score_checker.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.ats_score_checker.MainViewModel;
import com.example.ats_score_checker.R;
import com.example.ats_score_checker.data.AnalysisResponse;
import com.example.ats_score_checker.databinding.FragmentInputBinding;
import com.example.ats_score_checker.network.ApiService;
import com.example.ats_score_checker.network.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InputFragment extends Fragment {
    private FragmentInputBinding binding;
    private MainViewModel viewModel;
    private Uri selectedResumeUri;

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedResumeUri = result.getData().getData();
                    viewModel.setResumeUri(selectedResumeUri);
                    binding.textResumePath.setText(selectedResumeUri.getLastPathSegment());
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInputBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (binding.headerLogo != null) {
            binding.headerLogo.setOnClickListener(v -> {
                android.content.Intent browserIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://www.ggcb.edu.pk/"));
                startActivity(browserIntent);
            });
        }
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding.cardUpload.setOnClickListener(v -> openFilePicker());
        
        binding.buttonAnalyze.setOnClickListener(v -> analyzeResume());
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        filePickerLauncher.launch(intent);
    }

    private void analyzeResume() {
        String jobDescription = binding.editJobDescription.getText().toString().trim();
        if (selectedResumeUri == null) {
            Toast.makeText(getContext(), "Please upload a resume", Toast.LENGTH_SHORT).show();
            return;
        }
        if (jobDescription.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a job description", Toast.LENGTH_SHORT).show();
            return;
        }

        // Loading state
        binding.buttonAnalyze.setEnabled(false);
        binding.buttonAnalyze.setText("ANALYZING...");

        File file = getFileFromUri(selectedResumeUri);
        if (file == null) {
            binding.buttonAnalyze.setEnabled(true);
            binding.buttonAnalyze.setText("ANALYZE MATCH");
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("resume_file", file.getName(), requestFile);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), jobDescription);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.analyzeResume(body, description).enqueue(new Callback<AnalysisResponse>() {
            @Override
            public void onResponse(@NonNull Call<AnalysisResponse> call, @NonNull Response<AnalysisResponse> response) {
                if (!isAdded()) return;
                binding.buttonAnalyze.setEnabled(true);
                binding.buttonAnalyze.setText("ANALYZE MATCH");
                
                if (response.isSuccessful() && response.body() != null) {
                    viewModel.setAnalysisResult(response.body());
                    Navigation.findNavController(requireView()).navigate(R.id.action_inputFragment_to_dashboardFragment);
                } else {
                    try {
                        if (response.errorBody() != null) {
                            android.util.Log.e("API_ERROR", response.errorBody().string());
                        }
                    } catch (IOException ignored) {}
                    Toast.makeText(getContext(), "Analysis failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AnalysisResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                binding.buttonAnalyze.setEnabled(true);
                binding.buttonAnalyze.setText("ANALYZE MATCH");
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File getFileFromUri(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;
            File file = new File(requireContext().getCacheDir(), "temp_resume.pdf");
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
