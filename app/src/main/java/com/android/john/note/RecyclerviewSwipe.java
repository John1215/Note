package com.android.john.note;

/**
 * Created by John on 2016/11/30.
 */

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.android.john.note_01.R;


import java.util.UUID;

import static java.lang.Thread.sleep;

public class RecyclerViewSwipe extends RecyclerView {
    private int maxLength;
    private int mStartX = 0;
    private LinearLayout itemLayout;
    private int pos;
    private Rect mTouchFrame;
    private int xDown, xMove, yDown, yMove, mTouchSlop;
    private Scroller mScroller;
    private TextView textView;
    private ImageView imageView;
    private boolean isFirst = true;
    private RecyclerViewSwipe recyclerViewSwipe;
    private FloatingActionButton fab;
    private boolean flag = false;
    private Context mContext;
    private boolean isValid = true;

    public RecyclerViewSwipe(Context context) {
        this(context, null);
    }

    public RecyclerViewSwipe(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewSwipe(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //滑动到最小距离
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        //滑动的最大距离
        maxLength = ((int) (180 * context.getResources().getDisplayMetrics().density + 0.5f));
        //初始化Scroller
        mScroller = new Scroller(context, new LinearInterpolator(context, null));

        mContext = context;
    }


    private int dipToPx(Context context, int dip) {
        return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public boolean onTouchEvent(MotionEvent event) {


        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                xDown = x;
                yDown = y;
                //通过点击的坐标计算当前的position
                int mFirstPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
                Rect frame = mTouchFrame;
                if (frame == null) {
                    mTouchFrame = new Rect();
                    frame = mTouchFrame;
                }
                int count = getChildCount();
                for (int i = count - 1; i >= 0; i--) {
                    final View child = getChildAt(i);
                    if (child.getVisibility() == View.VISIBLE) {
                        child.getHitRect(frame);
                        if (frame.contains(x, y)) {
                            pos = mFirstPosition + i;
                        }
                    }
                }
                //通过position得到item的viewHolder
                View view = getChildAt(pos - mFirstPosition);
                if (view != null) {
//                    Toast.makeText(mContext, "not null!", Toast.LENGTH_SHORT).show();
                    isValid = true;
                    MainListAdapter.MainListViewHolder viewHolder = (MainListAdapter.MainListViewHolder) getChildViewHolder(view);
                    itemLayout = viewHolder.layout;
                    textView = (TextView) itemLayout.findViewById(R.id.item_delete_txt);
                    imageView = (ImageView) itemLayout.findViewById(R.id.item_delete_img);
                }else{
//                    Toast.makeText(mContext, "null!", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }

//                MainListAdapter.MainListViewHolder viewHolder = (MainListAdapter.MainListViewHolder) getChildViewHolder(view);
//                itemLayout = viewHolder.layout;
//                textView = (TextView) itemLayout.findViewById(R.id.item_delete_txt);
//                imageView = (ImageView) itemLayout.findViewById(R.id.item_delete_img);
            }
            break;

            case MotionEvent.ACTION_MOVE: {

                if (isValid) {


                    xMove = x;
                    yMove = y;
                    int dx = xMove - xDown;
                    int dy = yMove - yDown;

                    if (!MainActivity.DrawerIsOpen) {
                        //判断点击 大于X,Y位移大于4像素进行滑动判断
                        if (Math.abs(dy) > 4 && Math.abs(dx) > 4) {
                            flag = false;
                            if (Math.abs(dy) < mTouchSlop * 2 && Math.abs(dx) > mTouchSlop) {
                                int scrollX = itemLayout.getScrollX();
                                int newScrollX = mStartX - x;
                                if (newScrollX < 0 && scrollX <= 0) {
                                    newScrollX = 0;
                                } else if (newScrollX > 0 && scrollX >= maxLength) {
                                    newScrollX = 0;
                                }
                                if (scrollX > maxLength / 2) {

                                    textView.setVisibility(View.GONE);
                                    imageView.setVisibility(View.VISIBLE);


                                    if (isFirst) {
                                        ObjectAnimator animatorX = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 1.2f, 1f);
                                        ObjectAnimator animatorY = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 1.2f, 1f);
                                        AnimatorSet animSet = new AnimatorSet();
                                        animSet.play(animatorX).with(animatorY);
                                        animSet.setDuration(800);
                                        animSet.start();
                                        isFirst = false;
                                    }


                                } else {
                                    textView.setVisibility(View.VISIBLE);
                                    imageView.setVisibility(View.GONE);


                                }
                                itemLayout.scrollBy(newScrollX, 0);
                            }

                        }
                        //位移小于4像素，为点击
                        else {

                            flag = true;
                        }
                    }
                }
            }
            break;
            case MotionEvent.ACTION_UP: {
                if (isValid) {
                    //响应点击操作
                    if (flag) {
                        UUID id = ((MainListAdapter) getAdapter()).returnData().get(pos).getId();
                        Intent i = EditActivity.newIntent(getContext());
                        i.putExtra("UUID", id);
                        mContext.startActivity(i);
                    }
                    //响应滑动判断
                    int scrollX = itemLayout.getScrollX();
                    if (scrollX > maxLength / 2) {
                        ((MainListAdapter) getAdapter()).removeRecycle(pos);
                        recyclerViewSwipe = (RecyclerViewSwipe) findViewById(R.id.recycleview);
                        fab=(FloatingActionButton)findViewById(R.id.fab);
                        Snackbar.make(recyclerViewSwipe, "已删除", Snackbar.LENGTH_SHORT)
                                .setAction("撤销", new RecyclerViewSwipe.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ((MainListAdapter) getAdapter()).Undo_removeRecycle(pos);
                                    }
                                })
                                .setActionTextColor(getResources().getColor(R.color.colorAccent))
                                .setDuration(2500).show();
                    } else {
                        mScroller.startScroll(scrollX, 0, -scrollX, 0);
                        //刷新view
                        invalidate();

                    }
                    isFirst = true;
                }
            }
            break;

        }
        mStartX = x;
        ((MainListAdapter) getAdapter()).setItemClick(false);
        return super.onTouchEvent(event);
    }

    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            itemLayout.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

}

