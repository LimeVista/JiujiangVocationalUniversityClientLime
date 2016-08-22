package pers.lime.jjvu;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.ColorRes;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import pers.lime.jjvu.tools.FixKitKatTranslucentSystemBar;
import person.lime.tools.ColorUtil;
import person.lime.view.LeftBlockButton;

public class ThemeActivity extends AppCompatActivity implements View.OnClickListener {

    private LeftBlockButton blockBtnGreen,
            blockBtnBlue,
            blockBtnPink;
    private LinearLayout layout;

    private SharedPreferences preferences;

    private LimeApp app;

    private ColorUtil.ColorUtilCurrentContext colorUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //加载主题
        setTheme(((LimeApp) getApplication()).themeMain);
        super.onCreate(savedInstanceState);
        FixKitKatTranslucentSystemBar.fix(this);
        setContentView(R.layout.activity_theme);
        layout = (LinearLayout) findViewById(R.id.theme_layout);
        colorUtil = new ColorUtil.ColorUtilCurrentContext(this);
        {
            blockBtnGreen = makeButton(getString(R.string.theme_green), R.color.colorPrimary);
            layout.addView(blockBtnGreen);

            blockBtnBlue = makeButton(getString(R.string.theme_blue), R.color.blue_Primary);
            layout.addView(blockBtnBlue);

            blockBtnPink = makeButton(getString(R.string.theme_pink), R.color.pink_primary);
            layout.addView(blockBtnPink);
        }
        app = (LimeApp) getApplication();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        preferences = getSharedPreferences("JJVU_Setting", Context.MODE_PRIVATE);
    }

    private LeftBlockButton makeButton(String text, @ColorRes int color) {
        LeftBlockButton button = (LeftBlockButton) LayoutInflater.from(this).inflate(R.layout.theme_button, null, false);
        button.setText(text);
        button.setOnClickListener(this);
        button.setBlockColor(colorUtil.getColorResource(color));
        return button;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == blockBtnGreen) {
            changeTheme(R.style.GreenTheme,R.style.GreenTheme_NoActionBar,R.style.GreenTheme_AboutStyle);
        } else if (v == blockBtnBlue) {
            changeTheme(R.style.BlueTheme,R.style.BlueTheme_NoActionBar,R.style.BlueTheme_AboutStyle);
        } else if (v == blockBtnPink) {
            changeTheme(R.style.PinkTheme,R.style.PinkTheme_NoActionBar,R.style.PinkTheme_AboutStyle);
        }
    }

    /**
     * 改变主题
     * @param main 主主题
     * @param noActionBar 副主题，没有活动栏的样式
     * @param aboutStyle 副主题，关于主题样式
     */
    private void changeTheme(@StyleRes int main, @StyleRes int noActionBar, @StyleRes int aboutStyle) {
        if (app.themeMain == main)
            return;
        SharedPreferences.Editor editor = preferences.edit();
        app.themeMain = main;
        app.themeAbout = aboutStyle;
        app.themeNoActionBar = noActionBar;

        editor.putInt("Theme", main);//写入主题
        editor.putInt("Theme_About", aboutStyle);
        editor.putInt("Theme_No_Action_Bar", noActionBar);
        editor.commit();
        recreate();
    }
}
