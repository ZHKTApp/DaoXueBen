package com.zwyl.guide.main.detaile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mayigeek.frame.http.state.HttpSucess;
import com.zwyl.guide.App;
import com.zwyl.guide.R;
import com.zwyl.guide.base.BaseActivity;
import com.zwyl.guide.base.ComFlag;
import com.zwyl.guide.base.adapter.CommonAdapter;
import com.zwyl.guide.base.adapter.MultiItemTypeAdapter;
import com.zwyl.guide.base.adapter.ViewHolder;
import com.zwyl.guide.dialog.AddNoteDialog;
import com.zwyl.guide.dialog.ShowNoteDialog;
import com.zwyl.guide.dialog.bean.BeanAllYear;
import com.zwyl.guide.dialog.popwindow.PopupWindowA;
import com.zwyl.guide.dialog.popwindow.PopupWindowB;
import com.zwyl.guide.dialog.popwindow.PopupWindowC;
import com.zwyl.guide.http.ApiUtil;
import com.zwyl.guide.main.BeanHomeGrid;
import com.zwyl.guide.main.BeanTextBookGrid;
import com.zwyl.guide.main.WebViewActivity;
import com.zwyl.guide.main.subject.SubjectActivity;
import com.zwyl.guide.service.UserService;
import com.zwyl.guide.util.Constant;
import com.zwyl.guide.util.DensityUtil;
import com.zwyl.guide.util.DownloadManager;
import com.zwyl.guide.util.FileUtils;
import com.zwyl.guide.util.MediaUtils;
import com.zwyl.guide.util.MyProgress;
import com.zwyl.guide.util.SharedPrefsUtil;
import com.zwyl.guide.util.Utils;
import com.zwyl.guide.viewstate.ViewControlUtil;
import com.zwyl.guide.viewstate.treelist.FileBean;
import com.zwyl.guide.viewstate.treelist.Node;
import com.zwyl.guide.viewstate.treelist.SimpleTreeAdapter;
import com.zwyl.guide.viewstate.treelist.TreeListViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *
 */
public class TextDetaileActivity extends BaseActivity {

    @BindView(R.id.tv_textclass)
    TextView tvTextclass;

    @BindView(R.id.detaile_recyclerview)
    RecyclerView detaileRecyclerview;
    List mlist = new ArrayList<BeanCatelog>();
    List<BeanDetaile> mlistDetaile = new ArrayList<BeanDetaile>();
    @BindView(R.id.iv_detaile_select)
    ImageView ivDetaileSelect;
    @BindView(R.id.ll_detaile_select)
    LinearLayout llDetaileSelect;
    @BindView(R.id.catalog_lisitview)
    ListView catalogLisitview;
    private CommonAdapter cutelogAdapter;
    private CommonAdapter detaileAdapter;
    private int postionTag = -1;
    private String textBookId;
    private List<FileBean> mDatas = new ArrayList<FileBean>();
    private TreeListViewAdapter mAdapter;
    private UserService api;
    private List<BeanAllYear> years;
    private String timeasc = ComFlag.DESC;
    private int timeAscTag=0;//0代表降序,1代表升序
    private boolean catelogIsclick;
    private String clickId;//目录是否点击,有值点击.无值未点击,默认未点击(为了刷新详情时判断刷新默认说句还是选中后的数据 )
    private String schoolSubjectId;
    private String schoolTextBookName;
    private String stuId;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_textdetaile;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            getcatelogList(textBookId);
        }
        if (detaileAdapter != null) {
            getDefaltDetaileData();
        }
    }

    @Override
    protected void initView() {
        super.initView();
        ivDetaileSelect.setImageResource(R.mipmap.selecte);
        textBookId = getIntent().getStringExtra("textBookId");
        schoolSubjectId = getIntent().getStringExtra("schoolSubjectId");
        schoolTextBookName = getIntent().getStringExtra("schoolTextBookName");
        stuId = SharedPrefsUtil.get(this, Constant.STU_ID, "");
        setHeadView();
        //目录列表
        try {
            mAdapter = new SimpleTreeAdapter<FileBean>(catalogLisitview, this, mDatas, 10);
            catalogLisitview.setAdapter(mAdapter);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        //目录点击事件
        mAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
            @Override
            public void onClick(Node node, int position) {
//                if (node.isLeaf()) {
                    clickId = node.getClickId();
                    Log.e("http", "clickid : " + clickId);
                    TextDetaileActivity.this.getLeftItemclickData(clickId);
//                    catelogIsclick = true;
//                }
            }
        });
        //详情列表
        LinearLayoutManager linearLayoutManagerDetaile = new LinearLayoutManager(App.mContext, OrientationHelper.VERTICAL, false);
        detaileRecyclerview.setLayoutManager(linearLayoutManagerDetaile);
        detaileRecyclerview.setAdapter(detaileAdapter = new CommonAdapter<BeanDetaile>(App.mContext, R.layout.item_detaile, mlistDetaile) {
            @Override
            protected void convert(ViewHolder holder, BeanDetaile beanDetaile, int position) {
                String guideSoureId = beanDetaile.guideSoureId;
                holder.setText(R.id.tv_item_detaile_name, beanDetaile.sourceName);
                holder.setText(R.id.tv_item_detaile_time, "[" + beanDetaile.teacherName + "]" + beanDetaile.createTime + "创建");
                ImageView iv_item_detaile_img = holder.getView(R.id.iv_item_detaile_img);

                //修改
//                UIUtils.setCenterCrop(beanDetaile.filePreviewUri, iv_item_detaile_img);
                String name = beanDetaile.filePreviewUri.substring(beanDetaile.filePreviewUri.lastIndexOf("."));
                String fileName = beanDetaile.fileUri.substring(beanDetaile.fileUri.lastIndexOf("/") + 1);

                boolean isDownload = FileUtils.isFilesCreate(ComFlag.PACKAGE_NAME, beanDetaile.sourceName);
//                if (name.equals(".jpg"))
//                    Glide.with(App.mContext).load(beanDetaile.fileUri).into(iv_item_detaile_img);
//                else if (name.equals(".mp4")) {
//                MediaUtils.getImageForVideo(beanDetaile.fileUri, new MediaUtils.OnLoadVideoImageListener() {
//                    @Override
//                    public void onLoadImage(File file) {
//                        iv_item_detaile_img.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
//                    }
//                });
//                }
//                else
                iv_item_detaile_img.setImageResource(Utils.getBackPic(name));
                TextView tv_item_texting = holder.getView(R.id.tv_item_texting);
                TextView tv_item_note = holder.getView(R.id.tv_item_note);
                TextView tv_item_down = holder.getView(R.id.tv_item_down);
                MyProgress progressBar = holder.getView(R.id.progressBar);


                if (isDownload) {
                    tv_item_down.setSelected(true);
                    tv_item_down.setText("已下载");
                } else {
                    tv_item_down.setSelected(false);
                    tv_item_down.setText("下载");
                    //下载
                    tv_item_down.setOnClickListener(v -> {
                        //修改
                        showToast("开始下载");
                        FileUtils.creatDownFile();
                        progressBar.setVisibility(View.VISIBLE);
                        String fileUri = mlistDetaile.get(position).fileUri;
//                        String fileName = fileUri.substring(fileUri.lastIndexOf("/") + 1);
                        String downUrl = fileUri.substring(fileUri.lastIndexOf("=") + 1);
                        startDownload(downUrl, beanDetaile.sourceName, progressBar, guideSoureId, tv_item_down);//TODO 换成上面的url

//                        ApiUtil.doDefaultApi(api.downloadGuideBook(guideSoureId), new HttpSucess<String>() {
//                            @Override
//                            public void onSucess(String data) {
//                                showToast("开始下载");
//                                tv_item_down.setText("已下载");
//                                tv_item_down.setBackgroundResource(R.drawable.drawable_gray_bg);
//                                FileUtils.creatDownFile();
//                                progressBar.setVisibility(View.VISIBLE);
//                                String fileUri = mlistDetaile.get(position).fileUri;
//                                String fileName = fileUri.substring(fileUri.lastIndexOf("/") + 1);
//                                String downUrl = fileUri.substring(fileUri.lastIndexOf("=") + 1);
//                                startDownload(downUrl, fileName, progressBar);//TODO 换成上面的url
//                            }
//                        }, ViewControlUtil.create2Dialog(TextDetaileActivity.this));

                    });
                }
                /*笔记*/
                tv_item_note.setOnClickListener(v -> {
                    ApiUtil.doDefaultApi(api.selectSourceNoteBook(guideSoureId), data -> {
                        //显示note
                        new ShowNoteDialog(TextDetaileActivity.this, data, () -> {//添加笔记按钮点击
                            //添加note
                            new AddNoteDialog(TextDetaileActivity.this, etString -> {//保存按钮点击
                                ApiUtil.doDefaultApi(api.insertGuideNoteBook(guideSoureId, etString), new HttpSucess<String>() {
                                    @Override
                                    public void onSucess(String data1) {
                                        showToast("保存成功");
                                    }
                                }, ViewControlUtil.create2Dialog(TextDetaileActivity.this));

                            }).show();
                        }).show();
                    }, ViewControlUtil.create2Dialog(TextDetaileActivity.this));

                });
                if (beanDetaile.isExerciseExit) {
                    tv_item_texting.setSelected(false);
                    tv_item_texting.setClickable(true);
                    //测试
                    tv_item_texting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ApiUtil.doDefaultApi(api.addlog(beanDetaile.guideSoureId, ComFlag.NumFlag.WORKTYPE_GUIDE_TYPE, beanDetaile.cmTeacherId, textBookId), data -> {
                            });
                            Intent intent = createIntent(SubjectActivity.class);
                            intent.putExtra("guideSoureId", guideSoureId);
                            intent.putExtra("workPushLogId", beanDetaile.workPushLogId);
                            startActivity(intent);
                        }
                    });
                } else {
                    tv_item_texting.setSelected(true);
                    tv_item_texting.setClickable(false);
                }

                iv_item_detaile_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mUrl = beanDetaile.filePreviewUri;
                        //修改
                        if (mUrl.endsWith(".jpg") || mUrl.endsWith(".png") || mUrl.endsWith(".jpeg")) {
                            fileUri = beanDetaile.fileUri;
                        } else {
                            fileUri = beanDetaile.filePreviewUri;
                        }
                        String fileName = fileUri.substring(fileUri.lastIndexOf("/") + 1);
                        if (TextUtils.isEmpty(fileName)) {
                            showToast("预览文件不存在");
                            return;
                        }
                        String openPath = FileUtils.getFilePath(ComFlag.PACKAGE_NAME, fileName).getAbsolutePath();
                        boolean files = FileUtils.getFilesbolen(fileName);
                        if (files) {
                            FileUtils.openFile(App.mContext, new File(openPath));
                        } else {
                            if (openPath.endsWith(".mp4") || openPath.endsWith(".flv") || openPath.endsWith(".mp3")) {
                                Intent intent = new Intent(TextDetaileActivity.this, PlayActivity.class);
                                String fileUri1 = fileUri.substring(fileUri.lastIndexOf("=") + 1);
                                intent.putExtra("fileUri", fileUri1);
                                intent.putExtra("fileName", beanDetaile.sourceName);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(TextDetaileActivity.this, WebViewActivity.class);
                                intent.putExtra("fileUri", fileUri);
                                startActivity(intent);
                            }
                        }
                    }
                });


            }
        });

//        detaileAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
//                //修改
//                String mUrl = mlistDetaile.get(position).filePreviewUri;
//                if (mUrl.endsWith(".jpg") || mUrl.endsWith(".png") || mUrl.endsWith(".jpeg")) {
//                    fileUri = mlistDetaile.get(position).fileUri;
//                } else {
//                    fileUri = mlistDetaile.get(position).filePreviewUri;
//                }
//                String fileName = fileUri.substring(fileUri.lastIndexOf("/") + 1);
//                if (TextUtils.isEmpty(fileName)) {
//                    showToast("预览文件不存在");
//                    return;
//                }
////                String openPath = FileUtils.PROJECT_PATH + "/" + fileName;
//                String openPath = FileUtils.getFilePath(ComFlag.PACKAGE_NAME, fileName).getAbsolutePath();
//                boolean files = FileUtils.getFilesbolen(fileName);
//                if (files) {
//                    FileUtils.openFile(App.mContext, new File(openPath));
//                } else {
//                    if (openPath.endsWith(".mp4") || openPath.endsWith(".flv")) {
//                        Intent intent = new Intent(TextDetaileActivity.this, PlayActivity.class);
//                        String fileUri1 = fileUri.substring(fileUri.lastIndexOf("=") + 1);
//                        intent.putExtra("fileUri", fileUri1);
//                        startActivity(intent);
//                    } else {
//                        Intent intent = new Intent(TextDetaileActivity.this, WebViewActivity.class);
//                        intent.putExtra("fileUri", fileUri);
//                        startActivity(intent);
//                    }
//                }
//
//            }
//
//            @Override
//            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
//                return false;
//            }
//        });

    }

    String fileUri;

    @Override
    protected void initData() {
        super.initData();
        api = ApiUtil.createDefaultApi(UserService.class, stuId);
        //左边目录列表
        getcatelogList(textBookId);
        getDefaltDetaileData();


        //获取所有学年列表
        ApiUtil.doDefaultApi(api.allYearsByStudentId(null), new HttpSucess<List<BeanAllYear>>() {
            @Override
            public void onSucess(List<BeanAllYear> data) {
                years = data;
            }
        }, ViewControlUtil.create2Dialog(this));

    }

    private void getDefaltDetaileData() {
        //默认请求详情列表
        ApiUtil.doDefaultApi(api.selectGuideBookByTextBookId(textBookId, timeasc), new HttpSucess<List<BeanDetaile>>() {
            @Override
            public void onSucess(List<BeanDetaile> data) {
                detaileAdapter.setDatas(data);
                ivDetaileSelect.setImageResource(R.mipmap.selecte);
            }
        }, ViewControlUtil.create2Dialog(this));
    }

    private void getcatelogList(String BookId) {
        //左边目录列表
//        ApiUtil.doDefaultApi(api.selectTextBookChapter(textBookId), new HttpSucess<List<BeanCatelog>>() {
//            @Override
//            public void onSucess(List<BeanCatelog> data) {
//                mDatas.clear();
//                for (int i = 0; i < data.size(); i++) {
//                    BeanCatelog beanCatelog = data.get(i);
//                    FileBean fileBean = new FileBean(Integer.parseInt(beanCatelog.textBookChapterId), Integer.parseInt(beanCatelog.textBookChapterParentId), beanCatelog.textBookChapterName, beanCatelog.textBookChapterId);
//                    mDatas.add(fileBean);
//                }
//                mAdapter.refreshData(mDatas, 1);
//                mAdapter.notifyDataSetChanged();
//            }
//        }, ViewControlUtil.create2Dialog(this));
        ApiUtil.doDefaultApi(api.selectTextBookChapter(textBookId), new HttpSucess<List<ResultInfoBean>>() {
            @Override
            public void onSucess(List<ResultInfoBean> data) {
                mDatas.clear();
                traveTree(data);
                mAdapter.refreshData(mDatas, 1);
                mAdapter.notifyDataSetChanged();
            }
        }, ViewControlUtil.create2Dialog(this));
    }

    private void traveTree(List<ResultInfoBean> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            ResultInfoBean resultInfoBean = dataList.get(i);
            FileBean fileBean = new FileBean(resultInfoBean.getTextBookChapterId(), resultInfoBean.getTextBookChapterParentId(), resultInfoBean.getTextBookChapterName(), resultInfoBean.getTextBookChapterId() + "");
            mDatas.add(fileBean);
            traveTree(dataList.get(i).getChildrenList());
        }
    }

    //点击事件
    @OnClick({R.id.ll_detaile_select, R.id.detaile_recyclerview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_detaile_select:
                showPopWindow(ComFlag.PopFlag.TITLE);
                break;
        }
    }

    //设置顶部view显示及点击事件
    private void setHeadView() {
        setTitleCenter(schoolTextBookName);
        tvTextclass.setText(schoolTextBookName);
        setShowLeftHead(true);//左边顶部按钮
        setShowRightHead(true);//右边顶部按钮
        setShowFilter(false);//日历筛选
        setShowLogo(false);//logo筛选
        setShowRefresh(true);//刷新
        setRefreshClick(v -> {//刷新点击
//            if (mAdapter != null) {
//                getcatelogList(textBookId);
//            }
            if (!TextUtils.isEmpty(clickId)) {
                if (detaileAdapter != null) {
                    getLeftItemclickData(clickId);
                }
            } else {
                getDefaltDetaileData();
            }
        });
        setTimeClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//时间点击
                if (timeAscTag == 0) {
                    head_time.setCompoundDrawablesWithIntrinsicBounds(TextDetaileActivity.this.getResources().getDrawable(R.mipmap.asc1), null, null, null);
                    timeAscTag = 1;
                    timeasc = ComFlag.ASC;
                } else {
                    head_time.setCompoundDrawablesWithIntrinsicBounds(TextDetaileActivity.this.getResources().getDrawable(R.mipmap.desc1), null, null, null);
                    timeAscTag = 0;
                    timeasc = ComFlag.DESC;
                }
                if (clickId == null) {
                    TextDetaileActivity.this.getDefaltDetaileData();
                } else {
                    TextDetaileActivity.this.getLeftItemclickData(clickId);
                }
            }
        });
        setDownloadClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//全部下载
//                TextDetaileActivity.this.showToast("全部下载");
            }
        });
    }

    private PopupWindowA mWindowA;
    private PopupWindowB mWindowB;
    private PopupWindowC mWindowC;

    //显示popwindow
    private void showPopWindow(String tag) {
        if (mWindowC != null && mWindowC.isShowing()) {
            mWindowC.dismiss();
        }
        if (mWindowB != null && mWindowB.isShowing()) {
            mWindowB.dismiss();
        }
        if (mWindowA != null && mWindowA.isShowing()) {
            mWindowA.dismiss();
            if (ComFlag.PopFlag.TITLE.equals(tag)) {
                ivDetaileSelect.setImageResource(R.mipmap.selecte);
                return;
            }
        }
        ivDetaileSelect.setImageResource(R.mipmap.selectleft);
        this.mWindowA = new PopupWindowA<BeanAllYear>(this, years, new PopupWindowA.OnClickListener() {
            @Override
            public void onClick(int position1) {
                if (mWindowB != null && mWindowB.isShowing())
                    mWindowB.dismiss();
                if (mWindowC != null && mWindowC.isShowing())
                    mWindowC.dismiss();
                TextDetaileActivity.this.getSubject(years.get(position1).academicYearId);
            }
        });
        //根据指定View定位
        PopupWindowCompat.showAsDropDown(this.mWindowA, llDetaileSelect, 0, DensityUtil.dip2px(-37), Gravity.RIGHT);
    }

    //获取所有课本目录的数据
    private void getSubject(String academicYearId) {
        ApiUtil.doDefaultApi(api.selectGradeSubejectList(academicYearId), new HttpSucess<List<BeanHomeGrid>>() {
            @Override
            public void onSucess(List<BeanHomeGrid> data) {
                if (data.size() > 0) {
                    mWindowB = new PopupWindowB<BeanHomeGrid>(TextDetaileActivity.this, data, new PopupWindowB.OnClickListener() {
                        @Override
                        public void onClick(int position2) {

                            if (mWindowC != null && mWindowC.isShowing())
                                mWindowC.dismiss();
                            if (data.size() > 0) {
                                schoolSubjectId = data.get(position2).schoolSubjectId;
                                TextDetaileActivity.this.getTextBook(academicYearId, schoolSubjectId, data.get(position2).schoolSubjectName);
                            } else {
                                TextDetaileActivity.this.showToast("没有下一级了");
                            }

                        }
                    });
                    //根据指定View定位
                    PopupWindowCompat.showAsDropDown(mWindowB, llDetaileSelect, DensityUtil.dip2px(153), DensityUtil.dip2px(-37), Gravity.RIGHT);
                } else {
                    TextDetaileActivity.this.showToast("没有下一级了");
                }
            }
        }, ViewControlUtil.create2Dialog(this));

    }

    //获取上下册数据
    private void getTextBook(String academicYearId, String schoolSubjectId, String schoolSubjectName) {
        ApiUtil.doDefaultApi(api.selectTextBook(academicYearId, schoolSubjectId), new HttpSucess<List<BeanTextBookGrid>>() {
            @Override
            public void onSucess(List<BeanTextBookGrid> data) {
                if (data.size() > 0) {
                    mWindowC = new PopupWindowC<BeanTextBookGrid>(TextDetaileActivity.this, data, new PopupWindowC.OnClickListener() {
                        @Override
                        public void onClick(int position3) {
                            TextDetaileActivity.this.getcatelogList(data.get(position3).schoolTextBookId);
                            tvTextclass.setText(data.get(position3).schoolTextBookName);
                            mWindowC.dismiss();
                            mWindowB.dismiss();
                            mWindowA.dismiss();
                            //选择上下册时也刷新详情列表
                            textBookId = data.get(position3).schoolTextBookId;
                            TextDetaileActivity.this.getDefaltDetaileData();
                        }
                    });
                    //根据指定View定位
                    PopupWindowCompat.showAsDropDown(mWindowC, llDetaileSelect, DensityUtil.dip2px(260), DensityUtil.dip2px(-37), Gravity.RIGHT);
                } else {
                    TextDetaileActivity.this.showToast("没有下一级了");
                }
            }
        }, ViewControlUtil.create2Dialog(this));
    }

    //获取目录点击后的详情数据
    private void getLeftItemclickData(String clickId) {
        ApiUtil.doDefaultApi(api.selectGuideBookByChapterId(clickId), new HttpSucess<List<BeanDetaile>>() {
            @Override
            public void onSucess(List<BeanDetaile> data) {
                detaileAdapter.setDatas(data);
                detaileAdapter.notifyDataSetChanged();
            }
        }, ViewControlUtil.create2Dialog(this));
    }

    //修改
    private void startDownload(String url, String name, ProgressBar progressBar, String guideSoureId, TextView tv_item_down) {
        DownloadManager downloadManager = DownloadManager.getInstance();
//        String localUrl = FileUtils.PROJECT_PATH + "/" + name;
        downloadManager.download(name, url, new DownloadManager.DownloadListener() {

            //        downloadManager.download(localUrl, url, new DownloadManager.DownloadListener() {
            @Override
            public void preDownload() {

            }

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_item_down.setSelected(true);
                        tv_item_down.setClickable(false);
                        tv_item_down.setText("已下载");
                        progressBar.setVisibility(View.GONE);
//                        ApiUtil.doDefaultApi(api.downloadGuideBook(guideSoureId), new HttpSucess<String>() {
//                            @Override
//                            public void onSucess(String data) {
//                                showToast("你的文件已下载 ");
//                                tv_item_down.setText("已下载");
//                                tv_item_down.setBackgroundResource(R.drawable.drawable_gray_bg);
//                            }
//                        }, ViewControlUtil.create2Dialog(TextDetaileActivity.this));
                    }
                });
            }

            @Override
            public void onFail(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("下载失败");
                    }
                });

            }

            @Override
            public void onUpdate(int progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(progress);
                    }
                });
            }

            @Override
            public void onCacheUpdate() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }


}
