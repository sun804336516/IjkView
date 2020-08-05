package com.soundking.smartcampus.ijkview.mediacontroller;

import com.soundking.smartcampus.ijkview.view.IjkVideoView;

public interface IMediaController {

    /**
     * 播放错误 或者 播放处于刚刚初始化中
     */
    void updateUiIdle();

    /**
     * 播放开始准备
     */
    void updateUiPreparing();

    /**
     * 播放准备完毕，即将开始播放
     */
    void updateUiPrepared();

    /**
     * 播放结束
     */
    void updateUiCompleted();

    /**
     * 开始播放
     */
    void updateUiPlaying();

    /**
     * 播放暂停
     */
    void updateUiPaused();

    void setVideoView(IjkVideoView ijkVideoView);

    /**
     * 界面单击事件
     */
    void onSingleClick();

    /**
     * 界面双击事件
     */
    void onDoubleClick();


}
