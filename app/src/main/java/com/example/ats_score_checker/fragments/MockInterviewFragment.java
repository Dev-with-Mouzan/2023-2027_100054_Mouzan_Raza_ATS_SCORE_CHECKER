package com.example.ats_score_checker.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.ats_score_checker.R;
import com.example.ats_score_checker.data.InterviewRequest;
import com.example.ats_score_checker.data.InterviewResponse;
import com.example.ats_score_checker.databinding.FragmentMockInterviewBinding;
import com.example.ats_score_checker.network.ApiService;
import com.example.ats_score_checker.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MockInterviewFragment extends Fragment {
    private FragmentMockInterviewBinding binding;
    private final List<InterviewRequest.ChatMessage> messageHistory = new ArrayList<>();
    private String selectedJobRole = "General Professional";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMockInterviewBinding.inflate(inflater, container, false);
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

        binding.buttonSend.setOnClickListener(v -> {
            String message = binding.editMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
            }
        });
    }

    private void sendMessage(String content) {
        // 1. Add to UI
        addUserMessageToUI(content);
        binding.editMessage.setText("");

        // 2. Update history
        messageHistory.add(new InterviewRequest.ChatMessage("User", content));

        // 3. If first message, set it as job role
        if (messageHistory.size() == 1) {
            selectedJobRole = content;
        }

        // 4. API Call
        binding.buttonSend.setEnabled(false);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        InterviewRequest request = new InterviewRequest(messageHistory, selectedJobRole);

        apiService.mockInterview(request).enqueue(new Callback<InterviewResponse>() {
            @Override
            public void onResponse(@NonNull Call<InterviewResponse> call, @NonNull Response<InterviewResponse> response) {
                if (!isAdded()) return;
                binding.buttonSend.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    String aiResponse = response.body().getResponse();
                    addAiMessageToUI(aiResponse);
                    messageHistory.add(new InterviewRequest.ChatMessage("Interviewer", aiResponse));
                } else {
                    Toast.makeText(getContext(), "AI failed to respond", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<InterviewResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                binding.buttonSend.setEnabled(true);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUserMessageToUI(String message) {
        TextView textView = new TextView(getContext());
        textView.setText(message);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setBackgroundResource(R.drawable.card_dark_input);
        textView.setPadding(32, 24, 32, 24);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.END;
        params.setMargins(80, 0, 0, 32);
        textView.setLayoutParams(params);

        binding.chatContainer.addView(textView);
        scrollToBottom();
    }

    private void addAiMessageToUI(String message) {
        TextView textView = new TextView(getContext());
        textView.setText(message);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setBackgroundResource(R.drawable.neon_glow_card);
        textView.setPadding(32, 24, 32, 24);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.START;
        params.setMargins(0, 0, 80, 32);
        textView.setLayoutParams(params);

        binding.chatContainer.addView(textView);
        scrollToBottom();
    }

    private void scrollToBottom() {
        binding.chatScroll.post(() -> binding.chatScroll.fullScroll(View.FOCUS_DOWN));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
