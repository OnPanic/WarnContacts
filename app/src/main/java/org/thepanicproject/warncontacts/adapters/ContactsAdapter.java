package org.thepanicproject.warncontacts.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.thepanicproject.warncontacts.R;
import org.thepanicproject.warncontacts.fragments.ContactsListFragment;
import org.thepanicproject.warncontacts.providers.ContactsContentProvider;

public class ContactsAdapter extends CursorRecyclerViewAdapter<ContactsAdapter.ViewHolder> {

    private final ContactsListFragment.OnContactListener mListener;

    public ContactsAdapter(Cursor cursor, ContactsListFragment.OnContactListener listener) {
        super(cursor);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contacts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor) {
        final String contact_uri = cursor.getString(cursor.getColumnIndex(ContactsContentProvider.Contact.CONTACT_URI));

        /* holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onContactListenerCallback(id);
                }
            }
        }); */
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
