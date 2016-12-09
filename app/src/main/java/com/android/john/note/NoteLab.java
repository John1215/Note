package com.android.john.note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.NoteBaseHelper;
import database.NoteCursorWrapper;
import database.NoteDbSchema;
import database.NoteDbSchema.NoteTable;

/**
 * Created by Deer-Apple on 2016/12/8.
 */

public class NoteLab {
    private static NoteLab sNoteLab;

    //    private List<NoteItem> mNoteItems;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static NoteLab get(Context context) {
        if (sNoteLab == null) {
            sNoteLab = new NoteLab(context);
        }
        return sNoteLab;
    }

    private NoteLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new NoteBaseHelper(mContext).getWritableDatabase();
//        mNoteItems = new ArrayList<>();
//        for(int i = 0; i <  10; i++){
//            NoteItem item = new NoteItem();
//            item.setTitle("#Item" + i);
//            item.setContent("#Content" + i);
//            item.setColor(i % 6);
//            mNoteItems.add(item);
//        }
    }

    public List<NoteItem> getNoteItems() {
        List<NoteItem> items = new ArrayList<>();

        NoteCursorWrapper cursor = quertNoteItems(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                items.add(cursor.getNoteItem());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return items;
    }

    public NoteItem getNoteItem(UUID uuid) {
        NoteCursorWrapper cursor = quertNoteItems(
                NoteTable.Cols.UUID + " = ?",
                new String[]{uuid.toString()});
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getNoteItem();
        } finally {
            cursor.close();
        }
    }

    public void addNoteItem(NoteItem item) {
        ContentValues values = getContentValues(item);
        mDatabase.insert(NoteTable.NAME, null, values);
//        mNoteItems.add(item);
    }

    public void removeNoteItem(NoteItem item) {
        mDatabase.delete(NoteTable.NAME,
                NoteTable.Cols.UUID + " = ?",
                new String[]{item.getId().toString()});
    }

    public void updateNoteItem(NoteItem item) {
        String id = item.getId().toString();
        ContentValues values = getContentValues(item);

        mDatabase.update(NoteTable.NAME, values, NoteTable.Cols.UUID + " = ?", new String[]{id});
    }

    private static ContentValues getContentValues(NoteItem item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteTable.Cols.UUID, item.getId().toString());
        contentValues.put(NoteTable.Cols.TITLE, item.getTitle());
        contentValues.put(NoteTable.Cols.CONTENT, item.getContent());
        contentValues.put(NoteTable.Cols.DATE, item.getFormatDateString());
        contentValues.put(NoteTable.Cols.COLOR, item.getColor());

        return contentValues;
    }

    private NoteCursorWrapper quertNoteItems(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                NoteTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new NoteCursorWrapper(cursor);
    }
}
