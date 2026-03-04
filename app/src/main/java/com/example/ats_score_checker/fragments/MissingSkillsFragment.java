package com.example.ats_score_checker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.ats_score_checker.MainViewModel;
import com.example.ats_score_checker.R;
import com.example.ats_score_checker.databinding.FragmentMissingSkillsBinding;

import java.util.List;

public class MissingSkillsFragment extends Fragment {
    private FragmentMissingSkillsBinding binding;
    private MainViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMissingSkillsBinding.inflate(inflater, container, false);
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

        viewModel.getAnalysisResult().observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.getMissingKeywords() != null) {
                displaySkills(response.getMissingKeywords());
            }
        });
    }

    private void displaySkills(List<String> skills) {
        binding.skillsContainer.removeAllViews();
        for (String skill : skills) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_tip, binding.skillsContainer, false);
            TextView textSkill = itemView.findViewById(R.id.text_tip);
            textSkill.setText(skill);
            binding.skillsContainer.addView(itemView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
