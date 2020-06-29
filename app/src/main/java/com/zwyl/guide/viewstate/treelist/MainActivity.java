package com.zwyl.guide.viewstate.treelist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;


import com.zwyl.guide.R;
import com.zwyl.guide.base.BaseActivity;

public class MainActivity extends BaseActivity {
    private List<FileBean> mDatas = new ArrayList<FileBean>();
    private ListView mTree;
    private TreeListViewAdapter mAdapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_treelist;
    }

    @Override
    protected void initView() {
        super.initView();
        mTree = (ListView) findViewById(R.id.id_tree);
        try {

            mAdapter = new SimpleTreeAdapter<FileBean>(mTree, this, mDatas, 10);
            mTree.setAdapter(mAdapter);
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
        mAdapter.setOnTreeNodeClickListener((node, position) -> {
            if(node.isLeaf()) {
                showToast(node.getName());
            }
        });

    }

    @Override
    protected void initData() {
        super.initData();
//        mDatas.add(new FileBean(12, 0, "文件管理系统",null));
//        mDatas.add(new FileBean(2, 12, "游戏",null));
//        mDatas.add(new FileBean(3, 12, "文档",null));
//        mDatas.add(new FileBean(4, 1, "程序",null));
//        mDatas.add(new FileBean(5, 2, "war3",null));
//        mDatas.add(new FileBean(6, 2, "刀塔传奇",null));
//
//        mDatas.add(new FileBean(7, 4, "面向对象",null));
//        mDatas.add(new FileBean(8, 4, "非面向对象",null));
//
//        mDatas.add(new FileBean(9, 7, "C++",null));
//        mDatas.add(new FileBean(10, 7, "JAVA",null));
//        mDatas.add(new FileBean(11, 7, "Javascript",null));
//        mDatas.add(new FileBean(12, 8, "C",null));
        mAdapter.refreshData(mDatas, 0);
        mAdapter.notifyDataSetChanged();
    }


}