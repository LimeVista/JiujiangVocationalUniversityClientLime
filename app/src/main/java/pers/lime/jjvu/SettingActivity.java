package pers.lime.jjvu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import pers.lime.jjvu.tools.FixKitKatTranslucentSystemBar;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout layout;

    private Button aboutBtn,
        betaPersonBtn,
        themeBtn,
        licenseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //加载主题
        setTheme(((LimeApp)getApplication()).themeMain);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        layout = (LinearLayout) findViewById(R.id.settings_layout);

        themeBtn = makeButton("设置主题");
        themeBtn.setOnClickListener(this);
        layout.addView(themeBtn);

        licenseBtn = makeButton("许可协议");
        licenseBtn.setOnClickListener(this);
        layout.addView(licenseBtn);

        betaPersonBtn = makeButton("内测人员");
        betaPersonBtn.setOnClickListener(this);
        layout.addView(betaPersonBtn);

        aboutBtn = makeButton("关于我们");
        aboutBtn.setOnClickListener(this);
        layout.addView(aboutBtn);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //修复4.4状态栏沉浸Bug
        FixKitKatTranslucentSystemBar.fix(this);
    }

    private Button makeButton(String text) {
        Button button = (Button) LayoutInflater.from(this).inflate(R.layout.setting_button_style,null,false);
        button.setText(text);
        return button;
    }

    @Override
    public void onClick(View v) {
        if(v==aboutBtn){
            //关于
            startActivity(new Intent(SettingActivity.this,AboutActivity.class));
            finish();
        }else if(v==betaPersonBtn){
            //内测人员
            Intent it = new Intent(SettingActivity.this,BetaTestActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("url","text/beta_person.txt");
            it.putExtras(bundle);
            startActivity(it);
        }else if(v==themeBtn){
            startActivity(new Intent(SettingActivity.this,ThemeActivity.class));
            finish();
        }else if(v==licenseBtn){
            Intent it = new Intent(SettingActivity.this,BetaTestActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("url","text/license.txt");
            it.putExtras(bundle);
            startActivity(it);
        }
    }
}
