package pers.lime.jjvu;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;


import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import pers.lime.jjvu.html.HtmlHelper;
import pers.lime.jjvu.html.bean.Article;
import pers.lime.jjvu.info.InfoCode;
import pers.lime.jjvu.info.InfoText;
import pers.lime.jjvu.tools.FixKitKatTranslucentSystemBar;
import person.lime.tools.ColorUtil;
import person.lime.tools.DensityUtil;
import person.lime.tools.SaveImageToGalleryUtil;
import priv.lime.html.HtmlUnit;

public class ArticleActivity extends AppCompatActivity {

    private WebView webView;

    private LimeApp appVar;

    private CollapsingToolbarLayout toolbarLayout;

    private Toolbar bar;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //加载主题
        setTheme(((LimeApp) getApplication()).themeNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Bundle bundle = this.getIntent().getExtras();
        url = "http://www.person.lime.jjvu.jx.cn/" + bundle.getString("url");
        appVar = (LimeApp) getApplication();
        webView = (WebView) findViewById(R.id.webViewArticle);
        bar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(bar);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        //toolbarLayout.setTitle("Lime");
        //标题栏展开颜色
        toolbarLayout.setExpandedTitleColor(0x00FFFFFF);
        //关闭颜色
        toolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_close_white);
        //修复4.4状态栏沉浸Bug
        FixKitKatTranslucentSystemBar.fix(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    /*创建菜单*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.article, menu);
        return true;
    }


    private void init() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JsCallJava(), "Lime");
        update();
    }

    protected class JsCallJava {
        /**
         * 刷新
         */
        @JavascriptInterface
        public void refresh() {
            update();
        }
    }

    private void update() {
        Thread load = new Thread(new Runnable() {
            private String html;

            @Override
            public void run() {
                html = getHtml();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String baseUrl = "file:///android_asset";
                        webView.loadDataWithBaseURL(baseUrl, html, "text/html", "UTF-8", null);
                    }
                });
            }
        });
        load.start();
    }

    public String getHtml() {
        final Article article;
        try {
            String html = HtmlHelper.getHtml(url, "GBK");
            article = HtmlHelper.catchArticle(html);
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toolbarLayout.setTitle(article.getTitle());
                ((TextView) findViewById(R.id.title_text)).setText(article.getTitle());
                //副标题
                //bar.setSubtitle(article.getOther());
                ((TextView) findViewById(R.id.subtitle_text)).setText(article.getDate());
            }
        });
        HtmlUnit unit = appVar.getArticleUnit();
        unit.addData(new HtmlUnit.DataBean("title", article.getTitle()), new HtmlUnit.DataBean("content", article.getContent()));
        return unit.makeHtml();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.article_screenshot:
                if (SaveImageToGalleryUtil.save(this, captureWebView(), "JJVU_Lime"))
                    openSnackBarHint(R.string.article_screenshot_success);
                else
                    openSnackBarHint(R.string.article_screenshot_failed);
                break;
            case R.id.article_exit:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 捕获WebView截图，并且把标题栏一起放到快照中
     *
     * @return 图片
     */
    private Bitmap captureWebView() {
        int offsetHeight = DensityUtil.dip2px(this, 120);
        int textOffset = DensityUtil.dip2px(this, 20);
        Paint pen = new Paint(Paint.ANTI_ALIAS_FLAG);
        TextPaint textPen = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPen.setColor(ColorUtil.getColorResource(this, R.color.colorTitle));
        textPen.setTextSize(DensityUtil.sp2px(this, 20));
        pen.setColor(ColorUtil.getAttrColor(this, R.attr.colorPrimary));
        //得到WebView快照
        Picture snapShot = webView.capturePicture();
        Bitmap bmp = Bitmap.createBitmap(snapShot.getWidth(), snapShot.getHeight() + offsetHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        //画上标题栏
        canvas.drawRect(0, 0, snapShot.getWidth(), offsetHeight, pen);
        StaticLayout s = new StaticLayout(toolbarLayout.getTitle(), textPen, snapShot.getWidth() - (textOffset << 1), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        canvas.translate(textOffset, textOffset);
        s.draw(canvas);
        //画上WebView
        canvas.translate(-textOffset, offsetHeight-textOffset);
        snapShot.draw(canvas);
        return bmp;
    }

    /**
     * 用于显示提示消息，但不做消息处理
     *
     * @param resultId res消息id
     */
    private void openSnackBarHint(int resultId) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.article_layout), resultId, Snackbar.LENGTH_LONG);
        //设置背景色
        View v = snackbar.getView();
        v.setBackgroundColor(ColorUtil.getAttrColor(this, R.attr.colorPrimary));
        //设置字体颜色
        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(this, R.color.colorTitle));
        snackbar.show();
    }
}
