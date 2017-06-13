package com.lucky.friends;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    EditText email;
    EditText username;
    EditText password1,password2;
    Button signin;

    String usernameField,emailField,passwordField1,passwordField2;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.signEmail);
        password1 = (EditText) findViewById(R.id.signPassword1);
        password2 = (EditText) findViewById(R.id.signPassword2);
        signin = (Button) findViewById(R.id.signButton);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameField = username.getText().toString();
                emailField = email.getText().toString();
                passwordField1 = password1.getText().toString();
                passwordField2 = password2.getText().toString();

                if(!passwordField1.equals(passwordField2)){
                    Toast.makeText(SignUp.this,"Password Fields are not matching",Toast.LENGTH_SHORT).show();
                }else{
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(emailField,passwordField1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                mDatabase.child("Usernames").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String,Object> usernames = (Map<String,Object>) dataSnapshot.getValue();

                                        if(usernames == null){
                                            usernames = new HashMap<>();
                                        }

                                        usernames.put(emailField.split("@")[0],usernameField);
                                        mDatabase.child("Usernames").setValue(usernames);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                Intent intent = new Intent(SignUp.this,MainActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(SignUp.this,"Sorry  sth went wrong",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}
