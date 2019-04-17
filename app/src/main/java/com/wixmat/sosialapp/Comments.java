package com.wixmat.sosialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Comments extends AppCompatActivity {


    private FirebaseRecyclerAdapter <comment, Comments.CommentHolder> Adapter;
    private static String pid;
    private String uid;
    DatabaseReference commentRef,userRef;
    FirebaseAuth mAuth;
    FirebaseDatabase CommentRef;
    private TextView menu;
    private RecyclerView recyclerView;
    EditText commentContent;
    private  static Activity mActivity;
    private Toolbar toolbar;
    private static Context MContext = null;
    private RelativeLayout relativeLayout;
    private  static LayoutInflater inflater ;

    @SuppressLint({"WrongViewCast", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        FirebaseApp.initializeApp(Comments.this);
        pid = getIntent().getStringExtra("pid").toString();
        uid = getIntent().getStringExtra("uid").toString();
        Log.i("putextra coments", pid);
        Log.i("putextra commnets", uid);
        mAuth =FirebaseAuth.getInstance();
        commentRef =FirebaseDatabase.getInstance().getReference().child("Comments");
        userRef =FirebaseDatabase.getInstance().getReference().child("Users");
        commentContent = findViewById(R.id.Comment_edite);
        recyclerView = findViewById(R.id.rec_comment);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setStackFromEnd(true);
        MContext = getApplicationContext();
        mActivity = this;
        inflater =(LayoutInflater) MContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        toolbar = (Toolbar) findViewById(R.id.comment_layout);
        TextView toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.comment_title);
        relativeLayout = findViewById(R.layout.activity_comments);
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
     displayAllComments();







//        displayAllComments();
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

    public void SendToMain(){
        Intent loginIntent = new Intent(Comments.this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    //    public comment(String cid, String comment, String uid, String date, String time) {
    public void AddComment(View view) {
        Calendar calFordDate = Calendar.getInstance();
        Date date = currentDate();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        String saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        String saveCurrentTime = currentTime.format(calFordDate.getTime());

        HashMap commentMap = new HashMap();
        String cid = pid+saveCurrentDate+saveCurrentTime;
        commentRef =FirebaseDatabase.getInstance().getReference().child("Comments").child(pid).child(cid);

        String comment = commentContent.getText().toString().trim();
        if(comment.equals("")){
           Toast.makeText(this, "Post is Empty ",Toast.LENGTH_LONG).show();
        }else {
            commentMap.put("cid", cid);
            commentMap.put("comment", commentContent.getText().toString().trim());
            commentMap.put("uid", uid);
            commentMap.put("date", date.toString());
            commentMap.put("time", saveCurrentTime);
        }


       commentRef.updateChildren(commentMap).addOnCompleteListener(new OnCompleteListener() {
           @Override
           public void onComplete(@NonNull Task task) {
               if(task.isSuccessful()){
                    Toast.makeText(MContext,"Comment Added",Toast.LENGTH_LONG).show();
               }else{
                    Toast.makeText(MContext,"Try Again!",Toast.LENGTH_LONG).show();

               }
           }
       });
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(Comments.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


    private class CommentHolder extends RecyclerView.ViewHolder implements  View.OnCreateContextMenuListener{
        private View mView;
        TextView menu_commment,menu_commment1,uidmenu;
        TextView cidx;
        String cid;
        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }



        public void SetUname(String uname){
            TextView username = mView.findViewById(R.id.uname);
            username.setText(uname);
        }
        public void setDate(String date){
            TextView datec = mView.findViewById(R.id.comment_date);
            datec.setText(date);
        }
        public  void SetCid(String cid){
            TextView textView = mView.findViewById(R.id.menu_hide);
            textView.setText(cid);
        }
        public  void setUid(String uid){
            TextView textView = mView.findViewById(R.id.menu_hide_UID);
            textView.setText(uid);
        }

        public void setContent(String content){
            TextView comment = mView.findViewById(R.id.comment_content);
            comment.setText(content);
        }
        public void setUsername(String uanme){
            TextView uname = mView.findViewById(R.id.comment_username);
            uname.setText(uanme);
        }

        public void SetUserIamge(String Url){
            ImageView PostImage = (ImageView) mView.findViewById(R.id.uanme_comment);
            Picasso.get().load(Url).placeholder(R.drawable.profile_icon).into(PostImage);
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        }

        public void onClick(){
            menu_commment1 = mView.findViewById(R.id.menu_hide);
            uidmenu = mView.findViewById(R.id.menu_hide_UID);
            menu_commment = itemView.findViewById(R.id.menu_comment);

            String uidx = uidmenu.getText().toString();
            Log.i("uidse", uidx);
            String cuseid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if(uid.equals(uidx)){
                menu_commment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                            @Override
                            public void onCreateContextMenu(final ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                                menu.add(getAdapterPosition(),150,1,"Edit Comment").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        Log.i("Tagxxxxxxxxxxxx",menu_commment1.getText().toString());
                                        CounterDialog counterDialog = new CounterDialog(menu_commment1.getText().toString(),Comments.this);
                                        counterDialog.show();

                                        return false;
                                    }
                                });
                                menu.add(getAdapterPosition(),150,1,"Delete Comment").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Comments").child(pid).child(menu_commment1.getText().toString());
                                        db.removeValue();
                                        return false;
                                    }
                                });
                            }
                        });
                    }
                });
            }else{


            }


        }
    }
    public static class CounterDialog extends AlertDialog.Builder{
        private TextView textView;
        String cidp;

        public CounterDialog(String cidxx, final Context context) {
            super(context);
            cidp = cidxx;
            Log.i("comment_eidt_alert", "comment id"+cidxx + " postid" + pid);
            final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Comments").child(pid).child(cidxx);

            final LayoutInflater inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.aleartdialog_editcomemnt, null);
            setView(view);

            setTitle("Edit Comment!");

            textView = (TextView)view.findViewById(R.id.comment_edit_form);

            Button editBtn = (Button)view.findViewById(R.id.edit_form_dialog_comment);
            Log.i("Editcommentform", textView.getText().toString());

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap  map = new HashMap ();
                    map.put("comment", textView.getText().toString());
                    db.updateChildren(map).addOnCompleteListener(new OnCompleteListener <Void>() {
                        @Override
                        public void onComplete(@NonNull Task <Void> task) {
                            Toast.makeText(MContext,"Updated Succesfully",Toast.LENGTH_LONG).show();
                            view.setVisibility(View.GONE);
                            SendToCommetns();
                        }
                    });
                };
            });




        }

        private static void SendToCommetns() {
            Intent loginIntent = new Intent(MContext, MainActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            MContext.startActivity(loginIntent);
        }

    }
    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static String getTimeAgo(Date date, Context ctx) {

        if(date == null) {
            return null;
        }

        long time = date.getTime();

        Date curDate = currentDate();
        long now = curDate.getTime();
        if (time > now || time <= 0) {
            return null;
        }

        int dim = getTimeDistanceInMinutes(time);

        String timeAgo = null;

        if (dim == 0) {
            timeAgo = ctx.getResources().getString(R.string.date_util_term_less) + " " +  ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim == 1) {
            return "1 " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim >= 2 && dim <= 44) {
            timeAgo = dim + " " + ctx.getResources().getString(R.string.date_util_unit_minutes);
        } else if (dim >= 45 && dim <= 89) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "+ctx.getResources().getString(R.string.date_util_term_an)+ " " + ctx.getResources().getString(R.string.date_util_unit_hour);
        } else if (dim >= 90 && dim <= 1439) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 60)) + " " + ctx.getResources().getString(R.string.date_util_unit_hours);
        } else if (dim >= 1440 && dim <= 2519) {
            timeAgo = "1 " + ctx.getResources().getString(R.string.date_util_unit_day);
        } else if (dim >= 2520 && dim <= 43199) {
            timeAgo = (Math.round(dim / 1440)) + " " + ctx.getResources().getString(R.string.date_util_unit_days);
        } else if (dim >= 43200 && dim <= 86399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "+ctx.getResources().getString(R.string.date_util_term_a)+ " " + ctx.getResources().getString(R.string.date_util_unit_month);
        } else if (dim >= 86400 && dim <= 525599) {
            timeAgo = (Math.round(dim / 43200)) + " " + ctx.getResources().getString(R.string.date_util_unit_months);
        } else if (dim >= 525600 && dim <= 655199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "+ctx.getResources().getString(R.string.date_util_term_a)+ " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 655200 && dim <= 914399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_over) + " "+ctx.getResources().getString(R.string.date_util_term_a)+ " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 914400 && dim <= 1051199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_almost) + " 2 " + ctx.getResources().getString(R.string.date_util_unit_years);
        } else {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 525600)) + " " + ctx.getResources().getString(R.string.date_util_unit_years);
        }

        return timeAgo + " " + ctx.getResources().getString(R.string.date_util_suffix);
    }


    public void OpenAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MContext);
        builder.setTitle("Title");
        View mview  = getLayoutInflater().inflate(R.layout.aleartdialog_editcomemnt,null);

        final EditText input = new EditText(MContext);
    }
    private static int getTimeDistanceInMinutes(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }












//    String cid, String comment, String uid, String date, String time
    private void displayAllComments(){
                Log.i("Tag", "hellodddd"+pid);
        Query query = FirebaseDatabase.getInstance().getReference().child("Comments").child(pid);
        FirebaseRecyclerOptions <comment> options = new FirebaseRecyclerOptions.Builder<comment>().setQuery(query, new SnapshotParser <comment>() {
            @NonNull
            @Override
            public comment parseSnapshot(@NonNull DataSnapshot snapshot) {

                return new comment(snapshot.child("cid").getValue().toString(),
                        snapshot.child("comment").getValue().toString(),
                        snapshot.child("date").getValue().toString(),
                        snapshot.child("time").getValue().toString(),
                        snapshot.child("uid").getValue().toString());
            }
        }).build();

        Adapter = new FirebaseRecyclerAdapter <comment, CommentHolder>(options) {
            @NonNull
            @Override
            public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlecomment, parent, false);
                Log.i("Tag", "hellodddsssssssssssssssssssssssssssssssssssssssssssd"+pid);
                Log.i("infoab111111111", "heloooo");
                return new CommentHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final CommentHolder commentHolder, int i, @NonNull final comment comment) {
                Date date1 = null;
                final String[] usernmae = new String[1];
                Log.i("infoab111111111", "heloooo" + comment.getCid()+" "+" "+
                        comment.getComment()+" "+
                        comment.getDate()+" "+
                        comment.getTime());

                commentHolder.SetCid(comment.getCid());
                commentHolder.setUid(comment.getUid());
                commentHolder.onClick();
                final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", new Locale("us"));

                    BroadcastReceiver tickReceiver = new BroadcastReceiver(){
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            if(intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                                Log.v("Karl", "tick tock tick tock...");
                                try {
                                    commentHolder.setDate(getTimeAgo(sdf.parse(comment.getDate()), getApplicationContext()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                try {
                    commentHolder.setDate(getTimeAgo(sdf.parse(comment.getDate()), getApplicationContext()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK)); // register the broadcast receiver to receive TIME_TICK
                Log.i("Comment_uid", comment.getUid());
//                commentHolder.setContent(date1.toString());
                    commentHolder.setContent(comment.getComment());
                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getUid());
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            usernmae[0] = dataSnapshot.child("fname").getValue().toString().trim();

                            Log.i("Comment_uid-fname", usernmae[0]);
                            commentHolder.setUsername(usernmae[0]);

                            commentHolder.SetUserIamge(dataSnapshot.child("profileimage").getValue().toString().trim());

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            }
        };
        recyclerView.setAdapter(Adapter);
    }
}
