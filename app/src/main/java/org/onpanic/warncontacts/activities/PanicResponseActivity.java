package org.onpanic.warncontacts.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.onpanic.warncontacts.R;
import org.onpanic.warncontacts.constants.WarnConstants;
import org.onpanic.warncontacts.notifications.TriggerNotification;
import org.onpanic.warncontacts.providers.ContactsContentProvider;
import org.onpanic.warncontacts.providers.EmailsContentProvider;
import org.onpanic.warncontacts.providers.PhonesContentProvider;
import org.onpanic.warncontacts.senders.WarnSenders;

import info.guardianproject.panic.PanicResponder;

public class PanicResponseActivity extends Activity {
    private ContentResolver cr;

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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (PanicResponder.receivedTriggerFromConnectedApp(this)) {

            String message = prefs.getString(
                    getString(R.string.pref_alert_message),
                    getString(R.string.alert_message_default));

            cr = getContentResolver();

            Cursor contacts =
                    cr.query(ContactsContentProvider.CONTENT_URI, cProjection, ContactsContentProvider.Contact.ENABLED + "=1", null, null);

            if (contacts != null && contacts.getCount() > 0) {

                while (contacts.moveToNext()) {
                    String id = contacts.getString(contacts.getColumnIndex(ContactsContentProvider.Contact.CONTACT_ID));

                    if (contacts.getInt(contacts.getColumnIndex(ContactsContentProvider.Contact.SEND_POSITION)) == 1) {
                        String location = getLocation();

                        if (location != null)
                            message += location;
                    }

                    if (contacts.getInt(contacts.getColumnIndex(ContactsContentProvider.Contact.SEND_SMS)) == 1) {
                        sendSms(id, message);
                    }

                    if (contacts.getInt(contacts.getColumnIndex(ContactsContentProvider.Contact.SEND_EMAIL)) == 1) {
                        sendEmail(id, message);
                    }
                }

                contacts.close();

                if (prefs.getBoolean(getString(R.string.pref_runned_notification), true)) {
                    TriggerNotification notification = new TriggerNotification(getApplicationContext());
                    notification.show();
                }
            }

            ExitActivity.exitAndRemoveFromRecentApps(PanicResponseActivity.this);
        }

        finish();
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

    public String getLocation() {
        String locationURL = null;

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        try {
            String bestProvider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(bestProvider);

            locationURL = " " + WarnConstants.GOOGLE_MAP_URL
                    + location.getLatitude()
                    + "," + location.getLongitude()
                    + " via " + location.getProvider();

        } catch (SecurityException | NullPointerException e) {
            e.printStackTrace();
        }

        return locationURL;
    }
}
