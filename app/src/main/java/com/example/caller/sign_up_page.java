package com.example.caller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.caller.databinding.ActivitySignUpPageBinding;
import com.example.caller.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class sign_up_page extends AppCompatActivity {

    FirebaseAuth mAuth;
    ActivitySignUpPageBinding binding;

    Uri ImageUri;

    ProgressDialog progressDialog;
    StorageReference storageReference;

    String username,useremail,userpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        username=getIntent().getStringExtra("username");
        useremail=getIntent().getStringExtra("useremail");
        userpassword=getIntent().getStringExtra("userpassword");

        mAuth=FirebaseAuth.getInstance();

        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone=binding.phone.getText().toString().trim();
                if(ImageUri!=null)
                {
                    Intent intent=new Intent(sign_up_page.this,OTP_verification.class);
                    intent.putExtra("username",username);
                    intent.putExtra("userpassword",userpassword);
                    intent.putExtra("userphone",phone);
                    intent.putExtra("Uri",ImageUri.toString());
                    intent.putExtra("useremail",useremail);

                    startActivity(intent);
                }

            }
        });

        binding.addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();

            }
        });






    }



    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && data !=null && data.getData()!=null)
        {
            ImageUri=data.getData();
            binding.profilePic.setImageURI(ImageUri);
        }


    }



}