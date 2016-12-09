package com.android.john.note;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.android.john.note_01.R;

import com.android.john.note_01.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by John on 2016/11/27.
 * It is adapter of recycleview.
 */

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.MainListViewHolder> {
    private static final String TAG = MainListViewHolder.class.getSimpleName();
    public Context context;
    private List<NoteItem> mNoteItems;
    private List<NoteItem> mUndo;
    private LayoutInflater inflater;
    private boolean canClick = false;

    public MainListAdapter(List<NoteItem> data, Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        mNoteItems = data;
    }

    @Override
    public MainListAdapter.MainListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cardview, parent, false);
        MainListViewHolder viewHolder = new MainListViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MainListAdapter.MainListViewHolder holder, int position) {
        MainListViewHolder viewHolder = holder;
        holder.mTitle.setText(mNoteItems.get(position).getTitle());
        String content = mNoteItems.get(position).getContent();
        StringBuffer buffer = new StringBuffer();
        Pattern p = Pattern.compile("<img>(.+?)</img>");
        Matcher m = p.matcher(content);
        int index = 0;
        boolean flag = false;
        while(m.find()){
            flag = true;
            Log.i(TAG, "" + m.start());
            Log.i(TAG, "" + m.end());
            Log.i(TAG, content.substring(index, m.start()-1));
            buffer.append(content.substring(index, m.start()-1));
            index = m.end();
//            buffer.append(content.substring(index + 1));
//            Log.i(TAG, content.substring(index + 1));
        }
        if(flag){
            holder.mContent.setText(buffer.toString());
        }else{
            holder.mContent.setText(content);
        }

        holder.mColor.setBackgroundColor(Color.parseColor(mNoteItems.get(position).getColor()));
        holder.mDate.setText(mNoteItems.get(position).getFormatDateString());
        viewHolder.layout.scrollTo(0, 0);
    }

    @Override
    public int getItemCount() {
        if (mNoteItems != null) {
            return mNoteItems.size();
        } else {
            return 0;
        }

    }

    public void setNoteItems(List<NoteItem> items) {
        mNoteItems = items;
    }

    public class MainListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTitle;
        public TextView mContent;
        public TextView mColor;
        public TextView mDate;
        public LinearLayout layout;



        public MainListViewHolder(View itemView) {

            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.cardview_title);
            mContent = (TextView) itemView.findViewById(R.id.cardview_content);
            mColor = (TextView) itemView.findViewById(R.id.cardview_color);
            mDate = (TextView) itemView.findViewById(R.id.cardview_time);
            layout = (LinearLayout) itemView.findViewById(R.id.cardview_linear);
            if (canClick) {
                itemView.setOnClickListener(this);
            }

        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context, mTitle.getText().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void removeRecycle(int position) {
        mUndo = new ArrayList<>();
        NoteItem temp = mNoteItems.get(position);
        NoteLab.get(context).removeNoteItem(temp);
        mUndo.add(temp);
        mNoteItems.remove(position);

        notifyDataSetChanged();
        if (mNoteItems.size() == 0) {
            Toast.makeText(context, "已经没数据啦", Toast.LENGTH_SHORT).show();
        }
    }

    public void Undo_removeRecycle(int position) {
        NoteItem item = mUndo.get(0);
        NoteLab.get(context).addNoteItem(item);
        mNoteItems.add(item);
        notifyDataSetChanged();

    }

    public List<NoteItem> returnData() {
        return mNoteItems;
    }

    public void setItemClick(boolean flag) {

        canClick = flag;

    }

}
