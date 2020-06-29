package com.zwyl.guide.main.detaile;

import android.Manifest;
import android.app.Service;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import com.zwyl.guide.R;
import com.zwyl.guide.base.BaseActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.annotations.NonNull;

public class PlayActivity extends BaseActivity {
    @BindView(R.id.videoView)
    VideoView videoView;
    @BindView(R.id.rl_audio)
    RelativeLayout rl_audio;
    private MediaPlayer mediaPlayer;
    @BindView(R.id.start)
    Button btStart;
    //    @BindView(R.id.pause)
//    Button btPause;
    @BindView(R.id.stop)
    Button btStop;
    private AudioManager audioManager;
    private Timer timer;
    private boolean isSeekBarChanging;//互斥变量，防止进度条与定时器冲突。
    private int currentPosition;//当前音乐播放的进度
    SimpleDateFormat format;
    private TextView musicName, musicLength, musicCur;
    private SeekBar seekBar;
    private String resourceUri;
    private String fileName;

    /**
     * 设置顶部点击事件
     */
    private void setHeadView() {
        setTitleCenter("资源预览");
        setShowLeftHead(false);//左边顶部按钮
        setShowRightHead(false);//右边顶部按钮
        setShowFilter(false);//日历筛选
        setShowLogo(true);//logo
        setShowRefresh(false);//刷新
        setLogoClick(v -> {
            finish();
        });
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_play;
    }

    @Override
    protected void initView() {
        super.initView();
        setHeadView();
        resourceUri = getIntent().getStringExtra("fileUri");
        fileName = getIntent().getStringExtra("fileName");
        if (resourceUri != null) {
            if (resourceUri.endsWith(".mp4")) {
                videoView.setVisibility(View.VISIBLE);
                videoView.setMediaController(new MediaController(PlayActivity.this));
                videoView.setVideoPath(resourceUri);
                videoView.start();
            } else if (resourceUri.endsWith(".mp3")) {
                audioView();
                rl_audio.setVisibility(View.VISIBLE);
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    initMediaPlayer();//初始化mediaplayer
                }
            }
        }
    }

    private void audioView() {
        audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        format = new SimpleDateFormat("mm:ss");
        musicName = (TextView) findViewById(R.id.music_name);
        musicLength = (TextView) findViewById(R.id.music_length);
        musicCur = (TextView) findViewById(R.id.music_cur);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new MySeekBar());
        mediaPlayer = new MediaPlayer();
    }

    @OnClick({R.id.start, R.id.stop})
    void onclick(View view) {
        switch (view.getId()) {
            case R.id.start:
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();//开始播放
                    mediaPlayer.seekTo(currentPosition);

                    //监听播放时回调函数
                    timer = new Timer();
                    timer.schedule(new TimerTask() {

                        Runnable updateUI = new Runnable() {
                            @Override
                            public void run() {
                                musicCur.setText(format.format(mediaPlayer.getCurrentPosition()) + "");
                            }
                        };

                        @Override
                        public void run() {
                            if (!isSeekBarChanging) {
                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                runOnUiThread(updateUI);
                            }
                        }
                    }, 0, 50);
                }
                break;
//            case R.id.pause:
//                if (mediaPlayer.isPlaying()) {
//                    mediaPlayer.pause();//暂停播放
//                }
//                break;
            case R.id.stop:
                Toast.makeText(this, "停止播放", Toast.LENGTH_SHORT).show();
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();//停止播放
                    initMediaPlayer();
                }
                break;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMediaPlayer();
                } else {
                    Toast.makeText(this, "denied access", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    private void initMediaPlayer() {
        try {
            mediaPlayer.setDataSource(resourceUri);//指定音频文件的路径
            mediaPlayer.prepare();//让mediaplayer进入准备状态
            mediaPlayer.setLooping(true);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    seekBar.setMax(mediaPlayer.getDuration());
                    musicLength.setText(format.format(mediaPlayer.getDuration()) + "");
                    musicCur.setText("00:00");
                    musicName.setText(fileName);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*进度条处理*/
    public class MySeekBar implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        /*滚动时,应当暂停后台定时器*/
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = true;
        }

        /*滑动结束后，重新设置值*/
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = false;
            mediaPlayer.seekTo(seekBar.getProgress());
            currentPosition = seekBar.getProgress();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isSeekBarChanging = true;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

}
