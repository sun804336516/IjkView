package com.soundking.smartcampus.ijkview.interf;


import com.soundking.smartcampus.ijkview.mediacontroller.IMediaController;
import com.soundking.smartcampus.ijkview.view.IjkVideoView;

import tv.danmaku.ijk.media.player.misc.ITrackInfo;

public interface VideoPlayerIJK {

    void init(VideoStateListener videoStateListener);

    void setUrl(String url);

    IjkVideoView getVideoView();

    void pause();

    void enterForeground();

    void enterBackground();

    void start();

    void resume();

    void seekTo(int progress);

    void stopPlayback();

    boolean isPlaying();

    boolean isPlayCompleted();

    int getDuration();

    int getCurrentPosition();

    void setVolume(float left, float right);

    void setMediaController(IMediaController mediaController);

    ITrackInfo[] getTrackInfo();

    void selectTrack(int stream);

    void deSelectTrack(int stream);

    int getSelectedTrack(int stream);


}
