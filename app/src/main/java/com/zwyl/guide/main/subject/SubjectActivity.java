package com.zwyl.guide.main.subject;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.request.RequestOptions;
import com.zwyl.guide.App;
import com.zwyl.guide.R;
import com.zwyl.guide.base.BaseActivity;
import com.zwyl.guide.base.ComFlag;
import com.zwyl.guide.base.adapter.CommonAdapter;
import com.zwyl.guide.base.adapter.ViewHolder;
import com.zwyl.guide.customveiw.CustomLinearLayoutManager;
import com.zwyl.guide.dialog.TitleDialog;
import com.zwyl.guide.http.ApiUtil;
import com.zwyl.guide.imagewatcher.GlideSimpleLoader;
import com.zwyl.guide.imagewatcher.ImageWatcherHelper;
import com.zwyl.guide.service.UserService;
import com.zwyl.guide.util.Constant;
import com.zwyl.guide.util.SharedPrefsUtil;
import com.zwyl.guide.util.UIUtils;
import com.zwyl.guide.util.Utils;
import com.zwyl.guide.util.ViewUtil;
import com.zwyl.guide.viewstate.ViewControlUtil;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SubjectActivity extends BaseActivity {
    @BindView(R.id.rl_subject)
    RecyclerView rlSubject;
    List mlist = new ArrayList<BeanSubject>();
    List mRadio = new ArrayList<BeanAbc>();
    int[] mJudge = {R.mipmap.right, R.mipmap.close};//判断
    @BindView(R.id.tv_sunbject_save)
    TextView tvSunbjectSave;
    @BindView(R.id.ll_subject)
    LinearLayout ll_subject;
    private CustomLinearLayoutManager layoutManager;
    private String guideSoureId;
    private String workPushLogId;
    private CommonAdapter mAdapter;
    private UserService api;
    List<BeanInfo> infoList = new ArrayList<BeanInfo>();
    List<String> picturePathList = new ArrayList<String>();
    private int subjectSize;
    protected static final int REQUEST_CODE = 1;
    private String stuId;
    private boolean isGetBack = false;
    private ImageWatcherHelper iwHelper;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_subject;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean isTranslucentStatus = false;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
//            isTranslucentStatus = true;
//        }
        super.onCreate(savedInstanceState);
        iwHelper = ImageWatcherHelper.with(this, new GlideSimpleLoader()) // 一般来讲， ImageWatcher 需要占据全屏的位置
                .setTranslucentStatus(!isTranslucentStatus ? Utils.calcStatusBarHeight(SubjectActivity.this) : 0); // 如果不是透明状态栏，你需要给ImageWatcher标记 一个偏移值，以修正点击ImageView查看的启动动画的Y轴起点的不正确

    }

    @Override
    protected void initView() {
        super.initView();
        setShowFilter(false);
        setTitleCenter("测试题");
        setRefreshClick(v -> {//刷新点击
            if (mAdapter != null) {
                ApiUtil.doDefaultApi(api.selectGuideExercises(guideSoureId), data -> {
                    if (data.size() > 0 && !data.equals("")) {
                        subjectSize = data.size();
                        mAdapter.setDatas(data);
                        ll_subject.setVisibility(View.VISIBLE);
                    }
                }, ViewControlUtil.create2Dialog(this));
            }
        });
        //  webViewParticipationDetail.loadDataWithBaseURL(null, "<p>asdfasdfas<img src=\"http://qkc-oss.oss-cn-beijing.aliyuncs.com/1547541255125.jpg\" title=\"Tulips.jpg\" alt=\"Tulips.jpg\"></p>", "text/html", "UTF-8", null);
        guideSoureId = getIntent().getStringExtra("guideSoureId");
        workPushLogId = getIntent().getStringExtra("workPushLogId");
        stuId = SharedPrefsUtil.get(this, Constant.STU_ID, "");
        layoutManager = new CustomLinearLayoutManager(App.mContext);
        rlSubject.setLayoutManager(layoutManager);
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 8) {
                    picturePathList = (List<String>) msg.obj;
                    mAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        layoutManager = new CustomLinearLayoutManager(App.mContext);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setAutoMeasureEnabled(true);
        rlSubject.setLayoutManager(layoutManager);
        rlSubject.setHasFixedSize(true);
        rlSubject.setNestedScrollingEnabled(false);
        rlSubject.setAdapter(mAdapter = new CommonAdapter<BeanSubject>(App.mContext, R.layout.item_subject, mlist) {
            @Override
            protected void convert(ViewHolder holder, BeanSubject beanSubject, int position) {
                holder.setIsRecyclable(false);
                int exerciseTypeCode = beanSubject.exerciseTypeCode;
                String exercisesId = beanSubject.exercisesId;
                String exercisesTitle = beanSubject.exercisesTitle;

                LinearLayout ll_bottom = holder.getView(R.id.ll_bottom);
//                TextView tv_item_subject_title = holder.getView(R.id.tv_item_subject_title);
                TextView tv_item_subject_name = holder.getView(R.id.tv_item_subject_name);
                RelativeLayout rr_exerciseAnalysis = holder.getView(R.id.rr_exerciseAnalysis);
                View ic_answer = holder.getView(R.id.ic_answer);
                View ic_respondence = holder.getView(R.id.ic_respondence);
                View ic_exerciseAnswer = holder.getView(R.id.ic_exerciseAnswer);
                TextView tv_myanswer = holder.getView(R.id.tv_myanswer);
                TextView tv_sureanswer = holder.getView(R.id.tv_sureanswer);
                TextView tv_myanswer_str = holder.getView(R.id.tv_myanswer_str);
                WebView webView_participation_detail = holder.getView(R.id.webView_participation_detail);
                WebView webView_subject_detail = holder.getView(R.id.webView_subject_detail);
                WebView webView_participation_detail_item = holder.getView(R.id.webView_participation_detail_item);
                WebView webView_participation_detail_answer = holder.getView(R.id.webView_participation_detail_answer);
                WebView webView_participation_detail_eanswer = holder.getView(R.id.webView_participation_detail_eanswer);
                ImageView iv_participation_detail_item = holder.getView(R.id.iv_participation_detail_item);
                ImageView iv_participation_detail_item_answer = holder.getView(R.id.iv_participation_detail_item_answer);
                RelativeLayout rl_answer_ = holder.getView(R.id.rl_answer_);
                TextView tv_analysis = holder.getView(R.id.tv_analysis);
                LinearLayout ll_analysis = holder.getView(R.id.ll_analysis);
                TextView tv_answer_click = holder.getView(R.id.tv_answer_click);
                ll_bottom.removeAllViews();
                tv_item_subject_name.setText(Utils.getSubjectNo(position) + Utils.getsubjectType(exerciseTypeCode));
                if (beanSubject.isSeeAnswer) {
                    if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_RADIO || exerciseTypeCode == ComFlag.NumFlag.EXERCISE_JUDGE || exerciseTypeCode == ComFlag.NumFlag.EXERCISE_MULTIPLE) {
                        if (!TextUtils.isEmpty(beanSubject.exerciseAnswer))
                            rl_answer_.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(beanSubject.exerciseAnalysis))
                            rr_exerciseAnalysis.setVisibility(View.VISIBLE);
                        else {
                            rr_exerciseAnalysis.setVisibility(View.VISIBLE);
                            if (Utils.isPic(beanSubject.exerciseAnalysis)) {
                                iv_participation_detail_item.setVisibility(View.VISIBLE);
                                webView_participation_detail_item.setVisibility(View.GONE);
                                UIUtils.setCenterCrop(beanSubject.exerciseAnalysis, iv_participation_detail_item);
//                                webView_participation_detail_answer.loadDataWithBaseURL(null, Utils.getPicHtml(beanSubject.exerciseAnalysis), "text/html", "UTF-8", null);
                            } else {
                                iv_participation_detail_item.setVisibility(View.GONE);
                                webView_participation_detail_item.setVisibility(View.VISIBLE);
                                webView_participation_detail_item.loadDataWithBaseURL(null, beanSubject.exerciseAnalysis, "text/html", "UTF-8", null);
                            }
                        }
                    }
                    ic_answer.setVisibility(View.VISIBLE);
                } else {
                    rl_answer_.setVisibility(View.GONE);
                    rr_exerciseAnalysis.setVisibility(View.GONE);
                    ic_answer.setVisibility(View.GONE);
                }

                webView_subject_detail.loadDataWithBaseURL(null, exercisesTitle, "text/html", "UTF-8", null);

                if (!TextUtils.isEmpty(beanSubject.studentAnswerOptionId) || !TextUtils.isEmpty(beanSubject.studentAnswerFileUri)) {
                    tvSunbjectSave.setVisibility(View.GONE);
                }
                //单选
                if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_RADIO) {
                    List<BeanSubject.ExerciseOptionListBean> exerciseList = beanSubject.exerciseOptionList;
//                    if (!beanSubject.isSeeAnswer) {
//                        ll_bottom.addView(ViewUtil.getRadioGroup(exerciseList, null, new ViewUtil.OncheckedClick() {
//                            @Override
//                            public void OnChecked(int indexNum, String optionId) {
//                                deWeightRadio(exercisesId, optionId);
//                            }
//                        }));
//                        ll_bottom.addView(ViewUtil.getSingleView(exerciseList, (position + 1), new ViewUtil.OnMultipleClick() {
//                            @Override
//                            public void OnChecked(boolean isChecked, int indexNum, String optionId) {
//                                deWeightRadio(exercisesId, optionId);
//                            }
//                        }));
//                        LinearLayout view = ViewUtil.getSingleView(beanSubject.exerciseOptionList, (position + 1), new ViewUtil.OnMultipleClick() {
//                            @Override
//                            public void OnChecked(boolean isChecked, int indexNum, String optionId) {
//                                deWeightRadio(exercisesId, optionId);
//                            }
//                        });
//                        ll_bottom.addView(view);
//                        if (infoList != null && infoList.size() != 0)
//                            for (int i = 0; i < infoList.size(); i++) {
//                                for (int j = 0; j < beanSubject.exerciseOptionList.size(); j++) {
//                                    if (infoList.get(i).studentAnswer.equals(beanSubject.exerciseOptionList.get(j).optionId) && infoList.get(i).exerciseId.equals(beanSubject.exercisesId)) {
//                                        CheckBox checkBox = (CheckBox) view.getChildAt(j + 1);
//                                        checkBox.setChecked(true);
//                                    }
//                                }
//                            }
//                    }
                    if (!TextUtils.isEmpty(beanSubject.studentAnswerOptionId)) {
                        rl_answer_.setVisibility(View.VISIBLE);
                        if (beanSubject.isSeeAnswer) {
                            if (beanSubject.studentAnswerOptionId.equals(beanSubject.exerciseAnswer)) {
                                tv_myanswer.setText(beanSubject.studentAnswerOptionId);
                                tv_myanswer.setTextColor(getResources().getColor(R.color.black));
                            } else {
                                tv_myanswer.setText(beanSubject.studentAnswerOptionId);
                                tv_myanswer.setTextColor(getResources().getColor(R.color.red));
                            }
                        } else {
                            tv_myanswer.setText(beanSubject.studentAnswerOptionId);
                        }
                    } else {
                        LinearLayout view = ViewUtil.getSingleView(beanSubject.exerciseOptionList, (position + 1), new ViewUtil.OnMultipleClick() {
                            @Override
                            public void OnChecked(boolean isChecked, int indexNum, String optionId) {
                                deWeightRadio(exercisesId, optionId);
                            }
                        });
                        ll_bottom.addView(view);
                        if (infoList != null && infoList.size() != 0)
                            for (int i = 0; i < infoList.size(); i++) {
                                for (int j = 0; j < beanSubject.exerciseOptionList.size(); j++) {
                                    if (infoList.get(i).studentAnswer.equals(beanSubject.exerciseOptionList.get(j).optionId) && infoList.get(i).exerciseId.equals(beanSubject.exercisesId)) {
                                        CheckBox checkBox = (CheckBox) view.getChildAt(j + 1);
                                        checkBox.setChecked(true);
                                    }
                                }
                            }
                    }
                    tv_sureanswer.setText(beanSubject.exerciseAnswer);
                    //判断
                } else if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_JUDGE) {
//                    if (!beanSubject.isSeeAnswer) {
//                        LinearLayout view = ViewUtil.getSingleView(beanSubject.exerciseOptionList, (position + 1), new ViewUtil.OnMultipleClick() {
//                            @Override
//                            public void OnChecked(boolean isChecked, int indexNum, String optionId) {
//                                deWeightRadio(exercisesId, indexNum == 0 ? "1" : "0");
//                            }
//                        });
//                        ll_bottom.addView(view);
//                        if (infoList != null && infoList.size() != 0)
//                            for (int i = 0; i < infoList.size(); i++) {
//                                if (beanSubject.exerciseOptionList != null && beanSubject.exerciseOptionList.size() != 0)
//                                    for (int j = 0; j < beanSubject.exerciseOptionList.size(); j++) {
//                                        if (Utils.getJudge(infoList.get(i).studentAnswer).equals(Utils.getJudge(beanSubject.exerciseOptionList.get(j).optionNo)) && infoList.get(i).exerciseId.equals(beanSubject.exercisesId)) {
//                                            CheckBox checkBox = (CheckBox) view.getChildAt(j + 1);
//                                            checkBox.setChecked(true);
//                                        }
//                                    }
//                            }
//                    }
//                        ll_bottom.addView(ViewUtil.getRadioGroup(null, mJudge, new ViewUtil.OncheckedClick() {
//                            @Override
//                            public void OnChecked(int indexNum, String optionId) {
//                                deWeightRadio(exercisesId, indexNum == 0 ? "1" : "0");//判断题传1:对, 0:错;
//                            }
//                        }));
//                        ll_bottom.addView(ViewUtil.getSingleView(beanSubject.exerciseOptionList, (position + 1), new ViewUtil.OnMultipleClick() {
////                            @Override
////                            public void OnChecked(boolean isChecked, int indexNum, String optionId) {
////                                deWeightRadio(exercisesId, indexNum == 0 ? "1" : "0");//判断题传1:对, 0:错;
////                            }
////                        }));

                    if (!TextUtils.isEmpty(beanSubject.studentAnswerOptionId)) {
                        rl_answer_.setVisibility(View.VISIBLE);
                        if (beanSubject.isSeeAnswer) {
                            if (mJudge(beanSubject.studentAnswerOptionId).equals(mJudge(beanSubject.exerciseAnswer))) {
                                tv_myanswer.setText(mJudge(beanSubject.studentAnswerOptionId));
                                tv_myanswer.setTextColor(getResources().getColor(R.color.black));
                            } else {
                                tv_myanswer.setText(mJudge(beanSubject.studentAnswerOptionId));
                                tv_myanswer.setTextColor(getResources().getColor(R.color.red));
                            }
                        } else {
                            tv_myanswer.setText(mJudge(beanSubject.studentAnswerOptionId));
                        }
                    } else {
                        LinearLayout view = ViewUtil.getSingleView(beanSubject.exerciseOptionList, (position + 1), new ViewUtil.OnMultipleClick() {
                            @Override
                            public void OnChecked(boolean isChecked, int indexNum, String optionId) {
                                deWeightRadio(exercisesId, indexNum == 0 ? "1" : "0");
                            }
                        });
                        ll_bottom.addView(view);
                        if (infoList != null && infoList.size() != 0)
                            for (int i = 0; i < infoList.size(); i++) {
                                if (beanSubject.exerciseOptionList != null && beanSubject.exerciseOptionList.size() != 0)
                                    for (int j = 0; j < beanSubject.exerciseOptionList.size(); j++) {
                                        if (Utils.getJudge(infoList.get(i).studentAnswer).equals(Utils.getJudge(beanSubject.exerciseOptionList.get(j).optionNo)) && infoList.get(i).exerciseId.equals(beanSubject.exercisesId)) {
                                            CheckBox checkBox = (CheckBox) view.getChildAt(j + 1);
                                            checkBox.setChecked(true);
                                        }
                                    }
                            }
                    }
                    tv_sureanswer.setText(mJudge(beanSubject.exerciseAnswer));
                    //多选
                } else if (exerciseTypeCode == ComFlag.NumFlag.EXERCISE_MULTIPLE) {
                    multipleNum = multipleNum + 1;
                    List<BeanSubject.ExerciseOptionListBean> exerciseList = beanSubject.exerciseOptionList;
//                    if (!beanSubject.isSeeAnswer) {
//                        List<String> optionList = new ArrayList<>();
//                        LinearLayout view = ViewUtil.getRadioGroupMultiple(exerciseList, new ViewUtil.OnMultipleClick() {
//                            @Override
//                            public void OnChecked(boolean isChecked, int indexNum, String optionId) {
//                                if (isChecked) {
//                                    exerciseList.get(indexNum).ischeck = true;
//                                    if (!optionList.contains(exerciseList.get(indexNum).optionId)) {
//                                        optionList.add(exerciseList.get(indexNum).optionId);
//                                    }
//                                } else {
//                                    exerciseList.get(indexNum).ischeck = false;
//                                    if (optionList != null && optionList.size() != 0) {
//                                        optionList.remove(exerciseList.get(indexNum).optionId);
//                                    }
//                                }
//                                //修改
//                                StringBuilder sbuid = new StringBuilder();
//                                for (int i = 0; i < optionList.size(); i++) {
//                                    if (sbuid.length() == 0) {
//                                        sbuid.append(optionList.get(i));
//                                    } else {
//                                        sbuid.append("," + optionList.get(i));
//                                    }
//                                }
//                                deWeightRadio(exercisesId, sbuid.toString());
//                            }
//                        });
//
//                        ll_bottom.addView(view);
//                        if (infoList != null && infoList.size() != 0) {
//                            for (int i = 0; i < infoList.size(); i++) {
//                                if (infoList.get(i).exerciseId.equals(beanSubject.exercisesId)) {
//                                    String[] infoArr = infoList.get(i).studentAnswer.split(",");
//                                    for (int k = 0; k < infoArr.length; k++) {
//                                        for (int j = 0; j < beanSubject.exerciseOptionList.size(); j++) {
//                                            Log.e("http", "infoArr : " + infoArr[k] + " exerciseOptionList : " + beanSubject.exerciseOptionList.get(j).optionId);
//                                            if (infoArr[k].equals(beanSubject.exerciseOptionList.get(j).optionId)) {
//                                                CheckBox checkBox = (CheckBox) view.getChildAt(j);
//                                                checkBox.setChecked(true);
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                        ll_bottom.addView(ViewUtil.getRadioGroupMultiple(exerciseList, new ViewUtil.OnMultipleClick() {
//                            @Override
//                            public void OnChecked(boolean isChecked, int indexNum, String optionId) {
//                                if (isChecked) {
//                                    exerciseList.get(indexNum).ischeck = true;
//                                } else {
//                                    exerciseList.get(indexNum).ischeck = false;
//                                }
//                            }
//                        }));
                    if (!TextUtils.isEmpty(beanSubject.studentAnswerOptionId)) {
                        rl_answer_.setVisibility(View.VISIBLE);
                        if (beanSubject.isSeeAnswer) {
                            if (beanSubject.studentAnswerOptionId.equals(beanSubject.exerciseAnswer)) {
                                tv_myanswer.setText(beanSubject.studentAnswerOptionId);
                                tv_myanswer.setTextColor(getResources().getColor(R.color.black));
                            } else {
                                tv_myanswer.setText(beanSubject.studentAnswerOptionId);
                                tv_myanswer.setTextColor(getResources().getColor(R.color.red));
                            }
                        } else {
                            tv_myanswer.setText(beanSubject.studentAnswerOptionId);
                        }
                    } else {
                        List<String> optionList = new ArrayList<>();
                        LinearLayout view = ViewUtil.getRadioGroupMultiple(exerciseList, new ViewUtil.OnMultipleClick() {
                            @Override
                            public void OnChecked(boolean isChecked, int indexNum, String optionId) {
                                if (isChecked) {
                                    exerciseList.get(indexNum).ischeck = true;
                                    if (!optionList.contains(exerciseList.get(indexNum).optionId)) {
                                        optionList.add(exerciseList.get(indexNum).optionId);
                                    }
                                } else {
                                    exerciseList.get(indexNum).ischeck = false;
                                    if (optionList != null && optionList.size() != 0) {
                                        optionList.remove(exerciseList.get(indexNum).optionId);
                                    }
                                }
                                //修改
                                StringBuilder sbuid = new StringBuilder();
                                for (int i = 0; i < optionList.size(); i++) {
                                    if (sbuid.length() == 0) {
                                        sbuid.append(optionList.get(i));
                                    } else {
                                        sbuid.append("," + optionList.get(i));
                                    }
                                }
                                deWeightRadio(exercisesId, sbuid.toString());
                            }
                        });

                        ll_bottom.addView(view);
                        if (infoList != null && infoList.size() != 0) {
                            for (int i = 0; i < infoList.size(); i++) {
                                if (infoList.get(i).exerciseId.equals(beanSubject.exercisesId)) {
                                    String[] infoArr = infoList.get(i).studentAnswer.split(",");
                                    for (int k = 0; k < infoArr.length; k++) {
                                        for (int j = 0; j < beanSubject.exerciseOptionList.size(); j++) {
                                            Log.e("http", "infoArr : " + infoArr[k] + " exerciseOptionList : " + beanSubject.exerciseOptionList.get(j).optionId);
                                            if (infoArr[k].equals(beanSubject.exerciseOptionList.get(j).optionId)) {
                                                CheckBox checkBox = (CheckBox) view.getChildAt(j);
                                                checkBox.setChecked(true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    tv_sureanswer.setText(beanSubject.exerciseAnswer);
                } else {
                    if (beanSubject.isSeeAnswer) {
                        rl_answer_.setVisibility(View.GONE);
                        ic_exerciseAnswer.setVisibility(View.VISIBLE);
                        ic_respondence.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(beanSubject.studentAnswerFileUri)) {
                            if (Utils.isPic(beanSubject.studentAnswerFileUri)) {
                                iv_participation_detail_item_answer.setVisibility(View.VISIBLE);
                                webView_participation_detail_answer.setVisibility(View.GONE);
                                UIUtils.setCenterCrop(beanSubject.studentAnswerFileUri, iv_participation_detail_item_answer);
                                //                                webView_participation_detail_answer.loadDataWithBaseURL(null, Utils.getPicHtml(beanSubject.studentAnswerFileUri), "text/html", "UTF-8", null);
                                iv_participation_detail_item_answer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final List<String> longPictureList = new ArrayList<>();
                                        longPictureList.add(beanSubject.studentAnswerFileUri);
                                        final SparseArray<ImageView> mappingViews = new SparseArray<>();
                                        mappingViews.put(0, (ImageView) v);
                                        iwHelper.show((ImageView) v, mappingViews, convertList(longPictureList));
                                    }
                                });
                            }
//                             else {
//                                webView_participation_detail_answer.loadDataWithBaseURL(null, beanSubject.studentAnswerFileUri, "text/html", "UTF-8", null);
//                            }
                        }
                        if (!TextUtils.isEmpty(beanSubject.exerciseAnswer)) {
                            if (Utils.isPic(beanSubject.exerciseAnswer)) {
                                webView_participation_detail_eanswer.loadDataWithBaseURL(null, Utils.getPicHtml(beanSubject.exerciseAnswer), "text/html", "UTF-8", null);
                            } else {
                                webView_participation_detail_eanswer.loadDataWithBaseURL(null, beanSubject.exerciseAnswer, "text/html", "UTF-8", null);
                            }
                        }
                        if (TextUtils.isEmpty(beanSubject.exerciseAnalysis))
                            rr_exerciseAnalysis.setVisibility(View.VISIBLE);
                        else {
                            rr_exerciseAnalysis.setVisibility(View.VISIBLE);
                            if (Utils.isPic(beanSubject.exerciseAnalysis)) {
                                iv_participation_detail_item.setVisibility(View.VISIBLE);
                                webView_participation_detail_item.setVisibility(View.GONE);
                                UIUtils.setCenterCrop(beanSubject.exerciseAnalysis, iv_participation_detail_item);
                            } else {
                                iv_participation_detail_item.setVisibility(View.GONE);
                                webView_participation_detail_item.setVisibility(View.VISIBLE);
                                webView_participation_detail_item.loadDataWithBaseURL(null, beanSubject.exerciseAnalysis, "text/html", "UTF-8", null);
                            }
                        }
                    } else {
                        if (TextUtils.isEmpty(beanSubject.studentAnswerFileUri)) {
//                            View textViewTwo = ViewUtil.getTextViewTwo("作答: ", "点击此处开始作答");
                            if (!beanSubject.isSeeAnswer) {
                                tv_answer_click.setVisibility(View.VISIBLE);
                                tv_answer_click.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = createIntent(SubjectActivityWrite.class);
                                        intent.putExtra("exercisesId", beanSubject.exercisesId);
                                        intent.putExtra("exercisesTitle", beanSubject.exercisesTitle);
                                        intent.putExtra("exerciseAnalysis", beanSubject.exerciseAnalysis);
                                        intent.putExtra("title", (position+1) + "." + Utils.getsubjectType(exerciseTypeCode));
                                        startActivityForResult(intent, REQUEST_CODE);
                                    }
                                });
//                                ll_bottom.addView(textViewTwo);
//                                rl_answer_.setVisibility(View.GONE);
//                                ic_answer.setVisibility(View.GONE);
//                                textViewTwo.setOnClickListener(v -> {
//                                    Intent intent = createIntent(SubjectActivityWrite.class);
//                                    intent.putExtra("exercisesId", beanSubject.exercisesId);
//                                    intent.putExtra("exercisesTitle", beanSubject.exercisesTitle);
//                                    intent.putExtra("exerciseAnalysis", beanSubject.exerciseAnalysis);
//                                    startActivityForResult(intent, REQUEST_CODE);
//                                });
                            }
                        } else {
                            tv_answer_click.setVisibility(View.GONE);
                            ic_answer.setVisibility(View.VISIBLE);
                            ic_respondence.setVisibility(View.VISIBLE);
                            if (Utils.isPic(beanSubject.studentAnswerFileUri)) {
                                webView_participation_detail_answer.loadDataWithBaseURL(null, Utils.getPicHtml(beanSubject.studentAnswerFileUri), "text/html", "UTF-8", null);
                            } else {
                                webView_participation_detail_answer.loadDataWithBaseURL(null, beanSubject.studentAnswerFileUri, "text/html", "UTF-8", null);
                            }

                        }
                    }
                }

                iv_participation_detail_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = createIntent(SubjectActivityWrite.class);
                        intent.putExtra("exercisesId", beanSubject.exercisesId);
                        intent.putExtra("exercisesTitle", beanSubject.exercisesTitle);
                        intent.putExtra("exerciseAnalysis", beanSubject.exerciseAnalysis);
                        intent.putExtra("title", (position+1) + "." + Utils.getsubjectType(exerciseTypeCode));
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                });

                //修改
                if (picturePathList.size() != 0) {
                    for (int i = 0; i < picturePathList.size(); i++) {
                        if (beanSubject.exercisesId.equals(picturePathList.get(i).substring(picturePathList.get(i).lastIndexOf("/") + 1, picturePathList.get(i).length() - 4))) {
                            rr_exerciseAnalysis.setVisibility(View.VISIBLE);
                            ic_answer.setVisibility(View.VISIBLE);
                            rl_answer_.setVisibility(View.GONE);
                            tv_answer_click.setVisibility(View.GONE);
                            tv_analysis.setText("   作答:");
//                            String path = "file://" + picturePathList.get(i);
                            webView_participation_detail_item.setVisibility(View.GONE);
                            iv_participation_detail_item.setVisibility(View.VISIBLE);
                            Log.e("http", " picturePath : " + picturePathList.get(i));
                            UIUtils.updateOptions(new RequestOptions(), picturePathList.get(i), System.currentTimeMillis(), iv_participation_detail_item);
//                            UIUtils.setCenterCrop(picturePathList.get(i), iv_participation_detail_item);
                        }
                    }
                }
            }
        });
    }

    private Handler mHandler;

    private List<Uri> convertList(List<String> data) {
        List<Uri> list = new ArrayList<>();
        for (String d : data) list.add(Uri.parse(d));
        return list;
    }

    private String mJudge(String result) {
        if (result.equals("1")) return "√";
        else return "×";
    }

    /**
     * @param exercisesId 题目id
     * @param optionId    题目答案
     */
    //去重单选题
    private void deWeightRadio(String exercisesId, String optionId) {
        BeanInfo beanInfo = new BeanInfo(exercisesId, optionId);
        if (infoList.size() > 0) {
            for (int i = 0; i < infoList.size(); i++) {
                if (exercisesId.equals(infoList.get(i).exerciseId)) {
                    infoList.remove(infoList.get(i));
                }
            }
            infoList.add(beanInfo);
        } else {
            infoList.add(beanInfo);
        }
//        Log.e("http", "infolist : " + infoList.toString());
    }

    /**
     * @param view
     */
    //提交
    @OnClick({R.id.tv_sunbject_save})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.tv_sunbject_save:

                if (infoList.size() < subjectSize - multipleNum) {
                    new TitleDialog(SubjectActivity.this, "题目尚未答完,确定提交?", new TitleDialog.OnclickListener() {
                        @Override
                        public void OnSure() {
                            saveSubmit();
                        }

                        @Override
                        public void OnCancle() {

                        }
                    }).show();
                } else {
                    saveSubmit();
                }
                break;
        }
    }

    private int multipleNum;

    private void saveSubmit() {
        //多选选择答案 修改
//        List<BeanSubject> datas = mAdapter.getDatas();
//        for (int i = 0; i < datas.size(); i++) {
//            BeanSubject beanSubject = datas.get(i);
//            if (beanSubject.exerciseTypeCode == ComFlag.NumFlag.EXERCISE_MULTIPLE) {
//                List<BeanSubject.ExerciseOptionListBean> exerciseList = beanSubject.exerciseOptionList;
//                StringBuilder sbuid = new StringBuilder();
//                for (int j = 0; j < exerciseList.size(); j++) {
//                    if (exerciseList.get(j).ischeck) {
//                        String optionIdcheck = exerciseList.get(j).optionId;
//                        if (sbuid.length() == 0) {
//                            sbuid.append(optionIdcheck);
//                        } else {
//                            sbuid.append("," + optionIdcheck);
//                        }
//                    }
//
//                }
//                deWeightRadio(beanSubject.exercisesId, sbuid.toString());
//            }
//        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("cmStudentId", stuId);//导学本id
        map.put("workId", guideSoureId);//导学本id
        map.put("workPushLogId", workPushLogId);//作业记录id
        map.put("workType", ComFlag.NumFlag.WORKTYPE_GUIDE + "");//作业类型
        map.put("info", infoList);//题目详情
        //传参
        MultipartBody.Builder builder = new MultipartBody.Builder();
        HashMap<String, Object> mapParts = new HashMap<>();
        builder.addFormDataPart("jsonParam", JSON.toJSONString(map));
        for (int i = 0; i < picturePathList.size(); i++) {
            File file = new File(picturePathList.get(i));
            if (file.exists()) {
                Log.e("http", "picPath : " + file);
                RequestBody bodyFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                String name = file.getName();
                builder.addFormDataPart("files", file.getName(), bodyFile);
            }
        }

        ApiUtil.doDefaultApi(api.submitWork(builder.build().parts()), data -> {
            showToast("提交成功");
            finish();
        }, ViewControlUtil.create2Dialog(SubjectActivity.this));
    }


    @Override
    protected void initData() {
        super.initData();

        api = ApiUtil.createUploadApi(UserService.class, stuId);
        ApiUtil.doDefaultApi(api.selectGuideExercises(guideSoureId), data -> {
            if (data.size() > 0 && !data.equals("")) {
                subjectSize = data.size();
                mAdapter.setDatas(data);
                ll_subject.setVisibility(View.VISIBLE);
            }
        }, ViewControlUtil.create2Dialog(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                if (data != null) {
                    String pictureStr = data.getStringExtra("pictureStr");
                    String exercisesId = data.getStringExtra("exercisesId");
                    deWeightPicture(exercisesId, exercisesId, pictureStr);
                }
            }
        }
    }
    //修改

    /**
     * @param exercisesId 题目id
     * @param optionId    题目答案id
     */
    //去重主观题
    private void deWeightPicture(String exercisesId, String optionId, String pictureStr) {

        BeanInfo beanInfo = new BeanInfo(exercisesId, optionId);
        if (picturePathList.size() > 0) {
            //去重答案
            for (int i = 0; i < infoList.size(); i++) {
                if (exercisesId.equals(infoList.get(i).exerciseId)) {
                    infoList.remove(infoList.get(i));
                }
            }
            //去重图片
            for (int i = 0; i < picturePathList.size(); i++) {
                if (pictureStr.equals(picturePathList.get(i))) {
                    picturePathList.remove(picturePathList.get(i));
                }
            }
            infoList.add(beanInfo);
            picturePathList.add(pictureStr);
        } else {
            infoList.add(beanInfo);
            picturePathList.add(pictureStr);
        }
        Message message = Message.obtain();
        message.obj = picturePathList;
        message.what = 8;
        mHandler.sendMessage(message);

    }

}
