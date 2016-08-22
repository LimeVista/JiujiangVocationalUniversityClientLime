package pers.lime.jjvu.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.List;

import pers.lime.jjvu.R;
import pers.lime.jjvu.html.bean.Grade;

/**
 * Created by Lime(李振宇) on 2016-08-09.
 */
public abstract class GradeFragment extends Fragment implements View.OnTouchListener, View.OnClickListener {

    private ActionBar bar;
    private AppCompatActivity activity;
    protected List<Grade> grades;
    protected ListPopupWindow listPopupWindow;
    private ExpandableListView expandableListView;
    private FloatingActionButton floatingButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.activity = (AppCompatActivity) getActivity();
        bar = activity.getSupportActionBar();
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.setTitle(getString(R.string.grade_more));
        bar.setHomeAsUpIndicator(R.mipmap.ic_close_white);
        expandableListView.setOnTouchListener(new View.OnTouchListener() {

            private boolean isShow = true;
            private float y = 0.0f;

            //向下滑动异常FloatActionButton，反之显示
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Log.i("ListView触发中", "正在移动");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("Lime_Test", "Action Down");
                        y = event.getRawY();
                        Log.i("滑动Y轴:", "坐标：" + y);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i("Lime_Test", "Action Up");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float currentY = event.getRawY();
                        Log.i("滑动Y轴:", "当前坐标：" + currentY);
                        //向下滑动
                        if (currentY > y) {
                            if (isShow) {
                                floatingButton.hide();
                                isShow = false;
                            }
                        } else if (currentY < y) {
                            if (!isShow) {
                                floatingButton.show();
                                isShow = true;
                            }
                        }
                        Log.i("Lime_Test", "Action Move");
                        break;
                }
                return false;
            }
        });
        bar.setDisplayHomeAsUpEnabled(true);
        floatingButton.setOnClickListener(this);
    }

    /**
     * 传入成绩单
     */
    public abstract void initGradesWithListPopupWindow();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //添加布局，一定要设置第三个参数为false，否则有意想不到的的惊喜，不信你试试
        View view = inflater.inflate(R.layout.fragment_grade, container, false);
        //window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        //此举用来解决Fragment这艹蛋的点击穿透问题，只要onTouch返回true就不会了
        view.setOnTouchListener(this);
        initGradesWithListPopupWindow();
        expandableListView = (ExpandableListView) view.findViewById(R.id.grade_expandable_list);
        floatingButton = (FloatingActionButton) view.findViewById(R.id.grade_float);
        GradeExpandableListAdapterWithListener gelawl = new GradeExpandableListAdapterWithListener(activity, grades);
        expandableListView.setAdapter(gelawl);
        //expandableListView.setOnGroupClickListener(gelawl);
        return view;
    }

    //此举用来解决Fragment这艹蛋的点击穿透问题，只要onTouch返回true就不会了
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.grade_float:
                floatingButton.hide();
                listPopupWindow.show();
                break;
            default:
                break;
        }
    }
}
