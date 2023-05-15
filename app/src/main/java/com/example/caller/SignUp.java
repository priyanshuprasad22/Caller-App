package com.example.caller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.caller.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUp extends AppCompatActivity {


    @BindView(R.id.name)

    EditText name;

    @BindView(R.id.email)
    EditText email;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.nextButton)
    Button next;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);

        mAuth=FirebaseAuth.getInstance();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=name.getText().toString().trim();
                String useremail=email.getText().toString().trim();
                String userpassword=password.getText().toString().trim();

                Intent intent=new Intent(SignUp.this,sign_up_page.class);
                intent.putExtra("username",username);
                intent.putExtra("useremail",useremail);
                intent.putExtra("userpassword",userpassword);
                startActivity(intent);



            }
        });
    }


}