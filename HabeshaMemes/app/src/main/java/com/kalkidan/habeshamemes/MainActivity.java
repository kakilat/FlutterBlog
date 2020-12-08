package com.kalkidan.habeshamemes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef, postRef,likesRef;
    private CircleImageView navProfileImage;
    private TextView naveProfileUserName;
    private RecyclerView postList;
    String currentUserId;
    private ImageButton addNewPostButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likesRef=FirebaseDatabase.getInstance().getReference().child("Likes");
        setContentView(R.layout.activity_main);
        currentUserId = mAuth.getCurrentUser().getUid();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        addNewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navProfileImage = (CircleImageView) navigationView.findViewById(R.id.nav_profile_image);
        naveProfileUserName = (TextView) navigationView.findViewById(R.id.nav_user_full_name);
        postList = (RecyclerView) findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);


        UserRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("Fullname")) {
                        String fullName = dataSnapshot.child("Fullname").getValue().toString();
                        naveProfileUserName.setText(fullName);
                    }
                    if (dataSnapshot.hasChild("ProfileImages")) {
                        String profileImage = dataSnapshot.child("ProfileImages").getValue().toString();
                        Picasso.get().load(profileImage).placeholder(R.drawable.profile).into(navProfileImage);
                    } else {
                        Toast.makeText(MainActivity.this, "Profile Image Dosent exist", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        addNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SenDUserToPostActivity();
            }
        });

        DisplayAllUsersPost();

    }

    private void DisplayAllUsersPost() {

        FirebaseRecyclerOptions<Posts> options =
                new FirebaseRecyclerOptions.Builder<Posts>()
                        .setQuery(postRef, Posts.class)
                        .build();


        FirebaseRecyclerAdapter<Posts, PostViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Posts, PostViewHolder>(options) {


                    @Override
                    protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Posts model) {
                        final String postKey = getRef(position).getKey();
                        holder.setFullName(model.getFullName());
                        holder.setTime(model.getTime());
                        holder.setDate(model.getDate());
                        holder.setDescription(model.getDescription());
                        holder.setProfileImage(model.getProfileImage());
                        holder.setPostImage(model.getPostImage());

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent clickIntent = new Intent(MainActivity.this, ClickPostActivity.class);
                                clickIntent.putExtra("Postkey", postKey);
                                startActivity(clickIntent);
                            }
                        });


                    }

                    @NonNull
                    @Override
                    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return null;
                    }
                };
        postList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        View mView;
ImageButton likepostButton,commentPostButton;
TextView displayNoofLikes;

        public PostViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            likepostButton=(ImageButton) mView.findViewById(R.id.likeButton);
            commentPostButton=(ImageButton) mView.findViewById(R.id.commentButton);
            displayNoofLikes=(TextView) mView.findViewById(R.id.displayLikeButton);

        }

        public void setFullName(String fullName) {
            TextView userName = (TextView) mView.findViewById(R.id.post_user_name);
            userName.setText(fullName);

        }

        public void setProfileImage(String profileImage) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            Picasso.get().load(profileImage).into(image);

        }

        public void setTime(String time) {
            TextView postTime = (TextView) mView.findViewById(R.id.post_time);
            postTime.setText("   " + time);


        }

        public void setDate(String date) {
            TextView postDate = (TextView) mView.findViewById(R.id.post_date);
            postDate.setText("   " + date);
        }

        public void setDescription(String description) {
            TextView postDescription = (TextView) mView.findViewById(R.id.postDiscription);
            postDescription.setText(description);
        }

        public void setPostImage(String postImage) {
            ImageView postedImage = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.get().load(postImage).into(postedImage);
        }

    }

    private void SenDUserToPostActivity() {
        Intent ddnewPpostIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(ddnewPpostIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendUserToLoginActivity();
        } else {
            ChekeUserExistance();
        }
    }

    private void ChekeUserExistance() {
        final String current_ser_id = mAuth.getCurrentUser().getUid();
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(current_ser_id)) {
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();

    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_post) {
            SenDUserToPostActivity();

        } else if (id == R.id.nav_frainds) {

        } else if (id == R.id.nav_find_frainds) {

        } else if (id == R.id.nav_massages) {

        } else if (id == R.id.nav_setting) {
            Intent settingAtivity = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(settingAtivity);
            finish();
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            sendUserToLoginActivity();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
