package com.app.comwallet.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.comwallet.R;
import com.app.comwallet.databinding.ActivityWelcomeBinding;
import com.app.comwallet.fragments.CreateWalletFragment;
import com.app.comwallet.fragments.ImportWalletFragment;
import com.app.comwallet.utilities.AppUtils;
import com.app.comwallet.utilities.FragmentUtils;

public class WelcomeActivity extends AppCompatActivity {

    ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();

    }

    private void initViews() {

        changeTextColor();
        getAppVersion();

        //Navigate fragments
        binding.createWalletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.addFragmentWithAnimation(getSupportFragmentManager(), binding.welcomeLy.getId(), CreateWalletFragment.class,
                        R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out);
            }
        });

        binding.importWalletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.addFragmentWithAnimation(getSupportFragmentManager(), binding.welcomeLy.getId(), ImportWalletFragment.class,
                        R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out);
            }
        });

    }

    private void getAppVersion() {
        binding.versionTw.setText(String.format("Version %s", AppUtils.getAppVersionName(this)));
    }

    private void changeTextColor() {
        /* Get app name and Change app name first 3 chars color */
        String appName = getString(R.string.app_name);
        SpannableString spannableString = new SpannableString(appName);

        // Get the primary color
        int primaryColor = ContextCompat.getColor(this, R.color.primary);
        spannableString.setSpan(
                new ForegroundColorSpan(primaryColor),
                0,
                3,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        int remainColor = Color.WHITE;
        spannableString.setSpan(
                new ForegroundColorSpan(remainColor),
                3,
                appName.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        binding.appNameTw.setText(spannableString);
    }


}