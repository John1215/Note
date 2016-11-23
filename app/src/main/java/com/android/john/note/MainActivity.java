package com.android.john.note;
/*
created by john

 */
import android.graphics.drawable.Drawable;
import android.support.design.internal.NavigationMenu;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuAdapter;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.john.note_01.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import circleimageview.CircleImageView;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;


public class MainActivity extends AppCompatActivity {
    //侧滑栏
    private DrawerLayout drawerLayout;
    //下拉刷新
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    //圆形头像
    private CircleImageView icon;
    //侧滑栏按钮
    private ListView drawer_btn_list;
    private List<String> choices;
    private List<Integer> choiceIcon;


    //状态栏透明
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus(true);
        setContentView(R.layout.activity_main);
        //工具栏
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //圆形头像
        icon=(CircleImageView)findViewById(R.id.circleImageView);
        //icon.setImageDrawable(Drawable.createFromPath("/sdcard/01.jpg"));

        //侧滑实现
        drawerLayout=(DrawerLayout)findViewById(R.id.draw_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        //侧滑栏按钮
        drawer_btn_list = (ListView) findViewById(R.id.btn_list);
        choices = new ArrayList<String>();
        choiceIcon = new ArrayList<>();
        choices.add("同步");
        choices.add("回收站");
        choices.add("设置");
        choices.add("反馈");
        choices.add("帮助");
        choiceIcon.add(R.drawable.ic_drawer_sync);
        choiceIcon.add(R.drawable.ic_drawer_recycle);
        choiceIcon.add(R.drawable.ic_drawer_setting);
        choiceIcon.add(R.drawable.ic_drawer_feedback);
        choiceIcon.add(R.drawable.ic_drawer_help);
        ListAdapter adapter = new com.android.john.note.MenuAdapter(this,choices,choiceIcon);
        drawer_btn_list.setAdapter(adapter);
        //下拉刷新实现
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Collections.reverse(mData);
                        try {
                            Thread.sleep(1000); //模拟耗时,测试
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                       // Handler.sendEmptyMessage(1);
                    }
                }).start();
            }
        });
        //浮动按钮
        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_speed_dial);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

//            public boolean onOptionsItemSelected(MenuItem item) {
//                if (item.getItemId() == android.R.id.home) {
//                    finish();
//                    return true;
//                }
//                return super.onMenuItemSelected(item);
//            }
        });
    }
}
