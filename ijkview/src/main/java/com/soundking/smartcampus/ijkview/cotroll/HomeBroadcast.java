package com.soundking.smartcampus.ijkview.cotroll;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.soundking.smartcampus.ijkview.interf.VideoPlayerIJK;


/**
 * Created by kxy on 2017/12/11.
 */

public class HomeBroadcast {
    private Context mContext;
    private HomeReceiver hr;
    private VideoPlayerIJK videoPlayer;

    public HomeBroadcast(Context context, VideoPlayerIJK videoPlayer) {
        mContext = context;
        this.videoPlayer = videoPlayer;
        hr = new HomeReceiver();
    }

    private class HomeReceiver extends BroadcastReceiver {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM_HOME_KEY_LONG = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                    if (videoPlayer != null && videoPlayer.isPlaying()) {
                        videoPlayer.pause();
                    }
                }
            }
        }

    }

    public void begin() {
        mContext.registerReceiver(hr, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    public void unregister() {
        if (mContext != null && hr != null) {
            mContext.unregisterReceiver(hr);
        }
    }
}
