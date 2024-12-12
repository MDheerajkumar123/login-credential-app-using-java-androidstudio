package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TeaassignActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AssignmentAdapter adapter;
    private List<Assignment> assignments;
    private FloatingActionButton fab;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teaassign);

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);

        // Initialize Firebase Authentication
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            // Handle the case when the user is not authenticated.
            // You can navigate to the login or registration screen.
        }

        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("assignments");

        assignments = new ArrayList<>();

        // Set up the RecyclerView and Adapter
        adapter = new AssignmentAdapter(assignments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Load assignments from Firebase Realtime Database
        loadAssignments();

        // Handle click on the FAB to create a new assignment
        fab.setOnClickListener(v -> {
            // Add your logic here to create a new assignment and add it to Firebase Realtime Database
            // For example, you can open a dialog or activity to enter assignment details
            // Once details are entered, add the assignment to the database
            //showAssignmentInputDialog();

            Intent intent = new Intent(TeaassignActivity.this, Assignment.class);
            startActivity(intent);
        });
    }

    /*//private void showAssignmentInputDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Create New Assignment");

        // Create EditText fields to input assignment title and description
        final EditText titleEditText = new EditText(this);
        titleEditText.setHint("Assignment Title");
        final EditText descriptionEditText = new EditText(this);
        descriptionEditText.setHint("Assignment Description");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(titleEditText);
        layout.addView(descriptionEditText);

        dialogBuilder.setView(layout);

        dialogBuilder.setPositiveButton("Create", (dialog, which) -> {
            String title = titleEditText.getText().toString();
            String description = descriptionEditText.getText().toString();

            if (!title.isEmpty() && !description.isEmpty()) {
                // Assignment details are valid, add them to the Firebase Realtime Database
                Assignment newAssignment = new Assignment(title, description);
                addAssignmentToDatabase(newAssignment);
            } else {
                // Handle invalid input, show a message to the user, etc.
            }
        });

        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }*/

    // Function to add the assignment to Firebase Realtime Database
    private void addAssignmentToDatabase(Assignment assignment) {
        // You should have a reference to your Firebase Realtime Database
        DatabaseReference assignmentsRef = databaseReference.child("assignments");

        // Use push() to generate a unique key for the assignment
        DatabaseReference newAssignmentRef = assignmentsRef.push();

        // Set the value of the assignment using setValue()
        newAssignmentRef.setValue(assignment);

        // Optional: You can add an OnSuccessListener to handle success
        newAssignmentRef.setValue(assignment, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                // Assignment added successfully
                // You can also update the UI to reflect the new assignment
            } else {
                // Handle database write error
            }
        });
    }

    private void loadAssignments() {
        // Attach a ValueEventListener to read assignments from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                assignments.clear();
                for (DataSnapshot assignmentSnapshot : dataSnapshot.getChildren()) {
                    Assignment assignment = assignmentSnapshot.getValue(Assignment.class);
                    if (assignment != null) {
                        assignments.add(assignment);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database read errors here
            }
        });


    }
}