package com.ahmetertok.expirationDateTracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;
    public String userID;
    private String productId,date;
    private TextView textView;

    private Button  addButton,menuButton,shopingListbtn,allProductbtn,expButton,recipeButton;
    private EditText product;
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private List<Upload> mUploads;
    private ImageView recipeImage;
    private int loop=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);






        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String msg = task.getResult();

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("TAG", msg);
                        //Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        recipeImage=findViewById(R.id.image_recip);


        mRecyclerView = findViewById(R.id.recycler_view2);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUploads=new ArrayList<>();

        recipeButton=(Button)findViewById(R.id.button12);
        recipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,RecipeActivity.class));
            }
        });






        expButton=(Button)findViewById(R.id.button11);
        expButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,ExpActivity.class));
            }
        });

        allProductbtn = (Button)findViewById(R.id.button9);
        allProductbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,AllProducts.class));

            }
        });

        shopingListbtn = (Button)findViewById(R.id.button8);
        shopingListbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,ShopingListActivity.class));
            }
        });

        menuButton = (Button)findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,menuActivity.class));
            }
        });





        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();



        addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                
                //FirebaseDatabase.getInstance().getReference().child("recipe").child("8696971191565").child("date").setValue("1");
                //FirebaseDatabase.getInstance().getReference().child("recipe").child("8696971191568").child("date").setValue("1");
                startActivity(new Intent(ProfileActivity.this,AddActivity.class));

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {




                for (DataSnapshot postSnapshot : snapshot.getChildren()){
                    Product product = postSnapshot.getValue(Product.class);
                    product.setKey(postSnapshot.getKey());

                    if (dateMethode(product.getdate())){
                        System.out.println(product.getKey());
                        System.out.println(product.getmImageUrl());
                        System.out.println(product.getdate());
                        Upload upload =new Upload();
                        upload.setKey(product.getKey());
                        upload.setmImageUrl(product.getmImageUrl());
                        upload.setmName(product.getdate());
                        mUploads.add(upload);
                        recipeFinder(product.getKey());
                    }





                }
                mAdapter = new ImageAdapter(ProfileActivity.this,mUploads);
                mRecyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this,"ERROR!",Toast.LENGTH_SHORT).show();
            }
        });




    }
    public static boolean dateMethode(String string){
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        //System.out.println(currentDate);

        String[] parts = string.split("/");
        String part1 = parts[0];
        String part2 = parts[1];
        String part3 = parts[2];


        String[] parts2 = currentDate.split("/");
        String part21 = parts2[0];
        String part22 = parts2[1];
        String part23 = parts2[2];

        if (Integer.parseInt(part3)   >= Integer.parseInt(part23) && Integer.parseInt(part2)   >= Integer.parseInt(part22) && Integer.parseInt(part1)   > Integer.parseInt(part21)){
            return true;

        }
        else if (Integer.parseInt(part3)   > Integer.parseInt(part23)){
            return true;
        }
            else return false;




    }
    void recipeFinder(String url){

        if(loop==0){
            FirebaseDatabase.getInstance().getReference().child("recipe").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()){
                        Product product = postSnapshot.getValue(Product.class);
                        product.setKey(postSnapshot.getKey());

                        if (product.getKey().equals(url)){
                           // Toast.makeText(ProfileActivity.this,product.getmImageUrl(),Toast.LENGTH_SHORT).show();

                            Glide.with(recipeImage.getContext()).load(product.getmImageUrl()).into(recipeImage);
                            loop++;

                        }





                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfileActivity.this,"ERROR!",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

}