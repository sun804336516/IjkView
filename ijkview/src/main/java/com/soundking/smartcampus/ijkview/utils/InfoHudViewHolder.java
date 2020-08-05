package com.soundking.smartcampus.ijkview.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.MediaPlayerProxy;

public class InfoHudViewHolder {
    private static final String TAG = "InfoHudViewHolder";
    private IMediaPlayer mMediaPlayer;
    private LivePlayCallBack mLivePlayCallBack;
    private long lastTcpSpeedTime;
    /*播放速度持续0的时长 则默认播放失败*/
    private long tcpDuration = 10 * 1000;

    public void setTcpDuration(long tcpDuration) {
        this.tcpDuration = tcpDuration;
    }

    public InfoHudViewHolder(LivePlayCallBack livePlayCallBack) {
        mLivePlayCallBack = livePlayCallBack;
    }

    public void setMediaPlayer(IMediaPlayer mp) {
        mMediaPlayer = mp;
        mHandler.removeMessages(MSG_UPDATE_HUD);
        lastTcpSpeedTime = System.currentTimeMillis();
        if (mMediaPlayer != null) {
            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_HUD, 500);
        }
    }

    public void removeMsg() {
        mHandler.removeMessages(MSG_UPDATE_HUD);
    }

    private static final int MSG_UPDATE_HUD = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_HUD: {
                    IjkMediaPlayer mp = null;
                    if (mMediaPlayer == null) {
                        break;
                    }
                    if (mMediaPlayer instanceof IjkMediaPlayer) {
                        mp = (IjkMediaPlayer) mMediaPlayer;
                    } else if (mMediaPlayer instanceof MediaPlayerProxy) {
                        MediaPlayerProxy proxy = (MediaPlayerProxy) mMediaPlayer;
                        IMediaPlayer internal = proxy.getInternalMediaPlayer();
                        if (internal != null && internal instanceof IjkMediaPlayer) {
                            mp = (IjkMediaPlayer) internal;
                        }
                    }
                    if (mp == null) {
                        break;
                    }

                    boolean canTcp = true;
                    long tcpSpeed = mp.getTcpSpeed();
                    Log.d(TAG, "tcpSpeed:" + tcpSpeed);
                    if (tcpSpeed > 0) {
                        lastTcpSpeedTime = System.currentTimeMillis();
                    } else {
                        if (System.currentTimeMillis() - lastTcpSpeedTime > tcpDuration) {
                            canTcp = false;
                            if (mLivePlayCallBack != null) {
                                mLivePlayCallBack.playError();
                            }
                        }
                    }
                    if (canTcp) {
                        mHandler.removeMessages(MSG_UPDATE_HUD);
                        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_HUD, 500);
                    }
                }
            }
        }
    };

    public interface LivePlayCallBack {
        void playError();
    }
}
