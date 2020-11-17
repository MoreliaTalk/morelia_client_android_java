package ru.wtw.moreliatalkclient.ui.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import ru.wtw.moreliatalkclient.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_settings, rootKey);
    }
}