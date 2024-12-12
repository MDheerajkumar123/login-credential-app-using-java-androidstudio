package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {
    private TextView textviewWelcome,textviewFullName,textviewEmail,textviewdob,textviewGender,textviewMobile;
    private ProgressBar progressBar;
    private String fullname,email,dob,gender,mobile;
    private ImageView imageview;
    private FirebaseAuth authProfile;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        textviewWelcome=findViewById(R.id.textView_show_welcome);
        textviewFullName=findViewById(R.id.textView_show_full_name);
        textviewEmail=findViewById(R.id.textView_show_email);
        textviewdob=findViewById(R.id.textView_show_dob);
        textviewGender=findViewById(R.id.textView_show_gender);
        textviewMobile=findViewById(R.id.textView_show_mobile);
        progressBar=findViewById(R.id.progress_bar);
        imageview = findViewById(R.id.imageView_profile_dp);
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this,UploadProfilePicActivity.class);
                startActivity(intent);
            }
        });
        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        if(firebaseUser == null){
            Toast.makeText(UserProfileActivity.this,"Something went wrong! User details are not available at the momnet" ,Toast.LENGTH_LONG).show();
        }else{
            checkIfEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }

    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if(!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. you cant login without email verification next time.");
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


    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();
        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadwriterUserDetails readUserDetails = snapshot.getValue(ReadwriterUserDetails.class);

                if (readUserDetails != null){
                    fullname= readUserDetails.fullname;
                    email=firebaseUser.getEmail();
                    dob=readUserDetails.dob;
                    gender= readUserDetails.gender;
                    mobile= readUserDetails.mobile;
                    textviewWelcome.setText("Welcome, "+ fullname + "!");
                    textviewFullName.setText(fullname);
                    textviewEmail.setText(email);
                    textviewdob.setText(dob);
                    textviewGender.setText(gender);
                    textviewMobile.setText(mobile);
                    Uri uri=firebaseUser.getPhotoUrl();
                    Glide.with(UserProfileActivity.this).load(uri).into(imageview);

                }else{
                    Toast.makeText(UserProfileActivity.this,"Something went wrong!" ,Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this,"Something went wrong!" ,Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.common_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.menu_refresh){
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        }
        if(id==R.id.menu_update_profile){
            Intent intent = new Intent(UserProfileActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.menu_update_email){
            Intent intent = new Intent(UserProfileActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
        }
        /*//else if(id==R.id.menu_settings){
            Toast.makeText(UserProfileActivity.this,"menu_settings",Toast.LENGTH_SHORT).show();
        }
        else if(id==R.id.menu_change_password){
            Intent intent = new Intent(UserProfileActivity.this,ChangePasswordeActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.menu_delete_profile){
            Intent intent = new Intent(UserProfileActivity.this,DeleteProfileActivity.class);
            startActivity(intent);
        }*/
         if(id==R.id.menu_logout){
             authProfile.signOut();
            Intent intent = new Intent(UserProfileActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity(intent);
            Toast.makeText(UserProfileActivity.this,"Logged Out",Toast.LENGTH_SHORT).show();
            finish();
            return false;

        }else{
             Toast.makeText(UserProfileActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
         }
        return true;
    }
}