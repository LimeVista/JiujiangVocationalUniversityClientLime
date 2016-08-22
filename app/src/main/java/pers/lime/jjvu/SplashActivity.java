package pers.lime.jjvu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    private static final int SHOW_TIME_MIN = 2500;// 最小显示时间

    public static final int GUIDE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //mStartTime = System.currentTimeMillis();//记录开始时间
        new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {
                // 读取SharedPreferences中需要的数据
                SharedPreferences preferences = getSharedPreferences("JJVU_Setting", Context.MODE_PRIVATE);
                if(preferences.getInt("Guide",0)!=GUIDE){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("Guide",GUIDE);//写入并且提交数据
                    editor.putInt("Theme",R.style.GreenTheme);//写入主题
                    editor.putInt("Theme_About",R.style.GreenTheme_AboutStyle);
                    editor.putInt("Theme_No_Action_Bar",R.style.GreenTheme_NoActionBar);
                    editor.commit();

                    startActivity(new Intent(SplashActivity.this,GuideActivity.class));
                }else{
                    Intent mainIntent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                }
                finish();
            }

        }, SHOW_TIME_MIN);
    }
}
