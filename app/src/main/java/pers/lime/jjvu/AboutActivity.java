package pers.lime.jjvu;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private ActionBar bar;
    private Button schoolBtn,
            authorBtn,
            updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //加载主题
        setTheme(((LimeApp) getApplication()).themeAbout);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bar = getSupportActionBar();

        //用来添加Material Design Activity左上角返回按钮
        bar.setDisplayHomeAsUpEnabled(true);
        //不显示标题
        bar.setDisplayShowTitleEnabled(false);
        //去掉阴影
        bar.setElevation(0);
        authorBtn = (Button) findViewById(R.id.about_author_btn);
        schoolBtn = (Button) findViewById(R.id.about_school_btn);
        updateBtn = (Button) findViewById(R.id.update_button);
        //Android 4.4 以下特效全靠手写，我擦勒,而且还挺丑
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            authorBtn.setOnTouchListener(this);
            schoolBtn.setOnTouchListener(this);
            updateBtn.setOnTouchListener(this);
        }
        authorBtn.setOnClickListener(this);
        schoolBtn.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
        //设置版本
        ((TextView) findViewById(R.id.about_ver_text)).setText("当前版本:" + ((LimeApp) getApplication()).getVersion());
    }

    @Override
    protected void onStart() {
        super.onStart();
        //再Android 5.0提供特效
        setButtonStyle(updateBtn);
        setButtonStyle(authorBtn);
        setButtonStyle(schoolBtn);
    }

    /**
     * fix bug:用来修复Android 4.4 版本Button不支持矢量背景图,既然不支持那就不在4.4下提供这种特效
     *
     * @param button 按钮类
     */
    private void setButtonStyle(Button button) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            // V21直接上特效
            button.setBackgroundResource(R.drawable.button_color_style);
        } else {
            //v19只能上颜色，怪我咯
            button.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == authorBtn) {
            openSnackBarHint(R.string.about_author_more);
        } else if (v == schoolBtn) {
            openSnackBarHint(R.string.about_school_more);
        } else if (v == updateBtn) {
            PgyUpdateManager.register(this,
                    new UpdateManagerListener() {
                        @Override
                        public void onUpdateAvailable(final String result) {
                            // 将新版本信息封装到AppBean中
                            final AppBean appBean = getAppBeanFromString(result);
                            new AlertDialog.Builder(AboutActivity.this)
                                    .setTitle("新版本驾到...")
                                    .setMessage(appBean.getReleaseNote())
                                    .setNegativeButton(R.string.update_cancel,null)
                                    .setPositiveButton(
                                            R.string.update_ok,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    startDownloadTask(
                                                            AboutActivity.this,
                                                            appBean.getDownloadURL());
                                                }
                                            }).show();
                        }

                        @Override
                        public void onNoUpdateAvailable() {
                            openSnackBarHint(R.string.version_latest);
                        }
                    });
        }
    }

    @TargetApi(19)
    @Override
    public boolean onTouch(View v, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setBackgroundColor(getAttributeColor(R.attr.colorAccent));
                break;
            case MotionEvent.ACTION_UP:
                v.setBackgroundColor(Color.WHITE);
                break;
        }
        return false;
    }

    /**
     * 获取Attr属性的资源ID
     *
     * @param attr 属性ID
     * @return 颜色值
     */
    private int getAttributeColor(int attr) {
        TypedValue typedValue = new TypedValue();
        this.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }

    /**
     * 用于显示提示消息，但不做消息处理
     *
     * @param resultId res消息id
     */
    private void openSnackBarHint(int resultId) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.about_layout), resultId, Snackbar.LENGTH_LONG);
        //设置背景色
        View v = snackbar.getView();
        v.setBackgroundColor(getAttributeColor(R.attr.colorPrimary));
        //设置字体颜色
        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(this, R.color.colorTitle));
        snackbar.show();
    }

    /**
     * 用于显示提示消息，但不做消息处理
     *
     * @param message 消息
     */
    private void openSnackBarHint(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.query_grade_layout), message, Snackbar.LENGTH_LONG);
        //设置背景色
        View v = snackbar.getView();
        v.setBackgroundColor(getAttributeColor(R.attr.colorPrimary));
        //设置字体颜色
        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(this, R.color.colorTitle));
        snackbar.show();
    }


    //已经在Activity实现
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case android.R.id.home:
//                //终结Activity
//                finish();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
}
