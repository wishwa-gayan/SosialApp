package com.wixmat.sosialapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CircleImageView NavProfileImage;
    private FirebaseAuth mAuth;
    private static FirebaseAuth mAuth1;
    private DatabaseReference userRef;
    private DatabaseReference postRef;
    private static DatabaseReference likeref,commernRef;
    private TextView unametextView;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mGoogleSignIn;
    private static final String TAG = "GoogleActivity";
    private LikeButton likeButton;

    private String currentUserID = "";
    private Intent setUpIntetn;
    private FirebaseRecyclerAdapter <post, postViewHolder> Adapter;
    private static Context mContext;
    private ImageView imageView;
    private static  final int RC_SIGN_IN = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        FirebaseApp.initializeApp(MainActivity.this);
        mAuth = FirebaseAuth.getInstance();
        mAuth1 = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likeref = FirebaseDatabase.getInstance().getReference().child("Likes");
        commernRef = FirebaseDatabase.getInstance().getReference().child("Comments");
        postRef.keepSynced(true);
        toolbar = findViewById(R.id.main_page_toolbar);
        TextView textView = findViewById(R.id.toolbar_title);
        ImageView imageView = toolbar.findViewById(R.id.back_toolbar);
        textView.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_bar);


        drawerLayout = (DrawerLayout) findViewById(R.id.drwable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drwar_open, R.string.drwar_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigation_view);
        View navview = navigationView.inflateHeaderView(R.layout.nav_header);
        NavProfileImage = navview.findViewById(R.id.xxx);
        unametextView = navview.findViewById(R.id.uname);

        recyclerView = findViewById(R.id.alluserpostlist);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);


        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = v.findViewById(R.id._detlis_contetn);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("Tag", "error");
                    }
                });
            }
        });



        currentUserID = mAuth.getUid();
        Toast.makeText(getApplicationContext(), currentUserID, Toast.LENGTH_LONG).show();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserManualSelector(menuItem);
                return true;
            }
        });
        displayallusersPost();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
//                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("log", "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
//        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("google", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // [START_EXCLUDE]
//                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }


    private void displayallusersPost() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Posts").orderByChild("timestamp");
        FirebaseRecyclerOptions <post> options = new FirebaseRecyclerOptions.Builder <post>().setQuery(query, new SnapshotParser <post>() {
            @NonNull
            @Override
            public post parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new post(snapshot.child("uid").getValue().toString(), snapshot.child("time").getValue().toString(), snapshot.child("date").getValue().toString(), snapshot.child("postimage").getValue().toString(), snapshot.child("description").getValue().toString(), snapshot.child("title").getValue().toString(),snapshot.child("profileimage").getValue().toString(), snapshot.child("fullname").getValue().toString(),snapshot.child("timestamp").getValue().toString());

            }
        }).build();

        Adapter = new FirebaseRecyclerAdapter <post, postViewHolder>(options) {

            private RecyclerViewClickListener mListener;

            @NonNull
            @Override
            public postViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_all_post, parent, false);

                return new postViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(@NonNull final postViewHolder viewHolder, int i, @NonNull final post model) {

                {
                    Log.i("infoabout_list", "heloooo" + model.date);
                    viewHolder.setFullname(model.getFullname());

                    viewHolder.setDate(model.getDate(), model.getTime());
                    String full = model.getTitle();
                    Log.i("title_allpost", full);
                    viewHolder.setTitlesetTitle(full);
                    viewHolder.setDescription(full);
                    viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
                    final String[] ux = {""};
                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
                    Log.i("users+allpost", model.getUid());
                    dbref.child("Users").child(model.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ux[0] = dataSnapshot.child("profileimage").getValue().toString();
                    Log.i("users+url",ux[0]);
                    viewHolder.setProfileimage(mContext,ux[0]);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    viewHolder.setPostimage(getApplicationContext(), model.getPostimage() );
                    final postViewHolder pview =viewHolder;
                    viewHolder.setUid(model.getUid());



                    String dates = "    " + model.getDate() + " " + model.getTime();
                    final String pid = model.getUid().toString() +dates.trim().split(" ")[0] + dates.trim().split(" ")[1];
                    Log.i("model", pid);

                    commernRef.child(pid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            pview.setCommentCount(dataSnapshot.getChildrenCount()+"");

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });


                    likeref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean cond = dataSnapshot.child(pid).hasChild(currentUserID);
                            pview.setLikeCount(String.valueOf(dataSnapshot.child(pid).getChildrenCount()),cond);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                viewHolder.onClick();

            }

        };
        recyclerView.setAdapter(Adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Adapter.startListening();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.postacitivitys, menu);
    }


    public static class postViewHolder extends RecyclerView.ViewHolder  implements View.OnCreateContextMenuListener  {

        public String uidx,pids,currentUser;
        View mView;
        RelativeLayout layout;
        LikeButton likeButton;
        DatabaseReference postrefs;
        FirebaseAuth ma;
        public postViewHolder(@NonNull View itemView) {
            super(itemView);
            TextView mtextview = itemView.findViewById(R.id._detlis_contetn);
            layout = itemView.findViewById(R.id.zzz);
            mView = itemView;
            TextView date = (TextView) mView.findViewById(R.id.allpost_date);
            TextView likec = (TextView) mView.findViewById(R.id.likecount);
            TextView uidxx = (TextView) mView.findViewById(R.id.uid);
            ma = FirebaseAuth.getInstance();
            currentUser = ma.getCurrentUser().getUid();
            final String dates = date.getText().toString();
            pids = uidxx.getText().toString() + dates.trim().split(" ")[0] + dates.trim().split(" ")[1];


        }




        public  void setLikeCount(String count,boolean cond){
            TextView clike = mView.findViewById(R.id.likecount);
            LikeButton like = mView.findViewById(R.id.star_button);
            clike.setText(count);
            if (cond){
                like.setLiked(true);
            }
        }
        public  void setCommentCount(String count){
            TextView clike = mView.findViewById(R.id.ccounter);
           clike.setText(count);
        }

        public void setFullname(String fullname) {
            TextView username = (TextView) mView.findViewById(R.id.allpost_username);
            username.setText(fullname);
        }

        public void setProfileimage(Context ctx, String profileimage) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.allpost_profileiamge);
            Picasso.get().load(profileimage).into(image);
        }


        public void setDate(String date, String time) {
            TextView PostTime = (TextView) mView.findViewById(R.id.allpost_time);
            TextView PostDate = (TextView) mView.findViewById(R.id.allpost_date);
            PostDate.setText("    " + date + " " + time);
        }

        public void setDescription(String description) {
            TextView PostDescription = (TextView) mView.findViewById(R.id.allpost_contetn);
            PostDescription.setText(description);
        }
        public void setTitlesetTitle(String postimage) {
            TextView title = mView.findViewById(R.id.allpost_contetn);
            title.setText(postimage);
        }
        public void setPostimage(Context ctx1, String postimage) {
            ImageView PostImage = (ImageView) mView.findViewById(R.id.allpost_imageViewpost);
            Picasso.get().load(postimage).placeholder(R.drawable.add_post_high).into(PostImage);
        }


        public void setUid(String uid) {
            TextView uidxx = (TextView) mView.findViewById(R.id.uid);
            uidxx.setText(uid);
        }




        public void onClick() {
            final TextView con = (TextView) mView.findViewById(R.id.allpost_contetn);
            TextView red = (TextView) mView.findViewById(R.id.readmore);
            TextView date = (TextView) mView.findViewById(R.id.allpost_date);
            final TextView time = (TextView) mView.findViewById(R.id.allpost_time);
            TextView uidxx = (TextView) mView.findViewById(R.id.uid);
            TextView menu = (TextView) mView.findViewById(R.id.menu);
            TextView likecount = (TextView) mView.findViewById(R.id.likecount);
            ImageView imgcoment = mView.findViewById(R.id.commentbtn);
            final String dates = date.getText().toString();

            final String pid = uidxx.getText().toString() + dates.trim().split(" ")[0] + dates.trim().split(" ")[1];
            final LikeButton likbtn = (LikeButton) mView.findViewById(R.id.star_button);

            imgcoment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendTocomment(pid,currentUser);
                           }
            });


            likbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 if(likbtn.isLiked()){
                     likbtn.setLiked(false);
                     likeref.child(pid).child(currentUser).removeValue();

                     likeref.addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                             Log.i("likkeount", pid+String.valueOf(dataSnapshot.child(pid).getChildrenCount()));
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError databaseError) {

                         }
                     });

                 }else{
                     likbtn.setLiked(true);
                     likeref.child(pid).child(currentUser).setValue(true);
                     likeref.addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                             Log.i("likkeount", pid+String.valueOf(dataSnapshot.child(pid).getChildrenCount()));
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError databaseError) {

                         }
                     });



                 }
                }
            });

            con.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("TAG", "IFHO----------------------" + pid + dates.trim().split(" ")[0] + dates.split(" ")[2]);
                    sendToDetlaisview(pid);

                }
            });
            red.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("TAG", "IFHO----------------------");
                    sendToDetlaisview(pid);

                }
            });

            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                        @Override
                        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                            TextView con = (TextView) mView.findViewById(R.id.allpost_contetn);
                            TextView red = (TextView) mView.findViewById(R.id.readmore);
                            TextView time = (TextView) mView.findViewById(R.id.allpost_time);
                            TextView date = (TextView) mView.findViewById(R.id.allpost_date);
                            TextView uidxx = (TextView) mView.findViewById(R.id.uid);


                            final String dates = date.getText().toString();

                            final String pid = uidxx.getText().toString() + dates.trim().split(" ")[0] + dates.trim().split(" ")[1];

                            Log.i("dassss11", uidxx.getText().toString()+"   "+mAuth1.getCurrentUser().getUid());
                            final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Posts").child(pid);
                            String uid = db.child("uid").toString();
                            String type = db.child("type").toString();
                            Log.i("dassss", uid);
                            if (uidxx.getText().toString().trim().equals(mAuth1.getCurrentUser().getUid().trim() )||type.equals("1")) {
                                menu.setHeaderTitle("Post Setting");

                                menu.add(getAdapterPosition(), 121, 1, "Delete Post").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        db.removeValue().addOnCompleteListener(new OnCompleteListener <Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task <Void> task) {
                                                if (task.isSuccessful())
                                                    Toast.makeText(mContext, "hello workd detleted", Toast.LENGTH_LONG).show();

                                            }
                                        });


                                        return true;
                                    }
                                });
                                if (uidxx.getText().toString().trim().equals(mAuth1.getCurrentUser().getUid().trim() )){
                                    menu.add(getAdapterPosition(), 122, 2, "Edit Post").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem item) {
                                            sendToAddPost(pid);
                                            return true;
                                        }
                                    });
                                }
                            }
                        }
                    });

                }
            });

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {


        }


    }

    private static void sendToAddPost(String pid) {
        Intent newIntent = new Intent(mContext, EditPost.class);
        newIntent.putExtra("pid", pid);
        mContext.startActivity(newIntent);
    }
    private static void sendTocomment(String pid,String uid) {
        Intent newIntent = new Intent(mContext, Comments.class);
        newIntent.putExtra("pid", pid);
        newIntent.putExtra("uid", uid);
        mContext.startActivity(newIntent);
    }

    static class MyApp extends Application {

        private static Context mContext;

        public void onCreate() {
            super.onCreate();
            mContext = this.getApplicationContext();
        }

        public static Context getAppContext() {
            return mContext;
        }
    }

    public static void sendToDetlaisview(String pid) {
        Intent newIntent = new Intent(mContext, detalisPost.class);
        newIntent.putExtra("pid", pid);
        mContext.startActivity(newIntent);

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            SendUserToLogin();
        } else {
            checkUserExitanse();
            if (!mAuth.getUid().isEmpty()) {
                userRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("fname")) {
                                String fullname = dataSnapshot.child("fname").getValue().toString();
                                unametextView.setText(fullname);
                            }
                            if (dataSnapshot.hasChild("profileimage")) {
                                String image = dataSnapshot.child("profileimage").getValue(String.class);
                                Log.i("Error_loading", image);
                                Toast.makeText(MainActivity.this, image, Toast.LENGTH_SHORT).show();
                                Picasso.get().load(image).placeholder(R.drawable.profile).into((CircleImageView) NavProfileImage);
                            }

                            {
                                Toast.makeText(MainActivity.this, "Profile name do not exists...", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

        }
        Adapter.startListening();
    }

    private void checkUserExitanse() {

        final String current_user_id = mAuth.getCurrentUser().getUid();

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(current_user_id)) {
                    sendUserToSetupAcitiviy();
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToSetupAcitiviy() {
        Intent setupActitiyIntent = new Intent(MainActivity.this, setup.class);
        setupActitiyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupActitiyIntent);
        finish();
    }

    private void SendUserToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, login.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true;

        return super.onOptionsItemSelected(item);
    }

    private void UserManualSelector(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_post:
                sendToAddpost();
                break;
            case R.id.nav_profile:
                sendToSetup();
                break;
            case R.id.nav_frends:
                break;
            case R.id.nav_home:
                sendToHome();

                break;
            case R.id.nav_groups:
                break;
            case R.id.nav_msgs:
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                mGoogleSignInClient.signOut().addOnCompleteListener(this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                SendUserToLogin();
                break;
        }
    }

    private void sendToHome() {
        Intent setUpIntetn = new Intent(MainActivity.this, home.class);
        setUpIntetn.putExtra("backbutton", true);
        startActivity(setUpIntetn);
        finish();
    }
    private void sendToSetup() {
        Intent setUpIntetn = new Intent(MainActivity.this, setup.class);
        setUpIntetn.putExtra("backbutton", true);
        startActivity(setUpIntetn);
        finish();
    }

    public void addpost(View view) {
        sendToAddpost();
    }

    private void sendToAddpost() {
        Intent addpostIntent = new Intent(MainActivity.this, addPost.class);
        addpostIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(addpostIntent);
        finish();
    }
}
