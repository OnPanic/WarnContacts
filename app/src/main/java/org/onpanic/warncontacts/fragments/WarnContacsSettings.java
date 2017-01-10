package org.onpanic.warncontacts.fragments;


import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.onpanic.warncontacts.R;

public class WarnContacsSettings extends PreferenceFragment {
    private OnTriggerAppsListener mListener;

    public WarnContacsSettings() {
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
                mListener.onTriggerAppsCallback();
                return false;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTriggerAppsListener) {
            mListener = (OnTriggerAppsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTriggerAppsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnTriggerAppsListener {
        void onTriggerAppsCallback();
    }
}
