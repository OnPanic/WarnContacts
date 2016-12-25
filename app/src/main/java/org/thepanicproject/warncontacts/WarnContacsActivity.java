package org.thepanicproject.warncontacts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.thepanicproject.warncontacts.constants.WarnConstants;
import org.thepanicproject.warncontacts.fragments.ContactSettings;
import org.thepanicproject.warncontacts.fragments.ContactsFragment;

public class WarnContacsActivity extends AppCompatActivity implements ContactsFragment.OnContactListener {
    private FragmentManager mFragmentManager;
    private FloatingActionButton mFab;
    private String newContact = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warn_contacs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragmentManager = getSupportFragmentManager();

        // If we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return;
        }

        ContactsFragment contactsFragment = new ContactsFragment();
        mFragmentManager.beginTransaction()
                .add(R.id.fragment_container, contactsFragment).commit();

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(contactPickerIntent, WarnConstants.CONTACT_PICKER_RESULT);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_warn_contacs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case WarnConstants.CONTACT_PICKER_RESULT:
                if (resultCode == Activity.RESULT_OK) {
                    Uri result = data.getData();
                    newContact = result.getLastPathSegment();
                }
                return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onContactListenerCallback(int id) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (newContact != null) {
            ContactSettings contactSettings = new ContactSettings();
            Bundle args = new Bundle();
            args.putString(WarnConstants.CONTACT_ID, newContact);
            contactSettings.setArguments(args);
            newContact = null;
            mFab.hide();
            mFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .add(R.id.fragment_container, contactSettings).commit();
        }
    }
}
