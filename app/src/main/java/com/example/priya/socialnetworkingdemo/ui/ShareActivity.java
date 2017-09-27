package com.example.priya.socialnetworkingdemo.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.priya.socialnetworkingdemo.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;

import java.util.List;

import static com.example.priya.socialnetworkingdemo.constants.AppConstants.REQUEST_CODE_FB;
import static com.example.priya.socialnetworkingdemo.constants.AppConstants.REQUEST_CODE_GMAIL;
import static com.example.priya.socialnetworkingdemo.constants.AppConstants.REQUEST_CODE_GMAIL_SHARE;
import static com.example.priya.socialnetworkingdemo.constants.AppConstants.REQUEST_CODE_TWITTER;

public class ShareActivity extends AppCompatActivity {
    private int REQUEST_CODE;
    private EditText etMessage;
    ShareButton btnShareFb;
    Button btnShareGmail,btnShareTwitter,btnClick;
    ShareDialog shareDialog;
    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        initView();
    }
    private void initView() {
        etMessage = (EditText) findViewById(R.id.etMessage);
        btnClick=(Button)findViewById(R.id.btnclick);
        btnShareFb = (ShareButton) findViewById(R.id.btn_share_fb);
        btnShareGmail = (Button) findViewById(R.id.btn_share_gmail);
        btnShareTwitter = (Button) findViewById(R.id.btn_share_twitter);
        REQUEST_CODE = getIntent().getIntExtra("REQUEST_CODE", 0);
        if (REQUEST_CODE == REQUEST_CODE_FB) {
            btnClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fbShare();
                }
            });
            btnShareGmail.setVisibility(View.GONE);
            btnShareTwitter.setVisibility(View.GONE);
            btnShareFb.setVisibility(View.VISIBLE);
        } else if (REQUEST_CODE == REQUEST_CODE_GMAIL) {
            googleShare();
            btnShareGmail.setVisibility(View.VISIBLE);
            btnShareTwitter.setVisibility(View.GONE);
            btnShareFb.setVisibility(View.GONE);
            btnClick.setVisibility(View.GONE);
        } else if (REQUEST_CODE == REQUEST_CODE_TWITTER) {
            btnShareGmail.setVisibility(View.GONE);
            btnShareTwitter.setVisibility(View.VISIBLE);
            btnShareFb.setVisibility(View.GONE);
            btnClick.setVisibility(View.GONE);
           twitterShare();
        }
    }
    private void twitterShare() {
        btnShareTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tweetIntent = new Intent(Intent.ACTION_SEND);
                tweetIntent.putExtra(Intent.EXTRA_TEXT,etMessage.getText().toString());
                tweetIntent.setType("text/plain");

                PackageManager packManager = getPackageManager();
                List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent,  PackageManager.MATCH_DEFAULT_ONLY);

                boolean resolved = false;
                for(ResolveInfo resolveInfo: resolvedInfoList){
                    if(resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")){
                        tweetIntent.setClassName(
                                resolveInfo.activityInfo.packageName,
                                resolveInfo.activityInfo.name );
                        resolved = true;
                        break;
                    }
                }
                if(resolved){
                    startActivity(tweetIntent);
                }else{
                    Toast.makeText(ShareActivity.this, "Twitter app isn't found", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
    private void fbShare() {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://developers.facebook.com"))
                .setQuote(etMessage.getText().toString())
                .build();
        Log.e("message",etMessage.getText().toString());
        btnShareFb.setShareContent(content);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.show(this, content);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

                Log.e("result",result+"");
            }
            @Override
            public void onCancel() {

            }
            @Override
            public void onError(FacebookException error) {
                Log.e("error",error.getMessage()+"");

            }
        });
    }
    private void googleShare() {
        btnShareGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the Google+ share dialog with attribution to your app.
                Intent shareIntent = new PlusShare.Builder(ShareActivity.this)
                        .setType("text/plain")
                        .setText(etMessage.getText().toString())
                        .setContentUrl(Uri.parse("https://developers.google.com/+/"))
                        .getIntent();
                startActivityForResult(shareIntent,REQUEST_CODE_GMAIL_SHARE);
            }
        });

    }

}
