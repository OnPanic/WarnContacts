package org.onpanic.warncontacts.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.onpanic.warncontacts.R;
import org.onpanic.warncontacts.constants.WarnConstants;
import org.onpanic.warncontacts.location.PositionGetter;
import org.onpanic.warncontacts.notifications.TriggerNotification;
import org.onpanic.warncontacts.providers.ContactsContentProvider;
import org.onpanic.warncontacts.providers.EmailsContentProvider;
import org.onpanic.warncontacts.providers.PhonesContentProvider;
import org.onpanic.warncontacts.senders.WarnSenders;

import info.guardianproject.panic.PanicResponder;

public class PanicResponseActivity extends Activity {
    private ContentResolver cr;
    private SharedPreferences prefs;
    private String message;

    private String[] cProjection = new String[]{
            ContactsContentProvider.Contact._ID,
            ContactsContentProvider.Contact.CONTACT_ID,
            ContactsContentProvider.Contact.ENABLED,
            ContactsContentProvider.Contact.SEND_EMAIL,
            ContactsContentProvider.Contact.SEND_SMS,
            ContactsContentProvider.Contact.SEND_POSITION
    };

    private String[] pProjection = new String[]{
            PhonesContentProvider.Phone._ID,
            PhonesContentProvider.Phone.CONTACT_ID,
            PhonesContentProvider.Phone.PHONE
    };

    private String[] eProjection = new String[]{
            EmailsContentProvider.Email._ID,
            EmailsContentProvider.Email.CONTACT_ID,
            EmailsContentProvider.Email.EMAIL
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PanicResponder.receivedTriggerFromConnectedApp(this)) {
            prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            cr = getContentResolver();

            message = prefs.getString(
                    getString(R.string.pref_alert_message),
                    getString(R.string.alert_message_default));

            String where = ContactsContentProvider.Contact.ENABLED + "=1 AND " + ContactsContentProvider.Contact.SEND_POSITION + "=1";

            Cursor requestPosition =
                    cr.query(ContactsContentProvider.CONTENT_URI, cProjection, where, null, null);

            if (requestPosition != null && requestPosition.getCount() > 0) {
                requestPosition.close();
                PositionGetter getter = new PositionGetter(this);
                getter.get(new PositionGetter.PositionHandler() {
                    @Override
                    public void onGet(Location location) {
                        message += " " + WarnConstants.GOOGLE_MAP_URL
                                + location.getLatitude()
                                + "," + location.getLongitude()
                                + " via " + location.getProvider();
                        sendPanicAlerts(message);
                        endRun();
                    }
                });
            } else {
                sendPanicAlerts(message);
                endRun();
            }
        } else {
            // DO NOTHING
            endRun();
        }
    }

    private void sendSms(String user_id, String message) {

        Cursor cursor = cr.query(
                PhonesContentProvider.CONTENT_URI,
                pProjection,
                PhonesContentProvider.Phone.CONTACT_ID + "=" + user_id,
                null,
                null
        );

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                WarnSenders.sendSMS(
                        cursor.getString(cursor.getColumnIndex(PhonesContentProvider.Phone.PHONE)),
                        message);
            }

            cursor.close();
        }
    }

    private void sendEmail(String user_id, String message) {
        Cursor cursor = cr.query(
                EmailsContentProvider.CONTENT_URI,
                eProjection,
                EmailsContentProvider.Email.CONTACT_ID + "=" + user_id,
                null,
                null
        );

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                WarnSenders.sendEmail(
                        cursor.getString(cursor.getColumnIndex(EmailsContentProvider.Email.EMAIL)),
                        message);
            }

            cursor.close();
        }
    }

    private void sendPanicAlerts(String msg) {

        Cursor contacts = cr.query(
                ContactsContentProvider.CONTENT_URI,
                cProjection,
                ContactsContentProvider.Contact.ENABLED + "=1",
                null, null);

        if (contacts != null && contacts.getCount() > 0) {

            while (contacts.moveToNext()) {
                String id = contacts.getString(contacts.getColumnIndex(ContactsContentProvider.Contact.CONTACT_ID));

                if (contacts.getInt(contacts.getColumnIndex(ContactsContentProvider.Contact.SEND_SMS)) == 1) {
                    sendSms(id, msg);
                }

                if (contacts.getInt(contacts.getColumnIndex(ContactsContentProvider.Contact.SEND_EMAIL)) == 1) {
                    sendEmail(id, msg);
                }
            }

            contacts.close();

            if (prefs.getBoolean(getString(R.string.pref_runned_notification), false)) {
                TriggerNotification notification = new TriggerNotification(getApplicationContext());
                notification.show();
            }
        }
    }

    private void endRun() {
        if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        } else {
            finish();
        }
    }
}
