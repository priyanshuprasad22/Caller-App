package com.example.caller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.caller.databinding.ActivityCallBinding;
import com.example.caller.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.UUID;

public class CallActivity extends AppCompatActivity {

    ActivityCallBinding binding;
    String uniqueId="";
    FirebaseAuth auth;

    String username="";
    String friendusername="";
    boolean isPeerConnected = false;

    DatabaseReference databaseReference;

    boolean isAudio=true;
    boolean isVideo=true;

    String createdBy="",incoming="";

    boolean pageExit=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth=FirebaseAuth.getInstance();

        databaseReference= FirebaseDatabase.getInstance().getReference().child("OnlineUsers");

        username=getIntent().getStringExtra("username");
        incoming=getIntent().getStringExtra("incoming");
        createdBy= getIntent().getStringExtra("createdBy");

        friendusername=incoming;

        Log.d("username,incoming,created",username+" "+incoming+" "+createdBy);

        setupwebview();

        binding.mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAudio=!isAudio;

                callJavaScriptFunction("javascript:toggleAudio(\""+isAudio+"\")");
                if(isAudio)
                {
                    binding.mic.setImageResource(R.drawable.micoff);
                }
                else {
                    binding.mic.setImageResource(R.drawable.micon);
                }
            }
        });

        binding.video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVideo=!isVideo;

                callJavaScriptFunction("javascript:toggleVideo(\""+isVideo+"\")");
                if(isVideo)
                {
                    binding.video.setImageResource(R.drawable.baseline_videocam_24);
                }
                else {
                    binding.video.setImageResource(R.drawable.baseline_videocam_off_24);
                }
            }
        });

        binding.endcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });






    }

    void setupwebview()
    {
        binding.webView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
                      request.grant(request.getResources());
            }
        });
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        binding.webView.addJavascriptInterface(new callinterface(this),"Android");

        Log.d("SettingUp","Android");

        enablevideo();
    }

    public void enablevideo()
    {
        String filePath = "file:android_asset/call.html";
        binding.webView.loadUrl(filePath);



        binding.webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("WebView", "Page finished loading: " + url);
                Toast.makeText(CallActivity.this, "WebView page loaded", Toast.LENGTH_SHORT).show();
                intalizePeer();
            }
        });




}

    public void intalizePeer()
    {
        uniqueId= getUniqueId();
        callJavaScriptFunction("javascript:init(\""+ uniqueId + "\")");

        if(createdBy.equalsIgnoreCase(username))
        {
            Log.d("SnapId",uniqueId);
            if(pageExit)
                return;
            databaseReference.child(username).child("connId").setValue(uniqueId);
            databaseReference.child(username).child("isAvailable").setValue(true);

            binding.controls.setVisibility(View.VISIBLE);
            binding.loadingGroup.setVisibility(View.GONE);

            FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(friendusername).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Users users=snapshot.getValue(Users.class);

                            binding.name.setText(users.getName());



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
        else
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    friendusername = createdBy;
                    FirebaseDatabase.getInstance().getReference()
                            .child("Users")
                            .child(friendusername)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    Users user=snapshot.getValue(Users.class);
                                    binding.name.setText(user.getName());


                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                    Log.d("SnapError",error.toString());

                                }
                            });
                    FirebaseDatabase.getInstance().getReference()
                            .child("OnlineUsers")
                            .child(friendusername)
                            .child("connId")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    if(snapshot.getValue() != null) {
                                        sendCallRequest();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                    Log.d("SnapError1",error.toString());
                                }
                            });
                }
            }, 10000);
        }

    }

    public void onPeerConnected(){
        isPeerConnected = true;
    }

    void sendCallRequest(){
        if(!isPeerConnected) {
            Toast.makeText(this, " Not connected. Please check your internet.", Toast.LENGTH_SHORT).show();
            return;
        }

        listenConnId();
    }

    void listenConnId() {

        databaseReference.child(friendusername).child("connId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.getValue() == null) {
                    return;
                }



                binding.loadingGroup.setVisibility(View.GONE);
                binding.controls.setVisibility(View.VISIBLE);
                String connId = snapshot.getValue(String.class);
                Log.d("SnapshotID",connId);

                callJavaScriptFunction("javascript:startCall(\""+connId+"\")");
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    void callJavaScriptFunction(String function)
    {
        binding.webView.post(new Runnable() {
            @Override
            public void run() {
                binding.webView.evaluateJavascript(function,null);
            }
        });

    }
    String getUniqueId()
    {
        return UUID.randomUUID().toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pageExit=true;
        databaseReference.child(createdBy).setValue(null);
        setContentView(binding.getRoot());
    }
}