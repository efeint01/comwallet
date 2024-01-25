package com.app.comwallet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.os.Bundle;
import android.view.View;

import com.app.comwallet.R;
import com.app.comwallet.databinding.ActivityMainBinding;
import com.app.comwallet.fragments.ReceiveFragment;
import com.app.comwallet.fragments.SearchValidatorFragment;
import com.app.comwallet.fragments.SendFragment;
import com.app.comwallet.fragments.UnstakeFragment;
import com.app.comwallet.utilities.FragmentUtils;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    public static String endPoint = "ws://142.93.240.200:9944";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.cardLy.setBackgroundResource(R.drawable.gradient_animation);
        binding.sendTw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.addFragmentWithAnimation(getSupportFragmentManager(), binding.mainLy.getId(), SendFragment.class,
                        R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out);
            }
        });
        binding.receiveTw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("walletAddress", "5C8FMqAFpHkSVdhzjQzxCKHcuzfPgkf2LU4qLYPgdNoLpiWp");
                FragmentUtils.addFragmentWithAnimation(getSupportFragmentManager(), binding.mainLy.getId(), ReceiveFragment.class,
                        R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out, args);
            }
        });

        binding.stakeTw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.addFragmentWithAnimation(getSupportFragmentManager(), binding.mainLy.getId(), SearchValidatorFragment.class,
                        R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out);
            }
        });
        binding.unStakeTw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.addFragmentWithAnimation(getSupportFragmentManager(), binding.mainLy.getId(), UnstakeFragment.class,
                        R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out);
            }
        });

        AnimationDrawable animationDrawable = (AnimationDrawable) binding.cardLy.getBackground();
        animationDrawable.setEnterFadeDuration(750);
        animationDrawable.setExitFadeDuration(1500);
        animationDrawable.start();


    }



}