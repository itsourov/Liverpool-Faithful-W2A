package com.liverpoolfaithful.app;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.liverpoolfaithful.app.adapter.HomepagePagerAdapter;
import com.liverpoolfaithful.app.fragment.CategoryListFragment;
import com.liverpoolfaithful.app.fragment.HeartFragment;
import com.liverpoolfaithful.app.fragment.PostListFragment;
import com.liverpoolfaithful.app.fragment.SearchFragment;
import com.liverpoolfaithful.app.fragment.UserFragment;
import com.liverpoolfaithful.app.helper.Configs;
import com.liverpoolfaithful.app.helper.MasterSourov;
import com.liverpoolfaithful.app.helper.SaveState;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    MasterSourov sourov;
    ViewPager2 viewPager2;
    BottomNavigationView bottom_navigation_homepage;
    AppEventsLogger logger ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logger =   AppEventsLogger.newLogger(getApplicationContext());

        String postID;
        postID = getIntent().getStringExtra("postID");

        if (postID != null) {
            Intent intent = new Intent(MainActivity.this, PostDetails.class);

            intent.putExtras(getIntent().getExtras());
            startActivity(intent);

        }


        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        SaveState saveState = new SaveState(MainActivity.this);
        if (saveState.darkModeOn()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }


        sourov = new MasterSourov(MainActivity.this);
        sourov.checkAppUpdate();
        sourov.initNotification();

        Toolbar toolbar = findViewById(R.id.toolbarOnHomepage);
        setSupportActionBar(toolbar);


        drawerLayout = findViewById(R.id.drawer_layoutOnHomepage);
        NavigationView navigationView = findViewById(R.id.navigationViewOnHomepage);
        // navigationView.setItemIconTintList(null);

        //navigation toggle
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        bottom_navigation_homepage = findViewById(R.id.bottom_navigation_homepage);
        bottom_navigation_homepage.setOnItemSelectedListener(this::onNavigationItemSelected);


        HomepagePagerAdapter homepagePagerAdapter;
        viewPager2 = findViewById(R.id.pager2);
        homepagePagerAdapter = new HomepagePagerAdapter(getSupportFragmentManager(), getLifecycle());
        homepagePagerAdapter.add(new PostListFragment());
        homepagePagerAdapter.add(new CategoryListFragment());
        homepagePagerAdapter.add(new SearchFragment());
        homepagePagerAdapter.add(new HeartFragment());
        homepagePagerAdapter.add(new UserFragment());

        viewPager2.setAdapter(homepagePagerAdapter);
        viewPager2.setOffscreenPageLimit(2);
        viewPager2.setUserInputEnabled(Configs.homeTabSwipeable);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottom_navigation_homepage.getMenu().getItem(position).setChecked(true);

                navigationView.getMenu().getItem(position).setChecked(true);
            }
        });

    


        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Configs.showInterstitialAdsAfterXTime){
                            sourov.showInterstitialAds();
                        }                    }
                });
            }
        }, 40, 70, TimeUnit.SECONDS);

        createTimer(3);

    }


    private void createTimer(long seconds) {

        CountDownTimer countDownTimer =
                new CountDownTimer(seconds * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {


                        Application application = getApplication();
                        if (!(application instanceof MyApplication)) {
                            Log.e("ADS","Failed to cast application to MyApplication.");

                            return;
                        }

                        // Show the app open ad.
                        ((MyApplication) application)
                                .showAdIfAvailable(MainActivity.this, () -> {

                                });
                    }
                };
        countDownTimer.start();
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */

    public void logSentFriendRequestEvent () {
        logger.logEvent("sentFriendRequest");
    }

    @Override
    protected void onResume() {
        super.onResume();
        sourov.onResume();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        sourov.onActivityResult(requestCode, resultCode);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_home) {
            viewPager2.setCurrentItem(0);

        } else if (itemId == R.id.action_categories) {
            viewPager2.setCurrentItem(1);
        } else if (itemId == R.id.action_search) {
            viewPager2.setCurrentItem(2);
        } else if (itemId == R.id.action_heart) {
            viewPager2.setCurrentItem(3);
        } else if (itemId == R.id.action_user) {
            viewPager2.setCurrentItem(4);
        } else if (itemId == R.id.action_about_us) {
            sourov.openLinkInChromeView(getResources().getString(R.string.about_us_url));
        } else if (itemId == R.id.action_privacy_policy) {
            sourov.openLinkInChromeView(getResources().getString(R.string.privacy_policy_url));
        } else if (itemId == R.id.action_share) {
            final String appPackageName = getPackageName();
            sourov.shareText(getResources().getString(R.string.share_text) + "https://play.google.com/store/apps/details?id=" + appPackageName);
        } else if (itemId == R.id.action_rate_app) {
            final String appPackageName = getPackageName();
            sourov.openLinkInDefault("https://play.google.com/store/apps/details?id=" + appPackageName);
        } else if (itemId == R.id.action_more_app) {
            sourov.openLinkInDefault(getResources().getString(R.string.more_apps_url));
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.homepage_toolbar_menu, menu);
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setOnMenuItemClickListener(item1 -> {
            sourov.openActivity(SettingsActivity.class, false);
            return true;
        });


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (viewPager2.getCurrentItem() != 0) {
            viewPager2.setCurrentItem(0);
        } else {
            exitConfirmation();
        }

    }

    private void exitConfirmation() {

        MaterialDialog mDialog = new MaterialDialog.Builder(this)
                .setTitle(getResources().getString(R.string.exitAlertTitle))
                .setMessage(getResources().getString(R.string.exitAlertMsg))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.exit), (dialogInterface, which) -> System.exit(0))
                .setNegativeButton(getString(R.string.cancel), (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        // Show Dialog
        mDialog.show();
    }
}