package com.kalkidan.habeshamemes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
private Button LoginButton;
private EditText userEmail,userPassword;
private TextView needNewAccountLink;
private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private static final int RC_SIGN_IN=1;
    private GoogleApiClient mGoogleSignInClient;
private static final String TAG="LoginActivity";
    private ImageView googleSignninButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        googleSignninButton=(ImageView) findViewById(R.id.googleSigninbutton);
        needNewAccountLink=(TextView) findViewById(R.id.link_to_register);
        userEmail=(EditText) findViewById(R.id.login_email);
        userPassword=(EditText)findViewById(R.id.login_password);
        LoginButton=(Button) findViewById(R.id.login_button);
        loadingBar=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        needNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToRegisterActivity();
            }
        });
       LoginButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view)
           {
               AllowingUserToLogin();
           }
       });
        GoogleSignInOptions  gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient =new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
Toast.makeText(LoginActivity.this,"Connection To Google Signin Faild",Toast.LENGTH_LONG).show();
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();



        googleSignninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }












    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            loadingBar.setTitle("Google Sign In");
            loadingBar.setMessage("Please Wait, While We Are allowing you to login using google account your  ");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
if (result.isSuccess()){
    GoogleSignInAccount account=result.getSignInAccount();
    firebaseAuthWithGoogle(account);
    Toast.makeText(this,"please wait,While we are getting your auth result....",Toast.LENGTH_SHORT).show();
loadingBar.dismiss();
}else{
    Toast.makeText(this,"cant get Authentication Result",Toast.LENGTH_SHORT).show();
loadingBar.dismiss();
}

        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            SendUserToMainActivity();
                            loadingBar.dismiss();

                        } else {
                            String message=task.getException().toString();
                            SendUserToLoginActivity();
                            Log.w(TAG, "signInWithCredential:failure"+message, task.getException());
                            Toast.makeText(LoginActivity.this,"Unable To Autenticate to your Acount",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }

                    }
                });
    }

    private void SendUserToLoginActivity() {
        Intent selfIntent=new Intent(LoginActivity.this,LoginActivity.class);
        selfIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(selfIntent);
        finish();
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            SendUserToMainActivity();
        }
    }

    private void AllowingUserToLogin() {
        String email=userEmail.getText().toString();
        String password=userPassword.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"please provide your email...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"please provide your password...",Toast.LENGTH_SHORT).show();
        }
else{
            loadingBar.setTitle("Login");
            loadingBar.setMessage("Please Wait, ወደ ሐበሻ ሜም በመግባት ላይ  ");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
if(task.isSuccessful()) {
    SendUserToMainActivity();
    Toast.makeText(LoginActivity.this, "You Are Loged in S.....", Toast.LENGTH_SHORT).show();
    loadingBar.dismiss();
}  else{
        String message = task.getException().getMessage();
        Toast.makeText(LoginActivity.this, "Error Occured" + message, Toast.LENGTH_SHORT).show();
   loadingBar.dismiss();
}


    }
});

        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity(){
        Intent registerIntent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
        finish();
    }
}
