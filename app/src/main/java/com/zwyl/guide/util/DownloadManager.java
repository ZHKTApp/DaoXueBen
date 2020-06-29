package com.zwyl.guide.util;

import android.content.Context;
import android.os.Environment;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.os.Environment.MEDIA_MOUNTED;

public class DownloadManager {
    private static DownloadManager mDownload;

    private DownloadManager() {
        mClient = new OkHttpClient.Builder().build();
    }

    public static DownloadManager getInstance() {
        if (mDownload == null) {
            synchronized ("DownloadManager") {
                if (mDownload == null)
                    mDownload = new DownloadManager();
            }
        }
        return mDownload;
    }

//    /**
//     * 获取下载文件目录
//     *
//     * @param ctx
//     * @return /sdcard/attach/
//     */
//    public String getDownloadDir(Context ctx) {
//        String externalStorageState;
//        try {
//            externalStorageState = Environment.getExternalStorageState();
//        } catch (Exception e) {// (sh)it happens too (Issue #989)
//            externalStorageState = "";
//        }
//
//        if (MEDIA_MOUNTED.equals(externalStorageState) && PermissionMUtil.checkMPermission_WriteExtStorage(ctx)) {
//            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "attach" + File.separator + EMClient.getInstance().getCurrentUser() + File.separator;
//            File file = new File(path);
//            if (file.exists()) return path;
//            if (file.mkdirs()) return path;
//            throw new RuntimeException("创建文件失败, 请查看SD卡剩余空间");
//        } else {
//            throw new RuntimeException("SD卡状态有误, 或者没有写SD卡权限");
//        }
//    }

    /**
     * 获取下载长度
     *
     * @param downloadUrl
     * @return
     */
    private long getContentLength(String downloadUrl) {
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        try {
            Response response = mClient.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                long contentLength = response.body().contentLength();
                response.close();
                return contentLength == 0 ? 0 : contentLength;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private OkHttpClient mClient;//OKHttpClient;

    public void download(final String localUrl, final String remoteUrl, final DownloadListener listener) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
//                    File cacheFile = new File(localUrl);
//                    if (!cacheFile.exists()) {
//                        cacheFile.getParentFile().mkdirs();
//                        cacheFile.createNewFile();
//                    }
                    File cacheFile = FileUtils.getFilePath(Constant.PACKAGE_NAME, localUrl);
                    long readSize = cacheFile.length();
                    long mediaLength = getContentLength(remoteUrl);
                    if (mediaLength == readSize) {
                        listener.onSuccess();
                        return;
                    }
                    boolean delete = cacheFile.delete();
                    readSize = 0;
                    Request request = new Request.Builder()
                            //确定下载的范围,添加此头,则服务器就可以跳过已经下载好的部分
                            .addHeader("RANGE", "bytes=" + readSize + "-" + getContentLength(remoteUrl))
                            .url(remoteUrl)
                            .build();

                    Call call = mClient.newCall(request);

                    InputStream is = null;
                    FileOutputStream out = null;

                    Response response = call.execute();
                    is = response.body().byteStream();
                    out = new FileOutputStream(cacheFile);
                    byte[] buffer = new byte[4 * 2048];//缓冲数组8kB
                    int len;
                    long lastReadSize = 0;
                    listener.preDownload();
                    while ((len = is.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                        readSize += len;
                        if ((readSize - lastReadSize) > 1000 * 1024 * 2) {
                            lastReadSize = readSize;
                            listener.onCacheUpdate();
                        }
                        listener.onUpdate((int) (readSize * 100 / mediaLength));
                    }
                    listener.onSuccess();
                    out.flush();
                    is.close();
                    out.close();
                } catch (IOException e) {
                    listener.onFail(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public interface DownloadListener {
        void preDownload();

        void onSuccess();

        void onFail(Exception e);

        void onUpdate(int progress);

        void onCacheUpdate();
    }
}
