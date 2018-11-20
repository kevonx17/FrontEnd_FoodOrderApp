package com.example.xtreme.ui_clientorder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SingleFoodActivity extends AppCompatActivity {
    private String food_key = null;
    private DatabaseReference mDatabase,userData;
    private TextView singleFoodTitle,singleFoodDesc,singleFoodPrice;
    private ImageView singleFoodImage;
    private Button orderButton;
    private FirebaseAuth mAuth;
    private FirebaseUser current_user;
    private DatabaseReference mRef;
    private String food_name,food_price,food_desc,food_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_food);

        //The extracted food key item passed in by the intent to identify the item that was clicked.
        food_key = getIntent().getExtras().getString("FoodId");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Item"); // get the item from the db

        //The values the database returned assigned a title eg. singlePrice
        singleFoodDesc = (TextView) findViewById(R.id.singleDesc);
        singleFoodTitle = (TextView) findViewById(R.id.singleTitle);
        singleFoodPrice = (TextView) findViewById(R.id.singlePrice);
        singleFoodImage = (ImageView) findViewById(R.id.singleImageView);

        //Extract values for particular food item from the database
        mAuth = FirebaseAuth.getInstance();

        //Get a reference to the user to know who is accessing the items
        current_user = mAuth.getCurrentUser();
        userData = FirebaseDatabase.getInstance().getReference().child("users").child(current_user.getUid());

        mRef= FirebaseDatabase.getInstance().getReference().child("Orders");

        //Access to the db root key for what the user clicks and extract all the values and store them
        //into a string variable.
        mDatabase.child(food_key).addValueEventListener(new ValueEventListener() {
            //Extract the values into new string names eg. String food_name from the db, typecast
            //them to (String) and use the dataSnapshot to get the reference from the child and then
            //get the values (getValues)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 food_name = (String) dataSnapshot.child("name").getValue();
                 food_price = (String) dataSnapshot.child("price").getValue();
                 food_desc= (String) dataSnapshot.child("desc").getValue();
                 food_image = (String) dataSnapshot.child("image").getValue();
                singleFoodTitle.setText(food_name);
                singleFoodDesc.setText(food_desc);
                singleFoodPrice.setText(food_price);
                Picasso.get().load(food_image).into(singleFoodImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void orderItemClicked(View view){
        final DatabaseReference newOrder = mRef.push();
        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newOrder.child("itemname").setValue(food_name);
                //reference the user by using the datasnapshot and the onCompleteListener
                // directs the user back to the main menu to view additional orders.
                newOrder.child("username").setValue(dataSnapshot.child("Name")
                        .getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Directs user back to main activity
                        startActivity(new Intent(SingleFoodActivity.this,MenuActivity.class));
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
