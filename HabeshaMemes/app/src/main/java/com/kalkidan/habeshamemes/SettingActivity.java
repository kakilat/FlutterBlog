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
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
private EditText setting_status,setting_usernamme,setting_profile_fullname,
        settin_country,setting_dob,settig_gender,setting_relation_ship_status;
private Button update_account_setting_button;
private CircleImageView  setting_profile_image;

private DatabaseReference settingUserRef;
private FirebaseAuth mAuth;
private StorageReference userProfileImageRef;

    private StorageReference filePath;
    private DatabaseReference userRef;


    private String currentUserId;
final static int GALLERY_PIC=1;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        loadingBar=new ProgressDialog(this);


        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        settingUserRef=FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        userProfileImageRef= FirebaseStorage.getInstance().getReference().child("ProfileImages");
        userRef=FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        setting_status=(EditText) findViewById(R.id.setting_status);
        setting_usernamme=(EditText) findViewById(R.id.setting_usernamme);
        setting_profile_fullname=(EditText) findViewById(R.id.setting_profile_fullname);
        settin_country=(EditText) findViewById(R.id.settin_country);
        setting_dob=(EditText) findViewById(R.id.setting_dob);
        settig_gender=(EditText) findViewById(R.id.settig_gender);
        setting_relation_ship_status=(EditText) findViewById(R.id.setting_relation_ship_status);
        update_account_setting_button=(Button)findViewById(R.id.update_account_setting_button);
        setting_profile_image=(CircleImageView) findViewById(R.id.setting_profile_image);

settingUserRef.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
if(dataSnapshot.exists()){
    String myprofileImage=dataSnapshot.child("ProfileImage").getValue().toString();
    String myuserName=dataSnapshot.child("UserName").getValue().toString();
    String myFullName=dataSnapshot.child("FullName").getValue().toString();
    String myprofilestatus=dataSnapshot.child("Statuse").getValue().toString();
    String mydob=dataSnapshot.child("DateOfBirth").getValue().toString();
    String mycountry=dataSnapshot.child("UserCounty").getValue().toString();
    String mygender=dataSnapshot.child("Gender").getValue().toString();
    String myRelationshipstatus=dataSnapshot.child("RelationshipStatus").getValue().toString();


    Picasso.get().load(myprofileImage).placeholder(R.drawable.profile).into(setting_profile_image);
    setting_usernamme.setText(myuserName);
    setting_profile_fullname.setText(myFullName);
    setting_status.setText(myprofilestatus);
    setting_dob.setText(mydob);
    settin_country.setText(mycountry);
    settig_gender.setText(mygender);
    setting_relation_ship_status.setText(myRelationshipstatus);

}
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Intent galerryIntent=new Intent ();
        galerryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galerryIntent.setType("image/*");
        startActivityForResult(galerryIntent,GALLERY_PIC);
    }
});


update_account_setting_button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        ValidateAccountInformation();
    }
});

        setting_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_PIC && requestCode==RESULT_OK && data!=null){
            Uri ImageUri=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please Wait,we are updating Profile Image");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();
                Uri resultUri=result.getUri();
                filePath= userProfileImageRef.child(currentUserId+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Intent selfIntent= new Intent(SettingActivity.this,SetupActivity.class);
                            startActivity(selfIntent);
                            Toast.makeText(SettingActivity.this,"Your Profile Image is Stored Sucessfully",Toast.LENGTH_SHORT).show();

                            final String downloadUrl=filePath.getDownloadUrl().toString();

                            settingUserRef.child("ProfileImage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Intent selfIntent =new Intent (SettingActivity.this,SettingActivity.class);
                                                startActivity(selfIntent);
                                                Toast.makeText(SettingActivity.this,"Your Profile Image is Stored Sucessfully",Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                            else {
                                                String msg=task.getException().getMessage();
                                                Toast.makeText(SettingActivity.this,"Error Occures"+msg,Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                        else {

                            Toast.makeText(SettingActivity.this,"Error Occures: Image is not croped please try again",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        }
    }


    private void ValidateAccountInformation() {
        String username=setting_usernamme.getText().toString();
        String profileName=setting_profile_fullname.getText().toString();
        String statues=setting_status.getText().toString();
        String dob=setting_dob.getText().toString();
        String country=settin_country.getText().toString();
        String gender=settig_gender.getText().toString();
        String relation=setting_relation_ship_status.getText().toString();


        if(TextUtils.isEmpty(username)){
            Toast.makeText(this,"Please Type your user Name",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(profileName)){
            Toast.makeText(this,"Please Type Your Full Name",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(statues)){
            Toast.makeText(this,"Please Type Your Status to the accunt",Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(dob)){
            Toast.makeText(this,"Please Type Your Date of Birth",Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(country)){
            Toast.makeText(this,"Please Type Your Country Name",Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(gender)){
            Toast.makeText(this,"Please Type Your Gender",Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(relation)){
            Toast.makeText(this,"Please Type Your Relationship Status",Toast.LENGTH_LONG).show();
        }

        else{
            loadingBar.setTitle("Profile Image");
            loadingBar.setMessage("Please Wait,we are updating Profile Image");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            UpdateAccountInformation(username,profileName,statues,dob,country,gender,relation);
        }
       }

    private void UpdateAccountInformation(String username, String profileName, String statues, String dob, String country, String gender, String relation) {

        HashMap userMap=new HashMap();
        userMap.put("UserName",username);
        userMap.put("Fullname",profileName);
        userMap.put("Statuse",statues);
        userMap.put("DateOfBirth",dob);
        userMap.put("UserCounty",country);
        userMap.put("Gender",gender);
        userMap.put("RelationshipStatus",relation);
settingUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
    @Override
    public void onComplete(@NonNull Task task) {
        if (task.isSuccessful()){
            loadingBar.dismiss();
            Toast.makeText(SettingActivity.this,"Account Information is Updated Sucessfully",Toast.LENGTH_LONG).show();
            SendUserToMainActivity();
        }
        else {
            loadingBar.dismiss();
            Toast.makeText(SettingActivity.this,"Error Occure while updating Your Information",Toast.LENGTH_LONG).show();

        }

    }
});

    }

    private void SendUserToMainActivity() {
        Intent mainIntent=new Intent(SettingActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
