package org.thepanicproject.warncontacts.fragments;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.thepanicproject.warncontacts.R;

public class WarnContacsSettingsFragment extends PreferenceFragment {


    public WarnContacsSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.warncontacts_settings);
    }
}
