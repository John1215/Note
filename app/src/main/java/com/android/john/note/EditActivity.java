package com.android.john.note;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import com.android.john.note_01.R;

public class EditActivity extends AppCompatActivity {
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
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus(true);
        setContentView(R.layout.activity_edit);
        //工具栏
        toolbar=(Toolbar)findViewById(R.id.toolbar2);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_toolbar_back);
        setSupportActionBar(toolbar);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_save, menu);
      //  MenuItem save=menu.findItem(R.id.menu_items_save);
        return super.onCreateOptionsMenu(menu);
    }

}
