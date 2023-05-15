package com.example.caller;



import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caller.adapter.UserOnlineAdapter;
import com.example.caller.databinding.FragmentHomeBinding;
import com.example.caller.model.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirebaseAuth auth;
    FirebaseDatabase database;

    ArrayList<Users> onlineuser;
    String userid="";
    private Ringtone ringtone;
    private MediaPlayer mediaPlayer;

    boolean check=false;

    private String mParam1;
    private String mParam2;


    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentHomeBinding.inflate(inflater,container,false);
        View rootview=binding.getRoot();

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        userid=auth.getUid();
        onlineuser=new ArrayList<>();

        opencallactvity();



        return rootview;
    }


    public void opencallactvity()
    {
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
                                                UserOnlineAdapter userOnlineAdapter = new UserOnlineAdapter(HomeFragment.this.getActivity(), onlineuser,userSnapshot.getKey());
                                                binding.onlineRecycle.setAdapter(userOnlineAdapter);
                                                binding.onlineRecycle.setLayoutManager(new LinearLayoutManager(HomeFragment.this.getActivity()));

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


                                                            String incoming= snapshot.child(userid).child("incoming").getValue(String.class);
                                                            String createdBy=snapshot.child(userid).child("createdBy").getValue(String.class);
                                                            Boolean isAvailable=snapshot.child(userid).child("isAvailable").getValue(Boolean.class);

                                                            database.getReference().child("Users").child(incoming).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    String name=snapshot.child("name").getValue(String.class);
                                                                    binding.textCallerName.setText(name);

                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                    Log.d("Error in dataretrival",error.toString());

                                                                }
                                                            });


                                                            binding.callCardView.setVisibility(View.VISIBLE);

                                                            binding.btnAcceptCall.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {

                                                                    binding.callCardView.setVisibility(View.GONE);

                                                                    Handler handler = new Handler();
                                                                    handler.postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {

                                                                            Intent intent = new Intent(HomeFragment.this.getActivity(), CallActivity.class);
                                                                            intent.putExtra("username", userid);
                                                                            intent.putExtra("incoming", incoming);
                                                                            intent.putExtra("createdBy", createdBy);
                                                                            intent.putExtra("isAvailable", isAvailable);


                                                                            startActivity(intent);
                                                                        }
                                                                    }, 1000);




                                                                }
                                                            });

                                                            binding.btnRejectCall.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {





                                                                         database.getReference().child("OnlineUsers").child(userid).child("status")
                                                                            .setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    Toast.makeText(HomeFragment.this.getActivity(),"You rejected the call",Toast.LENGTH_LONG).show();
                                                                                    binding.callCardView.setVisibility(View.GONE);

                                                                                }
                                                                            });


                                                                }
                                                            });






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
//    private void stopRingtone() {
//        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//
//        AudioManager audioManager = (AudioManager) requireActivity().getSystemService(Context.AUDIO_SERVICE);
//        if (audioManager != null) {
//            audioManager.setStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
//        }
//    }

}