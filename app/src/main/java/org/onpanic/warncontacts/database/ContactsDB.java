package org.onpanic.warncontacts.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactsDB extends SQLiteOpenHelper {

    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String PHONES_TABLE_NAME = "phones";
    public static final String EMAILS_TABLE_NAME = "emails";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "warn_contacts";

    private static final String CONTACTS_TABLE_CREATE =
            "CREATE TABLE " + CONTACTS_TABLE_NAME + " (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "contact_id TEXT, " +
                    "contact_name TEXT, " +
                    "send_email INTEGER DEFAULT 0, " +
                    "enabled INTEGER DEFAULT 1, " +
                    "send_position INTEGER DEFAULT 0, " +
                    "send_sms INTEGER DEFAULT 0);";

    private static final String PHONES_TABLE_CREATE =
            "CREATE TABLE " + PHONES_TABLE_NAME + " (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "contact_id TEXT, " +
                    "phone TEXT);";

    private static final String EMAILS_TABLE_CREATE =
            "CREATE TABLE " + EMAILS_TABLE_NAME + " (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "contact_id TEXT, " +
                    "email TEXT);";

    public ContactsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CONTACTS_TABLE_CREATE);
        db.execSQL(PHONES_TABLE_CREATE);
        db.execSQL(EMAILS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

