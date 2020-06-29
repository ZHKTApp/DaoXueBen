package com.zwyl.guide.viewstate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zwyl.guide.R;
import com.mayigeek.frame.view.state.ViewHttpState;

import org.jetbrains.annotations.NotNull;

/**
 * @version V1.0
 * @Description: 用来实现加载，失败情况展示的布局
 * @date 16-8-31 下午5:21
 */
public class SimpleViewHttpState implements ViewHttpState {

    @NotNull
    @Override
    public View loadingView(@NotNull ViewGroup parentView) {
        return LayoutInflater.from(parentView.getContext()).inflate(R.layout.view_loading, parentView, false);
    }

    @NotNull
    @Override
    public View errorView(@NotNull ViewGroup parentView) {
        return LayoutInflater.from(parentView.getContext()).inflate(R.layout.view_error, parentView, false);
    }

    @NotNull
    @Override
    public View emptyView(@NotNull ViewGroup parentView) {
        return LayoutInflater.from(parentView.getContext()).inflate(R.layout.view_empty, parentView, false);
    }
}
