package com.android.john.note;

import android.media.Image;
import android.text.format.DateFormat;

import java.util.Date;
import java.util.UUID;

/**
 * Created by John on 2016/11/27.
 */

public class NoteItem {
    private static final String TAG = NoteItem.class.getSimpleName();
    public static final int BLUE = 0;
    public static final int CYAN = 1;
    public static final int GREEN = 2;
    public static final int ORANGE = 3;
    public static final int PURPLE = 4;
    public static final int RED = 5;

    private UUID mId;
    private String mTitle;
    private String mContent;
    private Date mDate;
//    private int mColor;
    private String mColor;

    public NoteItem() {
        this(UUID.randomUUID());
    }

    public NoteItem(UUID id){
        mId = id;
        mDate = new Date();
        mColor = "#03A9F4";
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getContent() {
        return mContent;
    }

    public Date getDate() {
        return mDate;
    }

    public String getFormatDateString(){
        String formatString = "yyyy年MM月dd日,kk:mm";
        return (String) DateFormat.format(formatString, getDate());
    }

    public String getColor(){
        return mColor;
    }

//    public String getColor() {
//        switch (mColor){
//            case NoteItem.BLUE:
//                return "#03A9F4";
//            case NoteItem.CYAN:
//                return "#00BCD4";
//            case NoteItem.GREEN:
//                return "#4CAF50";
//            case NoteItem.ORANGE:
//                return "#FF9800";
//            case NoteItem.RED:
//                return "#FF5252";
//            case NoteItem.PURPLE:
//                return "#673AB7";
//        }
//        return null;
//    }
//
//    public int getColorNum(){
//        switch (mColor){
//            case NoteItem.BLUE:
//                return NoteItem.BLUE;
//            case NoteItem.CYAN:
//                return NoteItem.CYAN;
//            case NoteItem.GREEN:
//                return NoteItem.GREEN;
//            case NoteItem.ORANGE:
//                return NoteItem.ORANGE;
//            case NoteItem.RED:
//                return NoteItem.RED;
//            case NoteItem.PURPLE:
//                return NoteItem.PURPLE;
//        }
//        return -1;
//    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void setColor(String color) {
        mColor = color;
    }
}
