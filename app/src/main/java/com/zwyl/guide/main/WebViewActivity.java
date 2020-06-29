package com.zwyl.guide.main;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zwyl.guide.R;
import com.zwyl.guide.base.BaseActivity;
import com.zwyl.guide.util.MyWebViewClient;

public class WebViewActivity extends BaseActivity {

    private WebView webView;
    private ProgressDialog dialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_webview;
    }

    @Override
    protected void initView() {
        super.initView();
        setHeadView();
        String fileUri = getIntent().getStringExtra("fileUri");
        webView = (WebView) findViewById(R.id.webView);
        //WebView加载本地资源
        //        webView.loadUrl("file:///android_asset/example.html");
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true); // 关键点
        webView.getSettings().setAllowFileAccess(true); // 允许访问文件
        settings.setPluginState(WebSettings.PluginState.ON);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.getSettings().setBlockNetworkImage(false);
        //覆盖WebView默认通过第三方或者是系统浏览器打开网页的行为，使得网页可以在WebView中打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候是控制网页在WebView中去打开，如果为false调用系统浏览器或第三方浏览器打开
                view.loadUrl(url);
                return true;
            }
            //WebViewClient帮助WebView去处理一些页面控制和请求通知

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                imgReset();
            }
        });


        //WebView加载页面优先使用缓存加载
//        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //WebView加载web资源
        if (fileUri.endsWith(".jpg") || fileUri.endsWith(".png") || fileUri.endsWith(".jpeg")) {
//            String body = "<img  src=\"" + fileUri + "\"/>";
//            String html = "<html><body>" + body + "</html></body>";
//            Log.e("http", "html : " + html);
//            webView.loadDataWithBaseURL("http://ow365.cn/?i=18074&ssl=1&furl=https://yishengjiaoyu.oss-cn-beijing.aliyuncs.com/", html, "text/html", "UTF-8", null);
            StringBuffer sb = new StringBuffer();
            sb.append("<html>")
                    .append("<head>")
                    .append("<meta http-equiv='Content-Type' content='text/html'; charset='UTF-8'>")
                    .append("<style type='text/css'>")
                    .append(".response-img {max-width: 100%;}")
                    .append("#box {width: 100%;height: 100%;display: table;text-align: center;background: #fff;}")
                    .append("#box span {display: table-cell;vertical-align: middle;}")
                    .append("</style>")
                    .append("<title>")
                    .append("</title>")
                    .append("</head>")
                    .append("<body style='text-align: center;' onClick='window.myInterfaceName.showToast(\"finish Activity\")'>")
                    .append("<div id='box'>")
                    .append("<span>")
                    .append("<img src='" + fileUri + "' class='response-img' style='width: 100%'/>")
                    .append("</span>")
                    .append("</div>")
                    .append("</body>")
                    .append("</html>");
            webView.loadDataWithBaseURL(null, sb.toString(), "text/html", "UTF-8", null);
        } else
            webView.loadUrl(fileUri);

        //页面加载
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //newProgress   1-100之间的整数
                if (newProgress == 100) {
                    //页面加载完成，关闭ProgressDialog
                    closeDialog();
                } else {
                    //网页正在加载，打开ProgressDialog
                    openDialog(newProgress);
                }
            }

            private void openDialog(int newProgress) {
                if (dialog == null) {
                    dialog = new ProgressDialog(WebViewActivity.this);
                    dialog.setTitle("正在加载");
                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    dialog.setProgress(newProgress);
                    dialog.setCancelable(true);
                    dialog.show();
                } else {
                    dialog.setProgress(newProgress);
                }
            }

            private void closeDialog() {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        });
    }

    //设置顶部view显示及点击事件
    private void setHeadView() {
        setTitleCenter("预览");
        setShowLeftHead(true);//左边顶部按钮
        setShowRightHead(false);//右边顶部按钮
        setShowFilter(false);//日历筛选
        setShowLogo(false);//logo筛选
        setShowRefresh(false);//刷新
    }


    //改写物理按键——返回的逻辑
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();   //返回上一页面
                return true;
            } else {
                System.exit(0);     //退出程序
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 对图片进行重置大小，宽度就是手机屏幕宽度，高度根据宽度比便自动缩放
     **/
    private void imgReset() {
        webView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName('img'); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "var img = objs[i];   " +
                "    img.style.maxWidth = '100%'; img.style.height = '100%';  " +
                "}" +
                "})()");
    }

}