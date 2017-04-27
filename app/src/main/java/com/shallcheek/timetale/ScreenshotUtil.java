package com.shallcheek.timetale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 截图操作
 * Created by Shall on 2015-07-22.
 */
public class ScreenshotUtil {

    private final static String FILE_SAVEPATH =
            Environment
            .getExternalStorageDirectory().getAbsolutePath()+"" ;
    public static String pathfile = FILE_SAVEPATH + "/ScreenshotUtil.png";
    public static int h = 0;

    /**
     * 因为课表是可以滑动 的所以截取
     * 截取scrollview的屏幕
     **/
    public static void getBitmapByView(final Context mContext, final ScrollView scrollView) {
        // 获取listView实际高度
        h = 0;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
            scrollView.getChildAt(i).setBackgroundResource(android.R.color.white);
        }
        // 创建对应大小的bitmap
        Bitmap bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);




        Bitmap newbmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        cv.drawBitmap(bitmap, 0, 0, null);
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        cv.restore();// 存储
        //回收
        bitmap.recycle();





//        Bitmap head = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.share_term_table_header);
//        Bitmap foot = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.share_term_table_footer);
//        Bitmap v = toConformBitmap(head, bitmap, foot);

        File appDir = new File(Environment.getExternalStorageDirectory(), "迈迈Mec");//迈迈
        if (!appDir.exists()) {
            appDir.mkdir();
        }

//        File savedir = new File(FILE_SAVEPATH);
//        if (!savedir.exists()) {
//            savedir.mkdirs();
//        }
//        pathfile=FILE_SAVEPATH+"/"+System.currentTimeMillis() + ".png";
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
            String mUri="";
            // 其次把文件插入到系统图库
//            try {
//                 mUri=MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
//                        file.getAbsolutePath(), fileName, null);
//                Log.e("111>>>>>>",""+file.getAbsolutePath()+"____"+fileName+"______"+mUri);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }

//            if(hasKitkat()){
//                MediaScannerConnection.scanFile(mContext,
//                        new String[] { appDir.getAbsolutePath() }, new String[]{ "mmm/*" },
//                        new MediaScannerConnection.OnScanCompletedListener() {
//                            public void onScanCompleted(String path, Uri uri) {
//                                mContext.sendBroadcast(new Intent(android.hardware.Camera.ACTION_NEW_PICTURE, uri));
//                                mContext.sendBroadcast(new Intent("com.android.camera.NEW_PICTURE", uri));
//                            }
//                        });
//                scanPhotos(file.getAbsolutePath(), mContext); // 实际起作用的方法
//            }else{
//                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
//            }

String f="file:" +file.getAbsolutePath();
            // 最后通知图库更新
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(f)));
//            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));

            //发广播告诉相册有图片需要更新，这样可以在图册下看到保存的图片了
//            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            Uri uri = Uri.fromFile(file);
//            intent.setData(uri);
//            mContext.sendBroadcast(intent);
            Log.e(">>>>>>",""+file.getAbsolutePath()+"____"+file.getName()+"---"+f);
            Toast.makeText(mContext,"保存成功___"+mUri,Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(mContext,"保存失败",Toast.LENGTH_SHORT).show();
        }

    }
    public static void scanPhotos(String filePath, Context context) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }
    public static boolean hasKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }
    /**
     * 合并图片
     *
     * @param head
     * @param kebiao
     * @param san
     * @return
     */
    public static Bitmap toConformBitmap(Bitmap head, Bitmap kebiao, Bitmap san) {
        if (head == null) {
            return null;
        }
        int headWidth = head.getWidth();
        int kebianwidth = kebiao.getWidth();
        int fotwid = san.getWidth();

        int headHeight = head.getHeight();
        int kebiaoheight = kebiao.getHeight();
        int footerheight = san.getHeight();
        //生成三个图片合并大小的Bitmap
        Bitmap newbmp = Bitmap.createBitmap(kebianwidth, headHeight + kebiaoheight + footerheight, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        cv.drawBitmap(head, 0, 0, null);// 在 0，0坐标开始画入headBitmap

        //因为手机不同图片的大小的可能小了 就绘制白色的界面填充剩下的界面
        if (headWidth < kebianwidth) {
            System.out.println("绘制头");
            Bitmap ne = Bitmap.createBitmap(kebianwidth - headWidth, headHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(ne);
            canvas.drawColor(Color.WHITE);
            cv.drawBitmap(ne, headWidth, 0, null);
        }
        cv.drawBitmap(kebiao, 0, headHeight, null);// 在 0，headHeight坐标开始填充课表的Bitmap
        cv.drawBitmap(san, 0, headHeight + kebiaoheight, null);// 在 0，headHeight + kebiaoheight坐标开始填充课表的Bitmap
        //因为手机不同图片的大小的可能小了 就绘制白色的界面填充剩下的界面
        if (fotwid < kebianwidth) {
            System.out.println("绘制");
            Bitmap ne = Bitmap.createBitmap(kebianwidth - fotwid, footerheight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(ne);
            canvas.drawColor(Color.WHITE);
            cv.drawBitmap(ne, fotwid, headHeight + kebiaoheight, null);
        }
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        cv.restore();// 存储
        //回收
        head.recycle();
        kebiao.recycle();
        san.recycle();
        return newbmp;
    }


}
