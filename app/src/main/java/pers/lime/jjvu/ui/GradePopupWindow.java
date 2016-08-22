package pers.lime.jjvu.ui;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.support.annotation.AttrRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import pers.lime.jjvu.R;
import pers.lime.jjvu.html.QueryStudentGrade;

/**
 * Created by KING on 16-8-10.
 */
public abstract class GradePopupWindow extends ListPopupWindow implements AdapterView.OnItemClickListener {

    protected Window window;

    protected Theme theme;

    protected String[] items;

    protected Context context;


    public GradePopupWindow(AppCompatActivity context, QueryStudentGrade queryStudentGrade) {
        super(context);
        setAnchorView(context.findViewById(R.id.grade_pop_window));
        setModal(true);
        this.window = context.getWindow();
        this.theme = context.getTheme();
        this.context = context;
        this.items = queryStudentGrade.getSchoolYearsKeyList();
        init();
    }

    private void init() {
        //设置属性
        //setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置高度
        DisplayMetrics dm = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(dm);
        setHeight(dm.heightPixels >> 1);
        //设置监听器
        setOnItemClickListener(this);
        setAdapter(new ArrayAdapter<String>(context, R.layout.list_grade_years, items));
        setAnimationStyle(R.style.popup_window_anim_style);
    }

    @Override
    public abstract void onItemClick(AdapterView<?> adapterView, View view, int position, long l);

    /**
     * 获取Attr属性的资源ID
     *
     * @param attr 属性ID
     * @return 颜色值
     */
    private int getAttributeColor(@AttrRes int attr) {
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }

}
