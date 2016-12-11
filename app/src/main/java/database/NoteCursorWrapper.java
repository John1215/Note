package database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.text.format.DateFormat;

import com.android.john.note.NoteItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Deer-Apple on 2016/12/8.
 */

public class NoteCursorWrapper extends CursorWrapper {

    public NoteCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public NoteItem getNoteItem(){
        String uuid = getString(getColumnIndex(NoteDbSchema.NoteTable.Cols.UUID));
        String title = getString(getColumnIndex(NoteDbSchema.NoteTable.Cols.TITLE));
        String content = getString(getColumnIndex(NoteDbSchema.NoteTable.Cols.CONTENT));
        String date = getString(getColumnIndex(NoteDbSchema.NoteTable.Cols.DATE));
        String color = getString(getColumnIndex(NoteDbSchema.NoteTable.Cols.COLOR));

        NoteItem item = new NoteItem(UUID.fromString(uuid));
        item.setTitle(title);
        item.setContent(content);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日,kk:mm");
        Date date1 = null;
        try {
            date1 = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        item.setDate(date1);
        item.setColor(color);

        return item;
    }
}
