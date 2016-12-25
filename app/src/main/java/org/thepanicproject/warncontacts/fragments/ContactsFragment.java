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

public class ContactsFragment extends Fragment {
    private ContentResolver mContentResolver;
    private ContactsAdapter mContacts;
    private ContactsObserver mContactsObserver;
    private OnContactListener mListener;

    private String[] mProjection = new String[]{
            ContactsContentProvider.Contact._ID};

    public ContactsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts_list, container, false);

        Context context = view.getContext();
        mContentResolver = context.getContentResolver();
        mContacts = new ContactsAdapter(
                mContentResolver.query(
                        ContactsContentProvider.CONTENT_URI, mProjection, null, null, null
                ),
                mListener);
        mContactsObserver = new ContactsObserver(new Handler());
        mContentResolver.registerContentObserver(ContactsContentProvider.CONTENT_URI, true, mContactsObserver);
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mContacts);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnContactListener) {
            mListener = (OnContactListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnContactListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
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
