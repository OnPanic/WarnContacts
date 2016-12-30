package org.onpanic.warncontacts.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import org.onpanic.warncontacts.R;

public class ContactActionsDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle arguments = getArguments();

        final View dialog_view = getActivity().getLayoutInflater().inflate(R.layout.contact_dialog, null);

        final AlertDialog actionDialog = new AlertDialog.Builder(getActivity())
                .setView(dialog_view)
                .setTitle(R.string.actions)
                .create();

        Button delete = (Button) dialog_view.findViewById(R.id.delete_contact);
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DeleteContactDialog dialog = new DeleteContactDialog();
                dialog.setArguments(arguments);
                dialog.show(getFragmentManager(), "DeleteContactDialog");
                actionDialog.dismiss();
            }
        });

        Button cancel = (Button) dialog_view.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                actionDialog.dismiss();
            }
        });

        return actionDialog;
    }
}
