package org.thepanicproject.warncontacts.fragments;

import android.app.Fragment;
import android.content.ContentResolver;
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

import org.thepanicproject.warncontacts.R;
import org.thepanicproject.warncontacts.adapters.EmailsAdapter;
import org.thepanicproject.warncontacts.adapters.PhonesAdapter;
import org.thepanicproject.warncontacts.constants.WarnConstants;

public class ContactSettings extends Fragment {
    private Context mContext;
    private Uri contactURI;
    private String contactName;
    private Switch sms;
    private Switch email;
    private Switch location;
    private ListView lPhones;
    private ListView eMails;
    private OnContacSettingsListener mListener;
    private ContentResolver cr;
    private Boolean isValid = false;

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

        View layout = inflater.inflate(R.layout.contact_settings, container, false);

        lPhones = (ListView) layout.findViewById(R.id.phones);
        eMails = (ListView) layout.findViewById(R.id.emails);

        sms = (Switch) layout.findViewById(R.id.send_sms);
        sms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lPhones.setVisibility(View.VISIBLE);
                    mListener.requestSendSMSPermissions();
                } else {
                    lPhones.setVisibility(View.GONE);
                }
            }
        });

        email = (Switch) layout.findViewById(R.id.send_email);
        email.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    eMails.setVisibility(View.VISIBLE);
                } else {
                    eMails.setVisibility(View.GONE);
                }
            }
        });

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
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid) {
                    mListener.onContactSaveCallback(
                            contactURI.getLastPathSegment(), contactName, sms.isChecked(), email.isChecked(), location.isChecked());
                } else {
                    mListener.onContactCancelCallback();
                }
            }
        });

        Button cancel = (Button) layout.findViewById(R.id.contact_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onContactCancelCallback();
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
            sms.setEnabled(true);
            lPhones.setAdapter(new PhonesAdapter(
                    mContext,
                    cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactURI.getLastPathSegment(),
                            null,
                            null
                    ),
                    0
            ));
        }
        cursor.close();

        // Get emails
        Cursor emails = cr.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactURI.getLastPathSegment(),
                null,
                null
        );

        if (emails != null && emails.getCount() > 0) {
            email.setEnabled(true);
            eMails.setAdapter(new EmailsAdapter(mContext, emails, 0));
        }

        // Is a valid contact??
        isValid = (email.isEnabled() || sms.isEnabled());

        // Enable only if we have a way to send the location
        location.setEnabled(isValid);

        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
        cr = mContext.getContentResolver();

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
        location.setEnabled(false);
    }

    public void onSendSMSPermissionDenied() {
        lPhones.setVisibility(View.GONE);
        sms.setChecked(false);
        sms.setEnabled(false);
    }

    public interface OnContacSettingsListener {
        void onContactSaveCallback(String contact_id, String name, Boolean sms, Boolean email, Boolean location);

        void onContactCancelCallback();

        void requestLocationPermissions();

        void requestSendSMSPermissions();
    }
}
