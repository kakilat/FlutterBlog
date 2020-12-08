package com.kalkidan.habeshamemes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {
    private ImageButton selectPostImage;
    private Button updatePosteButton;
    private EditText postDiscription;
    private String discriptiion;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private String saveCurrentDate, saveCurrentTime, postRandumName, downloadUrl, current_user_Id;
    private StorageReference PostImageReference;
    private static final int GALLERY_PIC = 1;
    private DatabaseReference userRef, postRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mAuth = FirebaseAuth.getInstance();
        current_user_Id = mAuth.getUid();
        loadingBar = new ProgressDialog(this);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("PostNode");
        selectPostImage = (ImageButton) findViewById(R.id.selectPostImage);
        postDiscription = (EditText) findViewById(R.id.postDiscription);
        updatePosteButton = (Button) findViewById(R.id.updatePostButton);
        PostImageReference = FirebaseStorage.getInstance().getReference();
        selectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });
        updatePosteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidatePostInformation();
            }
        });
    }

    private void ValidatePostInformation() {
        discriptiion = postDiscription.getText().toString();
        if (ImageUri == null) {
            Toast.makeText(this, "Please Select Image first", Toast.LENGTH_LONG).show();

        } else if (discriptiion == null) {
            Toast.makeText(this, "Please Write Some Discription about Your Image ", Toast.LENGTH_LONG).show();

        } else {
            loadingBar.setTitle("Add New Post");
            loadingBar.setMessage("Please Wait,we are updating your new post");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            StoringImageToFirebaseStorage();
        }

    }

    private void StoringImageToFirebaseStorage() {
        Calendar calpostdate = Calendar.getInstance();
        SimpleDateFormat curruentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = curruentDate.format(calpostdate.getTime());
        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentDate = currentTime.format(calForTime.getTime());
        postRandumName = saveCurrentDate + saveCurrentTime;
        final StorageReference filePath = PostImageReference.child("PostImage").child(ImageUri.getLastPathSegment() + postRandumName + ".jpg");


        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {//dont forget to checke the url if it works
                    downloadUrl = filePath.getDownloadUrl().toString();
                    Toast.makeText(PostActivity.this, "Image Uploaded Sucessfully", Toast.LENGTH_LONG).show();
                    SavingInformationToDatabase();

                } else {
                    Toast.makeText(PostActivity.this, "Error Ocured While Uploading Yur Post", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void SavingInformationToDatabase() {
        userRef.child(current_user_Id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userfullname = dataSnapshot.child("Fullname").getValue().toString();
                    String userProfileImage = dataSnapshot.child("ProfileImages").getValue().toString();
                    HashMap postmap = new HashMap();
                    postmap.put("Uid", current_user_Id);
                    postmap.put("date", saveCurrentDate);
                    postmap.put("time", saveCurrentTime);
                    postmap.put("Description", discriptiion);
                    postmap.put("PostImage", downloadUrl);
                    postmap.put("ProfileImage", userProfileImage);
                    postmap.put("FullName", userfullname);
                    postRef.child(userfullname + current_user_Id + postRandumName).updateChildren(postmap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {

                                        SendUserToMainActivity();
                                        Toast.makeText(PostActivity.this, "Your Post is On the way ", Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();
                                    } else {
                                        Toast.makeText(PostActivity.this, "Error Ocured While Updating ur post", Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void OpenGallery() {
        Intent galerryIntent = new Intent();
        galerryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galerryIntent.setType("image/*");
        startActivityForResult(galerryIntent, GALLERY_PIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PIC && requestCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            selectPostImage.setImageURI(ImageUri);

        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
