package com.android.john.note;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.john.note_01.R;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

public class EditActivity extends AppCompatActivity implements View.OnTouchListener {
    //状态栏隐藏
    private void setHideStatus() {
        Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    private ImageButton color_chose;
    private List<String> color_icon;
    private ListAdapter color_grid_adapter;
    private GridView gridView;
    private MaterialDialog ColorChoosePanel;
    private TextView color_tag;
    private RelativeLayout relativeLayout;
    private TextView time;

    //手指向右滑动时的最小速度
    private static final int XSPEED_MIN = 200;

    //手指向右滑动时的最小距离
    private static final int XDISTANCE_MIN = 150;

    //记录手指按下时的横坐标。
    private float xDown;

    //记录手指移动时的横坐标。
    private float xMove;

    //用于计算手指滑动的速度。
    private VelocityTracker mVelocityTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHideStatus();
        setContentView(R.layout.activity_edit);
        initData();
        initView();
        relativeLayout=(RelativeLayout)findViewById(R.id.edit_main);
        relativeLayout.setOnTouchListener(this);
    }
    private void initData() {
        color_icon = new ArrayList<>();
        color_icon.add("red");
        color_icon.add("cyan");
        color_icon.add("blue");
        color_icon.add("green");
        color_icon.add("orange");
        color_icon.add("purple");
    }

    private void initView() {
        //颜色条
        color_tag=(TextView)findViewById(R.id.edit_color);
        //颜色选择面板GridView
        color_grid_adapter = new com.android.john.note.ColorButtonAdapter(this, color_icon);
        gridView = new MyGridView(this);
        gridView.setNumColumns(3);
        gridView.setVerticalSpacing(120);
        gridView.setPadding(0, 40, 0, 40);
        gridView.setAdapter(color_grid_adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), color_grid_adapter.getItem(i).toString(), Toast.LENGTH_SHORT).show();
                changeColor(color_grid_adapter.getItem(i).toString());
                ColorChoosePanel.dismiss();

            }
        });
        //颜色选择面板弹出
        ColorChoosePanel = new MaterialDialog(this);
       // ColorChoosePanel.setCanceledOnTouchOutside(true);
        // ColorChoosePanel.setTitle("颜色");
        ColorChoosePanel.setContentView(gridView);
        //底部ImageButton
        color_chose = (ImageButton) findViewById(R.id.edit_color_choose);
        color_chose.setElevation(2);
        color_chose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorChoosePanel.show();
            }
        });
        time=(TextView)findViewById(R.id.edit_time);
        time.setText("修改时间：2016年12月7日");

    }

    private void changeColor(String color) {
        int id = 0;
        switch (color) {
            case "red":
                id=R.color.red;
                break;
            case "cyan":
                id=R.color.cyan;
                break;
            case "green":
                id=R.color.green;
                break;
            case "blue":
                id=R.color.blue;
                break;
            case "purple":
                id=R.color.purple;
                break;
            case "orange":
                id=R.color.orange;
                break;
            default:id=R.color.red;

        }
        color_tag.setBackgroundResource(id);
    }

    private void changeActivity(){
        startActivity(new Intent(EditActivity.this, MainActivity.class));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        createVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = event.getRawX();
                //活动的距离
                int distanceX = (int) (xMove - xDown);
                //获取瞬时速度
                int xSpeed = getScrollVelocity();
                //当滑动的距离大于我们设定的最小距离且滑动的瞬间速度大于我们设定的速度时，返回到上一个activity
                if(distanceX > XDISTANCE_MIN && xSpeed > XSPEED_MIN) {
                    Toast.makeText(this,"返回",Toast.LENGTH_SHORT).show();
                    //finish();
                    changeActivity();

                }
                break;
            case MotionEvent.ACTION_UP:
                recycleVelocityTracker();
                break;
            default:
                break;
        }
        return true;
    }

    //创建VelocityTracker对象，并将触摸content界面的滑动事件加入到VelocityTracker当中。
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

   // 回收VelocityTracker对象。

    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    /**
     * 获取手指在content界面滑动的速度。
     *
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }


}
