package com.soundking.smartcampus.ijkview.cotroll;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.text.TextUtils;

import com.soundking.smartcampus.ijkview.interf.VideoPlayerIJK;


/**
 * Created by kxy on 2017/12/11.
 */

public class ScreenListener {
    private Context mContext;
    private ScreenBroadcastReceiver mScreenReceiver;
    private VideoPlayerIJK videoPlayer;

    public ScreenListener(Context context, VideoPlayerIJK videoPlayer) {
        this.videoPlayer = videoPlayer;
        mContext = context;
        mScreenReceiver = new ScreenBroadcastReceiver();
    }

    /**
     * screen状态广播接收者
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (TextUtils.equals(Intent.ACTION_SCREEN_ON, action)) { // 开屏

            } else if (TextUtils.equals(Intent.ACTION_SCREEN_OFF, action)) { // 锁屏
                if (videoPlayer != null && videoPlayer.isPlaying()) {
                    videoPlayer.pause();
                }
            } else if (TextUtils.equals(Intent.ACTION_USER_PRESENT, action)) { // 解锁
                if (videoPlayer != null && !videoPlayer.isPlaying()) {
                    videoPlayer.start();
                }
            }
        }
    }

    /**
     * 开始监听screen状态
     */
    public void begin() {
        registerListener();
        getScreenState();
    }

    /**
     * 获取screen状态
     */
    private void getScreenState() {
        PowerManager manager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if (!manager.isScreenOn()) {
            if (videoPlayer != null && videoPlayer.isPlaying()) {
                videoPlayer.pause();
            }
        }
    }

    /**
     * 停止screen状态监听
     */
    public void unregister() {
        if (mContext != null && mScreenReceiver != null) {
            mContext.unregisterReceiver(mScreenReceiver);
        }
    }

    /**
     * 启动screen状态广播接收器
     */
    private void registerListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(mScreenReceiver, filter);
    }

}
