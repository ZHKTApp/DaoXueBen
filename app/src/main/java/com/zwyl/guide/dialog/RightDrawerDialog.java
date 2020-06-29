package com.zwyl.guide.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.zwyl.guide.App;
import com.zwyl.guide.R;
import com.zwyl.guide.base.adapter.CommonAdapter;
import com.zwyl.guide.base.adapter.MultiItemTypeAdapter;
import com.zwyl.guide.base.adapter.ViewHolder;
import com.zwyl.guide.dialog.bean.BeanAllYear;
import com.zwyl.guide.util.DensityUtil;

import java.util.List;

/**
 * 头像选择对话框 拍照 or 相册
 */
public class RightDrawerDialog extends Dialog {
    private int postionTag = - 1;
    private CommonAdapter madapter;

    public RightDrawerDialog(Activity activity, List<BeanAllYear> list, OnClickListener listener) {
        super(activity, R.style.dialog_left);
        View view = View.inflate(activity, R.layout.dialog_right_drawer, null);

        setContentView(view, new ViewGroup.LayoutParams(DensityUtil.dip2px(166),DensityUtil.dip2px(690)));
        //并不充满全屏
        Window window = getWindow();
        if(window != null) {
            getWindow().setGravity(Gravity.RIGHT|Gravity.BOTTOM);
        }

        RecyclerView rl_recyclerview = (RecyclerView) findViewById(R.id.rl_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(App.mContext, OrientationHelper.VERTICAL, false);
        rl_recyclerview.setLayoutManager(linearLayoutManager);
        rl_recyclerview.setAdapter(madapter = new CommonAdapter<BeanAllYear>(App.mContext, R.layout.item_shaixuan_data, list) {
            @Override
            protected void convert(ViewHolder holder, BeanAllYear bean, int position) {
                holder.setText(R.id.tv_item_data, bean.academicYear);
                TextView tv_item_data = holder.getView(R.id.tv_item_data);
                if(position == postionTag) {
                    tv_item_data.setBackgroundColor(activity.getResources().getColor(R.color.c_green));
                    tv_item_data.setTextColor(Color.WHITE);
                    postionTag = position;
                } else {
                    tv_item_data.setBackgroundColor(Color.WHITE);
                    tv_item_data.setTextColor(Color.BLACK);
                }

            }
        });
        madapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            //            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                listener.onClick(position);
                postionTag = position;
                madapter.notifyDataSetChanged();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    public interface OnClickListener {
        void onClick(int position);
    }

}

