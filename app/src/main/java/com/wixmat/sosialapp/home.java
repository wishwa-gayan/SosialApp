package com.wixmat.sosialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.squareup.picasso.Picasso;

public class home extends AppCompatActivity {
    private  static  Context mContext;
    private DatabaseReference userRef;
    private DatabaseReference postRef;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<post, MainActivity.postViewHolder> Adapter;
    private FirebaseAuth mAuth;
    private static FirebaseAuth mAuth1;
    private static DatabaseReference likeref,commernRef;
    private String currentUserID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = this;


        FirebaseApp.initializeApp(home.this);
        mAuth = FirebaseAuth.getInstance();
        mAuth1 = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUserID = mAuth.getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likeref = FirebaseDatabase.getInstance().getReference().child("Likes");
        commernRef = FirebaseDatabase.getInstance().getReference().child("Comments");


        recyclerView = findViewById(R.id.home_user_post);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setStackFromEnd(true);

        toolbar = (Toolbar) findViewById(R.id.home_title);
        TextView toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.home_title);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        ImageView imageView = toolbar.findViewById(R.id.back_toolbar);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToMain();
            }
        });


        recyclerView.setLayoutManager(linearLayoutManager);
        displayallusersPost();
    }


    public void SendToMain(){
        Intent loginIntent = new Intent(home.this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        Adapter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
        Adapter.stopListening();
    }

    static class MyApp extends Application {

        private static Context mContext;

        public void onCreate() {
            super.onCreate();





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
                                menu.setHeaderTitle("Header");

                                menu.add(getAdapterPosition(), 121, 1, "Delete Post").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        db.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
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

    private void displayallusersPost() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Posts").orderByChild("uid").equalTo(currentUserID);
        FirebaseRecyclerOptions<post> options = new FirebaseRecyclerOptions.Builder <post>().setQuery(query, new SnapshotParser<post>() {
            @NonNull
            @Override
            public post parseSnapshot(@NonNull DataSnapshot snapshot) {

                return new post(snapshot.child("uid").getValue().toString(), snapshot.child("time").getValue().toString(), snapshot.child("date").getValue().toString(), snapshot.child("postimage").getValue().toString(), snapshot.child("description").getValue().toString(), snapshot.child("title").getValue().toString(),snapshot.child("profileimage").getValue().toString(), snapshot.child("fullname").getValue().toString(),snapshot.child("timestamp").getValue().toString());

            }
        }).build();

            Adapter = new FirebaseRecyclerAdapter <post, MainActivity.postViewHolder>(options) {

                private MainActivity.RecyclerViewClickListener mListener;

                @NonNull
                @Override
                public MainActivity.postViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_all_post, parent, false);

                    return new MainActivity.postViewHolder(view);
                }


                @Override
                protected void onBindViewHolder(@NonNull final MainActivity.postViewHolder viewHolder, int i, @NonNull final post model) {

                    {
                        Log.i("infoabout_list", "heloooo" + model.date);
                        viewHolder.setFullname(model.getFullname());

                        viewHolder.setDate(model.getDate(), model.getTime());
                        String full = model.getTitle();

                        viewHolder.setDescription(full);
                        viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
                        final String[] ux = {""};
                        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
                        Log.i("users+allpost", model.getUid());
                        dbref.child("Users").child(model.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ux[0] = dataSnapshot.child("profileimage").getValue().toString();
                                Log.i("users+url", ux[0]);
                                viewHolder.setProfileimage(mContext, ux[0]);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        viewHolder.setPostimage(getApplicationContext(), model.getPostimage());
                        final MainActivity.postViewHolder pview = viewHolder;
                        viewHolder.setUid(model.getUid());


                        String dates = "    " + model.getDate() + " " + model.getTime();
                        final String pid = model.getUid().toString() + dates.trim().split(" ")[0] + dates.trim().split(" ")[1];
                        Log.i("model", pid);

                        commernRef.child(pid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                pview.setCommentCount(dataSnapshot.getChildrenCount() + "");

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });


                        likeref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                boolean cond = dataSnapshot.child(pid).hasChild(currentUserID);
                                pview.setLikeCount(String.valueOf(dataSnapshot.child(pid).getChildrenCount()), cond);


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


}
