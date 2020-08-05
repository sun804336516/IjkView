package com.soundking.smartcampus.ijkview.utils;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.soundking.smartcampus.ijkview.R;


/**
 * vcf测试数据
 *
 * @author jacen
 * @date 2018/8/14 14:35
 * @email jacen@iswsc.com
 */
public class AnimationUtil {

    /**
     * 顺时针自传 1000ms
     *
     * @param view
     */
    public static ObjectAnimator rotateCW(View view) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "rotation", 0, 359);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(1000);
        objectAnimator.setRepeatCount(-1);
        objectAnimator.start();
        return objectAnimator;
    }

    /**
     * 控件在右边 从自身往右移除控件
     *
     * @param context    案例参照视频播放 选择字幕
     * @param view
     * @param visibility
     */
    public static void translateViewRight_Left2Right(Context context, View view, int visibility) {
        animation(context, view, visibility, R.anim.translate_viewleft_left_2_right);
    }

    /**
     * 控件在右边 从右往左显示控件
     *
     * @param context    案例参照视频播放 选择字幕
     * @param view
     * @param visibility
     */
    public static void translateViewRight_Right2Left(Context context, View view, int visibility) {
        animation(context, view, visibility, R.anim.translate_viewleft_right_2_left);

    }

    /**
     * 控件在底部 从自身往下移除控件
     *
     * @param context 案例参照视频播放底部播放栏
     * @param view
     */
    public static void translateViewBottom_Top2Bottom(Context context, View view, int visibility) {
        animation(context, view, visibility, R.anim.translate_viewbottom_top_2_bottom);

    }

    /**
     * 控件在底部 从底部往自身显示控件
     *
     * @param context 案例参照视频播放底部播放栏
     * @param view
     */
    public static void translateViewBottom_Bottom2Top(Context context, View view, int visibility) {
        animation(context, view, visibility, R.anim.translate_viewbottom_bottom_2_top);
    }

    /**
     * 控件在顶部部 从顶部往自身移动显示控件
     *
     * @param context 案例参照视频播放顶部 标题栏
     * @param view
     */
    public static void translateViewTop_Top2Bottom(Context context, View view, int visibility) {
        animation(context, view, visibility, R.anim.translate_viewtop_top_2_bottom);

    }

    /**
     * 控件在顶部部 从自身往顶部移除控件
     *
     * @param context 案例参照视频播放顶部 标题栏
     * @param view
     */
    public static void translateViewTop_Bottom2Top(Context context, View view, int visibility) {
        animation(context, view, visibility, R.anim.translate_viewtop_bottom_2_top);

    }

    /**
     * 逐渐透明控件300ms
     *
     * @param context
     * @param view
     */
    public static void alpha2Gone(Context context, View view) {
        animation(context, view, View.GONE, R.anim.alpha_2_gone);
    }

    /**
     * 逐渐透明控件300ms
     *
     * @param context
     * @param view
     */
    public static void alpha2Invisible(Context context, View view) {
        animation(context, view, View.INVISIBLE, R.anim.alpha_2_gone);
    }

    /**
     * 逐渐显示控件300ms
     *
     * @param context
     * @param view
     */
    public static void alpha2Visible(Context context, View view) {
        animation(context, view, View.VISIBLE, R.anim.alpha_2_visible);

    }

    private static void animation(Context context, View view, int visibility, int anim) {
        Animation animation = AnimationUtils.loadAnimation(context, anim);
        view.startAnimation(animation);
        if (visibility == View.VISIBLE) {
            view.setVisibility(visibility);
        } else {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (view != null) {
                        view.setVisibility(visibility);
                    }
                }
            }, 300);
        }

    }


    public static void clearAnimation(View view, int visibility) {
        view.clearAnimation();
        view.setVisibility(visibility);
    }
}
