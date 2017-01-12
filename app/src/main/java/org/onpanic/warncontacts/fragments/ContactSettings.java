package org.onpanic.warncontacts.fragments;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import org.onpanic.warncontacts.R;
import org.onpanic.warncontacts.adapters.PhonesAdapter;
import org.onpanic.warncontacts.constants.WarnConstants;
import org.onpanic.warncontacts.providers.ContactsContentProvider;
import org.onpanic.warncontacts.providers.PhonesContentProvider;

import java.util.ArrayList;

public class ContactSettings extends Fragment {
    private Context mContext;
    private Uri contactURI;
    private String contactName;
    private Switch location;
    private ListView lPhones;
    private OnContactSettingsListener mListener;
    private ContentResolver cr;
    private Boolean isValid = false;
    private PhonesAdapter phonesAdapter;

    private View.OnClickListener saveButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ArrayList<String> checked_phones;

            if (!isValid) mListener.onContactFinishCallback();

            checked_phones = phonesAdapter.getSelectedString();

            if (!checked_phones.isEmpty()) {
                for (String phone : checked_phones) {
                    ContentValues phone_value = new ContentValues();
                    phone_value.put(PhonesContentProvider.Phone.CONTACT_ID, contactURI.getLastPathSegment());
                    phone_value.put(PhonesContentProvider.Phone.PHONE, phone);
                    cr.insert(PhonesContentProvider.CONTENT_URI, phone_value);
                }


                ContentValues contact_table = new ContentValues();
                contact_table.put(ContactsContentProvider.Contact.CONTACT_ID, contactURI.getLastPathSegment());
                contact_table.put(ContactsContentProvider.Contact.CONTACT_NAME, contactName);
                contact_table.put(ContactsContentProvider.Contact.SEND_POSITION, location.isChecked());
                cr.insert(ContactsContentProvider.CONTENT_URI, contact_table);

            }

            mListener.onContactFinishCallback();
        }
    };

    public ContactSettings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            contactURI = Uri.parse(args.getString(WarnConstants.CONTACT_URI));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_contact_settings, container, false);

        mListener.requestSendSMSPermissions();

        lPhones = (ListView) layout.findViewById(R.id.phones);


        location = (Switch) layout.findViewById(R.id.send_location);
        location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mListener.requestLocationPermissions();
                }
            }
        });

        Button save = (Button) layout.findViewById(R.id.contact_save);
        save.setOnClickListener(saveButtonClick);

        Button cancel = (Button) layout.findViewById(R.id.contact_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onContactFinishCallback();
            }
        });

        Cursor cursor = cr.query(contactURI, null, null, null, null);
        if (cursor == null) return layout; // That should never happen
        cursor.moveToFirst();

        // Get Name
        contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

        // Get phone numbers
        Boolean has_phone = (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) != 0);
        if (has_phone) {
            isValid = true;
            phonesAdapter = new PhonesAdapter(
                    mContext,
                    cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactURI.getLastPathSegment(),
                            null,
                            null
                    ),
                    0
            );

            lPhones.setAdapter(phonesAdapter);
        }

        cursor.close();

        // Enable only if we have a way to send the location
        location.setEnabled(isValid);

        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
        cr = mContext.getContentResolver();

        if (context instanceof OnContactSettingsListener) {
            mListener = (OnContactSettingsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnContacSettingsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onLocationPermissionDenied() {
        location.setChecked(false);
        location.setEnabled(false);
    }

    public void onSendSMSPermissionDenied() {
        mListener.onContactFinishCallback();
    }

    public interface OnContactSettingsListener {
        void onContactFinishCallback();

        void requestLocationPermissions();

        void requestSendSMSPermissions();
    }
}
