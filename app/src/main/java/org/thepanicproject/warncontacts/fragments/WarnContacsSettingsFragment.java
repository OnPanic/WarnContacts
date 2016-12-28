package org.thepanicproject.warncontacts.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.thepanicproject.warncontacts.R;
import org.thepanicproject.warncontacts.activities.ConfigureTriggerAppActivity;

public class WarnContacsSettingsFragment extends PreferenceFragment {


    public WarnContacsSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.warncontacts_settings);

        Preference triggerApp = (Preference) findPreference(getString(R.string.pref_trigger_app));
        triggerApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), ConfigureTriggerAppActivity.class);
                startActivity(intent);
                return false;
            }
        });
    }
}
