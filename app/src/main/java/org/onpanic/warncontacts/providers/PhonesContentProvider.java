package org.onpanic.warncontacts.providers;

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

import org.onpanic.warncontacts.database.ContactsDB;


public class PhonesContentProvider extends ContentProvider {
    private static final String AUTH = "org.onpanic.warncontacts.providers.PhonesContentProvider";
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTH + "/phones");
    //UriMatcher
    private static final int PHONES = 1;
    private static final int PHONE_ID = 2;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTH, "phones", PHONES);
        uriMatcher.addURI(AUTH, "phones/#", PHONE_ID);
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
        String where = selection;
        if (uriMatcher.match(uri) == PHONE_ID) {
            where = "_id=" + uri.getLastPathSegment();
        }

        SQLiteDatabase db = contactsDB.getReadableDatabase();

        return db.query(ContactsDB.PHONES_TABLE_NAME, projection, where,
                selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case PHONES:
                return "vnd.android.cursor.dir/vnd.warncontacts.phones";
            case PHONE_ID:
                return "vnd.android.cursor.item/vnd.warncontacts.phone";
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long regId;

        SQLiteDatabase db = contactsDB.getWritableDatabase();

        regId = db.insert(ContactsDB.PHONES_TABLE_NAME, null, values);

        mContext.getContentResolver().notifyChange(CONTENT_URI, null);

        return ContentUris.withAppendedId(CONTENT_URI, regId);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        String where = selection;
        if (uriMatcher.match(uri) == PHONE_ID) {
            where = "_id=" + uri.getLastPathSegment();
        }

        SQLiteDatabase db = contactsDB.getWritableDatabase();

        Integer rows = db.delete(ContactsDB.PHONES_TABLE_NAME, where, selectionArgs);

        mContext.getContentResolver().notifyChange(CONTENT_URI, null);

        return rows;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = contactsDB.getWritableDatabase();
        Integer rows = db.update(ContactsDB.PHONES_TABLE_NAME, values, selection, selectionArgs);
        mContext.getContentResolver().notifyChange(CONTENT_URI, null);
        return rows;
    }

    public static final class Phone implements BaseColumns {

        public static final String CONTACT_ID = "contact_id";
        public static final String PHONE = "phone";

        private Phone() {
        }
    }
}
