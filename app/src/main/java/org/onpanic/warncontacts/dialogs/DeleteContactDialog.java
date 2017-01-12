package org.onpanic.warncontacts.dialogs;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import org.onpanic.warncontacts.R;
import org.onpanic.warncontacts.providers.ContactsContentProvider;
import org.onpanic.warncontacts.providers.PhonesContentProvider;

public class DeleteContactDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle arguments = getArguments();
        final Context context = getContext();

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        ContentResolver cr = context.getContentResolver();
                        cr.delete(
                                ContactsContentProvider.CONTENT_URI,
                                ContactsContentProvider.Contact._ID + "=" + arguments.getInt("_id"),
                                null
                        );

                        cr.delete(
                                PhonesContentProvider.CONTENT_URI,
                                PhonesContentProvider.Phone.CONTACT_ID + "=" + arguments.getInt("_id"),
                                null
                        );

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // Do nothing
                        break;
                }
            }
        };

        return new AlertDialog.Builder(context)
                .setMessage(R.string.confirm_contact_deletion)
                .setPositiveButton(R.string.delete, dialogClickListener)
                .setNegativeButton(R.string.cancel, dialogClickListener)
                .create();
    }
}
