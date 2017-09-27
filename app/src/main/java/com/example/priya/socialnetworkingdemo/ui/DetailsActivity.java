package com.example.priya.socialnetworkingdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.priya.socialnetworkingdemo.R;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener{
   private TextView tvUserId,tvUserName,tvGender,tvDob,tvEmail,tvLocation;
    private ImageView profileImage;
    private String userId,userName,gender,dob,email,location,profile;
    private int REQUEST_CODE;
    private Button btnShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        initView();
    }
    //initialise views
    private void initView() {
        tvUserId = (TextView) findViewById(R.id.user_id);
        tvUserName = (TextView) findViewById(R.id.user_name);
        tvGender = (TextView) findViewById(R.id.gender);
        tvDob = (TextView) findViewById(R.id.dob);
        tvEmail = (TextView) findViewById(R.id.email);
        tvLocation = (TextView) findViewById(R.id.location);
        profileImage = (ImageView) findViewById(R.id.profile_image);
        btnShare = (Button) findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);
        userId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("userName");
        gender = getIntent().getStringExtra("gender");
        location = getIntent().getStringExtra("location");
        dob = getIntent().getStringExtra("dob");
        email = getIntent().getStringExtra("email");
        profile=getIntent().getStringExtra("profile");
        Log.e("profile",profile+"");
        REQUEST_CODE = getIntent().getIntExtra("REQUEST_CODE", 0);

        Log.e("userId", userId + "");
        tvUserId.setText(userId);
        tvUserName.setText(userName);
        tvGender.setText(gender);
        tvDob.setText(dob);
        tvEmail.setText(email);
        tvLocation.setText(location);
        Glide
                .with(DetailsActivity.this)
                .load(profile)
                .centerCrop()
                .into(profileImage);
        }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_share){
            Intent intent=new Intent(DetailsActivity.this,ShareActivity.class);
            intent.putExtra("REQUEST_CODE",REQUEST_CODE);
            startActivity(intent);

        }
    }
}
