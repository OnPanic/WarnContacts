package org.thepanicproject.warncontacts.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;

import org.thepanicproject.warncontacts.R;

public class PhonesAdapter extends CursorAdapter {
    private LayoutInflater cursorInflater;

    public PhonesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

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
        CheckBox box = (CheckBox) view.findViewById(R.id.phone_number);
        box.setText(number);
    }
}
