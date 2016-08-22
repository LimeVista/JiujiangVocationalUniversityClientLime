package pers.lime.jjvu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import pers.lime.jjvu.tools.FixKitKatTranslucentSystemBar;

public class BetaTestActivity extends AppCompatActivity {

    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //加载主题
        setTheme(((LimeApp)getApplication()).themeMain);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beta_test);
        bundle = getIntent().getExtras();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //修复4.4状态栏沉浸Bug
        FixKitKatTranslucentSystemBar.fix(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String url = bundle.getString("url");
        String s = ((LimeApp)getApplication()).getAssetsText(url);
        ((TextView)findViewById(R.id.beta_text)).setText(s);
    }
}
