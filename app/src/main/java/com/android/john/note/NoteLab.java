package com.android.john.note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private static final String TAG = NoteLab.class.getSimpleName();
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

    public List<NoteItem> queryNoteItemByColor(String color){
        List<NoteItem> items = new ArrayList<>();
        NoteCursorWrapper cursor = quertNoteItems(
                NoteTable.Cols.COLOR + " = ?",
                new String[]{color});
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

    public void removeNoteItemById(String uuid){
        mDatabase.delete(NoteTable.NAME,
                NoteTable.Cols.UUID + " = ?",
                new String[]{uuid});
    }

    public List<NoteItem> queryNoteItems(String text) throws ParseException {
        List<NoteItem> items = new ArrayList<>();
        String sql = "select * from " + NoteTable.NAME + " where title like ? or content like ?";
        String[] args = new String[]{"%" + text + "%", "%" + text + "%"};
        Cursor cursor = mDatabase.rawQuery(sql, args);
        if(cursor.getCount() == 0){
            Log.i(TAG, "find nothing!!!");
            return null;
        }
        cursor.moveToFirst();
        Log.i(TAG, "querying....");
        while (!cursor.isAfterLast()) {
            String uuid = cursor.getString(cursor.getColumnIndex("uuid"));
            NoteItem item = new NoteItem(UUID.fromString(uuid));
            item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            Log.i(TAG, "query title:" + cursor.getString(cursor.getColumnIndex("title")));
            item.setContent(cursor.getString(cursor.getColumnIndex("content")));
            Log.i(TAG, "query content:" + cursor.getString(cursor.getColumnIndex("content")));
            item.setColor(cursor.getString(cursor.getColumnIndex("color")));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日,kk:mm");
            item.setDate(simpleDateFormat.parse(date));
            items.add(item);
            cursor.moveToNext();
        }
        return items;
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
