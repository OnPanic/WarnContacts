package org.thepanicproject.warncontacts.fragments;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.thepanicproject.warncontacts.R;
import org.thepanicproject.warncontacts.adapters.ContactsAdapter;
import org.thepanicproject.warncontacts.providers.ContactsContentProvider;

public class ContactsList extends Fragment {
    private ContentResolver mContentResolver;
    private ContactsAdapter mContacts;
    private ContactsObserver mContactsObserver;
    private OnContactListener mListener;
    private Context mContext;
    private FloatingActionButton mFab;

    private String[] mProjection = new String[]{
            ContactsContentProvider.Contact._ID,
            ContactsContentProvider.Contact.CONTACT_ID,
            ContactsContentProvider.Contact.ENABLED,
            ContactsContentProvider.Contact.CONTACT_NAME
    };

    public ContactsList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts_list, container, false);
        RecyclerView list = (RecyclerView) view.findViewById(R.id.contact_list);

        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onFabClickCallback();
            }
        });

        mContacts = new ContactsAdapter(
                mContext,
                mContentResolver.query(
                        ContactsContentProvider.CONTENT_URI, mProjection, null, null, null
                ),
                mListener);

        mContentResolver.registerContentObserver(ContactsContentProvider.CONTENT_URI, true, mContactsObserver);

        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        list.setAdapter(mContacts);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mContentResolver = mContext.getContentResolver();
        mContactsObserver = new ContactsObserver(new Handler());

        if (context instanceof OnContactListener) {
            mListener = (OnContactListener) mContext;
        } else {
            throw new RuntimeException(mContext.toString()
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

        void onFabClickCallback();
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
