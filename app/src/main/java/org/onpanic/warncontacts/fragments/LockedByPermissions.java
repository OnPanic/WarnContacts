package org.onpanic.warncontacts.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.onpanic.warncontacts.R;

public class LockedByPermissions extends Fragment {


    public LockedByPermissions() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_locked_by_permissions, container, false);
    }

}
