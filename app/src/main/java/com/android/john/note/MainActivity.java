package com.android.john.note;
/*
created by john

 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.john.note_01.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzp.floatingactionbuttonplus.FabTagLayout;
import com.lzp.floatingactionbuttonplus.FloatingActionButtonPlus;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import circleimageview.CircleImageView;
import cz.msebera.android.httpclient.Header;
import me.drakeet.materialdialog.MaterialDialog;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MODE = MODE_PRIVATE;
    private static final String PERFERENCE_NAME = "NoteSetting";

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
    private SearchView mSearchView;


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
                    SharedPreferences sharedPreferences = getSharedPreferences(PERFERENCE_NAME, MODE);
                    String username = sharedPreferences.getString("username", "hehehaha");
                    if(username.equals("hehehaha")){
                        Toast.makeText(mContext, "请先登录", Toast.LENGTH_SHORT).show();
                    }else{
                        new GetImgListTask().execute("http://10.0.2.2:9091/getImg?username=" + username);
                        new GetNoteListTask().execute("http://10.0.2.2:9091/get_list?username=" + username);
                        updateUI();
                        recyclerView.invalidate();
                        Toast.makeText(mContext, "同步完成", Toast.LENGTH_SHORT).show();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    recyclerView.invalidate();
                    break;
                case 2:
                    Toast.makeText(mContext, "delete", Toast.LENGTH_SHORT).show();

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
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search, menu);
        MenuItem search = menu.findItem(R.id.menu_items_search);
        mSearchView= (SearchView) search.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(TAG, "text changed:" + newText);
                List<NoteItem> items = new ArrayList<>();
                if(newText.length() != 0){
                    try {
                        items = NoteLab.get(getApplicationContext()).queryNoteItems(newText);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    recyclerAdapter.setNoteItems(items);
                    recyclerAdapter.notifyDataSetChanged();
                }else{
                    updateUI();
                }
                return false;
            }
        });
        return true;
    }


    private void initData() {
        choices = new ArrayList<>();
        choiceIcon = new ArrayList<>();
        choices.add("同步");
        //choices.add("回收站");
        //choices.add("设置");
        choices.add("反馈");
        choices.add("帮助");
        choiceIcon.add(R.drawable.ic_drawer_sync);
      //  choiceIcon.add(R.drawable.ic_drawer_recycle);
      //  choiceIcon.add(R.drawable.ic_drawer_setting);
        choiceIcon.add(R.drawable.ic_drawer_feedback);
        choiceIcon.add(R.drawable.ic_drawer_help);

    }


    private void initView() {
        //工具栏
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_nav);

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
        drawer_btn_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(choices.get(position).equals("同步")){
//                    Toast.makeText(mContext, "同步", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getSharedPreferences(PERFERENCE_NAME, MODE);
                    String username = sharedPreferences.getString("username", "hehehaha");
                    if(username.equals("hehehaha")){
                        Toast.makeText(mContext, "请先登录", Toast.LENGTH_SHORT).show();
                    }
                    uploadNoteItems(username);
                    uploadImages(username);

                }else if(choices.get(position).equals("回收站")){
                    Toast.makeText(mContext, "回收站", Toast.LENGTH_SHORT).show();
                }else if(choices.get(position).equals("设置")){
                    Toast.makeText(mContext, "设置", Toast.LENGTH_SHORT).show();
                }else if(choices.get(position).equals("反馈")){
                    Toast.makeText(mContext, "反馈", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext, "帮助", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //下拉刷新实现
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
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
//                    i.putExtra("ic_edit_color", "note");
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
////                    i.putExtra("ic_edit_color", "note");
//                    mContext.startActivity(i);
//                }
//                  else if(position == 1){
//                    NoteItem item = new NoteItem();
//                    NoteLab.get(getApplicationContext()).addNoteItem(item);
//                    Intent i = EditActivity.newIntent(getApplicationContext());
//                    i.putExtra("UUID", item.getId());
//                    i.putExtra("ic_edit_color", "photo");
//                    mContext.startActivity(i);
//                }else{
//                    NoteItem item = new NoteItem();
//                    NoteLab.get(getApplicationContext()).addNoteItem(item);
//                    Intent i = EditActivity.newIntent(getApplicationContext());
//                    i.putExtra("UUID", item.getId());
//                    i.putExtra("ic_edit_color", "handwriting");
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
//                Toast.makeText(MainActivity.this, username + ":" + passwd, Toast.LENGTH_SHORT).show();
                String passwdMD5 = null;
                try {
                    passwdMD5 = MD5Utils.getMD5(passwd);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                String request = "http://10.0.2.2:9091/login?username=" + username + "&passwd=" + passwdMD5;
                Log.i(TAG, "url:" + request);
                new LoginTask().execute(request);
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

        // create the folder for images
        File dir = new File(getApplicationContext().getFilesDir(), "Images");
        System.out.println("directory:" + getApplicationContext().getFilesDir());
        if (!dir.exists()) {
            System.out.println("isSuccess?" + dir.mkdirs());
        }
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
            Log.i(TAG, "adapter null");
            recyclerAdapter = new MainListAdapter(items, getApplicationContext());
            recyclerView.setAdapter(recyclerAdapter);
        } else {
            Log.i(TAG, "adapter not null");
            recyclerAdapter.setNoteItems(items);
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                result = new WebFetcher().getUrlString(params[0]);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i(TAG, "login status:" + s);
            if(s.equals("true")){
                SharedPreferences sharedPreferences = getSharedPreferences(PERFERENCE_NAME, MODE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", username);
                editor.commit();
                drawer_login_btn.setText(username);
                login_panel.dismiss();
            }
        }
    }

    private void do_signIn(String username, String passwd) throws NoSuchAlgorithmException {
        String passwdMD5 = MD5Utils.getMD5(passwd);
        String url = "http://10.0.2.2:9091/add_user?username=" +
                username + "&passwd=" + passwdMD5;
        new SignInTask().execute(url);
    }

    private class SignInTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                result = new WebFetcher().getUrlString(params[0]);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.i(TAG, "login status:" + s);
            if(s.equals("success")){
                SharedPreferences sharedPreferences = getSharedPreferences(PERFERENCE_NAME, MODE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", username);
                editor.commit();
            }
        }
    }

    public void uploadNoteItems(String username){
        List<NoteItem> items = NoteLab.get(getApplicationContext()).getNoteItems();
        for(NoteItem item : items){
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("uuid", item.getId().toString());
            params.put("username", username);
            params.put("title", item.getTitle());
            params.put("content", item.getContent());
            params.put("color", item.getColor());
            params.put("date", item.getFormatDateString());
            client.post("http://10.0.2.2:9091/putInfo", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.i(TAG, "put info success");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.i(TAG, "put info failed");
                }
            });
        }
        Toast.makeText(mContext, "同步完成", Toast.LENGTH_SHORT).show();
    }

    public void uploadImages(String username){
        String imagePath = getApplicationContext().getFilesDir() + "/Images/";
        File root = new File(imagePath);
        File[] files = root.listFiles();
        for(File f : files){
            Log.i(TAG, f.getName());
            byte[] img = readImages(f);
            AsyncHttpClient client = new AsyncHttpClient();
            String imgStr = Base64.encodeToString(img, 0, img.length, Base64.DEFAULT);
            RequestParams params = new RequestParams();
            params.put("imgContent", imgStr);
            params.put("imgName", f.getName());
            params.put("username", username);
            client.post("http://10.0.2.2:9091/putImage", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.i(TAG, "success");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.i(TAG, "failure");
                }
            });
        }
    }

    private byte[] readImages(File file){
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            int length = fileInputStream.available();
            byte[] buffer = new byte[length];
            fileInputStream.read(buffer);
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class GetNoteListTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                result = new WebFetcher().getUrlString(params[0]);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONArray jsonArray = new JSONArray(s);
                for(int i = 0; i<jsonArray.length(); i++){
                    JSONObject object = jsonArray.getJSONObject(i);
                    String uuid = object.getString("note_uuid");
                    String username = object.getString("username");
                    String title = object.getString("title");
                    String content = object.getString("content");
                    String color = object.getString("color");
                    String date = object.getString("datetime");
//                    Log.i(TAG, "uuid:" + uuid);
//                    Log.i(TAG, "username:" + username);
                    Log.i(TAG, "title:" + title);
//                    Log.i(TAG, "content:" + content);
//                    Log.i(TAG, "color:" + color);
//                    Log.i(TAG, "date:" + date);
                    NoteItem res = NoteLab.get(getApplicationContext()).getNoteItem(UUID.fromString(uuid));
                    if(res != null){
                        NoteLab.get(getApplicationContext()).removeNoteItemById(uuid);
                    }
                    NoteItem item = new NoteItem(UUID.fromString(uuid));
                    item.setTitle(title);
                    item.setContent(content);
                    item.setColor(color);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日,kk:mm");
                    item.setDate(simpleDateFormat.parse(date));
                    NoteLab.get(getApplicationContext()).addNoteItem(item);
                    updateUI();
                }
            }catch(JSONException e){
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    private class GetImgListTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                result = new WebFetcher().getUrlString(params[0]);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONArray jsonArray = new JSONArray(s);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject object = jsonArray.getJSONObject(i);
//                    String username = object.getString("username");
                    String filename = object.getString("imgname");
                    Log.i(TAG, "img name:" + filename);
                    String img = object.getString("imgcontent");
                    byte[] bitmapArray;
                    bitmapArray = Base64.decode(img, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
                    saveImage(getApplicationContext(), bitmap, filename);
                }
            }catch(JSONException e){
                e.printStackTrace();
            }

        }
    }

    private String saveImage(Context context, Bitmap bitmap, String filename) {
        File dir = new File(context.getFilesDir(), "Images");
//        System.out.println("directory:" + context.getFilesDir());
//        if (!dir.exists()) {
//            System.out.println("isSuccess?" + dir.mkdirs());
//        }
//        String filename = "IMG_" + System.currentTimeMillis() + ".jpg";
        File file = new File(dir, filename);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        String imagePath = context.getFilesDir() + "/Images/" + filename;
//        Log.i(TAG, "saved file path:" + imagePath);
        return imagePath;
    }
}

