package com.wixmat.sosialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class detalisPost extends AppCompatActivity {
private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef,postRef;
    private ImageView profileView,postImage;
    TextView date,conent,name,time,uname;
    String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalis_post);

        mAuth = FirebaseAuth.getInstance();

        date = findViewById(R.id._detlis_date);
        uname = findViewById(R.id._detlis_username);
        time = findViewById(R.id._detlis_time);
        conent = findViewById(R.id._detlis_contetn);
        profileView = findViewById(R.id._detlis_profileiamge);
        postImage = findViewById(R.id._detlis_imageViewpost);
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(getIntent().getStringExtra("pid").split(" ")[0].toString());

        toolbar =  findViewById(R.id.layout_detaliview);
        final TextView toolbar_title1 = toolbar.findViewById(R.id.toolbar_title);
        ImageView backbutton = toolbar.findViewById(R.id.back_toolbar);


        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String url = dataSnapshot.child("profileimage").getValue().toString();
                Log.i("info", url);
                Picasso.get().load(dataSnapshot.child("profileimage").getValue().toString()).placeholder(R.drawable.profile).into(profileView);
                Picasso.get().load(dataSnapshot.child("postimage").getValue().toString()).placeholder(R.drawable.add_post_high).into(postImage);
                conent.setText(dataSnapshot.child("description").getValue().toString());
                uname.setText(dataSnapshot.child("fullname").getValue().toString());
                date.setText(dataSnapshot.child("date").getValue().toString());
                time.setText(dataSnapshot.child("time").getValue().toString());
                toolbar_title1.setText(dataSnapshot.child("title").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToMainActivity();
            }
        });
    }
    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(detalisPost.this, MainActivity.class);
        startActivity(mainIntent);
    }
    public void open(View view) {
        Intent in = new Intent(detalisPost.this, MainActivity.class);
        startActivity(in);
        finish();
    }
}
