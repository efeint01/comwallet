package com.app.comwallet.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.comwallet.ComApp;
import com.app.comwallet.R;
import com.app.comwallet.models.Wallet;
import com.app.comwallet.database.WalletDao;
import com.app.comwallet.databinding.ActivityWelcomeBinding;
import com.app.comwallet.fragments.CreateWalletFragment;
import com.app.comwallet.fragments.ImportWalletFragment;
import com.app.comwallet.utilities.AppUtils;
import com.app.comwallet.utilities.FragmentUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WelcomeActivity extends AppCompatActivity {

    ActivityWelcomeBinding binding;
    private WalletDao walletDao;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize DAO and ExecutorService
        walletDao = ComApp.database.walletDao();
        executor = Executors.newSingleThreadExecutor();

        checkWallets(); // Check if there are wallets present
        initViews();

    }

    private void checkWallets() {
        // Execute database operation on a background thread
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Check if there are any wallets in the database
                List<Wallet> wallets = walletDao.getAllWallets();
                if (!wallets.isEmpty()) {
                    // If there are wallets, send the user to MainActivity
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    finish(); // Optionally, close WelcomeActivity to prevent going back
                }
            }
        });
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

        //Get the primary text color of the theme
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, false);
        TypedArray arr = this.obtainStyledAttributes(typedValue.data, new int[]{
                android.R.attr.textColorPrimary});
        int remainColor = arr.getColor(0, -1);

        spannableString.setSpan(
                new ForegroundColorSpan(remainColor),
                3,
                appName.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        binding.appNameTw.setText(spannableString);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shutdown ExecutorService when the activity is destroyed
        if (executor != null) {
            executor.shutdown();
        }
    }
}