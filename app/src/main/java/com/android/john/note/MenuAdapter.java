package com.android.john.note;

/**
 * Created by John on 2016/11/22.
 * It is adapter of drawer menu button list.
 */

        import android.content.Context;
        import android.graphics.BitmapFactory;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;
        import com.android.john.note_01.R;
        import java.util.List;
public class MenuAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<String> mData;
    private List<Integer> mDataIcon;

    public MenuAdapter(Context context, List<String> data,List<Integer> dataicon) {
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
        View view = mInflater.inflate(R.layout.drawer_list_item,null);
        TextView textView = (TextView) view.findViewById(R.id.choice);
        ImageView imageView = (ImageView) view.findViewById(R.id.action_icon);
        imageView.setImageResource(mDataIcon.get(position));
        textView.setText(mData.get(position));
        return view;
    }
}
