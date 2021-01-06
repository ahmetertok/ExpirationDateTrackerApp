package com.ahmetertok.expirationDateTracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;


public class AddActivity extends AppCompatActivity {

    public static EditText barcodeText;

    private static final int PICK_IMAGE_REQUEST = 1;
    private String userID;

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private Button barcodeScan;
    private TextView mTextViewShowUploads;
    private EditText mEditTextFileName,mEditTextFileDate;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private Uri mImageUri,imageUrl;
    private String barcode;


    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        barcodeText=findViewById(R.id.textViewBarcode);
        barcodeScan=findViewById(R.id.button_scann);
        mButtonChooseImage=findViewById(R.id.button_choose_image);
        mButtonUpload=findViewById(R.id.button_upload);
        mTextViewShowUploads=findViewById(R.id.text_view_show_uploads);
        mEditTextFileName=findViewById(R.id.edit_text_file_name);
        mEditTextFileDate=findViewById(R.id.edit_text_date);
        mImageView=findViewById(R.id.image_view);
        mProgressBar=findViewById(R.id.progres_bar);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        barcodeScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBarcodeScanner();
            }
        });

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();

            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()){
                    Toast.makeText(AddActivity.this,"Upload in progress",Toast.LENGTH_SHORT).show();
                }else{
                    uploadFile();
                }

            }
        });

        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagesActivity();
            }
        });
    }

    private void openBarcodeScanner() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
        }
        else {
            Toast.makeText(this,"Permission Allowed...",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddActivity.this,MainActivity2.class);
            startActivity(intent);
        }
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
        && data != null && data.getData() != null) {

            mImageUri = data.getData();
           // new Picasso().load(mImageUri).into(mImageView);
            mImageView.setImageURI(mImageUri);
        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){
        if (mImageUri != null ){
            barcode=barcodeText.getText().toString().trim();
            StorageReference fileReferance  = mStorageRef.child(barcode+"."+getFileExtension(mImageUri));













            mUploadTask = fileReferance.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //mProgressBar.setProgress(0);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setProgress(0);
                        }
                    },500);




                    Toast.makeText(AddActivity.this,"Upload successful",Toast.LENGTH_LONG).show();
                    urlMaker();






                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                        mProgressBar.setProgress((int) progress);
                }
            });


        }
        else{
            Toast.makeText(this,"No file selected",Toast.LENGTH_SHORT).show();
        }

    }

    private void openImagesActivity(){
        Intent intent = new Intent(this,ImagesActivity.class);
       startActivity(intent);
    }
    private void urlMaker(){

        mStorageRef.child(barcode+"."+getFileExtension(mImageUri)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {


                Upload upload = new Upload(mEditTextFileName.getText().toString().trim(), uri.toString());
                String uploadId =barcode;
                mDatabaseRef.child(uploadId).setValue(upload);
                FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("products").child(barcode).child("mImageUrl").setValue(uri.toString());
                FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("products").child(barcode).child("date").setValue(mEditTextFileDate.getText().toString().trim());
            }
        });


    }
}