package com.kalkidan.um;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyPermission;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef, postRef, likesRef;
    private CircleImageView navProfileImage;
    private TextView naveProfileUserName;
    private RecyclerView postList;
    String currentUserId;
    private AdView mAdView;
    FirebaseRecyclerAdapter adapter;
    private List<Posts> pstList;
    Boolean LikeChecker = false;
    private static ProgressDialog progressDialog;
    Boolean login = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, String.valueOf(R.string.test_ad_unit));
        ShowLittleAd();
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                ShowLittleAd();
            }
        });
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        try {
            currentUserId = mAuth.getCurrentUser().getUid();
            login = true;
        } catch (Exception e) {

        }
        pstList = new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hnavigationView = navigationView.getHeaderView(0);
        navProfileImage = (CircleImageView) hnavigationView.findViewById(R.id.nav_profile_image);
        naveProfileUserName = (TextView) hnavigationView.findViewById(R.id.nav_user_full_name);
        navigationView.setNavigationItemSelectedListener(this);
        postList = (RecyclerView) findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);
        if (Connectivity.isConnected(getApplicationContext())) {
            DisplayAllUsersPost();
        } else {
            Toast.makeText(this, "እባክዎ ስልክዎን ከ ኢንተርኔት ጋር ያገናኙ  ", Toast.LENGTH_LONG).show();
        }
        if (login = true) {
            UserRef.child(currentUserId).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.hasChild("fullname")) {
                            String fullName = dataSnapshot.child("fullname").getValue().toString();
                            naveProfileUserName.setText(fullName);
                        }
                        if (dataSnapshot.hasChild("profileImage")) {
                            String profileImage = dataSnapshot.child("profileImage").getValue().toString();
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
        }


    }

    public void ShowLittleAd() {
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new
                AdRequest.Builder().addTestDevice("//MY DEVICE").
                addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        mAdView.loadAd(adRequest);
    }


    private void DisplayAllUsersPost() {
        ProgressCheeking();

        FirebaseRecyclerAdapter<Posts, PostViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Posts, PostViewHolder>(
                        Posts.class,
                        R.layout.all_post_layout,
                        PostViewHolder.class,
                        postRef
                ) {
                    @Override
                    protected void populateViewHolder(PostViewHolder viewHolder, Posts model, int position) {
                        final String postKey = getRef(position).getKey();
                        final String imageVal = model.getPostImage();
                        viewHolder.setFullName(model.getFullname());
                        viewHolder.setTime(model.getTime());
                        viewHolder.setDate(model.getDate());
                        viewHolder.setDescription(model.getDescription());
                        viewHolder.setProfileImage(getApplicationContext(), model.getProfileImage());
                        viewHolder.setPostImage(getApplicationContext(), model.getPostImage());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent clickIntent = new Intent(MainActivity.this, ClickPostActivity.class);
                                clickIntent.putExtra("Postkey", postKey);
                                startActivity(clickIntent);

                            }
                        });
                        viewHolder.commentPostButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent commentActivity = new Intent(MainActivity.this, CommentActivity.class);
                                commentActivity.putExtra("Postkey", postKey);
                                startActivity(commentActivity);


                            }
                        });
                        viewHolder.likepostButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                LikeChecker = true;
                                likesRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (LikeChecker.equals(true)) {
                                            try {
                                                if (dataSnapshot.child(postKey).hasChild(currentUserId)) {
                                                    likesRef.child(postKey).child(currentUserId).removeValue();
                                                    LikeChecker = false;
                                                } else {
                                                    likesRef.child(postKey).child(currentUserId).setValue(true);

                                                    LikeChecker = false;
                                                }
                                            } catch (Exception e) {

                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    }
                };
        postList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageButton likepostButton, commentPostButton;
        TextView displayNoofLikes;
        int countLikes;
        String currentUserId;
        DatabaseReference likesRef;

        public PostViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            likepostButton = (ImageButton) mView.findViewById(R.id.likeButton);
            commentPostButton = (ImageButton) mView.findViewById(R.id.commentButton);
            displayNoofLikes = (TextView) mView.findViewById(R.id.displayLikeButton);
            likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            try {
                currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            } catch (Exception e) {
            }

        }

        public void setLikeButtunStatus(final String postKey) {

            likesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.child(postKey).hasChild(currentUserId)) {
                            likepostButton.setImageResource(R.drawable.love);
                        } else {

                            likepostButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);


                        }
                    } catch (Exception e) {

                    }
                    countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                    displayNoofLikes.setText(Integer.toString(countLikes) + " Likes");
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        public void setFullName(String fullName) {
            TextView userName = (TextView) mView.findViewById(R.id.post_user_name);
            userName.setText(fullName);

        }

        public void setProfileImage(Context context, String profileImage) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            Picasso.get().load(profileImage).into(image);

        }

        public void setTime(String time) {
            TextView postTime = (TextView) mView.findViewById(R.id.post_time);
            postTime.setText("" + time);


        }

        public void setDate(String date) {
            TextView postDate = (TextView) mView.findViewById(R.id.post_date);
            postDate.setText("" + date);
        }

        public void setDescription(String description) {
            TextView postDescription = (TextView) mView.findViewById(R.id.post_discription);
            postDescription.setText(description);
        }

        public void setPostImage(Context context, String postImage) {
            ImageView postedImage = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.get().load(postImage).into(postedImage);
        }

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.addpost) {
            try {
                currentUserId = mAuth.getCurrentUser().getUid();
                SenDUserToPostActivity();
            } catch (Exception e) {

                AlertDialogShow();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/developer?id=Instant+Systems")));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/developer?id=Instant+Systems")));
            }
        } else if (id == R.id.nav_post) {
            try {
                currentUserId = mAuth.getCurrentUser().getUid();
                SenDUserToPostActivity();
            } catch (Exception e) {
                throw new IllegalStateException("A book has a null property", e);
            }


        } else if (id == R.id.nav_frainds) {

            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/developer?id=Instant+Systems")));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/developer?id=Instant+Systems")));
            }


        } else if (id == R.id.nav_find_frainds) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "ሐበሻ ሜም  ";
            String shareSub = "የሁላችንም ቀልድ የሁላችንም መዝናኛ  ";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        } else if (id == R.id.nav_massages) {
            Exit();


        } else if (id == R.id.nav_setting) {
            try {
                currentUserId = mAuth.getCurrentUser().getUid();

                SendUserToSetupActivity();

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "ይቅርታ!መጀመርያ አካውንት ይክፈቱ  ", Toast.LENGTH_LONG).show();
            }


        } else if (id == R.id.nav_logout) {
            try {
                mAuth.signOut();
            } catch (Exception e) {
                throw new IllegalStateException("A book has a null property", e);
            }

            sendUserToLoginActivity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();

    }

    private void SenDUserToPostActivity() {
        Intent ddnewPpostIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(ddnewPpostIntent);

    }

    public void Exit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("ሐበሻ ሜም 😎");
        //builder.setIcon(R.drawable.pp);
        builder.setMessage("አፕሊኬሽኑን መዝጋት ይፈልጋሉ? ")
                .setCancelable(false)
                .setPositiveButton("አዎ  ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("አልፈልግም ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void ProgressCheeking() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Processing...");
        progressDialog.setTitle("ሐበሻ ሜም 😎");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(true);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }).start();

    }

    private void AlertDialogShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("ሐበሻ MEME \uD83D\uDE02\uD83D\uDE0E ");
        builder.setIcon(R.drawable.iconum);
        builder.setMessage("የራስዎን ሜም ለመልቀቅ እንዲሁም ላይክ ለማድረግ እና አስተያየት ለመስጠት የሜም አካውንት ያስፈልግዎታል: አካውንት መክፈት ይፈልጋሉ?    ")
                .setCancelable(false)
                .setPositiveButton("አዎ  ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sendUserToLoginActivity();
                    }
                })
                .setNegativeButton("አልፈልግም ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


}
