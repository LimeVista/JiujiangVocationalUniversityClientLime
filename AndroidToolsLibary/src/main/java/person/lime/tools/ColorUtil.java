/**
 * Copyright (C) 2016 Lime.
 * Licensed under the MIT License(the "License");
 */

package person.lime.tools;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

/**
 * ColorUtil 用于处理颜色资源的和转换
 * @author Lime(李振宇)
 * Created at 2016.08.16
 * @version 1.0
 */
public class ColorUtil {

    /**
     * 根据Attribute属性的资源ID值，获取颜色值
     * @param context Context容器
     * @param attr 属性ID
     * @return 颜色值
     */
    public static int getAttrColor(Context context,@AttrRes int attr) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }

    /**
     * 根据Resources的Color id值获取颜色值
     * @param context Context容器
     * @param id res中颜色的id值
     * @return 颜色值
     */
    public static final int getColorResource(Context context,@ColorRes int id){
       return  ContextCompat.getColor(context,id);
    }

    /**
     * 根据Resources的Color id值获取颜色值
     * @param context Context容器
     * @param id res中颜色的id值
     * @return 颜色值
     */
    @Deprecated
    public static final int getColorResourceOld(Context context,@ColorRes int id){
        return context.getResources().getColor(id);
    }

    /**
     * 根据颜色值值生成ColorDrawable
     * @param color 颜色值
     * @return ColorDrawable画布
     */
    public static ColorDrawable getColorDrawable(int color){
        return new ColorDrawable(color);
    }

    /**
     * 根据Resources的Color id值生成ColorDrawable
     * @param context Context容器
     * @param id res中颜色的id值
     * @return ColorDrawable画布
     */
    public static ColorDrawable getColorDrawable(Context context,@ColorRes int id){
        return new ColorDrawable(getColorResource(context,id));
    }


    /**
     * 同一个 Content 容器下的ColorUtil
     */
    public static class ColorUtilCurrentContext{

        private Context context;

        public ColorUtilCurrentContext(Context context){
            this.context = context;
        }

        /**
         * 根据Attribute属性的资源ID值，获取颜色值
         * @param attr 属性ID
         * @return 颜色值
         */
        public final int getAttrColor(@AttrRes int attr) {
            return ColorUtil.getAttrColor(context,attr);
        }

        /**
         * 根据Resources的Color id值获取颜色值
         * @param id res中颜色的id值
         * @return 颜色值
         */
        public final int getColorResource(@ColorRes int id){
            return ColorUtil.getColorResource(context,id);
        }

        /**
         * 根据Resources的Color id值生成ColorDrawable
         * @param id res中颜色的id值
         * @return ColorDrawable画布
         */
        public ColorDrawable getColorDrawable(@ColorRes int id){
            return ColorUtil.getColorDrawable(context,id);
        }

    }
}
