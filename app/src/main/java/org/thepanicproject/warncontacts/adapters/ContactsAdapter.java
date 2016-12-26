package org.thepanicproject.warncontacts.adapters;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.thepanicproject.warncontacts.R;
import org.thepanicproject.warncontacts.fragments.ContactsListFragment;
import org.thepanicproject.warncontacts.providers.ContactsContentProvider;

import java.io.IOException;
import java.io.InputStream;

public class ContactsAdapter extends CursorRecyclerViewAdapter<ContactsAdapter.ViewHolder> {

    private final ContactsListFragment.OnContactListener mListener;
    private Context mContext;

    public ContactsAdapter(Context context, Cursor cursor, ContactsListFragment.OnContactListener listener) {
        super(cursor);
        mContext = context;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor) {
        final int id = cursor.getInt(cursor.getColumnIndex(ContactsContentProvider.Contact._ID));
        final String contact_name = cursor.getString(cursor.getColumnIndex(ContactsContentProvider.Contact.CONTACT_NAME));
        final Boolean active = (cursor.getInt(cursor.getColumnIndex(ContactsContentProvider.Contact.ENABLED)) == 1);
        final Long contactID = Long.parseLong(cursor.getString(cursor.getColumnIndex(ContactsContentProvider.Contact.CONTACT_ID)));

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(mContext.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactID));

            if (inputStream != null) {
                viewHolder.mImage.setImageBitmap(
                        BitmapFactory.decodeStream(inputStream));
                inputStream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        viewHolder.mActive.setChecked(active);
        viewHolder.mActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ContentResolver resolver = mContext.getContentResolver();
                ContentValues fields = new ContentValues();
                fields.put(ContactsContentProvider.Contact.ENABLED, isChecked);
                resolver.update(
                        ContactsContentProvider.CONTENT_URI, fields, "_ID=" + id, null
                );
            }
        });

        viewHolder.mName.setText(contact_name);
        viewHolder.mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onContactListenerCallback(id);
                }
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView mImage;
        final Switch mActive;
        final TextView mName;

        ViewHolder(View view) {
            super(view);
            mImage = (ImageView) view.findViewById(R.id.contact_image);
            mActive = (Switch) view.findViewById(R.id.contact_active);
            mName = (TextView) view.findViewById(R.id.contact_name);
        }
    }
}
