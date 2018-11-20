package com.example.xtreme.ui_clientorder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


//I had to add dependencies for cardview and recyclerview in the project structure directory before
//the design code. this will enable a single layout for displaying each layout item. Recycler view to
//display multiple items
public class MenuActivity extends AppCompatActivity {


    private RecyclerView mFoodList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    //Allows the user to access the activity only when the user is authorized
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //Get a reference to the recycler view from activity_menu.xml
        mFoodList = (RecyclerView) findViewById(R.id.foodList);
        mFoodList.setHasFixedSize(true);
        mFoodList.setLayoutManager(new LinearLayoutManager(this));
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Item");
        mAuth = FirebaseAuth.getInstance();

        //If user is not logged into the system this will redirect the user to the login activity
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MenuActivity.this, MainActivity.class);
                    //Prevents the user from hitting the back button
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter <Food,FoodViewHolder> FBRA = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.singlemenuitem,
                FoodViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setPrice(model.getPrice());
                viewHolder.setDec(model.getDesc());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                //This section handles the food item the client clicks on and opens up a new activity
                final String food_key = getRef(position).getKey().toString(); // Specific food key item
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //start a new intent/activity after button is clicked.
                        Intent singleFoodActivity = new Intent(MenuActivity.this,SingleFoodActivity.class);
                        singleFoodActivity.putExtra("FoodId",food_key); //The food key tells which food item to display
                        startActivity(singleFoodActivity);
                    }
                });
            }
        };
        mFoodList.setAdapter(FBRA);
    }
    public static class FoodViewHolder extends  RecyclerView.ViewHolder{
        View mView;
        public FoodViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void  setName(String name){
            TextView food_name = (TextView) mView.findViewById(R.id.foodName);
            food_name.setText(name);
        }
        public void  setDec(String desc){
            TextView food_desc = (TextView) mView.findViewById(R.id.foodDesc);
            food_desc.setText(desc);
        }
        public void  setPrice(String price){
            TextView food_price = (TextView) mView.findViewById(R.id.foodPrice);
            food_price.setText(price);
        }

        //The image view into which we want to load an image into food_image
        public void  setImage(Context ctx, String image){
            ImageView food_image = (ImageView) mView.findViewById(R.id.foodImage);
            Picasso.get().load(image).into(food_image);

        }
    }
}