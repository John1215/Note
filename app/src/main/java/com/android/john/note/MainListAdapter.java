package com.android.john.note;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.john.note_01.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 2016/11/27.
 * It is adapter of recycleview.
 *
 */

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.MainListViewHolder> {
    public Context context;
    private List<ItemShowObj> mData;
    private List<ItemShowObj> mUndo;
    private LayoutInflater inflater;
    private boolean canClick=false;

    public MainListAdapter(List<ItemShowObj> data,Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        mData = data;
    }

    @Override
    public MainListAdapter.MainListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cardview, null, false);
        MainListViewHolder viewHolder = new MainListViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MainListAdapter.MainListViewHolder holder, int position) {
        MainListViewHolder viewHolder=(MainListViewHolder)holder;
        holder.mTitle.setText(mData.get(position).getTitle());
        holder.mContent.setText(mData.get(position).getContent());
        holder.mColor.setBackgroundColor(Color.parseColor(mData.get(position).getColor()));
        holder.mTime.setText(mData.get(position).getTime());
        viewHolder.layout.scrollTo(0, 0);
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0;
        }

    }

    public class MainListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTitle;
        public TextView mContent;
        public TextView mColor;
        public TextView mTime;
        public ImageView img;
        public LinearLayout layout;



        public MainListViewHolder(View itemView) {

            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.cardview_title);
            mContent = (TextView) itemView.findViewById(R.id.cardview_content);
            mColor = (TextView) itemView.findViewById(R.id.cardview_color);
            mTime = (TextView) itemView.findViewById(R.id.cardview_time);
            layout = (LinearLayout) itemView.findViewById(R.id.cardview_linear);
            if(canClick){
            itemView.setOnClickListener(this);}

        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context,mTitle.getText().toString(),Toast.LENGTH_SHORT).show();
        }
    }

    public void removeRecycle(int position) {
        mUndo=new ArrayList<>();
        mUndo.add(mData.get(position));
        mData.remove(position);

        notifyDataSetChanged();
        if (mData.size() == 0) {
            Toast.makeText(context, "已经没数据啦", Toast.LENGTH_SHORT).show();
        }
    }
    public void Undo_removeRecycle(int position){
        mData.add(position,mUndo.get(0));
        notifyDataSetChanged();

    }
    public List<ItemShowObj>  returnData(){
        return mData;
    }
    public void setItemClick(boolean flag){

            canClick=flag;

    }

}
