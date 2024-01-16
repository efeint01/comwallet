package com.app.comwallet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;

import com.app.comwallet.R;
import com.app.comwallet.databinding.ActivityMainBinding;
import com.app.comwallet.fragments.ReceiveFragment;
import com.app.comwallet.fragments.SendFragment;
import com.app.comwallet.fragments.UnstakeFragment;
import com.app.comwallet.utilities.FragmentUtils;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

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