package com.android.john.note;

import android.media.Image;

import java.util.Date;

/**
 * Created by John on 2016/11/27.
 */

public class ItemShowObj {
    private String title;
    private  String time;
    private String content;
//    private Image pic;
    private String color;
    public ItemShowObj(String title, String time, String content, String color){

        this.title=title;
        this.time=time;
        this.content=content;
//        this.pic=pic;
        this.color=color;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

//    public Image getPic() {
//        return pic;
//    }

    public String getColor() {
        return color;
    }
}
