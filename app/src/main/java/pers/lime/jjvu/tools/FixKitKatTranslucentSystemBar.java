package pers.lime.jjvu.tools;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import pers.lime.jjvu.R;
import person.lime.tools.ColorUtil;

/**
 * Created by Lime(李振宇) on 2016-08-18.
 */
public class FixKitKatTranslucentSystemBar {

    /**
     * 修复状态栏透明
     * @param activity
     */
    public static void fix(AppCompatActivity activity){
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager manager = new SystemBarTintManager(activity);
            manager.setStatusBarTintEnabled(true);
            manager.setNavigationBarTintEnabled(true);
            manager.setStatusBarTintColor(ColorUtil.getAttrColor(activity, R.attr.colorPrimary));
        }
    }
}
