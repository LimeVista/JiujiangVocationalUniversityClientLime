package pers.lime.jjvu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import pers.lime.jjvu.html.HtmlHelper;
import pers.lime.jjvu.html.bean.MsgList;
import pers.lime.jjvu.info.InfoCode;
import pers.lime.jjvu.info.InfoText;
import pers.lime.jjvu.tools.FixKitKatTranslucentSystemBar;
import person.lime.view.MetroLoading;
import priv.lime.html.HtmlUnit;


public class NewsActivity extends AppCompatActivity {

    /**
     * 网站栏目URL（base）
     */
    public final static String pageUrl = "http://www.person.lime.jjvu.jx.cn/templet/default/ShowClass.jsp";

    /**
     * 用于实现区分栏目的连接符(常量)
     */
    private final String pageIdTag = "?id=";

    /**
     * 用于实现翻页的连接符(常量)
     */
    private final String nextPageTag = "&pn=";

    /**
     * 页码
     */
    private int page;

    /**
     * 网站栏目编号(id)
     */
    private String id;

    /**
     * 网页文本
     */
    private String html;

    /**
     * 原网页字符编码
     */
    private String pageCharSetOld = "GBK";

    /**
     * 网页名称
     */
    private String pageTitle;

    /**
     * 获取application全局变量
     */
    private LimeApp appVar;

    /**
     * WebView组件
     */
    private WebView webView;

    /**
     * 加载显示组件
     */
    private MetroLoading loading;


    /**
     * 初始化
     */
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //加载主题
        setTheme(((LimeApp)getApplication()).themeMain);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        //修复4.4状态栏沉浸Bug
        FixKitKatTranslucentSystemBar.fix(this);

        loading = (MetroLoading)findViewById(R.id.news_loading);
        init();
        getSupportActionBar().setTitle(pageTitle);
    }


    public void init() {

        Bundle bundle = this.getIntent().getExtras();
        //pageTitle = "校园新闻";
        pageTitle = bundle.getString("pageTitle");
        //id="911";
        id = bundle.getString("id");

        webView = (WebView) findViewById(R.id.webViewMsgList);
        page = 1;
        appVar = (LimeApp) getApplication();
        Log.i(InfoCode.getCode(InfoCode.LOADING), "WebView加载中...");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JsCallJava(), "Lime");
        //更新WebView显示
        update();
    }
    

    /**
     * JavaScript调用Java接口类
     */
    protected class JsCallJava {
        /**
         * 生成栏目新闻列表Json
         *
         * @return
         */
        @JavascriptInterface
        public String addMsgList() {
            Log.i(InfoCode.getCode(InfoCode.LOADING), InfoText.LOADING_NEW_LIST);
            String msg = null;
            try {
                //得到网页
                msg = HtmlHelper.getHtml(pageUrl + pageIdTag + id + nextPageTag + page, pageCharSetOld);
                //分析获得消息列表
                List<MsgList> msgList = HtmlHelper.catchList(msg);
                //转化为JSON
                msg = msgList.toString();
                page++;
            } catch (UnknownHostException e) {
                //网络断开、或永久迁移
                msg = InfoCode.getCode(InfoCode.NETWORK_DISCONNECTION);
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                //网络超时
                msg = InfoCode.getCode(InfoCode.NETWORK_TIMEOUT);
                e.printStackTrace();
            } catch (Exception e) {
                msg = InfoCode.getCode(InfoCode.UNKNOW_ERROR);
                e.printStackTrace();
            }
            return msg;
        }

        /**
         * 刷新
         */
        @JavascriptInterface
        public void refresh() {
            update();
        }

        @JavascriptInterface
        public void openArticle(String url){
            Intent it = new Intent();
            it.setClass(NewsActivity.this,ArticleActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("url",url);
            it.putExtras(bundle);
            startActivity(it);
        }
    }

    protected String getHtml() {
        String html = null;
        List<MsgList> msgList = null;
        try {
            String s = HtmlHelper.getHtml(pageUrl + pageIdTag + id + nextPageTag + page, pageCharSetOld);
            msgList = HtmlHelper.catchList(s);
        } catch (UnknownHostException e) {
            //网络异常
            Log.e(InfoCode.getCode(InfoCode.NETWORK_DISCONNECTION), InfoText.NETWORK_DISCONNECTION);
            e.printStackTrace();
            //返回错误页面
            return appVar.getNetworkErrorHtml();
        } catch (SocketTimeoutException e) {
            //网络超时
            Log.e(InfoCode.getCode(InfoCode.NETWORK_TIMEOUT), InfoText.NETWORK_TIMEOUT);
            e.printStackTrace();
            //返回错误页面
            return appVar.getNetworkErrorHtml();
        } catch (Exception e) {
            e.printStackTrace();
            //返回错误页面
            return appVar.getNetworkErrorHtml();
        }
        /*
        //从Assets拿出数据
        InputStream inputStream = getAssets().open("html/msglist.xhtml");
        //创建和文件大小相同的Bytes数组
        byte[] bytes = new byte[inputStream.available()];
        //一次性获取该文件数据
        inputStream.read(bytes);
        //处理得到的数据
        HtmlUnit unit = new HtmlUnitXtd(bytes);
        */
        page++;
        HtmlUnit unit = appVar.getMsgListUnit();
        //添加数据
        unit.addData(new HtmlUnit.DataBean("title", pageTitle), new HtmlUnit.DataBean("data", msgList.toString()));
        //生成HTML
        html = unit.makeHtml();
        return html;
    }

    /**
     * 更新网页
     */
    private void update() {
        //显示正在加载
        loading.setVisibility(View.VISIBLE);
        Thread load = new Thread(new Runnable() {
            @Override
            public void run() {
                html = getHtml();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String baseUrl = "file:///android_asset";
                        webView.loadDataWithBaseURL(baseUrl, html, "text/html", "UTF-8", null);
                        //关闭正在加载
                        loading.setVisibility(View.GONE);
                    }
                });
            }
        });
        load.start();
    }

    /**
     * 获取栏目编号ID
     *
     * @return 返回编号
     */
    public String getId() {
        return id;
    }

    /**
     * 设置栏目编号ID
     *
     * @param id 编号
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取原网页字符编码
     *
     * @return 字符编码
     */
    public String getPageCharSetOld() {
        return pageCharSetOld;
    }

    /**
     * 设置原网页字符编码
     *
     * @param pageCharSetOld 字符编码
     */
    public void setPageCharSetOld(String pageCharSetOld) {
        this.pageCharSetOld = pageCharSetOld;
    }

    /**
     * 获取网页标题
     *
     * @return 返回标题
     */
    public String getPageTitle() {
        return pageTitle;
    }

    /**
     * 设置网页标题
     *
     * @param pageTitle 标题
     */
    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }
}
