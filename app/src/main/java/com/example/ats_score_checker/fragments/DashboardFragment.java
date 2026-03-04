package com.example.ats_score_checker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.ats_score_checker.MainViewModel;
import com.example.ats_score_checker.R;
import com.example.ats_score_checker.data.AnalysisResponse;
import com.example.ats_score_checker.databinding.FragmentDashboardBinding;

import java.util.List;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private MainViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        viewModel.getAnalysisResult().observe(getViewLifecycleOwner(), this::displayResults);

        binding.buttonStartOver.setOnClickListener(v -> {
            viewModel.reset();
            Navigation.findNavController(v).navigate(R.id.action_dashboard_to_input);
        });

        binding.cardTips.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_dashboardFragment_to_improvementTipsFragment);
        });

        binding.cardSkills.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_dashboardFragment_to_missingSkillsFragment);
        });
    }

    private void displayResults(AnalysisResponse response) {
        if (response == null) return;

        int score = response.getMatchPercentage();
        binding.textScorePercentage.setText(String.valueOf(score));
        binding.progressScore.setProgress(score);

        // Update status capsule based on score
        if (score >= 70) {
            binding.textStatusCapsule.setText("STRONG CANDIDATE");
            binding.textStatusCapsule.setBackgroundResource(R.drawable.capsule_green);
        } else if (score >= 40) {
            binding.textStatusCapsule.setText("POTENTIAL MATCH");
            binding.textStatusCapsule.setBackgroundResource(R.drawable.capsule_orange);
        } else {
            binding.textStatusCapsule.setText("POOR MATCH");
            binding.textStatusCapsule.setBackgroundResource(R.drawable.capsule_red);
        }

        // Keywords Summary
        List<String> missingKeywords = response.getMissingKeywords();
        int missingCount = (missingKeywords != null) ? missingKeywords.size() : 0;
        binding.textMsVal.setText(String.valueOf(missingCount));

        // Improvement Areas
        List<String> improvementTips = response.getImprovementTips();
        int tipsCount = (improvementTips != null) ? improvementTips.size() : 0;
        binding.textKwVal.setText(String.valueOf(tipsCount));

        // AI Insight Summary
        binding.textAiRec.setText(response.getProfileSummary());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
