package org.thepanicproject.warncontacts.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import org.thepanicproject.warncontacts.database.ContactsDB;


public class ContactsContentProvider extends ContentProvider {
    private static final String AUTH = "org.thepanicproject.warncontacts.providers.ContactsContentProvider";
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTH + "/contacts");
    //UriMatcher
    private static final int CONTACTS = 1;
    private static final int CONTACT_ID = 2;
    private static final UriMatcher uriMatcher;

    //Inicializamos el UriMatcher
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTH, "contacts", CONTACTS);
        uriMatcher.addURI(AUTH, "contacts/#", CONTACT_ID);
    }

    private ContactsDB contactsDB;
    private Context mContext;

    @Override
    public boolean onCreate() {
        mContext = getContext();
        contactsDB = new ContactsDB(mContext);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Si es una consulta a un ID concreto construimos el WHERE
        String where = selection;
        if (uriMatcher.match(uri) == CONTACT_ID) {
            where = "_id=" + uri.getLastPathSegment();
        }

        SQLiteDatabase db = contactsDB.getReadableDatabase();

        return db.query(ContactsDB.CONTACTS_TABLE_NAME, projection, where,
                selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case CONTACTS:
                return "vnd.android.cursor.dir/vnd.megadldcli.contacts";
            case CONTACT_ID:
                return "vnd.android.cursor.item/vnd.megadldcli.contact";
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long regId;

        SQLiteDatabase db = contactsDB.getWritableDatabase();

        regId = db.insert(ContactsDB.CONTACTS_TABLE_NAME, null, values);

        mContext.getContentResolver().notifyChange(CONTENT_URI, null);

        return ContentUris.withAppendedId(CONTENT_URI, regId);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        //Si es una consulta a un ID concreto construimos el WHERE
        String where = selection;
        if (uriMatcher.match(uri) == CONTACT_ID) {
            where = "_id=" + uri.getLastPathSegment();
        }

        SQLiteDatabase db = contactsDB.getWritableDatabase();

        Integer rows = db.delete(ContactsDB.CONTACTS_TABLE_NAME, where, selectionArgs);

        mContext.getContentResolver().notifyChange(CONTENT_URI, null);

        return rows;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = contactsDB.getWritableDatabase();
        Integer rows = db.update(ContactsDB.CONTACTS_TABLE_NAME, values, selection, selectionArgs);
        mContext.getContentResolver().notifyChange(CONTENT_URI, null);
        return rows;
    }

    public static final class Contact implements BaseColumns {

        public static final String CONTACT_ID = "contact_id";
        public static final String CONTACT_NAME = "contact_name";
        public static final String SEND_EMAIL = "send_email";
        public static final String SEND_POSITION = "send_position";
        public static final String SEND_SMS = "send_sms";
        public static final String ENABLED = "enabled";

        private Contact() {
        }
    }
}
