package pers.lime.jjvu.ui;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;

import pers.lime.jjvu.R;
import pers.lime.jjvu.html.bean.Grade;

/**
 * Created by KING on 16-8-11.
 */
public class GradeExpandableListAdapterWithListener implements
        ExpandableListAdapter, ExpandableListView.OnGroupClickListener {

    private List<Grade> grades;

    private Context context;


    public GradeExpandableListAdapterWithListener(Context context, List<Grade> grades) {
        this.grades = grades;
        this.context = context;
    }


    /**
     * 我不需要你,这个方法在这里是没有用的
     *
     * @param dataSetObserver
     */
    @Deprecated
    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
    }

    /**
     * 我不需要你,这个方法在这里是没有用的
     *
     * @param dataSetObserver
     */
    @Override
    @Deprecated
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
    }

    /**
     * 获取分组个数
     *
     * @return 返回分组的个数
     */
    @Override
    public int getGroupCount() {
        if (grades != null)
            return grades.size();
        return 0;
    }

    /**
     * 获取指定子成员的选项个数
     *
     * @param i 第i个分组，从0开始
     * @return 返回子成员个数
     */
    @Override
    public int getChildrenCount(int i) {
        if (grades != null)
            return grades.get(i).getItemCount();
        return 0;
    }

    /**
     * 获得指定分组数据
     *
     * @param i 第i个分组，从0开始
     * @return
     */
    @Override
    public Object getGroup(int i) {
        if (grades != null)
            return grades.get(i);
        return null;
    }

    /**
     * 获得第i个分组的第iChild的数据
     *
     * @param i      第i个分组，从0开始
     * @param iChild 第iChild个子成员，从1开始
     * @return 返回子成员数据
     */
    @Override
    public Object getChild(int i, int iChild) {
        String s = "";
        if (grades != null) {
            Grade g = grades.get(i);
            switch (iChild) {
                case 0:
                    s = "学    年：" + g.getYear();
                    break;
                case 1:
                    s = "课程名：" + g.getCourseName();
                    break;
                case 2:
                    s = "课    时：" + g.getClassHour();
                    break;
                case 3:
                    s = "平时成绩一：" + getScore(g.getScore_1());
                    break;
                case 4:
                    s = "平时成绩二：" + getScore(g.getScore_2());
                    break;
                case 5:
                    s = "期末成绩：" + getScore(g.getFinalScore());
                    break;
                case 6:
                    s = "总  成  绩：" + getScore(g.getTotalScore());
                    break;
                case 7:
                    s = "课程性质：" + g.getCourseNature();
                    break;
                case 8:
                    s = "课程类别：" + g.getCourseCategory();
                    break;
            }
        }
        return s;
    }

    /**
     * 获得组成员编号
     *
     * @param i 第i个组，从0开始
     * @return 返回编号
     */
    @Override
    public long getGroupId(int i) {
        return i;
    }

    /**
     * 获取第i个组的第iChild成员编号
     *
     * @param i      第i个组，从0开始
     * @param iChild 第iChild子成员
     * @return 编号
     */
    @Override
    public long getChildId(int i, int iChild) {
        return iChild;
    }

    /**
     * 分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们。
     *
     * @return 恒等于是
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * 重中之重，设置组视图也就是组的UI界面
     *
     * @param i
     * @param b
     * @param view
     * @param viewGroup
     * @return
     */
    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grade_exp_group, viewGroup, false);
        }
        TextView textView = (TextView) view.findViewById(R.id.exp_group_text);
        textView.setText(grades.get(i).getCourseName());
        return view;
    }

    /**
     * 重中之重，设置子成员视图也就是组的UI界面
     *
     * @param i
     * @param iChild
     * @param b
     * @param view
     * @param viewGroup
     * @return
     */
    @Override
    public View getChildView(int i, int iChild, boolean b, View view, ViewGroup viewGroup) {
        TextView textView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grade_exp_group_child, viewGroup, false);
        }
        textView = (TextView) view.findViewById(R.id.exp_group_child_text);
        textView.setText((String) getChild(i, iChild));
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        if (grades == null)
            return true;
        return grades.isEmpty();
    }

    @Override
    public void onGroupExpanded(int i) {

    }

    @Override
    public void onGroupCollapsed(int i) {

    }

    @Override
    public long getCombinedChildId(long l, long l1) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long l) {
        return 0;
    }

    @Override
    public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
        return false;
    }

    private String getScore(float s) {
        return s == -1.0f ? "" : String.valueOf(s);
    }
}
