package com.liverpoolfaithful.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Objects;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // replace linear layout by preference screen
        getFragmentManager().beginTransaction().replace(R.id.content, new MyPreferenceFragment()).commit();

        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbarOnSettings);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.settings));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_preference);

            SwitchPreference vibrateSwitch = (SwitchPreference) findPreference("pref_dark_mode");

            if (vibrateSwitch != null) {
                vibrateSwitch.setOnPreferenceChangeListener((arg0, isVibrateOnObject) -> {
                    boolean isVibrateOn = (Boolean) isVibrateOnObject;
                    if (isVibrateOn) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    return true;
                });
            }
        }

    }
}