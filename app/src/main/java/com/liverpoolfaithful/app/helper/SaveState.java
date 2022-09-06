package com.liverpoolfaithful.app.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SaveState {
    Context context;
    SharedPreferences  mSettingsPreferences;


    public SaveState(Context context) {
        this.context = context;
        mSettingsPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }



    public boolean isNotificationOn() {
        return mSettingsPreferences.getBoolean("perf_notification", true);
    }
    public boolean isAnimationOn() {
        return mSettingsPreferences.getBoolean("pref_animation", true);
    }


//    public String getTextSize() {
//        return mSettingsPreferences.getString(Constants.PREF_FONT_SIZE, context.getResources().getString(R.string.default_text));
//    }

    public boolean darkModeOn() {
        return mSettingsPreferences.getBoolean("pref_dark_mode", false);
    }


}
