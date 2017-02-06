package com.imooc.zhangtao.viewplayertest;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    private FullVideoView mVideoView;
    private LinearLayout mControllerLayout;
    private TextView mCurrentTime , mTotalTime;
    private SeekBar mPosSeekBar , mVolumeSeekBar;
    private RelativeLayout mVideoLayout;
    private ImageView mPlayAndPause , mChangeFullScreen , mVolumeImg;
    private ImageView operationBgImg , operationPercentImg;
    private FrameLayout mProgressLayout;

    private int screenWidth , screenHeight;
    private AudioManager mAudioManager;
    private boolean isFullScreen = false;
    private boolean isAdjust = false;
    private int threshold = 54;
    float lastX = 0 , lastY = 0;

    private float mBrightNess;

    private static final int UPDATE_UI = 1;

    private Handler mUIHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (msg.what == UPDATE_UI)
            {
                int currentPosition = mVideoView.getCurrentPosition();
                int totalPosition = mVideoView.getDuration();
                updateTextViewWithTimeFormat(mCurrentTime , currentPosition);
                updateTextViewWithTimeFormat(mTotalTime , totalPosition);

                mPosSeekBar.setMax(totalPosition);
                mPosSeekBar.setProgress(currentPosition);
                mUIHandler.sendEmptyMessageDelayed(UPDATE_UI , 500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        initView();
        initEvent();

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/money.mp4";
        //本地视频播放
        mVideoView.setVideoPath(path);
        mVideoView.start();
        mUIHandler.sendEmptyMessage(UPDATE_UI);

        //网络视频播放
//        mVideoView.setVideoURI(Uri.parse(""));

//        //MediaController控制视频播发
//        MediaController mediaController = new MediaController(this);
//
//        //设置VideoView与MediaController的关联
//        mVideoView.setMediaController(mediaController);
//
//        //设置MediaController与VideoView的关联
//        mediaController.setMediaPlayer(mVideoView);
    }

    private void initView()
    {
        mVideoView = (FullVideoView) findViewById(R.id.videoView);
        mControllerLayout = (LinearLayout) findViewById(R.id.controllerBar);
        mCurrentTime = (TextView) findViewById(R.id.current_time_tv);
        mTotalTime = (TextView) findViewById(R.id.total_time_tv);
        mPosSeekBar = (SeekBar) findViewById(R.id.pos_seekBar);
        mVolumeSeekBar = (SeekBar) findViewById(R.id.volume_seek);
        mPlayAndPause = (ImageView) findViewById(R.id.pause_img);
        mChangeFullScreen = (ImageView) findViewById(R.id.change_screen);
        mVideoLayout = (RelativeLayout) findViewById(R.id.videoLayout);
        mVolumeImg = (ImageView) findViewById(R.id.volume_img);
        operationBgImg = (ImageView) findViewById(R.id.operation_bg);
        operationPercentImg = (ImageView) findViewById(R.id.operation_percent);
        mProgressLayout = (FrameLayout) findViewById(R.id.progress_layout);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int streamCurVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        mVolumeSeekBar.setMax(streamMaxVolume);
        mVolumeSeekBar.setProgress(streamCurVolume);
    }

    private void initEvent()
    {
        //控制视频的播放和暂停
        mPlayAndPause.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mVideoView.isPlaying())
                {
                    mPlayAndPause.setImageResource(R.drawable.video_start_style);
                    mVideoView.pause();
                    mUIHandler.removeMessages(UPDATE_UI);
                }
                else
                {
                    mPlayAndPause.setImageResource(R.drawable.video_stop_style);
                    mVideoView.start();
                    mUIHandler.sendEmptyMessage(UPDATE_UI);
                }
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                mPlayAndPause.setImageResource(R.drawable.video_start_style);
            }
        });

        mPosSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                updateTextViewWithTimeFormat(mCurrentTime , progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                mUIHandler.removeMessages(UPDATE_UI);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                int progress = seekBar.getProgress();
                mVideoView.seekTo(progress);
                mUIHandler.sendEmptyMessage(UPDATE_UI);
            }
        });

        mVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                //设置当前设备音量
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC , progress , 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });

        mChangeFullScreen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isFullScreen)
                {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                else
                {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        });

        //VideoView的事件监听
        mVideoView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                float x = event.getX();
                float y = event.getY();
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        lastX = x;
                        lastY = y;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float changeX = x - lastX;
                        float changeY = y - lastY;

                        float absChangeX = Math.abs(changeX);
                        float absChangeY = Math.abs(changeY);

                        if (absChangeX > threshold && absChangeY > threshold)
                        {
                            if (absChangeY > absChangeX)
                            {
                                isAdjust = true;
                            }
                            else
                            {
                                isAdjust = false;
                            }
                        }
                        else if (absChangeX < threshold && absChangeY > threshold)
                        {
                            isAdjust = true;
                        }
                        else if (absChangeX > threshold && absChangeY < threshold)
                        {
                            isAdjust = false;
                        }

                        if (isAdjust)
                        {
                            if (x < screenWidth / 2)
                            {
                                //调节亮度
                                if (changeY > 0)
                                {
                                    //降低亮度
                                    Log.d("MainActivity" , changeY + "");
                                }
                                else
                                {
                                    //调高亮度
                                    Log.d("MainActivity" , changeY + "");
                                }
                                changeBrightNess(-changeY);
                            }
                            else
                            {
                                if (changeY > 0)
                                {
                                    //降低亮度
                                    Log.d("MainActivity" , changeY + "");
                                }
                                else
                                {
                                    Log.d("MainActivity" , changeY + "");
                                }
                                changeVolume(-changeY);
                            }
                        }
                        lastX = x;
                        lastY = y;
                        break;

                    case MotionEvent.ACTION_UP:
                        mProgressLayout.setVisibility(View.GONE);
                        break;
                }
                return true;
            }
        });
    }

    public void changeVolume(float changY)
    {
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int index = (int) (changY / screenHeight * max * 3);

        int volume = Math.max(current + index , 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC , volume , 0);
        mVolumeSeekBar.setProgress(volume);

        if (mProgressLayout.getVisibility() == View.GONE)
        {
            mProgressLayout.setVisibility(View.VISIBLE);
        }
        operationBgImg.setImageResource(R.mipmap.video_voice_bg);
        ViewGroup.LayoutParams layoutParams = operationPercentImg.getLayoutParams();
        layoutParams.width = (int) (DensityUtils.dp2px(this , 94f) * (float) volume / max);
        operationPercentImg.setLayoutParams(layoutParams);
    }

    public void changeBrightNess(float changY)
    {
        WindowManager.LayoutParams attribute = getWindow().getAttributes();
        mBrightNess = attribute.screenBrightness;
        Log.d("mBrightness" , mBrightNess + "");
        float index = changY / screenHeight;
        mBrightNess += index;

        if (mBrightNess > 1.0f)
        {
            mBrightNess = 1.0f;
        }

        if (mBrightNess < 0.01f)
        {
            mBrightNess = 0.01f;
        }

        attribute.screenBrightness = mBrightNess;
        getWindow().setAttributes(attribute);

        if (mProgressLayout.getVisibility() == View.GONE)
        {
            mProgressLayout.setVisibility(View.VISIBLE);
        }
        operationBgImg.setImageResource(R.mipmap.video_brightness_bg);
        ViewGroup.LayoutParams layoutParams = operationPercentImg.getLayoutParams();
        layoutParams.width = (int) (DensityUtils.dp2px(this , 94f) * mBrightNess);
        operationPercentImg.setLayoutParams(layoutParams);
    }

    private void updateTextViewWithTimeFormat(TextView tv , int milliSecond)
    {
        int second = milliSecond/1000;
        int hh = second/3600;
        int mm = second%3600/60;
        int ss = second%60;

        String timeStr = null;
        if (hh != 0)
        {
            timeStr = String.format("%02d:%02d:%02d" , hh , mm , ss);
        }
        else
        {
            timeStr = String.format("%02d:%02d" , mm , ss);
        }
        tv.setText(timeStr);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        //当横屏时
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
            mVolumeSeekBar.setVisibility(View.VISIBLE);
            mVolumeImg.setVisibility(View.VISIBLE);
            isFullScreen = true;

            //强制移除半屏状态
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else
        {
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT , DensityUtils.dp2px(this , 240f));
            mVolumeSeekBar.setVisibility(View.GONE);
            mVolumeImg.setVisibility(View.GONE);
            isFullScreen = false;

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    private void setVideoViewScale(int width , int height)
    {
        ViewGroup.LayoutParams layoutParams = mVideoView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        mVideoView.setLayoutParams(layoutParams);

        ViewGroup.LayoutParams layoutParams1 = mVideoLayout.getLayoutParams();
        layoutParams1.width = width;
        layoutParams1.height = height;
        mVideoLayout.setLayoutParams(layoutParams1);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mUIHandler.removeMessages(UPDATE_UI);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
