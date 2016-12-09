package com.android.john.note;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.john.note_01.R;

import java.util.List;

/**
 * Created by John on 2016/12/4.
 */

public class ColorButtonAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<String> color_list;



    public ColorButtonAdapter(Context context,List<String> btn_color){
        this.layoutInflater = LayoutInflater.from(context);
        this.color_list = btn_color;

    }

    @Override
    public int getCount() {
        return color_list.size();
    }

    @Override
    public Object getItem(int i) {
        return color_list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i,View convertView, ViewGroup parent) {
        convertView=layoutInflater.inflate(R.layout.color_btn,null);
        TextView color =(TextView)convertView.findViewById(R.id.color_btn_icon);
        int id=0;
        switch (color_list.get(i)){
            case "red":
                id=R.drawable.color_button_red;
                break;
            case "cyan":
                id=R.drawable.color_button_cyan;
                break;
            case "green":
                id=R.drawable.color_button_green;
                break;
            case "blue":
                id=R.drawable.color_button_blue;
                break;
            case "purple":
                id=R.drawable.color_button_purple;
                break;
            case "orange":
                id=R.drawable.color_button_orange;
                break;
            default:id=R.drawable.color_button_red;
        }
        color.setBackgroundResource(id);
        return convertView;
    }
}
