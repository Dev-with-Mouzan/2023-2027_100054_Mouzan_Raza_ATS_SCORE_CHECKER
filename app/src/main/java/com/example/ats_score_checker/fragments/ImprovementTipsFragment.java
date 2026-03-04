package com.example.ats_score_checker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.ats_score_checker.MainViewModel;
import com.example.ats_score_checker.R;
import com.example.ats_score_checker.data.AnalysisResponse;
import com.example.ats_score_checker.databinding.FragmentImprovementTipsBinding;

import java.util.List;

public class ImprovementTipsFragment extends Fragment {
    private FragmentImprovementTipsBinding binding;
    private MainViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentImprovementTipsBinding.inflate(inflater, container, false);
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
            if (response != null && response.getImprovementTips() != null) {
                displayTips(response.getImprovementTips());
            }
        });
    }

    private void displayTips(List<String> tips) {
        binding.tipsContainer.removeAllViews();
        for (String tip : tips) {
            View tipView = LayoutInflater.from(getContext()).inflate(R.layout.item_tip, binding.tipsContainer, false);
            TextView textTip = tipView.findViewById(R.id.text_tip);
            textTip.setText(tip);
            binding.tipsContainer.addView(tipView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
