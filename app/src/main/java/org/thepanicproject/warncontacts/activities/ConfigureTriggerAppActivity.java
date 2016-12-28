package org.thepanicproject.warncontacts.activities;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.thepanicproject.warncontacts.R;
import org.thepanicproject.warncontacts.utils.ConnectedAppEntry;

import java.util.ArrayList;

import info.guardianproject.panic.Panic;
import info.guardianproject.panic.PanicResponder;

public class ConfigureTriggerAppActivity extends AppCompatActivity {
    private PackageManager pm;
    private ConnectedAppEntry NONE;
    private ArrayList<ConnectedAppEntry> list;
    private ListView apps;
    private int selectedApp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PanicResponder.checkForDisconnectIntent(this)) {
            finish();
            return;
        }

        setContentView(R.layout.activity_configure_trigger_app);

        pm = getPackageManager();
        apps = (ListView) findViewById(R.id.trigger_apps);

        NONE = new ConnectedAppEntry(this, Panic.PACKAGE_NAME_NONE, R.string.none);

        String packageName = PanicResponder.getTriggerPackageName(getApplicationContext());
        if (packageName == null) {
            packageName = NONE.packageName;
            PanicResponder.setTriggerPackageName(ConfigureTriggerAppActivity.this,
                    NONE.packageName);
        }

        list = new ArrayList<ConnectedAppEntry>();
        list.add(0, NONE);

        for (ResolveInfo resolveInfo : PanicResponder.resolveTriggerApps(pm)) {
            if (resolveInfo.activityInfo == null)
                continue;
            list.add(new ConnectedAppEntry(pm, resolveInfo.activityInfo));
            if (packageName.equals(resolveInfo.activityInfo.packageName)) {
                selectedApp = list.size() - 1;
            }
        }

        ListAdapter adapter = new ArrayAdapter<ConnectedAppEntry>(ConfigureTriggerAppActivity.this,
                android.R.layout.simple_list_item_single_choice, android.R.id.text1, list);

        apps.setAdapter(adapter);
        apps.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        apps.setItemChecked(selectedApp, true);
        apps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ConnectedAppEntry entry = list.get(i);
                PanicResponder.setTriggerPackageName(ConfigureTriggerAppActivity.this,
                        entry.packageName);
            }
        });
    }
}
