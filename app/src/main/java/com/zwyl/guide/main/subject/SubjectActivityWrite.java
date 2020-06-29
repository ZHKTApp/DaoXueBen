package com.zwyl.guide.main.subject;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Interpolator;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.zwyl.guide.App;
import com.zwyl.guide.R;
import com.zwyl.guide.base.BaseActivity;
import com.zwyl.guide.base.ComFlag;
import com.zwyl.guide.customveiw.PaletteView;
import com.zwyl.guide.dialog.TitleDialog;
import com.zwyl.guide.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

public class SubjectActivityWrite extends BaseActivity implements PaletteView.Callback, Handler.Callback {
    @BindView(R.id.tv_delete)
    RadioButton tvDelete;
    @BindView(R.id.tv_repeal)
    RadioButton tvRepeal;
    @BindView(R.id.tv_renew)
    RadioButton tvRenew;
    @BindView(R.id.tv_wipe)
    RadioButton tvWipe;
    @BindView(R.id.tv_write)
    RadioButton tvWrite;
    @BindView(R.id.rg_group)
    RadioGroup rgGroup;
    @BindView(R.id.rg_group_top)
    RadioGroup rgGroupTop;
    @BindView(R.id.tv_sunbjectWrite_save)
    TextView tvSunbjectWriteSave;
    @BindView(R.id.rb_top_write)
    RadioButton rbTopWrite;
    @BindView(R.id.rb_top_edit)
    RadioButton rbTopEdit;
    @BindView(R.id.rb_top_photo)
    RadioButton rbTopPhoto;
    @BindView(R.id.palette_write)
    PaletteView mPaletteView;
    @BindView(R.id.ed_content)
    EditText edContent;
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    @BindView(R.id.webView_subject_write)
    WebView webView_subject_write;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    private ProgressDialog mSaveProgressDlg;
    private static final int MSG_SAVE_SUCCESS = 1;
    private static final int MSG_SAVE_FAILED = 2;
    private Handler mHandler;
    private int toptag;//0手写笔,1输入法,2拍照
    private Bitmap bmpPhoto;
    private String exercisesId, exercisesTitle, exerciseAnalysis, tvTitleNo;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_subjectwrite;
    }

    @Override
    protected void initView() {
        super.initView();
        exercisesId = getIntent().getStringExtra("exercisesId");
        exercisesTitle = getIntent().getStringExtra("exercisesTitle");
        exerciseAnalysis = getIntent().getStringExtra("exerciseAnalysis");
        tvTitleNo = getIntent().getStringExtra("title");
        rbTopWrite.setChecked(true);//默认选中手写笔
        setTitleCenter("导学测验");
        setShowFilter(false);
        mHandler = new Handler(this);
        if (!TextUtils.isEmpty(exercisesTitle))
            webView_subject_write.loadDataWithBaseURL(null, exercisesTitle, "text/html", "UTF-8", null);
        if (!TextUtils.isEmpty(tvTitleNo)) {
            tvTitle.setText(tvTitleNo);
        }
    }

    private int tag = 0;

    @OnClick({R.id.tv_delete, R.id.tv_repeal, R.id.tv_renew, R.id.tv_wipe, R.id.tv_write, R.id.tv_sunbjectWrite_save, R.id.rb_top_write, R.id.rb_top_edit, R.id.rb_top_photo, R.id.iv_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_delete:
                tag = 0;
                mPaletteView.setMode(PaletteView.Mode.DRAW);
                mPaletteView.clear();
                break;
            case R.id.tv_repeal:
                tag = 0;
                mPaletteView.setMode(PaletteView.Mode.DRAW);
                mPaletteView.undo();
                break;
            case R.id.tv_renew:
                tag = 0;
                mPaletteView.setMode(PaletteView.Mode.DRAW);
                mPaletteView.redo();
                break;
            case R.id.tv_wipe:
                if (tag == 0) {
                    showToast("选中");
                    tag = 1;
                    mPaletteView.setMode(PaletteView.Mode.ERASER);
                    return;
                } else {
                    tag = 0;
                    showToast("未选中");
                    rgGroup.clearCheck();
                    mPaletteView.setMode(PaletteView.Mode.DRAW);
                }
            case R.id.rb_top_write:
//                if (toptag != ComFlag.NumFlag.RB_TOP_WRITE) {
                new TitleDialog(this, "当前数据将会被删除!", new TitleDialog.OnclickListener() {
                    @Override
                    public void OnSure() {
                        edContent.setVisibility(View.GONE);
                        ivPhoto.setVisibility(View.GONE);
                        rgGroup.setVisibility(View.VISIBLE);
                        mPaletteView.setVisibility(View.VISIBLE);
                        toptag = ComFlag.NumFlag.RB_TOP_WRITE;
                    }

                    @Override
                    public void OnCancle() {
                        if (toptag == ComFlag.NumFlag.RB_TOP_PHOTO) {
                            rbTopPhoto.setChecked(true);
                        } else {
                            rbTopEdit.setChecked(true);
                        }
                    }
                }).show();
//                } else {
//                    toptag = ComFlag.NumFlag.RB_TOP_WRITE;
//                }
                break;
            case R.id.rb_top_edit:
//                if (toptag != ComFlag.NumFlag.RB_TOP_EDIT) {
                edContent.setText("");
                new TitleDialog(this, "当前数据将会被删除!", new TitleDialog.OnclickListener() {
                    @Override
                    public void OnSure() {
                        edContent.setVisibility(View.VISIBLE);
                        ivPhoto.setVisibility(View.GONE);
                        rgGroup.setVisibility(View.GONE);
                        mPaletteView.setVisibility(View.GONE);
                        mPaletteView.clear();
                        toptag = ComFlag.NumFlag.RB_TOP_EDIT;
                    }

                    @Override
                    public void OnCancle() {
                        if (toptag == ComFlag.NumFlag.RB_TOP_WRITE) {
                            rbTopWrite.setChecked(true);
                        } else {
                            rbTopPhoto.setChecked(true);
                        }
                    }
                }).show();
//                } else {
//                    toptag = ComFlag.NumFlag.RB_TOP_EDIT;
//                }

                break;
            case R.id.rb_top_photo:
//                if (toptag != ComFlag.NumFlag.RB_TOP_PHOTO) {
                new TitleDialog(this, "当前数据将会被删除!", new TitleDialog.OnclickListener() {
                    @Override
                    public void OnSure() {
                        rgGroup.setVisibility(View.GONE);
                        edContent.setVisibility(View.GONE);
                        mPaletteView.setVisibility(View.GONE);
                        mPaletteView.clear();
                        ivPhoto.setVisibility(View.VISIBLE);
                        toptag = ComFlag.NumFlag.RB_TOP_PHOTO;
                        Acp.getInstance(SubjectActivityWrite.this).request(new AcpOptions.Builder().setPermissions(Manifest.permission.CAMERA).build(), new AcpListener() {
                            @Override
                            public void onGranted() {
                                //showToast("同意了相机权限");
                                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 1);
                            }

                            @Override
                            public void onDenied(List<String> permissions) {
                                showToast(permissions.toString() + "权限拒绝");
                            }
                        });
                    }

                    @Override
                    public void OnCancle() {
                        if (toptag == ComFlag.NumFlag.RB_TOP_EDIT) {
                            rbTopEdit.setChecked(true);
                        } else {
                            rbTopWrite.setChecked(true);
                        }

                    }
                }).show();
//                } else {
//                    toptag = ComFlag.NumFlag.RB_TOP_PHOTO;
//                }
                break;

            case R.id.tv_sunbjectWrite_save:
                showToast("提交");
                if (toptag == ComFlag.NumFlag.RB_TOP_WRITE) {
                    if (mSaveProgressDlg == null) {
                        initSaveProgressDlg();
                    }
                    mSaveProgressDlg.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //                        Bitmap bm = mPaletteView.buildBitmap();
                            bmpPhoto = loadBitmapFromView(mPaletteView);
                            if (bmpPhoto != null) {
                                mHandler.obtainMessage(MSG_SAVE_SUCCESS).sendToTarget();
                            } else {
                                mHandler.obtainMessage(MSG_SAVE_FAILED).sendToTarget();
                            }
                        }
                    }).start();
                } else if (toptag == ComFlag.NumFlag.RB_TOP_PHOTO) {
                    if (mSaveProgressDlg == null) {
                        initSaveProgressDlg();
                    }
                    mSaveProgressDlg.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //                        Bitmap bm = mPaletteView.buildBitmap();
//                            bmpPhoto = loadBitmapFromView(mPaletteView);
                            if (bmpPhoto != null) {
                                mHandler.obtainMessage(MSG_SAVE_SUCCESS).sendToTarget();
                            } else {
                                mHandler.obtainMessage(MSG_SAVE_FAILED).sendToTarget();
                            }
                        }
                    }).start();
                } else {
                    //输入法文字
                    bmpPhoto = Utils.textAsBitmap(edContent.getText().toString().trim(), 16);
                    if (mSaveProgressDlg == null) {
                        initSaveProgressDlg();
                    }
                    mSaveProgressDlg.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //                        Bitmap bm = mPaletteView.buildBitmap();
                            if (bmpPhoto != null) {
                                mHandler.obtainMessage(MSG_SAVE_SUCCESS).sendToTarget();
                            } else {
                                mHandler.obtainMessage(MSG_SAVE_FAILED).sendToTarget();
                            }
                        }
                    }).start();
                }
                setResult();

                break;
            case R.id.iv_photo:
                Acp.getInstance(this).request(new AcpOptions.Builder().setPermissions(Manifest.permission.CAMERA).build(), new AcpListener() {
                    @Override
                    public void onGranted() {
                        //showToast("同意了相机权限");
                        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 1);
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File photoFile = null;
                        photoFile = Utils.CreateImageFile(exercisesId);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        takePictureIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                        startActivityForResult(takePictureIntent, 1);
                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        showToast(permissions.toString() + "权限拒绝");
                    }
                });
                break;
        }
    }

    private void setResult() {
        if (bmpPhoto != null) {
            String pictureStr = Utils.saveCameraImage(bmpPhoto, exercisesId);
            Intent intent = getIntent();
            intent.putExtra("pictureStr", pictureStr);
            intent.putExtra("exercisesId", exercisesId);
            intent.putExtra("isGetBack", true);
            this.setResult(RESULT_OK, intent);
            finish();
        }
    }

    private Bitmap loadBitmapFromView(View v) {
        if (v == null) {
            return null;
        }
        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(screenshot);
        c.translate(-v.getScrollX(), -v.getScrollY());
        v.draw(c);
        return screenshot;
    }

    @Override
    protected void initData() {
        super.initData();
    }

    //加载progress
    private void initSaveProgressDlg() {
        mSaveProgressDlg = new ProgressDialog(this);
        mSaveProgressDlg.setMessage("正在保存,请稍候...");
        mSaveProgressDlg.setCancelable(false);
    }


    @Override
    public void onUndoRedoStatusChanged() {
        tvRepeal.setEnabled(mPaletteView.canUndo());
        tvRenew.setEnabled(mPaletteView.canRedo());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_SAVE_FAILED);
        mHandler.removeMessages(MSG_SAVE_SUCCESS);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_SAVE_FAILED:
                mSaveProgressDlg.dismiss();
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
                break;
            case MSG_SAVE_SUCCESS:
                mSaveProgressDlg.dismiss();
                setResult();
                Toast.makeText(this, "画板已保存", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    // 使用startActivityForResult返回结果时调用的方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果返回值是正常的话
        if (resultCode == Activity.RESULT_OK) {
            // 验证请求码是否一至，也就是startActivityForResult的第二个参数
            switch (requestCode) {
                case 1:

                    File mFilePath = Utils.CreateImageFile(exercisesId);
                    FileInputStream is = null;
                    try {
                        is = new FileInputStream(mFilePath);
                        // 把流解析成bitmap,此时就得到了清晰的原图
                        bmpPhoto = BitmapFactory.decodeStream(is);
                        //接下来就可以展示了（或者做上传处理）
                        ivPhoto.setVisibility(View.VISIBLE);
                        ivPhoto.setImageBitmap(bmpPhoto);
                        ivPhoto.setBackground(null);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
//                    bmpPhoto = (Bitmap) data.getExtras().get("data");
//                    ivPhoto.setVisibility(View.VISIBLE);
//                    ivPhoto.setImageBitmap(bmpPhoto);
//                    ivPhoto.setBackground(null);
                    //saveCameraImage(data);
                    break;
            }
        }
    }
}
