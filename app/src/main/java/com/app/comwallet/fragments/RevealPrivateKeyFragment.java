package com.app.comwallet.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.comwallet.R;
import com.app.comwallet.databinding.FragmentRevealPrivateKeyBinding;
import com.app.comwallet.utilities.AppUtils;
import com.app.comwallet.views.CenteredToast;


public class RevealPrivateKeyFragment extends Fragment {


    String walletName, walletPk;
    FragmentRevealPrivateKeyBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRevealPrivateKeyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews() {
        Bundle args = getArguments();
        if (args == null) return;
        walletName = args.getString("walletName");
        walletPk = args.getString("walletPk");

        binding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        binding.privateKeyInfoTw.setText(getString(R.string.pk_view_info, walletName));
        binding.privateKeyTw.setText(walletPk);


        binding.copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.copyToClipboard(getContext(), walletPk);
                CenteredToast.show(getContext(),"Private key copied");
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}