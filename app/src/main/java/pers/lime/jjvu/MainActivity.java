package pers.lime.jjvu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import person.lime.tools.ColorUtil;
import person.lime.tools.StatusBarUtil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    private LimeApp app;

    @StyleRes
    private int currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //加载主题
        app = (LimeApp)getApplication();
        currentTheme = app.themeNoActionBar;
        setTheme(currentTheme);
        //Log.i("Theme:","theme:"+((LimeApp)getApplication()).themeNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //修复4.4状态栏沉浸Bug
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            //透明状态栏,Fix Bug
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏,存在Bug禁止使用
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //fix Bug 巧妙的使用padding
            findViewById(R.id.main_app_bar_layout).setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);
            SystemBarTintManager manager = new SystemBarTintManager(this);
            manager.setStatusBarTintEnabled(true);
            manager.setNavigationBarTintEnabled(true);
            manager.setStatusBarTintColor(ColorUtil.getAttrColor(this, R.attr.colorPrimary));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentTheme != app.themeNoActionBar)
            recreate();
        update();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //跳转Activity
        Intent it = new Intent();

        if (id == R.id.nav_notice) {
            gotoNewActivity(it, getString(R.string.notice_title), getString(R.string.notice_id));
        } else if (id == R.id.nav_news) {
            gotoNewActivity(it, getString(R.string.school_news_title), getString(R.string.school_news_id));
        } else if (id == R.id.nav_dynamicMsg) {
            gotoNewActivity(it, getString(R.string.dynamic_msg_title), getString(R.string.dynamic_msg_id));
        } else if (id == R.id.nav_score) {
            it.setClass(MainActivity.this, QueryGradeActivity.class);
            startActivity(it);
        } else if (id == R.id.nav_theme) {
            it.setClass(MainActivity.this, ThemeActivity.class);
            startActivity(it);
        } else if (id == R.id.nav_setting) {
            it.setClass(MainActivity.this, SettingActivity.class);
            startActivity(it);
        } else if (id == R.id.nav_about) {
            //跳转到另一个Activity
            it.setClass(MainActivity.this, AboutActivity.class);
            startActivity(it);
        } else if (id == R.id.nav_exit) {
            finish();
            System.exit(0);
        }
        //延时关闭抽屉，要不然动画太可怕
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //0.2秒，应该没人能再返回看到动画了吧
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });
            }
        }).start();
        return true;
    }

    /**
     * 跳转到新闻Activity
     *
     * @param it    Intent
     * @param title 标题
     * @param id    网页id
     * @return 是否执行成功
     */
    private void gotoNewActivity(Intent it, String title, String id) {
        it.setClass(MainActivity.this, NewsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("pageTitle", title);
        bundle.putString("id", id);
        it.putExtras(bundle);
        startActivity(it);
    }

    private void update() {
        if (!app.isUpdate) {
            app.isUpdate = true;
            //更新检查
            PgyUpdateManager.register(this,
                    new UpdateManagerListener() {
                        @Override
                        public void onUpdateAvailable(final String result) {
                            // 将新版本信息封装到AppBean中
                            final AppBean appBean = getAppBeanFromString(result);
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("新版本驾到...")
                                    .setMessage(appBean.getReleaseNote())
                                    .setNegativeButton(R.string.update_cancel, null)
                                    .setPositiveButton(
                                            R.string.update_ok,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    startDownloadTask(
                                                            MainActivity.this,
                                                            appBean.getDownloadURL());
                                                }
                                            }).show();
                        }

                        @Override
                        public void onNoUpdateAvailable() {
                        }
                    });
        }
    }
}
