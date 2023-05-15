package com.example.caller;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.caller.databinding.FragmentPROFILEBinding;
import com.example.caller.databinding.FragmentProfileFrAGMENTBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class PROFILE extends Fragment {

    FirebaseAuth auth;

    FragmentPROFILEBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public PROFILE() {
        // Required empty public constructor
    }

    public static PROFILE newInstance(String param1, String param2) {
        PROFILE fragment = new PROFILE();
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
        binding=FragmentPROFILEBinding.inflate(inflater,container,false);
        View rootview=binding.getRoot();

        auth=FirebaseAuth.getInstance();
        String userId=auth.getUid();

        FirebaseFirestore db=FirebaseFirestore.getInstance();



        db.collection("images").whereEqualTo("userId",userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty())
                {
                    DocumentSnapshot documentSnapshot=queryDocumentSnapshots.getDocuments().get(0);

                    Glide.with(PROFILE.this.getActivity()).load(documentSnapshot.get("downloadUrl")).into(binding.profile);
                    binding.loadactivity.setVisibility(View.GONE);
                    binding.detail.setVisibility(View.VISIBLE);
                }

                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();

                databaseReference.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        binding.textViewPhone.setText(snapshot.child("phone").getValue(String.class));
                        binding.textViewEmail.setText(snapshot.child("email").getValue(String.class));
                        binding.textViewName.setText(snapshot.child("name").getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });





        return rootview;
    }
}