package com.soundking.smartcampus.ijkview.cotroll;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.soundking.smartcampus.ijkview.R;
import com.soundking.smartcampus.ijkview.interf.VideoPlayerIJK;
import com.soundking.smartcampus.ijkview.interf.VideoStateListener;
import com.soundking.smartcampus.ijkview.mediacontroller.IMediaController;
import com.soundking.smartcampus.ijkview.view.IjkVideoView;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;

public class VideoPlayerIJKImpl implements DefaultLifecycleObserver, VideoPlayerIJK, IjkVideoView.OnPlayerStateListener {
    private IjkVideoView mVV = null;
    private FragmentActivity mActivity;
    private ScreenListener screenListener;
    private HomeBroadcast homeBroadcast;
    private MobliePhoneStateListener mobliePhoneStateListener;
    private TelephonyManager telManager;
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public VideoPlayerIJKImpl(FragmentActivity activity) {
        this(activity, false);
    }

    /**
     * @param activity
     * @param mediCodec 是否硬解
     */
    public VideoPlayerIJKImpl(FragmentActivity activity, boolean mediCodec) {
        mActivity = activity;
        mVV = new IjkVideoView(activity);
        mVV.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
        mVV.setMediaCodec(mediCodec);
    }

    @Override
    public void init(VideoStateListener videoStateListener) {

        mVV.setBackgroundResource(R.color.black);
        mVV.setOnPreparedListener(videoStateListener);
        mVV.setOnCompletionListener(videoStateListener);
        mVV.setOnErrorListener(videoStateListener);
        mVV.setOnInfoListener(videoStateListener);
        mVV.setOnBufferingUpdateListener(videoStateListener);
        mVV.setOnPlayerStateListener(this);
    }

    @Override
    public void setUrl(String url) {
        mVV.setVideoPath(url);
    }

    @Override
    public IjkVideoView getVideoView() {
        return mVV;
    }

    @Override
    public void pause() {
        mVV.pause();
    }

    @Override
    public void enterForeground() {
        mVV.enterForeground();
    }

    @Override
    public void enterBackground() {
        mVV.enterBackground();
    }

    @Override
    public void start() {
        mVV.start();
    }

    @Override
    public void resume() {
        mVV.resume();
    }

    @Override
    public void seekTo(int progress) {
        mVV.seekTo(progress);
    }

    @Override
    public void stopPlayback() {
        mVV.stopPlayback();
    }

    @Override
    public boolean isPlaying() {
        return mVV.isPlaying();
    }

    @Override
    public boolean isPlayCompleted() {
        return mVV.getCurrentState() == IjkVideoView.STATE_PLAYBACK_COMPLETED;
    }

    @Override
    public int getDuration() {
        return mVV.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mVV.getCurrentPosition();
    }

    @Override
    public void setVolume(float left, float right) {
        mVV.setVolume(left, right);
    }

    @Override
    public void setMediaController(IMediaController mediaController) {
        mVV.setMediaController(mediaController);
    }

    @Override
    public ITrackInfo[] getTrackInfo() {
        return mVV.getTrackInfo();
    }

    @Override
    public void selectTrack(int stream) {
        mVV.selectTrack(stream);
    }

    @Override
    public void deSelectTrack(int stream) {
        mVV.deselectTrack(stream);
    }

    @Override
    public int getSelectedTrack(int stream) {
        return mVV.getSelectedTrack(stream);
    }

    @Override
    public void onPlayerStateChanged(int newState) {
        if (mActivity != null) {
            changeState();
        }
    }

    /**
     * 播放器状态改变的回调
     */
    public void changeState() {
        mainThreadHandler.post(new Runnable() {

            @Override
            public void run() {
                int status = mVV.getCurrentState();
//                if (videoPlayerView != null && mActivity != null) {
//
//                    if (status == IjkVideoView.STATE_IDLE || status == IjkVideoView.STATE_ERROR) {
//                        videoPlayerView.updateUiIdle();
//                    } else if (status == IjkVideoView.STATE_PREPARING) {
//                        videoPlayerView.updateUiPreparing();
//                    } else if (status == IjkVideoView.STATE_PREPARED) {
//                        videoPlayerView.updateUiPrepared();
//                    } else if (status == IjkVideoView.STATE_PLAYBACK_COMPLETED) {
//                        videoPlayerView.updateUiCompleted();
//                    } else if (status == IjkVideoView.STATE_PLAYING) {
//                        videoPlayerView.updateUiPlaying();
//                    } else if (status == IjkVideoView.STATE_PAUSED) {
//                        videoPlayerView.updateUiPaused();
//                    }
//                }
            }

        });

    }

    private void unRegisterListener() {
        if (mobliePhoneStateListener != null && telManager != null) {
            telManager.listen(mobliePhoneStateListener, PhoneStateListener.LISTEN_NONE);
            mobliePhoneStateListener = null;
            telManager = null;
        }
        if (screenListener != null) {
            screenListener.unregister();
            screenListener = null;
        }
        if (homeBroadcast != null) {
            homeBroadcast.unregister();
            homeBroadcast = null;
        }
    }

    private void registerListener() {
        if (telManager == null) {
            telManager = (TelephonyManager) mActivity.getSystemService(Context.TELEPHONY_SERVICE);
        }
        if (mobliePhoneStateListener == null) {
            mobliePhoneStateListener = new MobliePhoneStateListener(this);
        }
        telManager.listen(mobliePhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        if (screenListener == null) {
            screenListener = new ScreenListener(mActivity, this);
        }
        screenListener.begin();
        if (homeBroadcast == null) {
            homeBroadcast = new HomeBroadcast(mActivity, this);
        }
        homeBroadcast.begin();
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        mVV.pause();
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        enterBackground();
        unRegisterListener();
        if (isPlaying()) {
            pause();
        }
        IjkMediaPlayer.native_profileEnd();
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        enterForeground();
        registerListener();
        if (!isPlaying() && !isPlayCompleted()) {
            start();
        }
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        mActivity = null;
        stopPlayback();
    }
}
