package com.zwyl.guide.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zwyl.guide.R;
import com.zwyl.guide.main.detaile.BeanNote;

import java.util.List;

/**
 * 头像选择对话框 拍照 or 相册
 */
public class ShowNoteDialog extends Dialog {
    private Activity activity;

    public ShowNoteDialog(Activity activity, List<BeanNote> data, OnClickListener listener) {
        super(activity, R.style.dialog);
        this.activity = activity;
        View view = View.inflate(activity, R.layout.dialog_shownote, null);
        LinearLayout ll_dialogNote = (LinearLayout) view.findViewById(R.id.ll_dialogNote);
        ll_dialogNote.removeAllViews();
        for(int i = 0; i < data.size(); i++) {
            BeanNote beanNote = data.get(i);
            View noteItem = getNoteItem(beanNote);
            ll_dialogNote.addView(noteItem);
        }
        setContentView(view);
        //顶部关闭按钮
        view.findViewById(R.id.tv_dialog_close).setOnClickListener(v -> {
            dismiss();
        });
        //底部关闭按钮
        view.findViewById(R.id.tv_dialog_cancle).setOnClickListener(v -> {
            dismiss();
        });
        //底部提交按钮
        view.findViewById(R.id.iv_add).setOnClickListener(v -> {
            listener.onClick();
            dismiss();
        });
    }

    private View getNoteItem(BeanNote beanNote) {
        View noteItem = View.inflate(activity, R.layout.item_note, null);
        TextView tv_item_note_content = (TextView) noteItem.findViewById(R.id.tv_item_note_content);
        TextView tv_item_note_time = (TextView) noteItem.findViewById(R.id.tv_item_note_time);
        tv_item_note_content.setText(beanNote.noteContent);
        tv_item_note_time.setText(beanNote.createTime);
        return noteItem;
    }

    public interface OnClickListener {
        void onClick();
    }

}

