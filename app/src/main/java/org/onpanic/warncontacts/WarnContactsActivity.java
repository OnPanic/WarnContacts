package org.onpanic.warncontacts;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.onpanic.warncontacts.constants.WarnConstants;
import org.onpanic.warncontacts.dialogs.ContactActionsDialog;
import org.onpanic.warncontacts.fragments.ContactSettings;
import org.onpanic.warncontacts.fragments.ContactsList;
import org.onpanic.warncontacts.fragments.LockedByPermissions;
import org.onpanic.warncontacts.fragments.TriggerApps;
import org.onpanic.warncontacts.fragments.WarnContacsSettings;
import org.onpanic.warncontacts.permissions.PermissionManager;
import org.onpanic.warncontacts.providers.ContactsContentProvider;

public class WarnContactsActivity extends AppCompatActivity implements
        ContactsList.OnContactListener,
        ContactSettings.OnContactSettingsListener,
        WarnContacsSettings.OnTriggerAppsListener {

    private FragmentManager mFragmentManager;
    private ContactSettings contactSettings;
    private Uri newContact = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warn_contacs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragmentManager = getFragmentManager();

        // Do not overlapping fragments.
        if (savedInstanceState != null) return;

        if (PermissionManager.isLollipopOrHigher() && !PermissionManager.hasReadContactsPermission(this)) {
            PermissionManager.requestReadContactsPermissions(this, WarnConstants.REQUEST_READ_CONTACTS);
        } else {
            mFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, new ContactsList())
                    .commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case WarnConstants.REQUEST_READ_CONTACTS: {
                FragmentTransaction transaction = mFragmentManager.beginTransaction();

                if (grantResults.length < 1
                        || grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    Snackbar.make(
                            findViewById(android.R.id.content),
                            R.string.please_grant_permissions_for_read_contacts,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(
                                    R.string.activate,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ActivityCompat.requestPermissions(WarnContactsActivity.this,
                                                    new String[]{Manifest.permission.READ_CONTACTS},
                                                    WarnConstants.REQUEST_READ_CONTACTS);
                                        }
                                    }
                            ).show();

                    transaction.replace(R.id.fragment_container, new LockedByPermissions());
                } else {
                    transaction.replace(R.id.fragment_container, new ContactsList());
                }

                transaction.commit();

                break;
            }
            case WarnConstants.REQUEST_LOCATION_PERMISSION: {
                if (grantResults.length < 1
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    contactSettings.onLocationPermissionDenied();
                }

                break;
            }
            case WarnConstants.REQUEST_SMS_PERMISSION: {
                if (grantResults.length < 1
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    contactSettings.onSendSMSPermissionDenied();
                }

                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_warn_contacs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            mFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, new WarnContacsSettings())
                    .commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case WarnConstants.CONTACT_PICKER_RESULT:
                if (resultCode == Activity.RESULT_OK) {
                    newContact = data.getData();
                }
                return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (newContact != null) {
            contactSettings = new ContactSettings();
            Bundle args = new Bundle();
            args.putString(WarnConstants.CONTACT_URI, newContact.toString());
            contactSettings.setArguments(args);

            newContact = null;

            mFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, contactSettings)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (mFragmentManager.getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            mFragmentManager.popBackStack();
        }
    }

    @Override
    public void onContactListenerCallback(int id) {
        ContactActionsDialog dialog = new ContactActionsDialog();
        Bundle arguments = new Bundle();
        arguments.putInt(ContactsContentProvider.Contact._ID, id);
        dialog.setArguments(arguments);
        dialog.show(getSupportFragmentManager(), "ContactActionsDialog");
    }

    @Override
    public void onFabClickCallback() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, WarnConstants.CONTACT_PICKER_RESULT);
    }

    @Override
    public void onContactFinishCallback() {
        mFragmentManager.popBackStack();
    }

    @Override
    public void requestLocationPermissions() {
        if (PermissionManager.isLollipopOrHigher() && !PermissionManager.hasLocationPermission(this)) {
            PermissionManager.requestLocationPermissions(this, WarnConstants.REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void requestSendSMSPermissions() {
        if (PermissionManager.isLollipopOrHigher() && !PermissionManager.hasSendSMSPermission(this)) {
            PermissionManager.requestSendSMSPermissions(this, WarnConstants.REQUEST_SMS_PERMISSION);
        }
    }

    @Override
    public void onTriggerAppsCallback() {
        mFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, new TriggerApps())
                .commit();
    }
}
