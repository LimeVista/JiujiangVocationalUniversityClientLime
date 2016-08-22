package pers.lime.jjvu;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.StyleRes;

import priv.lime.html.HtmlUnit;
import priv.lime.html.HtmlUnitXtd;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;


/**
 * Created by Lime(李振宇) on 2016-7-30.
 * 这是一个Application全局变量池
 */
public class LimeApp extends Application {

    /**
     * 栏目消息列表HTML的全局保存副本，避免每次都去Assets文件夹获取
     */
    private HtmlUnit msgListUnit;

    /**
     * 文章HTML全局保存副本，避免每次都去Assets文件夹获取
     */
    private HtmlUnit articleUnit;

    /**
     * 网络异常提示页面，全局保存副本，避免每次都去Assets文件夹获取
     */
    private String networkErrorHtml;

    /**
     * 更新
     */
    public boolean isUpdate = false;

    /**
     * 主题
     */
    public
    @StyleRes
    int themeMain;
    public
    @StyleRes
    int themeAbout;
    public
    @StyleRes
    int themeNoActionBar;

    @Override
    public void onCreate() {
        //修改主题
        SharedPreferences preferences = getSharedPreferences("JJVU_Setting", Context.MODE_PRIVATE);
        themeMain = preferences.getInt("Theme", 0);
        themeAbout = preferences.getInt("Theme_About", 0);
        themeNoActionBar = preferences.getInt("Theme_No_Action_Bar", 0);
        //默认主题
        if (themeMain == 0) {
            themeMain = R.style.GreenTheme;
            themeAbout = R.style.GreenTheme_AboutStyle;
            themeNoActionBar = R.style.GreenTheme_NoActionBar;
        }
        setTheme(themeMain);
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                msgListUnit = initHtmlUnit("html/msglist.xhtml");
                articleUnit = initHtmlUnit("html/article.xhtml");
                networkErrorHtml = getAssetsText("html/network.html");
            }
        }).start();
        //全局变量初始化
    }

    /**
     * 获得当前版本字符串
     * @return  版本字符串
     */
    public  final String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "?.?.????";
        }
    }

    /**
     * 获取栏目消息列表HTML类
     *
     * @return 返回HtmlUnit全局变量副本
     */
    public HtmlUnit getMsgListUnit() {
        try {
            return msgListUnit.deepClone();
        } catch (Exception e) {
            //基本不会发生
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文章HTML类
     *
     * @return 返回HtmlUnit全局变量副本
     */
    public HtmlUnit getArticleUnit() {
        try {
            return articleUnit.deepClone();
        } catch (Exception e) {
            //基本不会发生
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取网络错误跳转页面
     *
     * @return Web Html
     */
    public String getNetworkErrorHtml() {
        return networkErrorHtml;
    }

    /**
     * 初始化HtmlUnit
     *
     * @param path 源路径(必须事Assets文件路径)
     * @return 已经初始化好的HtmlUnit
     */
    private HtmlUnit initHtmlUnit(String path) {
        try {
            //从Assets拿出数据
            InputStream inputStream = getAssets().open(path);
            //创建和文件大小相同的Bytes数组
            byte[] bytes = new byte[inputStream.available()];
            //一次性获取该文件数据
            inputStream.read(bytes);
            //处理得到的数据,并返回
            return new HtmlUnitXtd(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从Assets获得HTML
     *
     * @param path 源路径
     * @return 返回Text字符串流
     */
    public String getAssetsText(String path) {

        byte[] bytes = null;
        try {
            //从Assets拿出数据
            InputStream inputStream = getAssets().open(path);
            //创建和文件大小相同的Bytes数组
            bytes = new byte[inputStream.available()];
            //一次性获取该文件数据
            inputStream.read(bytes);
        } catch (IOException e) {
            try {
                //从Assets拿出数据
                InputStream inputStream = getAssets().open(path);
                //创建和文件大小相同的Bytes数组
                bytes = new byte[inputStream.available()];
                //一次性获取该文件数据
                inputStream.read(bytes);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}

