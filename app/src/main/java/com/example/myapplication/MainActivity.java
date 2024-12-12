package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private EditText edittextloginEmail,edittextloginpwd;
    private FirebaseAuth authprofile;
    private static final String TAG = "MainActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*//buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });*/
        edittextloginEmail=findViewById(R.id.edittext_login_email);
        edittextloginpwd=findViewById(R.id.edittext_login_pwd);
        authprofile=FirebaseAuth.getInstance();
        TextView textviewForgotPassword= findViewById(R.id.forgot_password);
        textviewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"You can reset your password now!",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,forgotPasswordActivity.class));
            }
        });
        Button buttonLogin = findViewById(R.id.buttonlogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textmail = edittextloginEmail.getText().toString();
                String textpwd = edittextloginpwd.getText().toString();
                if(TextUtils.isEmpty(textmail)){
                    Toast.makeText(MainActivity.this,"Please enter your email",Toast.LENGTH_SHORT).show();
                    edittextloginEmail.setError("Email is Requried");
                    edittextloginEmail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(textmail).matches()){
                    Toast.makeText(MainActivity.this,"Please re-enter your email",Toast.LENGTH_SHORT).show();
                    edittextloginEmail.setError("Email is Requried");
                    edittextloginEmail.requestFocus();
                }else if(TextUtils.isEmpty(textpwd)){
                    Toast.makeText(MainActivity.this,"Please enter your password",Toast.LENGTH_SHORT).show();
                    edittextloginpwd.setError("Password is Requried");
                    edittextloginpwd.requestFocus();
                }else {
                    loginUser(textmail,textpwd);
                }
            }

            private void loginUser(String email, String pwd) {
              if(authprofile !=null) {
                  authprofile.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                          if (task.isSuccessful()) {

                              FirebaseUser firebaseUser= authprofile.getCurrentUser();
                              if(firebaseUser.isEmailVerified()){
                                  DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Registered Users").child(firebaseUser.getUid());
                                  userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                      @Override
                                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                          if (dataSnapshot.exists()) {
                                              String role = dataSnapshot.child("role").getValue(String.class);
                                              if ("Student".equals(role)) {
                                                  // Redirect to StudentActivity
                                                  Intent intent = new Intent(MainActivity.this, StudentActivity.class);
                                                  startActivity(intent);
                                              } else if ("Faculty".equals(role)) {
                                                  // Redirect to TeacherActivity
                                                  Intent intent = new Intent(MainActivity.this, TeacherActivity.class);
                                                  startActivity(intent);
                                              }
                                          }
                                      }
                                      @Override
                                      public void onCancelled(@NonNull DatabaseError error) {
                                          // Handle any errors
                                      }
                                  });
                              }else{
                                  firebaseUser.sendEmailVerification();
                                  authprofile.signOut();
                                  showAlertDialog();
                              }
                          } else {
                              try {
                                  throw task.getException();
                              } catch (FirebaseAuthInvalidUserException e) {
                                  edittextloginEmail.setError("User does not exists or is no longer valid. Please register again");
                                  edittextloginEmail.requestFocus();
                              } catch (FirebaseAuthWeakPasswordException e) {
                                  edittextloginEmail.setError("Invalid credentials. Kindly, check and re-enter");
                                  edittextloginEmail.requestFocus();
                              } catch (Exception e) {
                                  Log.e(TAG, e.getMessage());
                                  Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                              }
                          }
                      }
                  });
              }
            }
        });


        TextView register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,registeractivity.class);
                startActivity(intent);
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. you cant login without email verification.");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent =new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(authprofile.getCurrentUser()!=null){
            Toast.makeText(MainActivity.this,"Already Logged In!",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this,UserProfileActivity.class));
            finish();
        }else{
            Toast.makeText(MainActivity.this,"You can login now!",Toast.LENGTH_SHORT).show();
        }
    }
}