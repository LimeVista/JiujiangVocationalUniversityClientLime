package person.lime.tools;

import android.content.Context;

/**
 * Created by Lime(李振宇) on 2016-08-16.
 */
public  class StatusBarUtil {
    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static final int getStatusBarHeight(Context context)
    {
        Class<?> c = null;
        Object obj = null;
        java.lang.reflect.Field field = null;
        int x = 0;
        int statusBarHeight = 0;
        try
        {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
            return statusBarHeight;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return statusBarHeight;
    }
}

/*
    getDimension
    获取某个dimen的值,如果是dp或sp的单位,将其乘以density,如果是px,则不乘   返回float

    getDimensionPixelOffset
    获取某个dimen的值,如果是dp或sp的单位,将其乘以density,如果是px,则不乘  返回int

    getDimensionPixelSize
    则不管写的是dp还是sp还是px,都会乘以denstiy.
 */