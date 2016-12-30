package org.thepanicproject.warncontacts.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;

import org.thepanicproject.warncontacts.R;

import java.util.ArrayList;

public class PhonesAdapter extends CursorAdapter {
    private LayoutInflater cursorInflater;
    private ArrayList<String> selectedStrings;

    public PhonesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        selectedStrings = new ArrayList<String>();
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.phone, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        final CheckBox box = (CheckBox) view.findViewById(R.id.phone_number);
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedStrings.add(box.getText().toString());
                } else {
                    selectedStrings.remove(box.getText().toString());
                }

            }
        });

        box.setText(number);
    }

    public ArrayList<String> getSelectedString() {
        return selectedStrings;
    }
}
