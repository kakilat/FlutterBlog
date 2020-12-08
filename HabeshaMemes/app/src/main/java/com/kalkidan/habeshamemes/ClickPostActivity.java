package com.kalkidan.habeshamemes;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {
    private ImageView postImage;
    private TextView postDescription;
    private Button editPost, deletePost;
    private String PostKey, currentUserId, databaseUserId, descriptio, image;
    private FirebaseAuth mAuth;
    private DatabaseReference clickpostRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);
        postImage = (ImageView) findViewById(R.id.clickedPostImage);
        PostKey = getIntent().getExtras().get("Postkey").toString();
        postDescription = (TextView) findViewById(R.id.clickedPostDescription);
        editPost = (Button) findViewById(R.id.editPostButton);
        deletePost = (Button) findViewById(R.id.deletePostButton);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        editPost.setVisibility(View.INVISIBLE);
        deletePost.setVisibility(View.INVISIBLE);
        clickpostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);
        clickpostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    descriptio = dataSnapshot.child("Description").getValue().toString();
                    image = dataSnapshot.child("PostImage").getValue().toString();
                    databaseUserId = dataSnapshot.child("Uid").getValue().toString();
                    postDescription.setText(descriptio);
                    Picasso.get().load(image).into(postImage);
                    if (currentUserId.equals(databaseUserId)) {
                        editPost.setVisibility(View.VISIBLE);
                        deletePost.setVisibility(View.VISIBLE);
                    }
                    editPost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EditCurrentPost(descriptio);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteCurrentPost();
            }
        });
    }

    private void EditCurrentPost(String descriptio) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Post");
        final EditText inputField = new EditText(ClickPostActivity.this);
        inputField.setText(descriptio);
        builder.setView(inputField);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clickpostRef.child("Description").setValue(inputField.getText().toString());
                Toast.makeText(ClickPostActivity.this, "Post Updated Seessfully", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_green_dark);

    }

    private void DeleteCurrentPost() {
        clickpostRef.removeValue();
        SendUserToMainActivity();
        Toast.makeText(ClickPostActivity.this, "Your Post is deleted Sucessfully", Toast.LENGTH_LONG).show();
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(ClickPostActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
