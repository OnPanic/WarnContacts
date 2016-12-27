package org.thepanicproject.warncontacts.activities;

import android.app.Activity;
import android.os.Bundle;

import info.guardianproject.panic.PanicResponder;

public class PanicResponseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PanicResponder.receivedTriggerFromConnectedApp(this)) {
            // TODO
        } else {
            ExitActivity.exitAndRemoveFromRecentApps(this);
        }
    }
}
