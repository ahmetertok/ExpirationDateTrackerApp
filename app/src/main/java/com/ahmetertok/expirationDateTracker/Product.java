package com.ahmetertok.expirationDateTracker;

import com.google.firebase.database.Exclude;

public class Product {
    private String date;
    private String mImageUrl;
    private String mKey;

    public Product(){

    }
    public Product(String Date, String imageUrl){

        date = Date;
        mImageUrl = imageUrl;
    }

    public String getdate(){
        return date;
    }
    public void setdate(String name){
        date = name;

    }
    public String getmImageUrl(){
        return mImageUrl;
    }
    public void setmImageUrl(String imageUrl){
        mImageUrl = imageUrl;
    }

    @Exclude
    public String getKey(){
        return mKey;
    }
    @Exclude
    public void setKey(String key){
        mKey = key;
    }
}
