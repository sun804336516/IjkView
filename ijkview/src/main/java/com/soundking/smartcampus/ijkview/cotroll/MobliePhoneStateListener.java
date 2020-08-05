package com.soundking.smartcampus.ijkview.cotroll;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.soundking.smartcampus.ijkview.interf.VideoPlayerIJK;


/**
 * 作者：Administrator on 2018/7/19/019 09:48
 * 邮箱：sun91985415@163.com
 */

public class MobliePhoneStateListener extends PhoneStateListener {
    private VideoPlayerIJK videoPlayer;

    public MobliePhoneStateListener(VideoPlayerIJK videoPlayer) {
        this.videoPlayer = videoPlayer;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                if (videoPlayer != null) {
                    videoPlayer.start();
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
            case TelephonyManager.CALL_STATE_RINGING:
                if (videoPlayer != null) {
                    videoPlayer.pause();
                }
                break;
            default:
                break;
        }
    }
}
