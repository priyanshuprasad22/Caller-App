package com.example.caller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import com.example.caller.databinding.ActivityHomePageBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomePage extends AppCompatActivity {

    ActivityHomePageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityHomePageBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.getRoot());
        addfragment(new HomeFragment());

        binding.bottomnavigationview.setOnItemSelectedListener(item -> {

                switch (item.getItemId())
                {
                    case R.id.home:
                        addfragment(new HomeFragment());
                        break;
                    case R.id.Profile:
                        addfragment(new PROFILE());
                        break;
                }
                return true;

        });



    }


    private void addfragment(Fragment fragment)
    {
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}