package com.app.comwallet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.comwallet.R;
import com.app.comwallet.databinding.FragmentDisclaimerRevealPrivateKeyBinding;
import com.app.comwallet.utilities.FragmentUtils;


public class DisclaimerRevealPrivateKeyFragment extends Fragment {


    FragmentDisclaimerRevealPrivateKeyBinding binding;

    String walletName, walletPk;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDisclaimerRevealPrivateKeyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle == null) return;

        walletName = bundle.getString("walletName");
        walletPk = bundle.getString("walletPk");

        binding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        binding.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                binding.showBtn.setEnabled(isChecked);
            }
        });

        binding.showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("walletName",walletName);
                args.putString("walletPk",walletPk);
                FragmentUtils.addFragmentWithAnimation(getParentFragmentManager(), R.id.mainLy, RevealPrivateKeyFragment.class, R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out, args);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}