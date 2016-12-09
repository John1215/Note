package com.android.john.note;
/*
created by john

 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.UUID;

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
    private List<NoteItem> mNoteItems;
    private MainListAdapter recyclerAdapter;
    private FloatingActionButton fab;
    private Activity mContext;
    private RecyclerViewSwipe recyclerView;
    private LinearLayoutManager manager;
    private ListAdapter adapter;
    private TextView drawer_login_btn;
    private TextView login_btn;
    private TextView cancel_btn;
    public static boolean DrawerIsOpen = false;
    private MaterialDialog login_panel;
    private EditText login_username;
    private EditText login_password;
    private String username;
    private String passwd;


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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(MainActivity.this, "顾隽逸是我儿子", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
//                    recyclerAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTranslucentStatus(true);
        mContext = this;
        initData();
        //静态测试数据
//        initData_Test();
        initView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search, menu);
        MenuItem search = menu.findItem(R.id.menu_items_search);
        SearchView searchView = (SearchView) search.getActionView();
        mEditText = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
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


    private void initView() {
        //工具栏
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_navicon);

        //圆形头像
        icon = (CircleImageView) findViewById(R.id.circleImageView);
        //icon.setImageDrawable(Drawable.createFromPath("/sdcard/01.jpg"));

        //侧滑实现
        drawerLayout = (DrawerLayout) findViewById(R.id.drawlayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close
        ) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                DrawerIsOpen = true;
//                Toast.makeText(getApplicationContext(),"false",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                DrawerIsOpen = false;
//                Toast.makeText(getApplicationContext(),"true",Toast.LENGTH_SHORT).show();

            }


        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        //侧滑栏按钮
        drawer_btn_list = (ListView) findViewById(R.id.btn_list);

        adapter = new com.android.john.note.MenuAdapter(this, choices, choiceIcon);
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
        recyclerView = (RecyclerViewSwipe) findViewById(R.id.recycleview);
        manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);

//        recyclerAdapter = new MainListAdapter(NoteLab.get(getApplicationContext()).getNoteItems(), getApplicationContext());
//        recyclerView.setAdapter(recyclerAdapter);
        updateUI();

        //浮动按钮
        fab = (FloatingActionButton) findViewById(R.id.fab);
       // fab.setPosition(FloatingActionButtonPlus.POS_RIGHT_BOTTOM);
       // fab.setAnimation(FloatingActionButton.ANIM_SCALE);
     //   fab.setAnimationDuration(150);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteItem item = new NoteItem();
                NoteLab.get(getApplicationContext()).addNoteItem(item);
                Intent i = EditActivity.newIntent(getApplicationContext());
                i.putExtra("UUID", item.getId());
//                    i.putExtra("tag", "note");
                mContext.startActivity(i);
            }
        });
//        fab.setVisibility(View.VISIBLE);
//        fab.setOnItemClickListener(new FloatingActionButtonPlus.OnItemClickListener() {
//            @Override
//            public void onItemClick(FabTagLayout tagView, int position) {
//                Toast.makeText(MainActivity.this, "Click btn" + position, Toast.LENGTH_SHORT).show();
//                if (position == 0) {
//                    //new note
//                    NoteItem item = new NoteItem();
//                    NoteLab.get(getApplicationContext()).addNoteItem(item);
//                    Intent i = EditActivity.newIntent(getApplicationContext());
//                    i.putExtra("UUID", item.getId());
////                    i.putExtra("tag", "note");
//                    mContext.startActivity(i);
//                }
//                  else if(position == 1){
//                    NoteItem item = new NoteItem();
//                    NoteLab.get(getApplicationContext()).addNoteItem(item);
//                    Intent i = EditActivity.newIntent(getApplicationContext());
//                    i.putExtra("UUID", item.getId());
//                    i.putExtra("tag", "photo");
//                    mContext.startActivity(i);
//                }else{
//                    NoteItem item = new NoteItem();
//                    NoteLab.get(getApplicationContext()).addNoteItem(item);
//                    Intent i = EditActivity.newIntent(getApplicationContext());
//                    i.putExtra("UUID", item.getId());
//                    i.putExtra("tag", "handwriting");
//                    mContext.startActivity(i);
//                }
//            }
//        });


        login_panel = new MaterialDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.login_popup_window, null);
        login_panel.setView(view);
        TextView textView = (TextView) view.findViewById(R.id.login_ok);
        TextView cancelView = (TextView) view.findViewById(R.id.login_cancel);
        login_username = (EditText) view.findViewById(R.id.username);
        login_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                username = editable.toString();
            }
        });
        login_password = (EditText) view.findViewById(R.id.password);
        login_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                passwd = editable.toString();
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),"登录",Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, username + ":" + passwd, Toast.LENGTH_SHORT).show();
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_panel.dismiss();
            }
        });


        drawer_login_btn = (TextView) findViewById(R.id.login);
        drawer_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_panel.show();

            }
        });
        login_btn = (TextView) findViewById(R.id.login_ok);
        cancel_btn = (TextView) findViewById(R.id.login_cancel);
    }


    @Override
    protected void onResume() {
        super.onResume();

        updateUI();
    }

    private void updateUI() {
        NoteLab lab = NoteLab.get(getApplicationContext());
        List<NoteItem> items = lab.getNoteItems();

        if (recyclerAdapter == null) {
            recyclerAdapter = new MainListAdapter(items, getApplicationContext());
            recyclerView.setAdapter(recyclerAdapter);
        } else {
            recyclerAdapter.setNoteItems(items);
            recyclerAdapter.notifyDataSetChanged();
        }
    }
}

