package com.zwyl.guide.util;

import android.content.Intent;


import com.zwyl.guide.App;
import com.zwyl.guide.R;
import com.zwyl.guide.base.BaseActivity;
import com.zwyl.guide.main.WebViewActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


//import com.lz.fqh.main.notice.util.DownloadManager;

/**
 * Created by yuanyueqing on 2017/7/3.
 */

public class FileDonwloadActivity extends BaseActivity {

    private String downUrl;

    @Override
    protected int getContentViewId() {
        return R.layout.ease_activity_show_file;
    }

    @Override
    protected void initView() {
        super.initView();
        Intent intent = getIntent();
        downUrl = intent.getStringExtra("downUrl");
        String fileName = intent.getStringExtra("fileName");
        startDownload(downUrl, fileName);
    }

    private void startDownload(String url, String name) {
        DownloadManager downloadManager = DownloadManager.getInstance();
        String localUrl = FileUtils.PROJECT_PATH + "/" + name;
        downloadManager.download(localUrl, url, new DownloadManager.DownloadListener() {
            @Override
            public void preDownload() {

            }

            @Override
            public void onSuccess() {
                          runOnUiThread(() -> showToast("你的文件已下载 "));
                finish();
            }

            @Override
            public void onFail(Exception e) {
                runOnUiThread(() -> showToast("下载失败"));
                finish();

            }

            @Override
            public void onUpdate(int progress) {
                //                runOnUiThread(() -> progressBar.setProgress(progress));
            }

            @Override
            public void onCacheUpdate() {

            }
        });
    }


    /**
     * 将时间戳转换为时间
     */
    public String stampToDate(long timeMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeMillis);
        return simpleDateFormat.format(date);
    }
}
