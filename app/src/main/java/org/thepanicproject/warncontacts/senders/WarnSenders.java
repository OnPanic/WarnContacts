package org.thepanicproject.warncontacts.senders;


import android.telephony.SmsManager;

public class WarnSenders {
    public static void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sendEmail(String phoneNo, String msg) {
        // TODO
    }
}
