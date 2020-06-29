package com.zwyl.guide.main;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zwyl.guide.App;
import com.zwyl.guide.R;
import com.zwyl.guide.base.BaseActivity;
import com.zwyl.guide.base.adapter.CommonAdapter;
import com.zwyl.guide.base.adapter.MultiItemTypeAdapter;
import com.zwyl.guide.base.adapter.ViewHolder;
import com.zwyl.guide.http.ApiUtil;
import com.zwyl.guide.main.detaile.TextDetaileActivity;
import com.zwyl.guide.service.UserService;
import com.zwyl.guide.viewstate.SpaceItemDecoration;
import com.zwyl.guide.viewstate.ViewControlUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class TextbookActivity extends BaseActivity {

    @BindView(R.id.textbook_recyclerview)
    RecyclerView textbookRecyclerview;
    private List mlist = new ArrayList<BeanTextBookGrid>();
    private CommonAdapter mAdapter;
    private String academicYearId;
    private String schoolSubjectId;
    private String subjectName;
    private String cmStudentId;
    private UserService api;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_textbook;
    }

    @Override
    protected void initView() {
        super.initView();
        //设置顶部按钮事件,日历筛选
        setHeadView();
        //获取主界面传过来的数据
        Intent intent = getIntent();
        academicYearId = intent.getStringExtra("academicYearId");
        schoolSubjectId = intent.getStringExtra("schoolSubjectId");
        subjectName = intent.getStringExtra("subjectName");
        cmStudentId = intent.getStringExtra("cmStudentId");
        //Gridview内容
        GridLayoutManager layoutManager = new GridLayoutManager(App.mContext, 4, OrientationHelper.VERTICAL, false);
        textbookRecyclerview.setLayoutManager(layoutManager);
        int spacingInPixels = 34;
        textbookRecyclerview.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        textbookRecyclerview.setAdapter(mAdapter = new CommonAdapter<BeanTextBookGrid>(App.mContext, R.layout.item_textbook_grid, mlist) {
            @Override
            protected void convert(ViewHolder holder, BeanTextBookGrid bean, int position) {
                holder.setText(R.id.tv_item_textbook_name, subjectName);//科目
                holder.setText(R.id.tv_item_textbook_class, bean.schoolEducationGradeName + (bean.updownType ? "上册" : "下册"));//年级
                holder.setText(R.id.tv_item_textbook_creat, bean.schoolPublisherName);//出版社
                // holder.setImageDrawable(R.id.iv_item_textbook, bean.image);
            }
        });
    }

    @Override
    protected void initControl() {
        super.initControl();
        //条目点击
        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Intent intent = createIntent(TextDetaileActivity.class);
                intent.putExtra("textBookId", ((BeanTextBookGrid) mlist.get(position)).schoolTextBookId);
                intent.putExtra("schoolSubjectId", schoolSubjectId);
                intent.putExtra("schoolTextBookName", ((BeanTextBookGrid) mlist.get(position)).schoolTextBookName);
                intent.putExtra("cmStudentId", cmStudentId);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        api = ApiUtil.createDefaultApi(UserService.class, cmStudentId);
        ApiUtil.doDefaultApi(api.selectTextBook(academicYearId, schoolSubjectId), data -> {
            if (data.size() > 0) {
                mAdapter.setDatas(data);
            }
        }, ViewControlUtil.create2Dialog(this));

    }

    /**
     * 设置顶部点击事件
     */
    private void setHeadView() {
        setTitleCenter(subjectName);
        setShowLeftHead(true);//左边顶部按钮
        setShowRightHead(false);//右边顶部按钮
        setShowFilter(false);//日历筛选
        setShowLogo(false);//logo筛选
        setShowRefresh(true);//刷新
        setRefreshClick(v -> {
            ApiUtil.doDefaultApi(api.selectTextBook(academicYearId, schoolSubjectId), data -> {
                if (data.size() > 0) {
                    mAdapter.setDatas(data);
                }
            }, ViewControlUtil.create2Dialog(this));
        });
    }
}

