package com.app.comwallet.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.comwallet.R;
import com.app.comwallet.activities.MainActivity;
import com.app.comwallet.databinding.FragmentCreateWalletBinding;
import com.app.comwallet.utilities.MnemonicUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

import io.github.novacrypto.bip39.SeedCalculator;

public class CreateWalletFragment extends Fragment {


    FragmentCreateWalletBinding binding;
    String TAG = CreateWalletFragment.class.getSimpleName();

    //TODO: Add screen lock when mnemonic is ready

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateWalletBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();

    }

    private void generateWallet() {
        String mnemonic_phrase = MnemonicUtils.generateMnemonic();
        byte[] seed = new SeedCalculator().calculateSeed(mnemonic_phrase, "");
        parseMnemonicToTextViews(mnemonic_phrase.split(" "));
    }

    private void parseMnemonicToTextViews(String[] mnemonic) {
        for (int i = 0; i < 12; i++) {
            TextView textView = getTextViewByIndex(i);
            if (textView != null) {
                textView.setText(String.format(Locale.getDefault(), "%d. %s", i + 1, mnemonic[i]));
            }
        }
    }

    private void showWarningDialog() {
        if (getContext() == null) return;
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.are_you_sure)
                .setMessage(R.string.secret_words_disclaimer)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (getActivity() == null) return;
                        startActivity(new Intent(getContext(), MainActivity.class));
                        getActivity().finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }


    private TextView getTextViewByIndex(int index) {
        switch (index) {
            case 0:
                return binding.secretWordTw;
            case 1:
                return binding.secretWordTw2;
            case 2:
                return binding.secretWordTw3;
            case 3:
                return binding.secretWordTw4;
            case 4:
                return binding.secretWordTw5;
            case 5:
                return binding.secretWordTw6;
            case 6:
                return binding.secretWordTw7;
            case 7:
                return binding.secretWordTw8;
            case 8:
                return binding.secretWordTw9;
            case 9:
                return binding.secretWordTw10;
            case 10:
                return binding.secretWordTw11;
            case 11:
                return binding.secretWordTw12;
            default:
                return null;
        }
    }

    private void initViews() {
        binding.mnemonicLockView.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateWallet();
                binding.mnemonicLockView.getRoot().setVisibility(View.GONE);
                binding.createBtn.setEnabled(true);
            }
        });

        binding.createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWarningDialog();
            }
        });

        binding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}