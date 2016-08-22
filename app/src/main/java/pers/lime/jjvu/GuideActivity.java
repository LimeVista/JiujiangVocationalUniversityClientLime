package pers.lime.jjvu;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity {

    private ViewPager pager;

    private List<View> views;

    private ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        pager = (ViewPager) findViewById(R.id.guide_pager);
        initPager();
    }

    private void initPager() {
        LayoutInflater lf = LayoutInflater.from(this);
        views = new ArrayList<View>(3);
        Log.i("Loading...","加载引导界面中...");
        views.add(lf.inflate(R.layout.view_page_guide, null));
        views.add(lf.inflate(R.layout.view_page_guide_2, null));
        views.add(lf.inflate(R.layout.view_page_guide_3, null));
         button = (ImageButton) (views.get(2).findViewById(R.id.guide_enter));
        ImageButtonEvent event = new ImageButtonEvent();
        button.setOnClickListener(event);
        button.setOnTouchListener(event);
        pager.setAdapter(event);
    }

    private class ImageButtonEvent extends PagerAdapter implements View.OnTouchListener,View.OnClickListener {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = views.get(position);
            container.addView(v,0);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            if(views==null)
                return 0;
            return views.size();
        }

        /**
         * Determines whether a page View is associated with a specific key object
         * as returned by instantiateItem(ViewGroup, int). This method is
         * required for a PagerAdapter to function properly.
         *
         * @param view   Page View to check for association with <code>object</code>
         * @param object Object to check for association with <code>view</code>
         * @return true if <code>view</code> is associated with the key object <code>object</code>
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    button.setImageResource(R.mipmap.guide_enter_down);
                    break;
                case MotionEvent.ACTION_UP:
                    button.setImageResource(R.mipmap.guide_enter);
                    break;
            }
            return false;
        }

        @Override
        public void onClick(View view) {
            startActivity(new Intent(GuideActivity.this,MainActivity.class));
            finish();
        }
    }
}
