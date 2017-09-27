package com.example.priya.socialnetworkingdemo.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.priya.socialnetworkingdemo.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static com.example.priya.socialnetworkingdemo.constants.AppConstants.REQUEST_CODE_FB;
import static com.example.priya.socialnetworkingdemo.constants.AppConstants.REQUEST_CODE_GMAIL;
import static com.example.priya.socialnetworkingdemo.constants.AppConstants.REQUEST_CODE_TWITTER;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private String userName, gender, location, dob, userId, profile, email;
    private CallbackManager callbackManager;
    private SignInButton signInButton;
    private GoogleApiClient mGoogleApiClient;
    private LoginButton fbLogin;
    private TwitterLoginButton btnSignIn;
    private Intent intent;
    private final String TAG = getClass().getSimpleName();
    private TwitterSession session;
    String token, secret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!isNetworkAvailable()){
            messageBox();
        }
        initView();
    }
    // initialise views
    private void initView() {
        btnSignIn=(TwitterLoginButton)findViewById(R.id.login_button);
        fbLogin = (LoginButton) findViewById(R.id.fb_login);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
//        if(fbLogin.getText().toString().equals("Log out")){
//            LoginManager.getInstance().logOut();
//            fbLogin.setText("Continue with facebook");
//        }
        Log.e("button",fbLogin.getText().toString());
        fbSignIn();
        googleSignIn();
        twitterSignIn();
    }
    //Google sign in
    private void googleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(Scopes.PLUS_LOGIN)) // "https://www.googleapis.com/auth/plus.login"
                .requestScopes(new Scope(Scopes.PLUS_ME))
                .requestScopes(new Scope(Scopes.PROFILE))
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }
    //Facebook sign in
    private void fbSignIn() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        fbLogin.setReadPermissions(Arrays.asList("public_profile, email, user_birthday,user_location"));
        callbackManager = CallbackManager.Factory.create();
        fbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Toast.makeText(MainActivity.this,"Login Successfully",Toast.LENGTH_LONG).show();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("Main", response.toString());
                                setProfileToView(object);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,gender,birthday,location,name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e("error", "on cancel called");
            }
            @Override
            public void onError(FacebookException error) {
                Log.e("error", error.toString());
            }
        });
    }
    //Twitter sign in
    private void twitterSignIn() {
        intent=new Intent(MainActivity.this,DetailsActivity.class);
        btnSignIn.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.e("UserId", result.data.getUserId() + "");
                intent.putExtra("userName",result.data.getUserId() + "");
                Log.e("UserName", result.data.getUserName() + "");
                intent.putExtra("userId",result.data.getUserName() + "");
                session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                token = authToken.token;
                secret = authToken.secret;
                requestEmail();
            }
            // This method request twitter user email address
            private void requestEmail() {
                TwitterAuthClient authClient = new TwitterAuthClient();
                authClient.requestEmail(session, new Callback<String>() {
                    @Override
                    public void success(Result<String> result) {
                        email=result.data.toString();
                        intent.putExtra("email",email);
                        intent.putExtra("REQUEST_CODE",REQUEST_CODE_TWITTER);
                        startActivity(intent);
                        Log.e(TAG,email);
                    }
                    @Override
                    public void failure(TwitterException exception) {
                        // Do something on failure
                        Log.e(TAG, "error message "+exception.getMessage());
                    }
                });
            }
            @Override
            public void failure(TwitterException exception) {
                Log.e(TAG, "error message "+exception.getMessage());
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Clicked","Clicked");
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                if (session != null){
                    TwitterAuthToken authToken = session.getAuthToken();
                    token = authToken.token;
                    secret = authToken.secret;
                }
            }
        });
    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GMAIL) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else
            btnSignIn.onActivityResult(requestCode, resultCode, data);
    }

    // Handle facebook signin result
    private void setProfileToView(JSONObject jsonObject) {
        intent = new Intent(MainActivity.this, DetailsActivity.class);
        try {
            userName = jsonObject.getString("name");
            Log.e("Name", (jsonObject.getString("name")));
            intent.putExtra("userName", userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            email = jsonObject.getString("email");
            if (email.equals(null)) {
                email = "no email registered";
            }
            Log.e("email", (jsonObject.getString("email")));
            intent.putExtra("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            gender = jsonObject.getString("gender");
            Log.e("gender", (jsonObject.getString("gender")));
            intent.putExtra("gender", gender);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonobject_location = jsonObject.getJSONObject("location");
            location = jsonobject_location.getString("name");
            Log.e("Location", location);
            intent.putExtra("location", location);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            Log.e("DOB", (jsonObject.getString("birthday")));
            dob = jsonObject.getString("birthday");
            intent.putExtra("dob", dob);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            Log.e("USER ID", (jsonObject.getString("id")));
            userId = jsonObject.getString("id");
            intent.putExtra("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtra("REQUEST_CODE", REQUEST_CODE_FB);
        profilePic();
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, REQUEST_CODE_GMAIL);
    }
    // for handling google plus sign in result
    private void handleSignInResult(GoogleSignInResult result) {
        Log.e("handleSignInResult:", result.isSuccess() + "");
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            intent.putExtra("userName", acct.getDisplayName());
            intent.putExtra("email", acct.getEmail());
            if(acct.getPhotoUrl()!=null)
            intent.putExtra("profile", acct.getPhotoUrl().toString());
            Log.e("profile Url", acct.getPhotoUrl() + "");
            intent.putExtra("userId", acct.getId());
            intent.putExtra("TAG", REQUEST_CODE_GMAIL);
            Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            intent.putExtra("dob", person.getBirthday());
            int gender = person.getGender();
            if (gender == 0)
                intent.putExtra("gender", "Male");
            else if (gender == 1)
                intent.putExtra("gender", "Female");

        }
        signOut();
        intent.putExtra("REQUEST_CODE", REQUEST_CODE_GMAIL);
        startActivity(intent);
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this, "YOUR CONNECTION FAILDED... TRY AGAIN", Toast.LENGTH_LONG).show();
    }
    //for retreiving facebook profile image
    private void profilePic() {
        Bundle params = new Bundle();
        params.putBoolean("redirect", false);
        params.putString("type", "large");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "me/picture", params, HttpMethod.GET, new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
                Log.e("Response 2", response + "");
                try {
                    profile = (String) response.getJSONObject().getJSONObject("data").get("url");
                    intent.putExtra("profile", profile);
                    startActivity(intent);
                    Log.e("pofile",profile+"");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).executeAsync();
    }
    //open alert dialog
    private void messageBox() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Please Check Your Internet Connection");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    //check network availability
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    //for sign out google account
    private void signOut(){
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

    }
}
