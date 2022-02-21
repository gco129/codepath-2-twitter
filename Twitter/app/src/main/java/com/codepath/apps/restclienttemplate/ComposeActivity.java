package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 140;

    EditText etCompose;
    TextView tvCounter;
    Button btnTweet;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApplication.getRestClient(this);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        tvCounter = findViewById(R.id.tvCounter);

        // Set logo
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_twitter_icon_top);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Text changed listener
        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int tweetLength = editable.toString().length();
                tvCounter.setText(tweetLength + "/280");
                if(tweetLength > 280){
                    tvCounter.setTextColor(Color.RED);
                }
                else{
                    tvCounter.setTextColor(Color.GRAY);
                }
            }
        });

        // Button click listener
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                if(tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Tweets cannot be empty. Please enter some text and try again.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, "Tweets cannot be longer than 280 characters. Please remove some text and try again.", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_LONG).show();
                // Twitter API call to publish the tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess for publishTweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet says: " + tweet.body);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            // Set result code and bundle data for response
                            setResult(RESULT_OK, intent);
                            // Close activity, pass data to parent
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure for publishTweet", throwable);
                    }
                });
            }
        });

    }
}