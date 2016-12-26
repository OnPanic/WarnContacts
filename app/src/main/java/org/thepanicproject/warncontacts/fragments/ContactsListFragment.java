package org.thepanicproject.warncontacts.fragments;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.thepanicproject.warncontacts.R;
import org.thepanicproject.warncontacts.adapters.ContactsAdapter;
import org.thepanicproject.warncontacts.providers.ContactsContentProvider;

public class ContactsListFragment extends Fragment {
    private ContentResolver mContentResolver;
    private ContactsAdapter mContacts;
    private ContactsObserver mContactsObserver;
    private OnContactListener mListener;
    private Context mCotext;

    private String[] mProjection = new String[]{
            ContactsContentProvider.Contact._ID,
            ContactsContentProvider.Contact.CONTACT_ID,
            ContactsContentProvider.Contact.ENABLED,
            ContactsContentProvider.Contact.CONTACT_NAME
    };

    public ContactsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView view = (RecyclerView) inflater.inflate(R.layout.fragment_contacts_list, container, false);

        mContacts = new ContactsAdapter(
                mCotext,
                mContentResolver.query(
                        ContactsContentProvider.CONTENT_URI, mProjection, null, null, null
                ),
                mListener);

        mContactsObserver = new ContactsObserver(new Handler());
        mContentResolver.registerContentObserver(ContactsContentProvider.CONTENT_URI, true, mContactsObserver);

        view.setLayoutManager(new LinearLayoutManager(view.getContext()));
        view.setAdapter(mContacts);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCotext = context;
        mContentResolver = mCotext.getContentResolver();

        if (context instanceof OnContactListener) {
            mListener = (OnContactListener) mCotext;
        } else {
            throw new RuntimeException(mCotext.toString()
                    + " must implement OnContactListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mContentResolver.unregisterContentObserver(mContactsObserver);
    }

    public interface OnContactListener {
        void onContactListenerCallback(int id);
    }

    class ContactsObserver extends ContentObserver {
        ContactsObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            // New data
            mContacts.changeCursor(mContentResolver.query(
                    ContactsContentProvider.CONTENT_URI, mProjection, null, null, null
            ));
        }

    }
}
