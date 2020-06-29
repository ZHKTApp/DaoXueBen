package com.zwyl.guide.util;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Administrator on 2018/9/7 0007.
 */
public class MediaUtils
{
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static File file;

    /**
     *创建用于保存图像或视频的文件URI
     */
    public static Uri getOutputMediaFileUri(Context context, int type)
    {
        Uri uri = null;
        //适配Android N
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", getOutputMediaFile(type));
        } else
        {
            return Uri.fromFile(getOutputMediaFile(type));
        }
    }

    /**
     * 创建保存图像或视频的文件
     */
    public static File getOutputMediaFile(int type)
    {
//        为了安全起见，您应该检查SD卡是否已安装。
        //在执行此操作之前，使用Enguly.GutExalStReAgAgestEATE（）。
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "image");
        // 如果希望创建的图像共享，则此位置最有效。


        // 在应用程序之间并在卸载应用程序后继续执行。
        // 如果存储目录不存在，则创建存储目录
        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                return null;
            }
        }
        // 创建媒体文件名
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else
        {
            return null;
        }
        file = mediaFile;
        return mediaFile;
    }

    /**
     * 获取视频的第一帧图片
     */
    public static void getImageForVideo(String videoPath, OnLoadVideoImageListener listener)
    {
        LoadVideoImageTask task = new LoadVideoImageTask(listener);
        task.execute(videoPath);
    }

    public static class LoadVideoImageTask extends AsyncTask<String, Integer, File>
    {
        private OnLoadVideoImageListener listener;

        public LoadVideoImageTask(OnLoadVideoImageListener listener)
        {
            this.listener = listener;
        }

        @Override
        protected File doInBackground(String... params)
        {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            String path = params[0];
            if (path.startsWith("http"))
                //获取网络视频第一帧图片
                mmr.setDataSource(path, new HashMap());
            else
                //本地视频
                mmr.setDataSource(path);
            Bitmap bitmap = mmr.getFrameAtTime();
            //保存图片
            File f = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (f.exists())
            {
                f.delete();
            }
            try
            {
                FileOutputStream out = new FileOutputStream(f);
                if(bitmap==null){
                    return null;
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            mmr.release();
            return f;
        }

        @Override
        protected void onPostExecute(File file)
        {
            super.onPostExecute(file);
            if (listener != null)
            {
                listener.onLoadImage(file);
            }
        }
    }

    public interface OnLoadVideoImageListener
    {
        void onLoadImage(File file);
    }
}
