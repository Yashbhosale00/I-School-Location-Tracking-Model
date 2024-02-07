package com.example.locationdetect;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddSalesExecutive extends AppCompatActivity {

    private EditText nameEditText, surnameEditText, emailEditText, passEditText,userEditText;
    private Spinner genderSpinner;
    private Button loginButton;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sales_executive);

        // Find the UI elements by their IDs
        nameEditText = findViewById(R.id.name);
        surnameEditText = findViewById(R.id.surname);
        emailEditText = findViewById(R.id.email);
        passEditText = findViewById(R.id.pass);
        userEditText = findViewById(R.id.username);


        genderSpinner = findViewById(R.id.genderSpinner);
        setupGenderSpinner();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddSalesExecutive.this, "User Added Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddSalesExecutive.this,Admin_Activity.class);
                startActivity(intent);
                saveUserToFirebase();

            }
        });
    }

    private void saveUserToFirebase() {
        String name = nameEditText.getText().toString();
        String surname = surnameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passEditText.getText().toString();
        String username = userEditText.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(username)) {
            String userId = mDatabase.push().getKey();
            User user = new User(name, surname, email, password, username, gender);
            mDatabase.child("users").child(userId).setValue(user);

            Toast.makeText(AddSalesExecutive.this, "User Added Successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddSalesExecutive.this, Admin_Activity.class);
            startActivity(intent);
        } else {
            Toast.makeText(AddSalesExecutive.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupGenderSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender_options,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        genderSpinner.setAdapter(adapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedGender = parentView.getItemAtPosition(position).toString();
                Toast.makeText(AddSalesExecutive.this, "Selected Gender: " + selectedGender, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });


    }

}
