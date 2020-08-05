package com.soundking.smartcampus.ijkview.view;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.soundking.smartcampus.ijkview.R;
import com.soundking.smartcampus.ijkview.interf.VideoPlayerIJK;
import com.soundking.smartcampus.ijkview.mediacontroller.IMediaController;
import com.soundking.smartcampus.ijkview.utils.AnimationUtil;


/**
 * sxw on 2019/6/19 14:42
 * 播放器控制UI
 */
public class CommonVideoPlayContorller extends RelativeLayout implements IMediaController {
    private static final String TAG = "视频UI控制";

    ImageView mPlayIv;
    TextView mCurrentPositionTv;
    TextView mDurationTv;
    SeekBar mSeekBar;

    LinearLayout mPlayOperateLayout;

    private FragmentActivity mActivity;
    private boolean isDrag = false;

    private float mBrightness = -1;
    private AudioManager mAudioManager;
    private int mVolume = -1;
    private VideoPlayerIJK mVideoPlayer;
    private long maxSlop = 4 * 60 * 1000;//屏幕滑动最大跨越时间  4分钟
    private int slopPosition;

    private static final int HIDE_VIEW = 0x111;
    private static final int UPDATE_POSITION = 0x112;
    private boolean autoHideCroller = false;

    private boolean canUpdateTime = true;
    private boolean canUpdateProgress = true;
    private boolean canUpdateCurentPosition = true;
    private boolean canUpdateDuration = true;

    public void setCanUpdateTime(boolean canUpdateTime) {
        this.canUpdateTime = canUpdateTime;
    }

    public void setCanUpdateProgress(boolean canUpdateProgress) {
        this.canUpdateProgress = canUpdateProgress;
    }

    public void setCanUpdateCurentPosition(boolean canUpdateCurentPosition) {
        this.canUpdateCurentPosition = canUpdateCurentPosition;
    }

    public void setCanUpdateDuration(boolean canUpdateDuration) {
        this.canUpdateDuration = canUpdateDuration;
    }

    public void setAutoHideCroller(boolean autoHideCroller) {
        this.autoHideCroller = autoHideCroller;
        mHandler.removeMessages(HIDE_VIEW);
        mHandler.sendEmptyMessageDelayed(HIDE_VIEW, 5000);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case HIDE_VIEW:
                    showOrHideView(false);
                    break;
                case UPDATE_POSITION:
                    if (mActivity == null || mVideoPlayer == null
                            || getVisibility() != View.VISIBLE || !canUpdateTime || isDrag || !canSeekTo()) {
                        mHandler.sendEmptyMessageDelayed(UPDATE_POSITION, 500);
                        return;
                    }
                    int currentPosition = mVideoPlayer.getCurrentPosition();
                    int duration = mVideoPlayer.getDuration();
                    if (canUpdateProgress) {
                        mSeekBar.setMax(duration);
                        mSeekBar.setProgress(currentPosition);
                    }
                    if (canUpdateDuration) {
                        updateDuration(duration);
                    }
                    if (canUpdateCurentPosition) {
                        updateCurrentPosiition(currentPosition);
                    }

                    mHandler.sendEmptyMessageDelayed(UPDATE_POSITION, 500);
                    break;
            }
        }
    };

    public CommonVideoPlayContorller(@NonNull Context context) {
        this(context, null);
    }

    public CommonVideoPlayContorller(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonVideoPlayContorller(@NonNull Context context, @Nullable AttributeSet attrs,
                                     int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.common_videoplaycontroller_layout, this, true);
        mPlayIv = findViewById(R.id.playIv);
        mCurrentPositionTv = findViewById(R.id.currentTimeTv);
        mDurationTv = findViewById(R.id.durationTv);
        mSeekBar = findViewById(R.id.seekBar);
        mPlayOperateLayout = findViewById(R.id.playOperateLayout);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (canUpdateCurentPosition) {
                    updateCurrentPosiition(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDrag = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDrag = false;
                /*拖拽结束，开始跳转进度*/
                seekTo(seekBar.getProgress());
            }
        });

        mPlayIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoPlayer.isPlaying()) {
                    setPlayImageViewPlayRes();
                    mVideoPlayer.pause();
                } else {
                    setPlayImageViewPauseRes();
                    mVideoPlayer.start();
                }
            }
        });
    }

    public void init(FragmentActivity mActivity, VideoPlayerIJK mVideoPlayer) {
        this.mActivity = mActivity;
        this.mVideoPlayer = mVideoPlayer;
        this.mAudioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        this.mBrightness = mActivity.getWindow().getAttributes().screenBrightness;
        this.mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    private void showOrHideView(boolean show) {
        mHandler.removeMessages(HIDE_VIEW);
        if (show) {
            AnimationUtil.translateViewBottom_Bottom2Top(mActivity, mPlayOperateLayout, View.VISIBLE);
            mHandler.sendEmptyMessageDelayed(HIDE_VIEW, 5000);
        } else {
            AnimationUtil.translateViewBottom_Top2Bottom(mActivity, mPlayOperateLayout, View.GONE);
        }
    }

    private void seekTo(int position) {
        if (mVideoPlayer != null && canSeekTo() && position <= mVideoPlayer.getDuration()) {
            mVideoPlayer.seekTo(position);
        }
    }

    @Override
    public void updateUiIdle() {
        Log.d(TAG, "updateUiIdle");
        stopPositionTimer();
        mPlayIv.setEnabled(true);
        setPlayImageViewPlayRes();
        mSeekBar.setEnabled(false);
        updateCurrentPosiition(mVideoPlayer == null ? 0 : mVideoPlayer.getCurrentPosition());
        updateDuration(mVideoPlayer == null ? 0 : mVideoPlayer.getDuration());
    }

    @Override
    public void updateUiPaused() {
        Log.d(TAG, "updateUiPaused");

        mPlayIv.setEnabled(true);
        setPlayImageViewPlayRes();
    }

    @Override
    public void setVideoView(IjkVideoView ijkVideoView) {

    }

    @Override
    public void onSingleClick() {
        showOrHideView(mPlayOperateLayout.getVisibility() != View.VISIBLE);
    }

    @Override
    public void onDoubleClick() {

    }

    @Override
    public void updateUiPreparing() {
        Log.d(TAG, "updateUiPreparing");

        mPlayIv.setEnabled(false);

        mSeekBar.setEnabled(false);
    }

    @Override
    public void updateUiPrepared() {
        Log.d(TAG, "updateUiPrepared");

        mPlayIv.setEnabled(true);

        mSeekBar.setEnabled(false);
        updateCurrentPosiition(mVideoPlayer == null ? 0 : mVideoPlayer.getCurrentPosition());
        updateDuration(mVideoPlayer == null ? 0 : mVideoPlayer.getDuration());
    }

    @Override
    public void updateUiPlaying() {
        Log.d(TAG, "updateUiPlaying");

        startPositionTimer();
        mSeekBar.setEnabled(canSeekTo());
        mPlayIv.setEnabled(true);
        setPlayImageViewPauseRes();
        if (autoHideCroller) {
            mHandler.removeMessages(HIDE_VIEW);
            mHandler.sendEmptyMessageDelayed(HIDE_VIEW, 5000);
        }

    }

    @Override
    public void updateUiCompleted() {
        stopPositionTimer();
        mSeekBar.setProgress(mSeekBar.getMax());
        mSeekBar.setEnabled(false);
        mPlayIv.setEnabled(true);
        setPlayImageViewPlayRes();
    }

    public void setPlayImageViewPlayRes() {
        setPlayImageViewRes(R.mipmap.icon_live_source_play_black);
    }

    public void setPlayImageViewPauseRes() {
        setPlayImageViewRes(R.mipmap.icon_live_source_pause_black);
    }

    public void setPlayImageViewRes(int resId) {
        if (mPlayIv != null) {
            mPlayIv.setImageResource(resId);
        }
    }

    private void startPositionTimer() {
        stopPositionTimer();
        mHandler.sendEmptyMessage(UPDATE_POSITION);
    }

    private void stopPositionTimer() {
        mHandler.removeMessages(UPDATE_POSITION);
    }

    private void updateDuration(long duration) {
        if (mDurationTv != null && duration > 0) {
            mDurationTv.setText(FormatMediaDuration(duration));
        }
    }

    private void updateCurrentPosiition(long currentPosition) {
        if (mCurrentPositionTv != null && currentPosition > 0) {
            mCurrentPositionTv.setText(FormatMediaDuration(currentPosition));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeMessages(HIDE_VIEW);
        stopPositionTimer();
    }

    /**
     * seekbar是否可以拖拽
     * 直播流的话没有时长，不能拖拽
     *
     * @return
     */
    protected boolean canSeekTo() {
        return mVideoPlayer != null && mVideoPlayer.getDuration() > 0;
    }

    public static String FormatMediaDuration(long duration) {
        return FormatMediaDuration(duration, ":");
    }

    /**
     * 格式化时长
     *
     * @param duration_ 毫秒值
     * @param split     :
     * @return
     */
    public static String FormatMediaDuration(long duration_, String split) {
        long duration = duration_ / 1000;
        String h;
        String m;
        h = "%d" + split + "%02d" + split + "%02d";
        m = "%d" + split + "%02d";
        int hour, minute, second;
        if (duration <= 60) {
            return String.format(m, 0, duration);
        } else if (duration <= 60 * 60) {
            minute = (int) (duration / 60);
            second = (int) (duration % 60);
            return String.format(m, minute, second);
        } else {
            hour = (int) (duration / (60 * 60));
            minute = (int) (duration % (60 * 60) / 60);
            second = (int) (duration % 60);
            return String.format(h, hour, minute, second);
        }
    }

}
