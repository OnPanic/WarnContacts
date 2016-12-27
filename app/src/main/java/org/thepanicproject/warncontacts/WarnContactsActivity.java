package org.thepanicproject.warncontacts;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.thepanicproject.warncontacts.constants.WarnConstants;
import org.thepanicproject.warncontacts.dialogs.ContactActionsDialog;
import org.thepanicproject.warncontacts.fragments.ContactSettings;
import org.thepanicproject.warncontacts.fragments.ContactsListFragment;
import org.thepanicproject.warncontacts.fragments.WarnContacsSettingsFragment;
import org.thepanicproject.warncontacts.permissions.PermissionManager;
import org.thepanicproject.warncontacts.providers.ContactsContentProvider;

public class WarnContactsActivity extends AppCompatActivity implements
        ContactsListFragment.OnContactListener, ContactSettings.OnContacSettingsListener {

    private FragmentManager mFragmentManager;
    private FloatingActionButton mFab;
    private ContactSettings contactSettings;

    private Uri newContact = null;
    private View.OnClickListener fabClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                    ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(contactPickerIntent, WarnConstants.CONTACT_PICKER_RESULT);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warn_contacs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragmentManager = getFragmentManager();

        // Do not overlapping fragments.
        if (savedInstanceState != null) return;

        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new ContactsListFragment())
                .commit();

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        if (PermissionManager.isLollipopOrHigher() && !PermissionManager.hasReadContactsPermission(this)) {
            mFab.hide();
            PermissionManager.requestReadContactsPermissions(this, WarnConstants.REQUEST_READ_CONTACTS);
        } else {
            mFab.setOnClickListener(fabClick);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case WarnConstants.REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mFab.show();
                    mFab.setOnClickListener(fabClick);
                }

                break;
            }
            case WarnConstants.REQUEST_LOCATION_PERMISSION: {
                if (grantResults.length < 1
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    contactSettings.onLocationPermissionDenied();
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
            mFab.hide();

            mFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, new WarnContacsSettingsFragment())
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
            mFab.hide();

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
            mFab.show();
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
    public void onContactSaveCallback(String contact_id, String contact_name, Boolean sms, Boolean email, Boolean location) {
        mFragmentManager.popBackStack();
        mFab.show();
        mFab.setOnClickListener(fabClick);

        ContentValues values = new ContentValues();
        values.put(ContactsContentProvider.Contact.CONTACT_ID, contact_id);
        values.put(ContactsContentProvider.Contact.CONTACT_NAME, contact_name);
        values.put(ContactsContentProvider.Contact.SEND_SMS, sms);
        values.put(ContactsContentProvider.Contact.SEND_EMAIL, email);
        values.put(ContactsContentProvider.Contact.SEND_POSITION, location);
        getContentResolver().insert(ContactsContentProvider.CONTENT_URI, values);
    }

    @Override
    public void onContactCancelCallback() {
        mFragmentManager.popBackStack();
        mFab.show();
        mFab.setOnClickListener(fabClick);
    }

    @Override
    public void requestLocationPermissions() {
        if (PermissionManager.isLollipopOrHigher() && !PermissionManager.hasLocationPermission(this)) {
            PermissionManager.requestLocationPermissions(this, WarnConstants.REQUEST_LOCATION_PERMISSION);
        }
    }
}
