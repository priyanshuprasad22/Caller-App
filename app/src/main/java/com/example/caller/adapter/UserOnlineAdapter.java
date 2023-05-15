package com.example.caller.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caller.CallActivity;
import com.example.caller.R;
import com.example.caller.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UserOnlineAdapter extends RecyclerView.Adapter<UserOnlineAdapter.ViewHolder>
{
    Context context;
    ArrayList<Users> onlineuser;

    String Key;
    FirebaseDatabase database;
    FirebaseAuth auth;
    public UserOnlineAdapter(Context context, ArrayList<Users> onlineuser,String Key)
    {
        this.context=context;
        this.onlineuser=onlineuser;
        this.Key=Key;
        database=FirebaseDatabase.getInstance();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        auth=FirebaseAuth.getInstance();

        Users user=onlineuser.get(position);

        holder.name.setText(user.getName());



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.getReference().child("OnlineUsers").child(Key).child("status").setValue(1);
                database.getReference().child("OnlineUsers").child(Key).child("incoming").setValue(auth.getUid());

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(context, CallActivity.class);
                        intent.putExtra("username", auth.getUid());
                        intent.putExtra("incoming", auth.getUid());
                        intent.putExtra("createdBy", Key);
                        intent.putExtra("isAvailable", "true");
                        Log.d("CurrentUser", auth.getUid());

                        context.startActivity(intent);
                    }
                }, 1000);
            }
        });



    }

    @Override
    public int getItemCount() {
        return onlineuser.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.nameTextView);
        }
    }
}
