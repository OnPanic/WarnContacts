package org.thepanicproject.warncontacts.fragments;

import android.app.Fragment;
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
import android.widget.Switch;

import org.thepanicproject.warncontacts.R;
import org.thepanicproject.warncontacts.constants.WarnConstants;

public class ContactSettings extends Fragment {
    private Uri contactURI;
    private String contactName;
    private Switch sms;
    private Switch email;
    private Switch location;
    private OnContacSettingsListener mListener;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_contact_settings, container, false);

        sms = (Switch) layout.findViewById(R.id.send_sms);
        sms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mListener.requestSendSMSPermissions();
                }
            }
        });

        email = (Switch) layout.findViewById(R.id.send_email);

        location = (Switch) layout.findViewById(R.id.send_location);
        location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mListener.requestLocationPermissions();
                }
            }
        });

        Cursor cursor = getActivity()
                .getContentResolver()
                .query(contactURI, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            cursor.close();
        }

        Button save = (Button) layout.findViewById(R.id.contact_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onContactSaveCallback(
                        contactURI.getLastPathSegment(), contactName, sms.isChecked(), email.isChecked(), location.isChecked());
            }
        });

        Button cancel = (Button) layout.findViewById(R.id.contact_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onContactCancelCallback();
            }
        });


        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnContacSettingsListener) {
            mListener = (OnContacSettingsListener) context;
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
    }

    public void onSendSMSPermissionDenied() {
        sms.setChecked(false);
    }

    public interface OnContacSettingsListener {
        void onContactSaveCallback(String contact_id, String name, Boolean sms, Boolean email, Boolean location);

        void onContactCancelCallback();

        void requestLocationPermissions();

        void requestSendSMSPermissions();
    }
}
