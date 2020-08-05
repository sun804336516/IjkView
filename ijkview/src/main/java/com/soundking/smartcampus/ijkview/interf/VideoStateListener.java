package com.soundking.smartcampus.ijkview.interf;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 作者：sxw on 2019/6/20 10:41
 */
public interface VideoStateListener extends IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnCompletionListener, IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnInfoListener, IMediaPlayer.OnBufferingUpdateListener{
}
