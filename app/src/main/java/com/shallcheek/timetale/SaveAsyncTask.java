package com.shallcheek.timetale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by chang on 2017/4/28.
 */

public class SaveAsyncTask extends AsyncTask<Integer, Integer, String> {

    //    private TextView textView;
    private ScrollView scrollView;
    private Bitmap bitmap;
    private int index;
    private Context mContext;
    private String path;

    public SaveAsyncTask(Context mContext, Bitmap bitmap, ScrollView scrollView, int index, String path) {
        super();
        this.bitmap = bitmap;
        this.scrollView = scrollView;
        this.index = index;
        this.mContext = mContext;
        this.path = path;
    }


    /**
     * 这里的Integer参数对应AsyncTask中的第一个参数
     * 这里的String返回值对应AsyncTask的第三个参数
     * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
     * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
     */
    @Override
    protected String doInBackground(Integer... params) {

        Bitmap newbmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        cv.drawBitmap(bitmap, 0, 0, null);
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        cv.restore();// 存储
        //回收
        bitmap.recycle();
        File appDir = new File(Environment.getExternalStorageDirectory(), path);//迈迈
        if (!appDir.exists()) {
            appDir.mkdir();
        }
//        System.currentTimeMillis()
        String fileName = index + ".png";
        File file = new File(appDir, fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "保存失败";
//            Toast.makeText(mContext,"保存失败",Toast.LENGTH_SHORT).show();
        }
        try {
            if (null != out) {
                newbmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
            String f = "file:" + file.getAbsolutePath();
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(f)));
//            Toast.makeText(mContext,"保存成功___"+index,Toast.LENGTH_SHORT).show();
            return "保存成功";
        } catch (IOException e) {
//            Toast.makeText(mContext,"保存失败",Toast.LENGTH_SHORT).show();
            return "保存失败";
        }
    }


    /**
     * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
     * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
     */
    @Override
    protected void onPostExecute(String result) {
        if (index < 50) {
            if (index % 2 == 0) {
                Log.e("隐藏", "" + index);
                scrollView.findViewById(R.id.main_timetable_ly).setVisibility(View.GONE);
            } else {
                Log.e("显示", "" + index);
                scrollView.findViewById(R.id.main_timetable_ly).setVisibility(View.VISIBLE);
            }
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    index = index + 1;
                    onSaveAsyncTask(mContext, scrollView, index, path);
                }
            });
        } else {
            Toast.makeText(mContext, "处理完了" + index + "个", Toast.LENGTH_SHORT).show();
        }
    }


    //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
    @Override
    protected void onPreExecute() {
//        textView.setText("开始执行异步线程");
    }


    /**
     * 这里的Intege参数对应AsyncTask中的第二个参数
     * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
     * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        int vlaue = values[0];
    }

    public static void onSaveAsyncTask(Context mContext, ScrollView scrollView, int index, String path) {
        Bitmap bitmap = Bitmap.createBitmap(scrollView.getWidth(), scrollView.getChildAt(0).getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        new SaveAsyncTask(mContext, bitmap, scrollView, index, path).execute();
    }
}