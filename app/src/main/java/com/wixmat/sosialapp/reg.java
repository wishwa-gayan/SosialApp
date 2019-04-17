package com.wixmat.sosialapp;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class reg extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText pwd;
    private EditText cpwd;

    private Button regButton;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        FirebaseApp.initializeApp(reg.this);
        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.reg_email);
        pwd = findViewById(R.id.reg_pwd);
        cpwd = findViewById(R.id.reg_cpwd);

        regButton = findViewById(R.id.reb_btn);
        progressDialog = new ProgressDialog(this);



    }

    private void createNewAccoutn() {
        String emaiil = email.getText().toString();
        String pwds = pwd.getText().toString();
        String cpwds = cpwd.getText().toString();

                                    Toast.makeText(reg.this, emaiil+" "+pwds, Toast.LENGTH_LONG).show();
        if(isValidEmail(emaiil)){
            if(pwds.equals(cpwds)&&pwds.length()>=6){
                progressDialog.setTitle(R.string.reg_title);
                progressDialog.setMessage(R.string.reg_msg+"");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(true);
                mAuth.createUserWithEmailAndPassword(emaiil,pwds)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("error", "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(reg.this, R.string.reg_toast,Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    SendUserTSetup();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("error", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(reg.this, R.string.reg_toast,
                                            Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();

                                }
                            }
                        });
            }else{
                Toast.makeText(reg.this, R.string.reg_toast1, Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(reg.this, R.string.reg_toast2, Toast.LENGTH_LONG).show();
        }


    }

    private void SendUserTSetup() {
//        Intent intent = new Intent(this,Set)
        Intent mainActitiyIntent = new Intent(reg.this, MainActivity.class);
        startActivity(mainActitiyIntent);
        finish();
    }

//    @Override
   /* protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            sendUserToMainAcitity();
        }
    }*/

    private void sendUserToMainAcitity() {
        Intent mainActitiyIntent = new Intent(reg.this, MainActivity.class);
        startActivity(mainActitiyIntent);
        finish();
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void regbutn(View view) {
        createNewAccoutn();
    }
}
