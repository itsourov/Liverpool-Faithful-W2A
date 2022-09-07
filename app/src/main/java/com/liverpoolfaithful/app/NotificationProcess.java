package com.liverpoolfaithful.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import com.liverpoolfaithful.app.adapter.RecentPostsAdapter;
import com.liverpoolfaithful.app.model.Post;

import java.util.ArrayList;
import java.util.List;

public class NotificationProcess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<Post> list = new ArrayList<>();
        RecentPostsAdapter adapter = new RecentPostsAdapter(list,NotificationProcess.this);
        adapter.showInterstitialAds();

        String postID,title,imageLink,selfUrl;
        postID = getIntent().getStringExtra("postID");
        title = getIntent().getStringExtra("title");
        imageLink = getIntent().getStringExtra("imageLink");
        selfUrl = getIntent().getStringExtra("selfUrl");
        if (postID != null) {
            Intent intent = new Intent(NotificationProcess.this, PostDetails.class);

            Bundle bundle = new Bundle();
            bundle.putString("title", title);
            bundle.putString("postID", postID);
            bundle.putString("imageLink", imageLink);
            bundle.putString("selfUrl", selfUrl);


            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }else {
            startActivity(new Intent(NotificationProcess.this,MainActivity.class));
            finish();
        }

    }
}