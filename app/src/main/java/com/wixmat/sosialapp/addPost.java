package com.wixmat.sosialapp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class addPost extends AppCompatActivity {
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference userRef, accref, userRef1,PostsRef;
    private StorageReference PostsImagesRefrence;
    ProgressDialog loadingBar;
    final static int Gallery_Pick = 1;
    private String cuserid;
    private ImageView SelectPostImage;
    private EditText PostDescription,title_addpost;
    private Uri ImageUri;
    private String Description,pid,title;
    private boolean exits =false;

    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl, current_user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseApp.initializeApp(addPost.this);
        PostsImagesRefrence = FirebaseStorage.getInstance().getReference().child("Post Images");
        current_user_id = mAuth.getCurrentUser().getUid();


        title_addpost = findViewById(R.id.title);
        loadingBar = new ProgressDialog(this);
        toolbar =  findViewById(R.id.addposttoolbar);
        TextView toolbar_title1 = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title1.setText(R.string.addpost_tool_bar_title);
        ImageView backbutton = toolbar.findViewById(R.id.back_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setTitle(R.string.addpost_tool_bar_title);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);




        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToMainActivity();
            }
        });

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");


//        if(!pid.isEmpty()){
//            exits =true;
//            PostsRef.child(pid).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if(dataSnapshot.exists()){
//                        String image = dataSnapshot.child("postimage").getValue().toString();
//                        String content = dataSnapshot.child("description").getValue().toString();
//
//                        Picasso.get().load(image).into(SelectPostImage);
//                        PostDescription.setText(content);
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//
//        }



        SelectPostImage = findViewById(R.id.addpostiamge);

        PostDescription = findViewById(R.id.contetn_post);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profileimage")) {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).into(SelectPostImage);
                    } else {
                        Toast.makeText(addPost.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    public void backtoMain() {
        Intent homeIntent = new Intent(addPost.this, MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }

    public void backtomain(View view) {
        backtoMain();


    }

    public void addimagetostorage(View view) {
        OpenGallery();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            SelectPostImage.setImageURI(ImageUri);
        }
    }

    public void saveinfo(View view) {

        ValidatePostInfo();

    }



    private void ValidatePostInfo()
    {
        Description = PostDescription.getText().toString();
        title = title_addpost.getText().toString();

        if(ImageUri == null)
        {
            Toast.makeText(this, "Please select post image...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description)){

            Toast.makeText(this, "Please say something about your image...", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(title)){

            Toast.makeText(this, "Please Enter Title For a Story", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Add New Post");
            loadingBar.setMessage("Please wait, while we are updating your new post...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StoringImageToFirebaseStorage();
        }
    }

    private void StoringImageToFirebaseStorage()
    {
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
                    Log.i("eroor", downloadUri.toString());
                    SavingPostInformationToDatabase();
                }else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(addPost.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
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
        Log.i("cuser", current_user_id);
        userRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String userFullName = dataSnapshot.child("uname").getValue().toString()+" "+dataSnapshot.child("fname").getValue().toString();
                    String userProfileImage = dataSnapshot.child("profileimage").getValue().toString();



                        Log.i("inof-------", downloadUrl);

                        HashMap postsMap = new HashMap();
                        postsMap.put("uid", current_user_id);
                        postsMap.put("date", saveCurrentDate);
                        postsMap.put("time", saveCurrentTime);
                        postsMap.put("title",title_addpost.getText().toString());
                        postsMap.put("timestamp", ServerValue.TIMESTAMP);
                        postsMap.put("description", Description);
                        postsMap.put("postimage", downloadUrl);
                        postsMap.put("profileimage", userProfileImage);
                        postsMap.put("fullname", userFullName);

                        PostsRef.child(current_user_id + postRandomName).updateChildren(postsMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    SendUserToMainActivity();
                                    Toast.makeText(addPost.this, "New Post is updated successfully.", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                } else {
                                    Toast.makeText(addPost.this, "Error Occured while updating your post.", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                            }
                        });
                    }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(addPost.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


}

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.OFF)
                    .start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                progressDialog.setTitle("Profile Image");
                progressDialog.setMessage("Please wait, while we updating your profile image...");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();

                final StorageReference filePath = UserProfileImageRef.child(cuserid + ".jpg");
                UploadTask urlTask = filePath.putFile(resultUri);
                Task<Uri> urlTask1 = urlTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Log.i("eroor", downloadUri.toString());

                            userRef.child("profileimage").setValue(downloadUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {


                                        Intent selfIntent = new Intent(addPost.this, addPost.class);
                                        startActivity(selfIntent);

                                        Toast.makeText(addPost.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();

                                    }
                                    else
                                    {
                                        String message = task.getException().getMessage();
                                        Toast.makeText(addPost.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });




               *//*addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {


                            final UploadTask.TaskSnapshot snapshots = task.getResult();
                            Task<Uri> downloadUri = snapshots.getStorage().getDownloadUrl();
                            System.out.println("Upload " + downloadUri.toString());
                            progressDialog.dismiss();
                            userRef.child("profileimage").setValue(downloadUri)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {


                                                Intent selfIntent = new Intent(setup.this, setup.class);
                                                startActivity(selfIntent);

                                                Toast.makeText(setup.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(setup.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });

                        }
                    }
                });*//*


            }
            else
            {
                Toast.makeText(this, "Error Occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }

    }
    private void StoringImageToFirebaseStorage()
    {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calFordDate.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;


        StorageReference filePath = PostsImagesRefrence.child("Post Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");

        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if(task.isSuccessful())
                {
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
        });
    }




    private void SavingPostInformationToDatabase()
    {
        userRef.child(cuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String userFullName = dataSnapshot.child("fullname").getValue().toString();
                    String userProfileImage = dataSnapshot.child("profileimage").getValue().toString();

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", cuserid);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("description", Description);
                    postsMap.put("postimage", downloadUrl);
                    postsMap.put("profileimage", userProfileImage);
                    postsMap.put("fullname", userFullName);
                    PostsRef.child(current_user_id + postRandomName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        SendUserToMainActivity();
                                        Toast.makeText(PostActivity.this, "New Post is updated successfully.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                    else
                                    {
                                        Toast.makeText(PostActivity.this, "Error Occured while updating your post.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            SelectPostImage.setImageURI(ImageUri);
        }
    }
*/


   /* public void saveinfo(View view) {

        Calendar calFordDate = Calendar.getInstance();
        final String content = addContetn.getText().toString();
        progressDialog.setTitle(R.string.setup_title);
        progressDialog.setMessage(R.string.setup_msg + "");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(true);
        SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm");
        final String time = currenttime.format(calFordDate.getTime());


        final SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(calFordDate.getTime());

        String fullname;
        accref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()) {
                   String fname = dataSnapshot.child("fname").getValue().toString();
                   String lname = dataSnapshot.child("lname").getValue().toString();
                   String username = dataSnapshot.child("uname").getValue().toString();
                   String bod = dataSnapshot.child("bod").getValue().toString();
                   String gender = dataSnapshot.child("gender").getValue().toString();
                   String profileiamge = dataSnapshot.child("profileimage").getValue().toString();

                    String fullname = fname+" "+lname;

                    HashMap usermap = new HashMap();
                    usermap.put("content",content);
                    usermap.put("group_id","1");
                    usermap.put("uid",mAuth.getCurrentUser().getUid().toString());
                    usermap.put("time_stamp", time);
                    usermap.put("full_name", fullname);
                    usermap.put("profileiamge", profileiamge);
                    usermap.put("date", currentDate);


                   userRef.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
                       @Override
                       public void onComplete(@NonNull Task task) {
                           if (task.isSuccessful()) {
                               sendToMainAcitity();
                           } else
//                        Toast.makeText(setup.this, R.string.setup_toast2+task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();

                               progressDialog.dismiss();
                       }
                   });

               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sendToMainAcitity() {
    }
}
*/