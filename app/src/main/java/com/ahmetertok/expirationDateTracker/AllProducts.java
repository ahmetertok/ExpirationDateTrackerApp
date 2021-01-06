package com.ahmetertok.expirationDateTracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AllProducts extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private List<Upload> mUploads;
    private FirebaseUser user;
    private DatabaseReference reference;
    public String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        mRecyclerView = findViewById(R.id.recycler_view4);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUploads=new ArrayList<>();

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();




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
                    }





                }
                mAdapter = new ImageAdapter(AllProducts.this,mUploads);
                mRecyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AllProducts.this,"ERROR!",Toast.LENGTH_SHORT).show();
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
}