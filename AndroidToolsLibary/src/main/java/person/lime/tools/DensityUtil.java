/**
 * Copyright (C) 2016 Lime.
 * Licensed under the MIT License(the "License");
 */
package person.lime.tools;

import android.content.Context;

/**
 * DensityUtil用于处理像素点与dp单位sp单位之间的相互转换
 * @author Lime(李振宇)
 * Created at 2016.08.16
 * @version 1.0
 */
public class DensityUtil {

    /**
     * 根据手机的分辨率从 dp 值 转成为 px(像素)值
     * @param context 被转换值的所在Context容器
     * @param dpValue dp值
     * @return px(像素)值
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素)值 转成为 dp 值
     * @param context 被转换值的所在Context容器
     * @param pxValue px(像素)值
     * @return dp值
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机分辨率，将 px 值转换为 sp 值，保证文字大小不变，利用（DisplayMetrics类中属性scaledDensity）
     * @param pxValue px(像素)值
     * @return sp值
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 根据手机分辨率，将 sp 值转换为 px 值，保证文字大小不变，利用（DisplayMetrics类中属性scaledDensity）
     * @param spValue sp值
     * @return px(像素)值
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}