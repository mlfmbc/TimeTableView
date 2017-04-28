package com.shallcheek.timetale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by chang on 2017/4/27.
 */

public class SaveUtil {
    public static void SaveBitmapByView(final Context mContext, final ScrollView scrollView, final String path, int index) {
        // 创建对应大小的bitmap
        Bitmap bitmap = Bitmap.createBitmap(scrollView.getWidth(), scrollView.getChildAt(0).getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
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
        String fileName = System.currentTimeMillis() + ".png";
        File file = new File(appDir, fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(mContext,"保存失败",Toast.LENGTH_SHORT).show();
        }
        try {
            if (null != out) {
                newbmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
            String f="file:" +file.getAbsolutePath();
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(f)));
            Toast.makeText(mContext,"保存成功___"+index,Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(mContext,"保存失败",Toast.LENGTH_SHORT).show();
        }

        if(index%2==0){
            Log.e("隐藏",""+index);
            scrollView.findViewById(R.id.main_timetable_ly).setVisibility(View.GONE);
        }else{
            Log.e("显示",""+index);
            scrollView.findViewById(R.id.main_timetable_ly).setVisibility(View.VISIBLE);
        }
        index=index+1;
        final int finalIndex = index;
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                if(finalIndex <100){
                    SaveBitmapByView(mContext,scrollView,path, finalIndex);
                }else{
                    Toast.makeText(mContext,"处理完了"+ finalIndex +"个",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public static int get(final Context mContext,  ScrollView scrollView, final String path, int index) {
        // 创建对应大小的bitmap
        Bitmap bitmap = Bitmap.createBitmap(scrollView.getWidth(), scrollView.getChildAt(0).getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
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
        String fileName =index + ".png";
        File file = new File(appDir, fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
//            Toast.makeText(mContext,"保存失败",Toast.LENGTH_SHORT).show();
        }
        try {
            if (null != out) {
                newbmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
            String f="file:" +file.getAbsolutePath();
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(f)));
//            Toast.makeText(mContext,"保存成功___"+index,Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
//            Toast.makeText(mContext,"保存失败",Toast.LENGTH_SHORT).show();
        }
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        index=index+1;
return index;
    }
    public static void RxSave(final Context mContext, final ScrollView scrollView, final String path, int index){
        Integer [] files = {index};
//        Observable<Integer> myObservable = Observable.create(
//                new Observable.OnSubscribe<Integer>() {
//                    @Override
//                    public void call(Subscriber<? super Integer> sub) {
//                        sub.onNext(index);
//                        sub.onCompleted();
//                    }
//                }
//        );
        Observable.from(files)
//                .flatMap(new Func1<Integer, Observable<Integer>>() {
//                    @Override public Observable<Integer> call(Integer file) {
//                        return Observable.from(file);
//                    }
//                })
//                .filter(new Func1<Integer, Boolean>() {
//                    @Override public Boolean call(Integer file) {
//                        return file.getName().endsWith(".png"); } }
//                )
//                .observeOn(AndroidSchedulers.mainThread())
        .map(new Func1<Integer, Integer>() {
                    @Override public Integer call(Integer file) {
//                        Toast.makeText(mContext,"保存成图片"+index,Toast.LENGTH_SHORT).show();
                        return get(mContext,scrollView,path,file);
                    } }
                )
                .subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(final Integer index) {
                        Log.e("RxSave","____"+index);

                        if(index <50){
                            if(index%2==0){
                                Log.e("隐藏",""+index);
                                scrollView.findViewById(R.id.main_timetable_ly).setVisibility(View.GONE);
                            }else{
                                Log.e("显示",""+index);
                                scrollView.findViewById(R.id.main_timetable_ly).setVisibility(View.VISIBLE);
                            }
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                  int  next_index=index+1;
                                    RxSave(mContext,scrollView,path,next_index);
                                }
                            });
                        }else{
                            Toast.makeText(mContext,"处理完了"+ index +"个",Toast.LENGTH_SHORT).show();
                        }


                    }
                });

//                .map(new Func1<Integer, Observable<Integer> >() {
//            @Override public Observable<Integer>  call(Integer file) {
//                Toast.makeText(mContext,"修改图片"+file,Toast.LENGTH_SHORT).show();
//                return createData(mContext,scrollView,file);
//            } })
//
//                .subscribe(
//                        new Action1< Observable<Integer>>() {
//                    @Override public void call( Observable<Integer> index) {
//
//                        index.subscribe(new Action1<Integer>() {
//                            @Override
//                            public void call(Integer integer) {
//                                Log.e("RxSave","____"+integer);
//                                integer=integer+1;
//                                RxSave(mContext,scrollView,path,integer);
//                            }
//                        });
//                    }
//                }
//                );


//        Observable observable = ob.compose(result)
//                .doOnSubscribe(new Action0() {
//                    @Override
//                    public void call() {
//                        //显示Dialog和一些其他操作
//                        subscriber.showProgressDialog();
//                    }
//                });
//        observable.subscribe(subscriber);
    }

    private static Observable<Integer> createData(final Context mContext,final ScrollView scrollView, final Integer index) {

        return  Observable.create(
                new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(final Subscriber<? super Integer> sub) {
                                                if(index%2==0){
                            Log.e("隐藏",""+index);
                            scrollView.findViewById(R.id.main_timetable_ly).setVisibility(View.GONE);
                        }else{
                            Log.e("显示",""+index);
                            scrollView.findViewById(R.id.main_timetable_ly).setVisibility(View.VISIBLE);
                        }

                        final int finalIndex = index;
                        if(index <100){
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
//                              int   next= index+1;

                                    sub.onNext(index);
                                    sub.onCompleted();
////                                    SaveBitmapByView(mContext,scrollView,path, finalIndex);

                            }
                        });
                        }else{
                            Toast.makeText(mContext,"处理完了"+ index +"个",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );


    }
}
