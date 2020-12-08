package com.kalkidan.habeshamemes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    private EditText userName, fullName, countryName;
    private Button saveInformationButton;
    private CircleImageView userProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    String currentUserId;
    private StorageReference filePath;
    private StorageReference userProfileImageRef;
    private ProgressDialog loadingBar;
    final static int GALLERY_PIC = 1;
    private Uri ImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");
        loadingBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        userName = (EditText) findViewById(R.id.setup_user_name);
        fullName = (EditText) findViewById(R.id.setup_full_name);
        countryName = (EditText) findViewById(R.id.setup_country);
        saveInformationButton = (Button) findViewById(R.id.setup_information_Button);
        userProfileImage = (CircleImageView) findViewById(R.id.setup_profile_image);
        saveInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveAccountSetupInformation();
            }
        });
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galerryIntent = new Intent();
                galerryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galerryIntent.setType("image/*");
                startActivityForResult(galerryIntent, GALLERY_PIC);
            }
        });


        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("ProfileImages")) {
                        String image = dataSnapshot.child("ProfileImages").getValue().toString();


                    } else {
                        Toast.makeText(SetupActivity.this, "Please provide Your Profile Image", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PIC && requestCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

              //  Picasso.get().load(result).placeholder(R.drawable.profile).into(userProfileImage);
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please Wait,we are updating Profile Image");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);
                Uri resultUri = result.getUri();
                filePath = userProfileImageRef.child(currentUserId + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Intent selfIntent = new Intent(SetupActivity.this, SetupActivity.class);
                            startActivity(selfIntent);
                            Toast.makeText(SetupActivity.this, "Your Profile Image is Stored Sucessfully", Toast.LENGTH_SHORT).show();
                            final String downloadUrl = filePath.getDownloadUrl().toString();
                            userRef.child("ProfileImage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent selfIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                                startActivity(selfIntent);
                                                Toast.makeText(SetupActivity.this, "Your Profile Image is Stored Sucessfully", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();

                                            } else {
                                                String msg = task.getException().getMessage();
                                                Toast.makeText(SetupActivity.this, "Error Occures" + msg, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        } else {

                            Toast.makeText(SetupActivity.this, "Error Occures: Image is not croped please try again", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        }
    }

    private void SaveAccountSetupInformation() {
        String username = userName.getText().toString();
        String fullname = fullName.getText().toString();
        String countryname = countryName.getText().toString();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "እባክዎ ስምዎን በትክክል ያስገቡ", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(fullname)) {
            Toast.makeText(this, "እባክዎ ሙሉ ስምዎን ያስገቡ", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(countryname)) {
            Toast.makeText(this, "እባክዎ የሰፈርዎን ስም ያስገቡ", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please Wait,we are registering Your Profile Information ");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            HashMap userMap = new HashMap();
            userMap.put("UserName", username);
            userMap.put("Fullname", fullname);
            userMap.put("UserCounty", countryname);
            userMap.put("Statuse", "የሃበሻ ሜም ተጠቃሚ");
            userMap.put("Gender", "none");
            userMap.put("DateOfBirth", "none");
            userMap.put("RelationshipStatus", "single");
            userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "መረጃዎ በትክክል ገብቷል..... ", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Error Occured" + message, Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                }

            });


        }

    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
