package com.example.caller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.caller.databinding.ActivityOtpVerificationBinding;
import com.example.caller.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class OTP_verification extends AppCompatActivity {

    ActivityOtpVerificationBinding binding;
    FirebaseAuth mAuth;

    String verificationcode="";

    Uri ImageUri;

    StorageReference storageReference;
    String username,useremail,userphone,userpassword;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityOtpVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth=FirebaseAuth.getInstance();

        username=getIntent().getStringExtra("username").trim();
        userphone=getIntent().getStringExtra("userphone").trim();
        useremail=getIntent().getStringExtra("useremail").trim();
        userpassword=getIntent().getStringExtra("userpassword").trim();
        String tempuri=getIntent().getStringExtra("Uri");
        ImageUri=Uri.parse(tempuri);



        binding.enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.number.getText().toString().trim().length()<10 || binding.number.getText().toString().isEmpty())
                {
                    Toast.makeText(OTP_verification.this,"Please enter a valid number",Toast.LENGTH_LONG).show();
                }
                else
                {
                    progressDialog=new ProgressDialog(OTP_verification.this);
                    progressDialog.setTitle("Saving details and Sending OTP...");
                    progressDialog.show();

                    String phone= "+91" + binding.number.getText().toString().trim();
                    signup(username,useremail,userpassword,phone);
                }
            }
        });

        binding.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.otp.getText().toString().isEmpty())
                {
                    Toast.makeText(OTP_verification.this,"Please enter OTP",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    verify(binding.otp.getText().toString());
                }
            }
        });
    }

    private void sendcode(String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth).setPhoneNumber(number).setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallBack)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationcode=s;

        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            final String code=phoneAuthCredential.getSmsCode();

            if(code != null)
            {
                binding.otp.setText(code);
                verify(code);
            }

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

            Toast.makeText(OTP_verification.this,e.getMessage(),Toast.LENGTH_LONG).show();

        }
    };

    private void verify(String code)
    {
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationcode,code);
        SignInWithCredential(credential);

    }

    private void SignInWithCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(OTP_verification.this,"Success"+mAuth.getUid(),Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(OTP_verification.this,HomePage.class);
                            startActivity(intent);


                        }
                        else
                        {
                            Toast.makeText(OTP_verification.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    void updateuser(String uid,String name,String email,String phone)
    {
        DatabaseReference mDbRef= FirebaseDatabase.getInstance().getReference();
        mDbRef.child("Users").child(uid).setValue(new Users(name,phone,email));
        setdatabase(name,email,phone);
    }
    void signup(String name,String email,String password,String phone)
    {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateuser(user.getUid(),name,email,phone);



                        } else {
                            Toast.makeText(OTP_verification.this,"Unable to sign up",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void setdatabase(String name,String email,String phone)
    {
        storageReference= FirebaseStorage.getInstance().getReference().child("images/" + UUID.randomUUID().toString());;

        UploadTask uploadTask= storageReference.putFile(ImageUri);

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful())
                {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

                            if (user != null) {
                                String uid = user.getUid();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Map<String, Object> image = new HashMap<>();
                                image.put("downloadUrl", uri.toString());
                                image.put("userId", uid);
                                image.put("name",name);
                                image.put("email",email);
                                image.put("phone",phone);

                                db.collection("images").add(image);
                            }
                            Toast.makeText(OTP_verification.this,"Success",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            sendcode(phone);




                        }
                    });
                }



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OTP_verification.this,"Failed to Upload",Toast.LENGTH_LONG).show();

            }
        });
    }

}