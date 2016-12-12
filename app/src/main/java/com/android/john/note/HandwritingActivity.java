package com.android.john.note;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;

import com.android.john.note.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

public class HandwritingActivity extends AppCompatActivity {

    private ImageButton mPenImageButton;
    private ImageButton mRubberImageButton;
    private ImageButton mUndoImageButton;
    private ImageButton mSaveImageButton;
    private ImageButton mClearImageButton;
    private ImageButton mColorButton;
    private HandwritingView mHandwritingView;
    private ListAdapter adapter;
    private GridView gridView;
    private List<String> color_icon;
    private MaterialDialog ColorChoosePanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handwriting);
        setHideStatus();
        initData();




        adapter = new com.android.john.note.ColorButtonAdapter(this, color_icon);
        gridView = new MyGridView(this);
        gridView.setNumColumns(3);
        gridView.setVerticalSpacing(120);
        gridView.setPadding(0, 40, 0, 40);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Toast.makeText(getApplicationContext(), color_grid_adapter.getItem(i).toString(), Toast.LENGTH_SHORT).show();

                chooseColor(adapter.getItem(i).toString());
                ColorChoosePanel.dismiss();

            }
        });
        ColorChoosePanel = new MaterialDialog(this);

        ColorChoosePanel.setContentView(gridView);
        ColorChoosePanel.setCanceledOnTouchOutside(true);




        mHandwritingView = (HandwritingView) findViewById(R.id.hand_drawing_view);
        mPenImageButton = (ImageButton) findViewById(R.id.handwriting_pen);
        mRubberImageButton = (ImageButton) findViewById(R.id.handwriting_rubber);
        mUndoImageButton = (ImageButton) findViewById(R.id.handwriting_undo);
        mSaveImageButton = (ImageButton) findViewById(R.id.handwriting_save);
        mClearImageButton = (ImageButton) findViewById(R.id.handwriting_clear);
        mColorButton=(ImageButton)findViewById(R.id.handwriting_color);
        mPenImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandwritingView.setPen();
            }
        });
        mRubberImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandwritingView.setRubber();
            }
        });
        mUndoImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandwritingView.undo();
            }
        });
        mSaveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                Bitmap bitmap = mHandwritingView.getBitmap();
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte [] bitmapByte =baos.toByteArray();
                data.putExtra("bitmap", bitmapByte);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        mClearImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandwritingView.deleteAll();
            }
        });
        mColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorChoosePanel.show();
            }
        });
    }



    private void setHideStatus() {
        Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
    private void chooseColor(String color) {


        switch (color) {
            case "red":

                mHandwritingView.chooseColor(0xffff5252);
                break;
            case "cyan":

                mHandwritingView.chooseColor(0xff00BCD4);
                break;
            case "green":

                mHandwritingView.chooseColor(0xff4CAF50);
                break;
            case "blue":

                mHandwritingView.chooseColor(0xff03A9F4);
                break;
            case "purple":

                mHandwritingView.chooseColor(0xff673AB7);
                break;
            case "orange":

                mHandwritingView.chooseColor(0xffFF9800);
                break;
            default:
                mHandwritingView.chooseColor(0xffffffff);
        }
    }
}
