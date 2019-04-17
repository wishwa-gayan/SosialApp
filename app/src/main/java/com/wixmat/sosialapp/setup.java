package com.wixmat.sosialapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.InternalTokenProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.util.Calendar;
import java.util.HashMap;

public class setup extends AppCompatActivity {

    private EditText firstNameTxt, lastNameTxt, dateTxt, uname;
    private RadioGroup radioGroupGender;
    private RadioButton malegbtn, femalegbtn;
    private ImageView imageView,backbutton;


    private int gselection;
    private int date;
    private int year;
    private int month;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private StorageReference UserProfileImageRef;
    private LikeButton likeButton;

    private final String Tag = "DataPicker";
    private String cuserid,downloadurl;

    final static int Gallery_Pick = 1;

    ProgressDialog progressDialog;
    private Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        imageView = findViewById(R.id.profile_image1);
        firstNameTxt = findViewById(R.id.first_name_txt);
        lastNameTxt = findViewById(R.id.last_name_txt);
        malegbtn = findViewById(R.id.malegbtn);
        femalegbtn = findViewById(R.id.femalegbtn);
        radioGroupGender = findViewById(R.id.gnder_grop);
        dateTxt = findViewById(R.id.bithday_txt);
        uname = findViewById(R.id.unametxt);
       
        toolbar = (Toolbar) findViewById(R.id.setup_toolbar);
        TextView toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.setup_tool_bar_title);
        ImageView backbutton = toolbar.findViewById(R.id.back_toolbar);



        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseApp.initializeApp(setup.this);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        cuserid = mAuth.getCurrentUser().getUid();
        userRef = database.getInstance().getReference().child("Users").child(cuserid);
        progressDialog = new ProgressDialog(this);
        cuserid = mAuth.getCurrentUser().getUid();




        boolean sessionId = getIntent().getBooleanExtra("backbutton", false);
        if(sessionId){
            backbutton.setVisibility(View.VISIBLE);
                intialize();



        }else{
            backbutton.setVisibility(View.GONE);
        }
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if (dataSnapshot.hasChild("profileimage"))
                    {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(imageView);
                    }
                    else
                    {
                        Toast.makeText(setup.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        Intent mainIntent = new Intent(setup.this, MainActivity.class);
        startActivity(mainIntent);
    }
    public void intialize(){
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String fname = "";
                    fname = dataSnapshot.child("fname").getValue().toString();
                    String lname ="";
                    lname = dataSnapshot.child("lname").getValue().toString();
                    String username =  "";
                    username = dataSnapshot.child("uname").getValue().toString();
                    String bod = "";
                    bod = dataSnapshot.child("bod").getValue().toString();
                    String gender = "";
                    gender = dataSnapshot.child("gender").getValue().toString();


                    uname.setText(username);
                    firstNameTxt.setText(fname);
                    lastNameTxt.setText(lname);
                    dateTxt.setText(bod);

                    gselection = radioGroupGender.getCheckedRadioButtonId();
                    if (gender == "male") {
                        malegbtn.setSelected(true);
                    } else {
                        femalegbtn.setSelected(true);
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void setDate(View view) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog dialog = new DatePickerDialog(
                setup.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(Tag, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                dateTxt.setText(date);
            }
        };

    }

    public void reg(View view) {
        saveAccoutnInformation();
    }

    private void saveAccoutnInformation() {
        String fname = "";
        fname = firstNameTxt.getText().toString();
        String lname = "";
        lname = lastNameTxt.getText().toString();
        String bod ="";
        bod = dateTxt.getText().toString();
        String uanme ="";
        uanme = uname.getText().toString();
        String gender = "";
        gselection = radioGroupGender.getCheckedRadioButtonId();
        if (gselection == R.id.malegbtn) {
            gender = "male";
        } else if (gselection == R.id.femalegbtn) {
            gender = "female";

        }

        if (fname.isEmpty())
            Toast.makeText(setup.this, R.string.setup_fname_error, Toast.LENGTH_LONG).show();
        if (lname.isEmpty())
            Toast.makeText(setup.this, R.string.setup_lname_error, Toast.LENGTH_LONG).show();
        if (bod.isEmpty())
            Toast.makeText(setup.this, R.string.setup_bod_error, Toast.LENGTH_LONG).show();
        if (gender.isEmpty())
            Toast.makeText(setup.this, R.string.setup_gender_error, Toast.LENGTH_LONG).show();
        if (uanme.isEmpty())
            Toast.makeText(setup.this, R.string.setup_uname_error, Toast.LENGTH_LONG).show();
        else {
            progressDialog.setTitle(R.string.setup_title);
            progressDialog.setMessage(R.string.setup_msg + "");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);

            HashMap usermap = new HashMap();
            usermap.put("fname", fname);
            usermap.put("uid", cuserid);
            usermap.put("lname", lname);
            usermap.put("uname", uanme);
            usermap.put("proflieiamge", downloadurl);
            usermap.put("bod", bod);
            usermap.put("type", "0");
            usermap.put("gender", gender);
            usermap.put("status", R.string.setup_status);

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
    protected void onStart() {
        checkUserExitanse();
        super.onStart();


    }


    private void checkUserExitanse() {

        final String current_user_id = mAuth.getCurrentUser().getUid();

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(current_user_id)) {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void sendToMainAcitity() {
        Intent mainActitiyIntent = new Intent(setup.this, MainActivity.class);
        mainActitiyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );
        startActivity(mainActitiyIntent);
        finish();
    }

    public void setImage(View view) {
        Intent gallIntent = new Intent();
        gallIntent.setAction(Intent.ACTION_GET_CONTENT);
        gallIntent.setType("image/*");
        startActivityForResult(gallIntent, Gallery_Pick);


        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if (dataSnapshot.hasChild("profileimage"))
                    {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(imageView);

                    }
                    else
                    {
                        Toast.makeText(setup.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
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
                            downloadurl = downloadUri.toString();
                            Log.i("eroor", downloadUri.toString());

                            userRef.child("profileimage").setValue(downloadUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {


//                                        Intent selfIntent = new Intent(setup.this, setup.class);
//                                        startActivity(selfIntent);

                                        Toast.makeText(setup.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                      //  intialize();//
                                    }
                                    else
                                    {
                                        String message = task.getException().getMessage();
                                        Toast.makeText(setup.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
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




               /*addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                });*/


            }
            else
            {
                Toast.makeText(this, "Error Occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }

    }

    public void sendToMain(View view) {

        Intent mainActitiyIntent = new Intent(setup.this, MainActivity.class);
        startActivity(mainActitiyIntent);
        finish();


    }
}