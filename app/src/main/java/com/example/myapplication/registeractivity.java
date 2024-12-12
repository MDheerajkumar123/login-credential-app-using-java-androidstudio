package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class registeractivity extends AppCompatActivity {

    EditText editTextID;
    RadioGroup radioGroupRole;
    Button buttonRegister;

    private EditText edittextregisterfullname, edittextregisteremail, edittextregisterdob, edittextregistermobile, edittextregisterpwd, edittextregisterconfirmpwd;
    private ProgressBar progressbar;
    private RadioGroup radiogroupregistergender;
    private RadioButton radiobuttonregistergenderselected;
    private DatePickerDialog picker;
    private static final String Tag = "registeractivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeractivity);
        FirebaseApp.initializeApp(this);
        Toast.makeText(registeractivity.this, "You can register now", Toast.LENGTH_LONG).show();
        progressbar = findViewById(R.id.progressBar);
        radiogroupregistergender = findViewById(R.id.radio_group_register_gender);
        edittextregisterfullname = findViewById(R.id.editText_register_full_name);
        edittextregisteremail = findViewById(R.id.editText_register_Email);
        edittextregisterdob = findViewById(R.id.editText_register_dob);
        edittextregistermobile = findViewById(R.id.editText_register_mobile);
        edittextregisterpwd = findViewById(R.id.editText_register_password);
        edittextregisterconfirmpwd = findViewById(R.id.edit_text_register_confirm_password);
        Button buttonregister = findViewById(R.id.button_register);

        // Integration of registration logic from RegistrationActivity
        editTextID = findViewById(R.id.editText_register_id);
        radioGroupRole = findViewById(R.id.radio_group_register_role);
        //buttonRegister = findViewById(R.id.buttonRegister);

        /*//buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredID = editTextID.getText().toString();
                int selectedRoleRadioButtonId = radioGroupRole.getCheckedRadioButtonId();
                String role = "";

                if (selectedRoleRadioButtonId != -1) {
                    RadioButton selectedRoleRadioButton = findViewById(selectedRoleRadioButtonId);
                    role = selectedRoleRadioButton.getText().toString();
                }

                if (isValidID(enteredID, role)) {
                    // Handle the registration based on the entered ID and role
                    // Example: Start the registration process
                    Toast.makeText(registeractivity.this, "Valid ID for the selected role. Starting registration...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(registeractivity.this, "Invalid ID for the selected role.", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        // Existing registration logic
        edittextregisterdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                picker = new DatePickerDialog(registeractivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        edittextregisterdob.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        buttonregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedgenderid = radiogroupregistergender.getCheckedRadioButtonId();
                radiobuttonregistergenderselected = findViewById(selectedgenderid);
                String textfullname = edittextregisterfullname.getText().toString();
                String textemail = edittextregisteremail.getText().toString();
                String textdob = edittextregisterdob.getText().toString();
                String textmobile = edittextregistermobile.getText().toString();
                String textpwd = edittextregisterpwd.getText().toString();
                String textconfirmpwd = edittextregisterconfirmpwd.getText().toString();
                String textgender;

                String enteredID = editTextID.getText().toString();
                int selectedRoleRadioButtonId = radioGroupRole.getCheckedRadioButtonId();
                String role = "";

                if (selectedRoleRadioButtonId != -1) {
                    RadioButton selectedRoleRadioButton = findViewById(selectedRoleRadioButtonId);
                    role = selectedRoleRadioButton.getText().toString();
                }

                /*//if (isValidID(enteredID, role)) {
                    // Handle the registration based on the entered ID and role
                    // Example: Start the registration process
                    Toast.makeText(registeractivity.this, "Valid ID for the selected role. Starting registration...", Toast.LENGTH_SHORT).show();
                } */


                if (TextUtils.isEmpty(textfullname)) {
                    Toast.makeText(registeractivity.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                    edittextregisterfullname.setError("Full name is required");
                    edittextregisterfullname.requestFocus();
                } else if (TextUtils.isEmpty(textemail)) {
                    Toast.makeText(registeractivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    edittextregisteremail.setError("Valid email is required");
                    edittextregisteremail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textemail).matches()) {
                    Toast.makeText(registeractivity.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    edittextregisteremail.setError("Valid email is required");
                    edittextregisteremail.requestFocus();
                } else if (TextUtils.isEmpty(textdob)) {
                    Toast.makeText(registeractivity.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
                    edittextregisterdob.setError("Valid email is required");
                    edittextregisterdob.requestFocus();
                } else if (radiogroupregistergender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(registeractivity.this, "Please select your gender", Toast.LENGTH_LONG).show();
                    radiobuttonregistergenderselected.setError("Gender is required");
                    radiobuttonregistergenderselected.requestFocus();
                }else if (!isValidID(enteredID, role)) {
                    Toast.makeText(registeractivity.this, "Invalid Role or Enrollment ID", Toast.LENGTH_LONG).show();
                    editTextID.setError("Enrollment ID is required");
                    editTextID.requestFocus();

                    //return; // Registration should stop
                }
                else if (TextUtils.isEmpty(textmobile)) {
                    Toast.makeText(registeractivity.this, "Please enter your mobile number", Toast.LENGTH_LONG).show();
                    edittextregistermobile.setError("Mobile number is required");
                    edittextregistermobile.requestFocus();
                } else if (textmobile.length() != 10) {
                    Toast.makeText(registeractivity.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    edittextregistermobile.setError("Mobile number should be 10 digits");
                    edittextregistermobile.requestFocus();
                } else if (TextUtils.isEmpty(textpwd)) {
                    Toast.makeText(registeractivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    edittextregisterpwd.setError("Password is required");
                    edittextregisterpwd.requestFocus();
                } else if (textpwd.length() < 8) {
                    Toast.makeText(registeractivity.this, "Password should be at least 8 characters", Toast.LENGTH_LONG).show();
                    edittextregisterpwd.setError("Password is too weak");
                    edittextregisterpwd.requestFocus();
                } else if (TextUtils.isEmpty(textconfirmpwd)) {
                    Toast.makeText(registeractivity.this, "Please confirm your password", Toast.LENGTH_LONG).show();
                    edittextregisterconfirmpwd.setError("Password confirmation is required");
                    edittextregisterconfirmpwd.requestFocus();
                } else if (!textpwd.equals(textconfirmpwd)) {
                    Toast.makeText(registeractivity.this, "Please re-check your password", Toast.LENGTH_LONG).show();
                    edittextregisterconfirmpwd.setError("Password confirmation is required");
                    edittextregisterconfirmpwd.requestFocus();
                    edittextregisterpwd.clearComposingText();
                    edittextregisterconfirmpwd.clearComposingText();
                }else if ("Student".equals(role)) {
                    // Additional checks for the Student role
                    if (isValidStudentID(textemail, enteredID)) {
                        textgender = radiobuttonregistergenderselected.getText().toString();
                        progressbar.setVisibility(View.VISIBLE);
                        registeractivity(textfullname, textemail, textdob, textgender,role, textmobile, textpwd);
                    } else {
                        Toast.makeText(registeractivity.this, "Email and Enrollment ID do not match for the Student role.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // For other roles (e.g., Teacher)
                    textgender = radiobuttonregistergenderselected.getText().toString();
                    progressbar.setVisibility(View.VISIBLE);
                    registeractivity(textfullname, textemail, textdob, textgender,role, textmobile, textpwd);
                }
                /*//else {
                    textgender = radiobuttonregistergenderselected.getText().toString();
                    progressbar.setVisibility(View.VISIBLE);
                    registeractivity(textfullname, textemail, textdob, textgender,role, textmobile, textpwd);
                }*/
            }
        });
    }

    private boolean isValidStudentID(String email, String enteredID) {
        String[] emailParts = email.split("@");
        if (emailParts.length == 2) {
            String[] idParts = emailParts[0].split("\\@");
            if (idParts.length == 1) {
                String studentID = idParts[0];
                return studentID.equals(enteredID);
            }
        }
        return false;
    }

    // Integration of the registration logic from RegistrationActivity
    private boolean isValidID(String id, String role) {
        if (role.equals("Student")) {
            // Check if the 3rd, 4th, and 6th digits are 6, 9, and A, respectively
            return id.length() == 10 && id.charAt(2) == '6' && id.charAt(3) == '9' && id.charAt(5) == 'A';
        } else if (role.equals("Faculty")) {
            // Check if the 1st and 2nd digits are 6 and 9
            return id.length() == 8 && id.charAt(0) == '6' && id.charAt(1) == '9';
        }
        return false;
    }

    // Registration method
    private void registeractivity(String textfullname, String textemail, String textdob, String textgender,String role, String textmobile, String textpwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        final Context context = this; // Get a reference to the activity context

        auth.createUserWithEmailAndPassword(textemail, textpwd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "User registered successfully", Toast.LENGTH_LONG).show();
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    ReadwriterUserDetails writeUserDetails = new ReadwriterUserDetails(textfullname, textdob,role, textgender, textmobile);

                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(registeractivity.this, "User registered successfully. Please verify your email", Toast.LENGTH_LONG).show();
                                if ("Student".equals(role)) {
                                    Intent intent = new Intent(registeractivity.this, StudentActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                } else if ("Teacher".equals(role)) {
                                    Intent intent = new Intent(registeractivity.this, TeacherActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                }

                                finish();
                                Intent intent = new Intent(registeractivity.this, UserProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                //firebaseUser.sendEmailVerification();
                            } else {
                                Toast.makeText(registeractivity.this, "User registration failed. Please try again", Toast.LENGTH_LONG).show();
                            }
                            progressbar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    // Handle the registration failure
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        edittextregisterpwd.setError("Your password is too weak. Kindly use a mix of alphabets, numbers, and special characters");
                        edittextregisterpwd.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        edittextregisterpwd.setError("Your email is invalid or already in use. Kindly re-enter");
                        edittextregisterpwd.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        edittextregisterpwd.setError("You have already registered with this email. Use another email");
                        edittextregisterpwd.requestFocus();
                    } catch (Exception e) {
                        Log.e(Tag, e.getMessage());
                        Toast.makeText(registeractivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressbar.setVisibility(View.GONE);
                }
            }
        });
    }
}
