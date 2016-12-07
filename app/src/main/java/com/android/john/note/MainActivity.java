package com.android.john.note;
/*
created by john

 */
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.john.note_01.R;
import com.lzp.floatingactionbuttonplus.FabTagLayout;
import com.lzp.floatingactionbuttonplus.FloatingActionButtonPlus;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import circleimageview.CircleImageView;
import me.drakeet.materialdialog.MaterialDialog;


public class MainActivity extends AppCompatActivity {
    //侧滑栏
    private DrawerLayout drawerLayout;
    //下拉刷新
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    //圆形头像
    private CircleImageView icon;
    private TextView title;
    private EditText mEditText;
    //侧滑栏按钮
    private ListView drawer_btn_list;
    private List<String> choices;
    private List<Integer> choiceIcon;
    private ListView listView;
    private List<ItemShowObj> mData;
    private MainListAdapter recyclerAdapter;
    private FloatingActionButtonPlus fab;
    private Activity mContext;
    private RecyclerViewSwipe recyclerView;
    private LinearLayoutManager manager;
    private ListAdapter adapter;
    private TextView drawer_login_btn;
    private TextView login_btn;
    private TextView cancel_btn;
    public static boolean DrawerIsOpen=false;
    private MaterialDialog login_panel;
    private EditText login_username;
    private EditText login_password;
    private String name;
    private String psd;




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

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    swipeRefreshLayout.setRefreshing(false);
                    recyclerAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus(true);
        mContext=this;
        setContentView(R.layout.activity_main);
        initData();
        //静态测试数据
        initData_Test();
        initView();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search, menu);
        MenuItem search=menu.findItem(R.id.menu_items_search);
        SearchView searchView = (SearchView)search.getActionView();
        mEditText=(SearchView.SearchAutoComplete)searchView.findViewById(R.id.search_src_text);
        mEditText.setHint(R.string.search_hint);
        return true;
    }


    private void initData() {
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

    }



    private void initView(){
        //工具栏
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        title=(TextView)findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_navicon);

        //圆形头像
        icon=(CircleImageView)findViewById(R.id.circleImageView);
        //icon.setImageDrawable(Drawable.createFromPath("/sdcard/01.jpg"));

        //侧滑实现
        drawerLayout=(DrawerLayout)findViewById(R.id.drawlayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close
        ){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                DrawerIsOpen=true;
//                Toast.makeText(getApplicationContext(),"false",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                DrawerIsOpen=false;
//                Toast.makeText(getApplicationContext(),"true",Toast.LENGTH_SHORT).show();

            }


        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        //侧滑栏按钮
        drawer_btn_list = (ListView) findViewById(R.id.btn_list);

        adapter = new com.android.john.note.MenuAdapter(this,choices,choiceIcon);
        drawer_btn_list.setAdapter(adapter);
        //下拉刷新实现
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Collections.reverse(mData);
                        try {
                            Thread.sleep(1000); //模拟耗时,测试
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHandler.sendEmptyMessage(1);
                    }
                }).start();
            }
        });
        //RecyclerView
        recyclerView=(RecyclerViewSwipe) findViewById(R.id.recycleview);
        manager = new LinearLayoutManager(getApplicationContext());
        recyclerAdapter = new MainListAdapter(mData,getApplicationContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(recyclerAdapter);



        //浮动按钮
        fab=(FloatingActionButtonPlus)findViewById(R.id.FabPlus);
        fab.setPosition(FloatingActionButtonPlus.POS_RIGHT_BOTTOM);
        fab.setAnimation(FloatingActionButtonPlus.ANIM_SCALE);
        fab.setAnimationDuration(150);
        fab.setOnItemClickListener(new FloatingActionButtonPlus.OnItemClickListener() {
            @Override
            public void onItemClick(FabTagLayout tagView, int position) {
                Toast.makeText(MainActivity.this, "Click btn" + position, Toast.LENGTH_SHORT).show();
                if(position==1){
                    Toast.makeText(MainActivity.this,"hello", Toast.LENGTH_SHORT).show();
                }
            }
        });


        login_panel=new MaterialDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.login_popup_window,null);
        login_panel.setView(view);
        drawer_login_btn=(TextView)findViewById(R.id.login);
        drawer_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_panel.show();
            }
        });
        login_btn=(TextView)findViewById(R.id.login_ok);
        cancel_btn=(TextView)findViewById(R.id.login_cancel);
        login_username=(EditText)findViewById(R.id.username);
        login_password=(EditText)findViewById(R.id.password);




    }


    private void initData_Test(){
        mData = new ArrayList<ItemShowObj>();
        ItemShowObj obj1 = new ItemShowObj("无Bug无法尽快付款减肥搞飞纷纷就能付款呢机哥","2016.11.27","如果看人家搞看人家过年公开日公开认购公开日公开认购你个人看过基坑丰富和开发及客人就分开万积分空间访客积分你就能进风口九分裤客服即可放假无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug","#FBC02D");
        mData.add(obj1);
        ItemShowObj obj2 = new ItemShowObj("无Bug","2016.11.27","无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug","#E91E63");
        mData.add(obj2);
        ItemShowObj obj3 = new ItemShowObj("无Bug","2016.11.27","无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug","#03A9F4");
        mData.add(obj3);
        ItemShowObj obj4 = new ItemShowObj("无Bug","2016.11.27","无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug","#FFEB3B");
        mData.add(obj4);
        ItemShowObj obj5 = new ItemShowObj("无Bug","2016.11.27","无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug","#FF5722");
        mData.add(obj5);
        ItemShowObj obj6 = new ItemShowObj("无Bug","2016.11.27","无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug","#00BCD4");
        mData.add(obj6);
        ItemShowObj obj7 = new ItemShowObj("无Bug","2016.11.27","无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug","#536DFE");
        mData.add(obj7);
        ItemShowObj obj8 = new ItemShowObj("无Bug","2016.11.27","无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug","#757575");
        mData.add(obj8);
        ItemShowObj obj9 = new ItemShowObj("无Bug","2016.11.27","无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug","#757575");
        mData.add(obj9);
        ItemShowObj obj10 = new ItemShowObj("无Bug","2016.11.27","无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug","#757575");
        mData.add(obj10);
        ItemShowObj obj11 = new ItemShowObj("无Bug","2016.11.27","无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug无Bug","#757575");
        mData.add(obj11);


    }
    //弹出登录窗两个按钮的执行函数
    public void login(View view){
//        name=login_username.getText().toString();
//        psd=login_password.getText().toString();
//        if(psd==null) Log.v("psd","null");
//        else
        Toast.makeText(getApplicationContext(),"登录",Toast.LENGTH_SHORT).show();
      //  Toast.makeText(getApplicationContext(),login_password.getText().toString(),Toast.LENGTH_SHORT).show();

    }
    public void cancel(View view){

        login_panel.dismiss();
    }






}
