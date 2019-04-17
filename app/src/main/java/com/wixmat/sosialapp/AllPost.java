package com.wixmat.sosialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.zip.Inflater;

public class AllPost extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, postRef;
    private ImageView profileView, postImage;
    TextView date, conent, name, time, uid;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_post);
        mAuth = FirebaseAuth.getInstance();

        date = findViewById(R.id.allpost_date);
        time = findViewById(R.id.allpost_time);
        conent = findViewById(R.id.allpost_contetn);
        profileView = findViewById(R.id.allpost_profileiamge);
        postImage = findViewById(R.id.allpost_imageViewpost);
        uid = findViewById(R.id.uid);
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(getIntent().getStringExtra("pid").toString());
        final String[] imageurl = new String[1];


        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final String uids = dataSnapshot.child("uid").getValue().toString();
                userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uids);
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        imageurl[0] = dataSnapshot.child("profileimage").getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Picasso.get().load(dataSnapshot.child("postimage").getValue().toString()).placeholder(R.drawable.add_post_high).into(postImage);
                Picasso.get().load(imageurl[0]).placeholder(R.drawable.profile).into(profileView);
                String url = dataSnapshot.child("profileimage").getValue().toString();
                conent.setText(dataSnapshot.child("title").getValue().toString());
                date.setText(dataSnapshot.child("date").getValue().toString());
                time.setText(dataSnapshot.child("time").getValue().toString());
                uid.setText(dataSnapshot.child("uid").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.postacitivitys, menu);


        return true;
    }
}
