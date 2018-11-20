package com.example.xtreme.ui_clientorder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText userEmail,userPass;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //user login referencing data from firebase database
        userEmail = (EditText) findViewById(R.id.userEmail);
        userPass= (EditText) findViewById(R.id.userPass);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    }

    //Extract the values from the user via the onCreate method
    public void signinButtonClicked(View view){
        String email = userEmail.getText().toString().trim();
        String pass = userPass.getText().toString().trim();
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
            mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        checkUserExists();
                    }
                }
            });
        }
    }

    //Get the user id from mAuth and check for the unique id stored in the database
    public void checkUserExists(){
        final String user_id = mAuth.getCurrentUser().getUid();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //check if the user id from mAuth matches the database user id
                if (dataSnapshot.hasChild(user_id)){
                    //If it does create a new intent and give the user access to the menu
                    Intent menuIntent = new Intent(LoginActivity.this,MenuActivity.class);
                    startActivity(menuIntent);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
