package com.example.caller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.airbnb.lottie.LottieComposition;
import com.example.caller.databinding.ActivityWelcomeBinding;

public class Welcome extends AppCompatActivity {

    ActivityWelcomeBinding binding;
    private int requestcode=1;
    String[] permissions=new String[] {android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        binding=ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.discription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Welcome.this,HomePage.class);
                startActivity(intent);
            }
        });



        binding.btnBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPermissionGranted()) {
                    Intent intent = new Intent(Welcome.this, HomePage.class);
                    startActivity(intent);

                }
                else
                {
                    askpermission();

                }

            }
        });


    }
    void askpermission()
    {
        ActivityCompat.requestPermissions(this,permissions,requestcode);
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);

    }
    private boolean isPermissionGranted() {
        for (String permission : permissions)
        {
            if(ActivityCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED)
            {
                return false;

            }
        }
        return true;

    }
}