package com.liverpoolfaithful.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.liverpoolfaithful.app.fragment.PostListFragment;

import java.util.Objects;

public class CategoryBasedPostList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_based_post_list);

        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        Toolbar toolbar = findViewById(R.id.toolbarOnCBP);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle(getIntent().getStringExtra("catName"));

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        PostListFragment postListFragment = new PostListFragment();
        postListFragment.setArguments(getIntent().getExtras());

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, postListFragment);
        transaction.commit();
    }
}