package com.liverpoolfaithful.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.liverpoolfaithful.app.adapter.RecentPostsAdapter;
import com.liverpoolfaithful.app.database.BookmarkSaver;
import com.liverpoolfaithful.app.fragment.CommentListFragment;
import com.liverpoolfaithful.app.helper.Configs;
import com.liverpoolfaithful.app.helper.Constants;
import com.liverpoolfaithful.app.helper.MasterSourov;
import com.liverpoolfaithful.app.helper.SaveState;
import com.liverpoolfaithful.app.model.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class PostDetails extends AppCompatActivity {
    Bundle bundle;
    String imageLink, postID, date, title, content, catID, selfUrl, catName;
    ImageView post_img;
    TextView title_text, date_text, comment_text;
    WebView web_view;
    SaveState saveState;
    LottieAnimationView animation_view;
    boolean postLoaded = false;
    MasterSourov sourov;
    BookmarkSaver bookmarkSaver;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (Configs.postDetailsNoFM) {
            setContentView(R.layout.activity_post_details_without_fm);
        } else {
            setTheme(R.style.Theme_TransparentStatusBar);
            setContentView(R.layout.activity_post_details);
        }

        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        saveState = new SaveState(PostDetails.this);
        sourov = new MasterSourov(PostDetails.this);
        bookmarkSaver = new BookmarkSaver(PostDetails.this);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            imageLink = bundle.getString("imageLink");
            postID = bundle.getString("postID");
            title = bundle.getString("title");
            selfUrl = bundle.getString("selfUrl");
            catName = bundle.getString("catName");


        }


        animation_view = findViewById(R.id.animation_view);

        title_text = findViewById(R.id.title_text);
        date_text = findViewById(R.id.date_text);
        comment_text = findViewById(R.id.comment_text);
        web_view = findViewById(R.id.web_view);

        animation_view.setVisibility(View.VISIBLE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        toolbar.setNavigationOnClickListener(vie -> onBackPressed());


        if (!Configs.postDetailsNoFM) {
            post_img = findViewById(R.id.post_img);
            post_img.setOnClickListener(v -> {

                Intent intent = new Intent(PostDetails.this, ImageViewer.class);
                intent.putExtra("imageLink", imageLink);

                if (saveState.isAnimationOn()) {
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(PostDetails.this, post_img, "image");
                    startActivity(intent, optionsCompat.toBundle());
                } else {
                    startActivity(intent);
                }

            });

            Glide.with(this).load(imageLink)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.mipmap.ic_launcher)
                    .into(post_img);
        }




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            title_text.setText(Html.fromHtml(title, Html.FROM_HTML_MODE_COMPACT));
        } else {
            title_text.setText(Html.fromHtml(title));
        }

        loadPost(postID);
        getCommentsCount(postID);

        findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommentListFragment fragment = new CommentListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("postID", postID);
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), "TAG");
            }
        });


        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, postID);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, title);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "post");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (postLoaded) {
            animation_view.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.post_details_toolbar_menu, menu);
        MenuItem item = menu.findItem(R.id.action_heart);

        if (bookmarkSaver.IfBookmarkExists(postID)) {
            item.setIcon(R.drawable.ic_baseline_favorite_24);

        }
        item.setOnMenuItemClickListener(item1 -> {
            if (bookmarkSaver.IfBookmarkExists(postID)) {
                sourov.showToast(bookmarkSaver.deleteBookmarkPost(postID));
                item.setIcon(R.drawable.ic_baseline_favorite_border_24);
            } else {
                sourov.showToast(bookmarkSaver.addBookmark(title, catName, postID, selfUrl, imageLink, date));
                item.setIcon(R.drawable.ic_baseline_favorite_24);
            }
            return true;
        });

        MenuItem share = menu.findItem(R.id.action_share);
        share.setOnMenuItemClickListener(item12 -> {
            sourov.shareText(getResources().getString(R.string.post_share_text) + selfUrl);

            return true;
        });

        MenuItem copy = menu.findItem(R.id.action_copy);
        copy.setOnMenuItemClickListener(item12 -> {
            sourov.copyText(selfUrl);
            sourov.showToast("Copied!!");

            return true;
        });


        return super.onCreateOptionsMenu(menu);
    }

    private void loadPost(String postID) {
        String loadingUrl = Constants.baseRestUrl + "posts/" + postID;

        queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, loadingUrl,
                this::processData, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                MaterialDialog mDialog = new MaterialDialog.Builder(PostDetails.this)
                        .setAnimation(R.raw.no_internet)
                        .setTitle(getResources().getString(R.string.got_an_error))
                        .setMessage(sourov.getVolleyResponse(error))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.retry), (dialogInterface, which) -> {
                            finish();
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);
                        })
                        .setNegativeButton(getResources().getString(R.string.exit), (dialogInterface, which) -> System.exit(0))
                        .build();

                // Show Dialog
                mDialog.show();
            }
        });

        if (!Configs.shouldCacheRequest) {
            stringRequest.setShouldCache(false);
        }
        queue.add(stringRequest);
    }

    private void processData(String data) {
        try {

            JSONObject obj = new JSONObject(data);
            date_text.setText(obj.getString("date_gmt").substring(0, 10));

            content = obj.getJSONObject("content").getString("rendered");

            loadRelatedPosts(obj.getJSONArray("categories").getString(0));
        } catch (Throwable t) {
            Toast.makeText(this, "Invalid Json Response", Toast.LENGTH_SHORT).show();
            animation_view.setVisibility(View.GONE);
        }
        sourov.initWebView(web_view);
        web_view.setWebViewClient(new myWebViewClient());
        web_view.loadDataWithBaseURL(null, Constants.CSS_PROPERTIES + content + Constants.js_PROPERTIES, "text/html; charset=utf-8", "UTF-8", null);


    }

    List<Post> posts;
    RecentPostsAdapter adapter;

    private void loadRelatedPosts(String catId) {

        posts = new ArrayList<>();

        RecyclerView rvRelated = findViewById(R.id.rvRelated);

        adapter = new RecentPostsAdapter(posts, this);

        if (Configs.applyGridLayout){
            rvRelated.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            adapter.isHorizontal = true;
        }else {
            rvRelated.setLayoutManager(new LinearLayoutManager(this));
        }
        rvRelated.setAdapter(adapter);


        @SuppressLint("NotifyDataSetChanged")
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.baseRestUrl + "posts?categories=" + catId,
                data -> {


                    try {
                        JSONArray jsonArr = new JSONArray(data);
                        loadToRev(jsonArr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        gotAnError();

                    }

                }, error -> {
            gotAnError();
        });
        if (!Configs.shouldCacheRequest) {
            stringRequest.setShouldCache(false);
        }
        queue.add(stringRequest);
    }

    private void loadToRev(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {


            try {
                Post p = new Post();

                JSONObject jsonObjectData = response.getJSONObject(i);


                // extract the date
                p.setDate(jsonObjectData.getString("date_gmt"));

                // extract the title
                JSONObject titleObject = jsonObjectData.getJSONObject("title");
                p.setTitle(titleObject.getString("rendered"));


                //extract the id;
                p.setId(jsonObjectData.getString("id"));

                p.setSelfUrl(jsonObjectData.getString("link"));


                try {
                    p.setCategory_name(jsonObjectData.getJSONObject("w2a_by_sourov").getString("catName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {

                    // extract feature image
                    p.setFeature_image_thumb(jsonObjectData.getJSONObject("w2a_by_sourov").getString("image_full"));  //image_thumbnail & image_full
                    p.setFeature_image_full(jsonObjectData.getJSONObject("w2a_by_sourov").getString("image_full"));

                } catch (JSONException e) {
                    e.printStackTrace();

                }


                posts.add(p);
                adapter.showShimmer = false;

                if (posts.size() > 1) {
                    adapter.notifyItemInserted(posts.size() - 1);
                } else {
                    adapter.notifyDataSetChanged();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void gotAnError() {

        if (adapter.showShimmer) {
            adapter.showShimmer = false;
            adapter.notifyDataSetChanged();
        }
    }


    private void getCommentsCount(String ID) {


        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Constants.baseRestUrl + "comments?per_page=1000&post=" + ID , null, response -> comment_text.setText(String.valueOf(response.length())), error -> {


        });
        if (!Configs.shouldCacheRequest) {
            request.setShouldCache(false);
        }
        queue.add(request);
    }


    public class myWebViewClient extends WebViewClient {


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Log.d("TAG", "shouldOverrideUrlLoading: " + url);
            if (url.endsWith("this_is_a_image")) {
                Intent intent = new Intent(PostDetails.this, ImageViewer.class);
                intent.putExtra("imageLink", url.substring(0, url.length() - 16));
                startActivity(intent);
            } else {
                sourov.openLinkInChromeView(url);
            }


            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            animation_view.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.cancel();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);


        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            animation_view.setVisibility(View.GONE);
            postLoaded = true;

            if (Configs.showBannerAds){
                showAds();
            }



        }

    }

    private void showAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adViewInPostDetails);
        AdView mAdViewTop = findViewById(R.id.adViewInPostDetailsTop);
        AdView mAdViewBottom = findViewById(R.id.adViewInPostDetailsBottom);

        mAdViewTop.setVisibility(View.VISIBLE);
        mAdViewBottom.setVisibility(View.VISIBLE);

        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);
        mAdViewTop.loadAd(adRequest);
        mAdViewBottom.loadAd(adRequest);
    }

}