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
import android.widget.ImageButton;

import com.android.john.note_01.R;

import java.io.ByteArrayOutputStream;

public class HandwritingActivity extends AppCompatActivity {

    private ImageButton mPenImageButton;
    private ImageButton mRubberImageButton;
    private ImageButton mUndoImageButton;
    private ImageButton mSaveImageButton;
    private ImageButton mClearImageButton;
    private HandwritingView mHandwritingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handwriting);
        setHideStatus();
        mHandwritingView = (HandwritingView) findViewById(R.id.hand_drawing_view);
        mPenImageButton = (ImageButton) findViewById(R.id.handwriting_pen);
        mRubberImageButton = (ImageButton) findViewById(R.id.handwriting_rubber);
        mUndoImageButton = (ImageButton) findViewById(R.id.handwriting_undo);
        mSaveImageButton = (ImageButton) findViewById(R.id.handwriting_save);
        mClearImageButton = (ImageButton) findViewById(R.id.handwriting_clear);
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
    }



    private void setHideStatus() {
        Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
