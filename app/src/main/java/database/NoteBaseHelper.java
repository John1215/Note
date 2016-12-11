package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Deer-Apple on 2016/12/8.
 */

public class NoteBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "noteBase.db";

    public NoteBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + NoteDbSchema.NoteTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                NoteDbSchema.NoteTable.Cols.UUID + ", " +
                NoteDbSchema.NoteTable.Cols.TITLE + ", " +
                NoteDbSchema.NoteTable.Cols.CONTENT + ", " +
                NoteDbSchema.NoteTable.Cols.DATE + ", " +
                NoteDbSchema.NoteTable.Cols.COLOR +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
