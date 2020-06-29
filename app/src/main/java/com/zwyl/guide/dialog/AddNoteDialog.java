package com.zwyl.guide.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.EditText;

import com.zwyl.guide.R;

/**
 * 头像选择对话框 拍照 or 相册
 */
public class AddNoteDialog extends Dialog {

    public AddNoteDialog(Activity activity, OnClickListener listener) {
        super(activity, R.style.dialog);
        View view = View.inflate(activity, R.layout.dialog_addnote, null);
        setContentView(view);
        EditText et_text = (EditText) view.findViewById(R.id.et_text);
        //顶部关闭按钮
        view.findViewById(R.id.tv_dialog_close).setOnClickListener(v -> {
            dismiss();
        });
        //底部关闭按钮
        view.findViewById(R.id.tv_dialog_cancle).setOnClickListener(v -> {
            dismiss();
        });
        //底部提交按钮
        view.findViewById(R.id.tv_dialog_save).setOnClickListener(v -> {
            listener.onClick(et_text.getText().toString());
            dismiss();
        });
    }

    public interface OnClickListener {
        void onClick(String etString);
    }

}

