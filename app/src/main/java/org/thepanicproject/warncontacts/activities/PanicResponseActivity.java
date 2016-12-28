package org.thepanicproject.warncontacts.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.thepanicproject.warncontacts.R;
import org.thepanicproject.warncontacts.notifications.TriggerNotification;

import info.guardianproject.panic.PanicResponder;

public class PanicResponseActivity extends Activity {
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (PanicResponder.receivedTriggerFromConnectedApp(this)) {
            if (prefs.getBoolean(getString(R.string.pref_runned_notification), true)) {
                TriggerNotification notification = new TriggerNotification(getApplicationContext());
                notification.show();
            }

            ExitActivity.exitAndRemoveFromRecentApps(PanicResponseActivity.this);
        }

        finish();
    }
}
