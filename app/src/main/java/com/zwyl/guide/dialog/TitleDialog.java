package com.zwyl.guide.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

import com.zwyl.guide.R;

/**
 * 头像选择对话框 拍照 or 相册
 */
public class TitleDialog extends Dialog {

    public TitleDialog(Activity activity, String title, OnclickListener listener) {
        super(activity, R.style.dialog);
        View view = View.inflate(activity, R.layout.dialog_title, null);
        setContentView(view);
        //title
        TextView tv_titledialog_title = (TextView) view.findViewById(R.id.tv_titledialog_title);
        tv_titledialog_title.setText(title);
        //底部关闭按钮
        view.findViewById(R.id.tv_titledialog_cancle).setOnClickListener(v -> {
            dismiss();
            listener.OnCancle();
        });
        //底部提交按钮
        view.findViewById(R.id.tv_titledialog_sure).setOnClickListener(v -> {
            listener.OnSure();
            dismiss();
        });
    }

    public interface OnclickListener {
        void OnSure();
        void OnCancle();
    }

}

