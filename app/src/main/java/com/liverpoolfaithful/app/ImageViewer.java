package com.liverpoolfaithful.app;

import android.net.Uri;
import android.os.Bundle;
import android.webkit.URLUtil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Objects;

public class ImageViewer extends AppCompatActivity {
    PhotoView imageViewOnImageViewer;
    String imageLink;
    Toolbar toolbarOnImageViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        imageLink = getIntent().getStringExtra("imageLink");

        if (imageLink == null) {
            imageLink = getURLForResource(R.mipmap.ic_launcher);
        }


        if (imageLink.contains("?resize")) {
            imageLink = imageLink.substring(0, imageLink.lastIndexOf("?resize"));
        }

        toolbarOnImageViewer = findViewById(R.id.toolbar);
        setSupportActionBar(toolbarOnImageViewer);
        Objects.requireNonNull(getSupportActionBar()).setTitle(URLUtil.guessFileName(imageLink, null, null));
        toolbarOnImageViewer.setNavigationOnClickListener(v -> onBackPressed());

        imageViewOnImageViewer = findViewById(R.id.imageViewOnImageViewer);
        Glide
                .with(this)
                .load(imageLink)
                .into(imageViewOnImageViewer);


    }

    public String getURLForResource(int resourceId) {
        //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse("android.resource://" + Objects.requireNonNull(R.class.getPackage()).getName() + "/" + resourceId).toString();
    }
}