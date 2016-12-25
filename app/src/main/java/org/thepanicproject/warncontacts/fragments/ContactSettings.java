package org.thepanicproject.warncontacts.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.thepanicproject.warncontacts.R;
import org.thepanicproject.warncontacts.constants.WarnConstants;

public class ContactSettings extends Fragment {
    private String contactID;
    private OnContacSettingsListener mListener;

    public ContactSettings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            contactID = args.getString(WarnConstants.CONTACT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_contact_settings, container, false);
        // (TextView) layout.findViewById();
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

    public interface OnContacSettingsListener {
        void onContactSettingsCallback();
    }
}
