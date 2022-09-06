package com.liverpoolfaithful.app.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.net.Uri;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.messaging.FirebaseMessaging;
import com.liverpoolfaithful.app.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class MasterSourov {
    private final Activity activity;
    private final Context context;
    private final Toast myToast;
    WebView webView;

    AppUpdateManager appUpdateManager;
    public static final int RC_APP_UPDATE = 69420;

    SaveState saveState ;
    public MasterSourov(Activity activity) {
        this.activity = activity;
        context = activity;
        myToast = Toast.makeText(activity, null, Toast.LENGTH_SHORT);

        saveState = new SaveState(activity);


    }

    public void  initNotification(){
        if (saveState.isNotificationOn()) {
            FirebaseMessaging.getInstance().subscribeToTopic(context.getPackageName());
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(context.getPackageName());
        }
    }


    public void showToast(String text) {

        myToast.setText(text);
        myToast.show();
    }

    public void openActivity(Class destination, boolean finish) {
        Intent intent = new Intent(context, destination);
        context.startActivity(intent);
        if (finish) {
            activity.finish();
        }
    }

    public String getVolleyResponse(VolleyError error) {
        String message = "Got an network error";
        if (error instanceof NetworkError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (error instanceof ServerError) {
            message = "The server could not be found. Please try again after some time!!";
        } else if (error instanceof AuthFailureError) {
            message = "Authentication failed! you don't have permission to perform this action";
        } else if (error instanceof ParseError) {
            message = "Parsing error! Please try again after some time!!";
        } else if (error instanceof TimeoutError) {
            message = "Connection TimeOut! Please check your internet connection.";
        }
        return message;
    }

    public void initWebView(WebView web_view) {
        this.webView = web_view;

        initWebViewSettings();
        web_view.setWebChromeClient(new MyChrome(context));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewSettings() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAppCachePath(context.getCacheDir().getAbsolutePath());
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);


        webView.getSettings().setDefaultFontSize(20);

        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            switch (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_YES:
                    WebSettingsCompat.setForceDark(webView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    WebSettingsCompat.setForceDark(webView.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);
                    break;
            }
        }


    }

    public void checkAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(activity);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE,
                            activity, RC_APP_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void onResume() {
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE,
                            activity, RC_APP_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });

        appUpdateManager.registerListener(compliteListener);

    }

    InstallStateUpdatedListener compliteListener = installState -> {

        if (installState.installStatus() == InstallStatus.FAILED) {
            showToast("Installing Failed!");
        }
    };

    public void onActivityResult(int requestCode, int resultCode) {
        if (requestCode == RC_APP_UPDATE && resultCode != Activity.RESULT_OK) {
            showUpdateWarning();
        }
    }

    private void showUpdateWarning() {
        MaterialDialog mDialog = new MaterialDialog.Builder(activity)
                .setTitle(activity.getString(R.string.update_alert_title))
                .setMessage(getWhatsNewFromPlayStore())
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.ok), (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        // Show Dialog
        mDialog.show();
    }

    private String getWhatsNewFromPlayStore() {
        return context.getResources().getString(R.string.update_alert_desc);
    }



    public void shareText(String s) {
        try {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, s);
            sendIntent.setType("text/plain");
            context.startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copyText(String text) {
        ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("post", text);
        clipboard.setPrimaryClip(clip);
    }

    public void openLinkInChromeView(String url) {
        CustomTabsIntent.Builder customIntent = new CustomTabsIntent.Builder();

        try {
            String packageName = "com.android.chrome";
            customIntent.build().intent.setPackage(packageName);
            customIntent.build().launchUrl(activity, Uri.parse(url));
        } catch (Exception e) {
            e.printStackTrace();

            openLinkInDefault(url);
        }
    }

    public void openLinkInDefault(String url) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(url)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String convertToAGo(String date) {

        String convTime = null;
        try {


            String ago_date = date.replace("T", " "), currant_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            String suffix = "Ago";

            SimpleDateFormat convert_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(convert_date.parse(ago_date));
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(convert_date.parse(currant_date));
            long millis1 = calendar1.getTimeInMillis();
            long millis2 = calendar2.getTimeInMillis();
            long diff = millis2 - millis1;
            // Calculate difference in seconds
            long diffSeconds = diff / 1000;
            // Calculate difference in minutes
            long diffMinutes = diff / (60 * 1000);
            // Calculate difference in hours
            long diffHours = diff / (60 * 60 * 1000);
            // Calculate difference in days
            long diffDays = diff / (24 * 60 * 60 * 1000);
            // calculate how much month in days
            long multipledays = diffDays;
            int maonths = (int) multipledays / 30;
            int reamainigdays = maonths % 30;
            // Calculate  the years of monthes
            int getyears = maonths / 12;
            int reamainigmothe = getyears % 12;


            if (diffSeconds < 60) {
                convTime = diffSeconds + " Seconds " + suffix;
            } else if (diffMinutes < 60) {
                convTime = diffMinutes + " Minutes " + suffix;
            } else if (diffHours < 24) {
                convTime = diffHours + " Hours " + suffix;
            } else if (diffDays >= 7) {
                if (reamainigmothe != 0 && reamainigmothe <= getyears % 12) {
                    convTime = reamainigmothe + " Years " + suffix;
                } else if (diffDays > 30) {
                    convTime = maonths + " Months " + suffix;
                } else {
                    convTime = (diffDays / 7) + " Week " + suffix;
                }
            } else if (diffDays < 7) {
                convTime = diffDays + " Days " + suffix;
            }


            return convTime;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convTime;
    }

}
