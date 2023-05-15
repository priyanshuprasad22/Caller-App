package com.example.caller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.example.caller.adapter.UserOnlineAdapter;
import com.example.caller.databinding.ActivityConnectBinding;
import com.example.caller.model.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Connect extends AppCompatActivity {

    ActivityConnectBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;

    ArrayList<Users> onlineuser;

    boolean check=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityConnectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        String userid=auth.getUid();
        onlineuser=new ArrayList<>();

        database.getReference().child("OnlineUsers").orderByChild("status")
                .equalTo(0).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Log.d("FirstSnap",snapshot.toString());

                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String curruserId = userSnapshot.getKey();
                                database.getReference().child("Users").child(curruserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                        if (userSnapshot.exists()) {

                                            check=true;


                                            if (!userid.equals(curruserId)) {
                                                String name = userSnapshot.child("name").getValue(String.class);
                                                String phone = userSnapshot.child("phone").getValue(String.class);
                                                String email = userSnapshot.child("email").getValue(String.class);

                                                Users user = new Users(name, phone, email);
                                                onlineuser.add(user);
                                                UserOnlineAdapter userOnlineAdapter = new UserOnlineAdapter(Connect.this, onlineuser,userSnapshot.getKey());
                                                binding.onlineRecycle.setAdapter(userOnlineAdapter);
                                                binding.onlineRecycle.setLayoutManager(new LinearLayoutManager(Connect.this));

                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }





                        } else {
                            HashMap<String, Object> block=new HashMap<>();
                            block.put("incoming",userid);
                            block.put("createdBy",userid);
                            block.put("isAvailable",true);
                            block.put("status",0);

                            database.getReference().child("OnlineUsers")
                                    .child(userid).setValue(block).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            database.getReference().child("OnlineUsers").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {



                                                    if(snapshot.child(userid).child("status").exists())
                                                    {
                                                        int status = snapshot.child(userid).child("status").getValue(Integer.class);
                                                        if(status==1)
                                                        {
                                                            Log.d("Status Update",Integer.toString(status));
                                                            if(check)
                                                                return;

                                                            check=true;
                                                            Intent intent=new Intent(Connect.this,CallActivity.class);
                                                            String incoming= snapshot.child(userid).child("incoming").getValue(String.class);
                                                            String createdBy=snapshot.child(userid).child("createdBy").getValue(String.class);
                                                            Boolean isAvailable=snapshot.child(userid).child("isAvailable").getValue(Boolean.class);
                                                            intent.putExtra("username",userid);
                                                            intent.putExtra("incoming",incoming);
                                                            intent.putExtra("createdBy",createdBy);
                                                            intent.putExtra("isAvailable",isAvailable);
                                                            startActivity(intent);
                                                            finish();



                                                        }
                                                    }


                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        }
                                    });

                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });






    }
}