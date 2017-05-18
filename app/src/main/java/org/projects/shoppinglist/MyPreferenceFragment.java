package org.projects.shoppinglist;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class MyPreferenceFragment extends PreferenceFragment {

    private static String SETTINGS_NAMEKEY = "name";
    private static String SETTINGS_THEMEKEY = "themePreference";

    public static String getName(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SETTINGS_NAMEKEY, "");
    }

    public static String getSettingsThemekey(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SETTINGS_THEMEKEY, "");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }
}
