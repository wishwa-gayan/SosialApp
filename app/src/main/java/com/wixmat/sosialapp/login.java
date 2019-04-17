package com.wixmat.sosialapp;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;

public class login extends AppCompatActivity {

    private EditText uname,pwd;
    private FirebaseAuth mauth;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private static FirebaseAuth mAuth1;
    private DatabaseReference userRef, postRef;
    private TextView unametextView;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mGoogleSignIn;
    private static final String TAG = "GoogleActivity";
    private static  final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        uname = findViewById(R.id.login_email);
        pwd = findViewById(R.id.loign_pwd);
        progressDialog = new ProgressDialog(this);
        FirebaseApp.initializeApp(login.this);
        mAuth = FirebaseAuth.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignIn = new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Log.i(TAG, "HELLO");
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
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
        Log.i("log", "firebaseAuthWithGoogle:" + acct.getId());
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
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.i("google", "signInWithCredential:success"+user.getEmail());
                            sendUserToMainAcitity();
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




    public void login_signup(View view) {
        Intent signupIntent = new Intent(login.this, reg.class);
        startActivity(signupIntent);
    }

    public void login_with_google(View view) {
        signIn();

    }

    public void login(View view) {
        String password = pwd.getText().toString();
        String email = uname.getText().toString();

        if(pwd.equals("")){
            Toast.makeText(this, R.string.login_toast1, Toast.LENGTH_LONG).show();

        }else if(uname.equals("")) {
            Toast.makeText(this, R.string.login_toast, Toast.LENGTH_LONG).show();
        }else{
            progressDialog.setTitle("Login In");
            progressDialog.setMessage(R.string.login_progressbar+"");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);
            mauth = FirebaseAuth.getInstance();
            mauth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(login.this,R.string.login_complete, Toast.LENGTH_LONG).show();
                        sendUserToMainAcitity();
                        progressDialog.dismiss();
                    }else
                        Toast.makeText(login.this, R.string.login_incomplete+task.getException().toString(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

                }
            });
        }

    }




    private void sendUserToMainAcitity() {
        Intent mainActitiyIntent = new Intent(login.this, MainActivity.class);
        mainActitiyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActitiyIntent);
        finish();
    }

    public void singupss(View view) {
        signIn();
    }
}
