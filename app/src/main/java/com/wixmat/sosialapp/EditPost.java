package com.wixmat.sosialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class EditPost extends AppCompatActivity {
    private Toolbar toolbar;
    private DatabaseReference PostRef,userRef;
    private ImageView imageView;
    private EditText PostDescription,titles;
    private String toolbar_title;
    private String Description;
    final static int Gallery_Pick = 1;
    private Uri ImageUri;
    private String cuserid;
    private ImageView SelectPostImage;
    private ProgressDialog loadingBar;
    TextView textView;
    private StorageReference PostsImagesRefrence;
    private String saveCurrentDate, saveCurrentTime,title_edit, postimage ,postRandomName, downloadUrl, current_user_id,pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        FirebaseApp.initializeApp(EditPost.this);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        SelectPostImage = findViewById(R.id.editpostiamge);
        PostDescription = findViewById(R.id.edit_contetn_post);
        titles = findViewById(R.id.edtipost_title);
        PostsImagesRefrence = FirebaseStorage.getInstance().getReference().child("Post Images");
        loadingBar = new ProgressDialog(this);

        Log.i("Edit Post", "On Create");
        toolbar = findViewById(R.id.editpost_toolbar);
         textView = findViewById(R.id.toolbar_title);
        ImageView imageView = toolbar.findViewById(R.id.back_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_bar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);



        pid = getIntent().getStringExtra("pid").toString().trim();
        if(!pid.isEmpty()){
            PostRef.child(pid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String description = dataSnapshot.child("description").getValue().toString();
                        postimage = dataSnapshot.child("postimage").getValue().toString();
                        title_edit = dataSnapshot.child("title").getValue().toString();
                        titles.setText(title_edit);
                        textView.setText(dataSnapshot.child("title").getValue().toString());
                        Picasso.get().load(postimage).into(SelectPostImage);
                        PostDescription.setText(description);

                        Log.i("Edit Post", "On Create pid is not empthy");


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Edit Post", "On Create Image View send to main");

                Intent intent = new Intent(EditPost.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void OpenGallery() {
        Log.i("Edit Post", " Open Gallry");

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    public void backtoMain() {
        Log.i("Edit Post", "Back To main");

        Intent homeIntent = new Intent(EditPost.this, MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i("Edit Post", "On Activity Resutly");

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            SelectPostImage.setImageURI(ImageUri);
        }
    }



    private void ValidatePostInfo()
    {
        Log.i("Edit Post", "Vaild Info");

        Description = PostDescription.getText().toString();

        if(ImageUri==null)
            Log.i("Edit Post", "Image Url Is null");
            ImageUri = Uri.parse(postimage);


        if(ImageUri ==null)
        {
        Log.i("Edit Post", "Image Url Is null");
            Toast.makeText(this, "Please select post image...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description))
        {
        Log.i("Edit Post", "Descrtiption Is null");
            Toast.makeText(this, "Please say something about your image...", Toast.LENGTH_SHORT).show();
        }
        else
        {
        Log.i("Edit Post", " not null Descrtiption Is null");
            loadingBar.setTitle("Add Edit Post");
            loadingBar.setMessage("Please wait, while we are updating your new post...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StoringImageToFirebaseStorage();
        }
    }

    private void StoringImageToFirebaseStorage()
    {
        Log.i("Edit Post", "StoringImageToFirebaseStorage inside");

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calFordDate.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;


        final StorageReference filePath = PostsImagesRefrence.child("Post Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");

        UploadTask uptask =  filePath.putFile(ImageUri);
        Task<Uri> urlTask1 = uptask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    downloadUrl = downloadUri.toString();

                    SavingPostInformationToDatabase();
                }else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(EditPost.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });


        /*.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    Uri downloadUri = task.getResult();
                    downloadUrl = task.getResult().getDownloadUrl().toString();
                    Toast.makeText(PostActivity.this, "image uploaded successfully to Storage...", Toast.LENGTH_SHORT).show();

                    SavingPostInformationToDatabase();

                }
                else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }
    private void SavingPostInformationToDatabase()
    {
        Log.i("Edit Post", "SavingPostInformationToDatabase");
        String content = PostDescription.getText().toString();
        String tilesx = titles.getText().toString();
        if(!tilesx.equals(""))
            title_edit =tilesx;

        if (!(downloadUrl == "")) {
            PostRef.child(pid).child("postimage").setValue(downloadUrl);
            if(!(content==""))
                PostRef.child(pid).child("description").setValue(content);
            PostRef.child(pid).child("title").setValue(title_edit);

        }else{
           if(!(content==""))
             PostRef.child(pid).child("description").setValue(content);
             PostRef.child(pid).child("title").setValue(title_edit);
        }
        loadingBar.dismiss();
    }




    public void saveinfo(View view) {
        Log.i("Edit Post", "saveinfo");

        ValidatePostInfo();


    }


    public void addimagetostorage(View view) {

        Log.i("Edit Post", "addimagetostorage");


        OpenGallery();
    }


    public void backtoMain(View view) {
        Intent mainIntent = new Intent(EditPost.this, MainActivity.class);
        startActivity(mainIntent);
    }
}
