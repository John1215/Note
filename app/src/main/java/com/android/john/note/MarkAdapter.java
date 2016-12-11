package com.android.john.note;

/**
 * Created by John on 2016/11/22.
 * It is adapter of drawer menu button list.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.john.note.R;

import java.util.List;

public class MarkAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<String> mData;
    private List<Integer> mDataIcon;

    public MarkAdapter(Context context, List<String> data, List<Integer> dataicon) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mDataIcon = dataicon;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.drawer_list_mark, null);
        TextView textView = (TextView) view.findViewById(R.id.mark_tag);
        ImageView imageView = (ImageView) view.findViewById(R.id.mark_icon);
        imageView.setImageResource(mDataIcon.get(position));
        textView.setText(mData.get(position));
        return view;
    }
}
