package com.android.john.note;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deer-Apple on 2016/12/8.
 */

public class HandwritingView extends View {
    private static final String TAG = HandwritingView.class.getSimpleName();

    public boolean isRubber = false;

    private Path mCurrentPath;
    private myPath mCurrentMyPath;
    private List<myPath> mPaths = new ArrayList<>();
    private Paint mBoxPaint;
    private Paint mRubberPaint;
    private Paint mBackgroundPaint;
    private Bitmap mBitmap;
    private static int colorCode = Color.BLACK;
    private Rect mSrcRect, mDestRect;
    private boolean delete_flag =false;

    //Used when creating the view in the code
    public HandwritingView(Context context) {
        super(context);
    }

    public void chooseColor(int color) {
        colorCode = color;
        String tmp = String.valueOf(colorCode);
     //   Toast.makeText(getContext(), tmp, Toast.LENGTH_SHORT).show();
        mBoxPaint.setColor(colorCode);
    }

    //Used when inflating the view from xml
    public HandwritingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBoxPaint = new Paint();
        mBoxPaint.setAntiAlias(true);
        mBoxPaint.setStrokeWidth(50);
        mBoxPaint.setDither(true);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setAntiAlias(true);
        mBoxPaint.setStrokeJoin(Paint.Join.ROUND);
        mBoxPaint.setStrokeCap(Paint.Cap.ROUND);
        mBitmap=Bitmap.createBitmap(10,10, Bitmap.Config.ARGB_8888);
        mRubberPaint = new Paint();
        mRubberPaint.setColor(0xffffffff);
        mRubberPaint.setAntiAlias(true);
        mRubberPaint.setStrokeWidth(50);
        mRubberPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xffffffff);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        PointF current = new PointF(event.getX(), event.getY());
        String action = "";

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                mCurrentPath = new Path();
                mCurrentPath.moveTo(current.x, current.y);
                //  mCurrentPath.lineTo(current.x,current.y);
                mCurrentMyPath = new myPath(mCurrentPath, isRubber, mBoxPaint.getColor());
                if(mPaths.size()==25){
                    mBitmap=getBitmap();
                    invalidate();
                    mPaths.remove(0);
                }
                mPaths.add(mCurrentMyPath);
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                if (mCurrentPath != null) {
                    mCurrentPath.cubicTo(current.x, current.y, current.x, current.y, current.x, current.y);
                    // mCurrentPath.quadTo(current.x, current.y, current.x, current.y);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                mCurrentPath = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                mCurrentPath = null;
                break;
        }

        Log.i(TAG, action + " at x=" + current.x + ", at y=" + current.y);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(delete_flag){
            canvas.drawPaint(mBackgroundPaint);
            mPaths.clear();
            mBitmap=Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
            //mBitmap.setPixel(getWidth(),getHeight(),Color.WHITE);
            delete_flag=false;
        }
        mSrcRect =new Rect(0,0,getWidth(),getHeight());
        mDestRect=new Rect(0,0,getWidth(),getHeight());
        canvas.drawBitmap(mBitmap,mSrcRect,mDestRect,null);

       //

        for (myPath path : mPaths) {
            if (path.isRubber()) {
                canvas.drawPath(path.getPath(), mRubberPaint);
            } else {
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStrokeWidth(50);
                paint.setDither(true);
                paint.setStyle(Paint.Style.STROKE);
                paint.setAntiAlias(true);
                paint.setStrokeJoin(Paint.Join.ROUND);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setColor(path.getColor());
                canvas.drawPath(path.getPath(), paint);
            }
            //            canvas.drawPath(path, mBoxPaint);
        }
    }


    public void setRubber() {
        isRubber = true;
    }

    public void setPen() {
        isRubber = false;
    }

    public void undo() {
        if (!mPaths.isEmpty()) {
            mPaths.remove(mPaths.size() - 1);
            invalidate();
        }
    }

    public void deleteAll() {
        mPaths.clear();
        delete_flag=true;
        invalidate();
    }

    public void saveDrawing(Context context) {
        Bitmap bitmap = getBitmap();
        File dir = new File(context.getFilesDir(), "Bitmap");
        System.out.println("directory:" + context.getFilesDir());
        if (!dir.exists()) {
            System.out.println("isSuccess?" + dir.mkdirs());
        }
        String filename = System.currentTimeMillis() + ".jpg";
        File file = new File(dir, filename);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
        draw(canvas);
        return bitmap;
    }

    public class myPath {
        private Path mPath;
        private boolean mIsRubber;
        private int mColor;

        public myPath(Path path, boolean flag, int color) {
            mPath = path;
            mIsRubber = flag;
            mColor = color;
        }

        public Path getPath() {
            return mPath;
        }

        public boolean isRubber() {
            return mIsRubber;
        }

        public int getColor() {
            return mColor;
        }
    }

}
