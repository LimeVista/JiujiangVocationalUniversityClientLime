package pers.lime.jjvu;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import pers.lime.jjvu.html.QueryStudentGrade;
import pers.lime.jjvu.html.bean.Grade;
import pers.lime.jjvu.info.InfoCode;
import pers.lime.jjvu.info.InfoText;
import pers.lime.jjvu.tools.FixKitKatTranslucentSystemBar;
import pers.lime.jjvu.ui.GradeFragment;
import pers.lime.jjvu.ui.GradePopupWindow;

public class QueryGradeActivity extends AppCompatActivity {


    private EditText userText, pwdText;
    private Button loginBtn;
    private TextInputLayout pwdLayout, userNameLayout;

    private QueryStudentGrade queryStudentGrade;
    private ClickLoginButton clickLoginButton;
    private volatile boolean isLoginInfoChange = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //加载主题
        setTheme(((LimeApp)getApplication()).themeMain);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_grade);
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setElevation(0);
        }
        //修复4.4状态栏沉浸Bug
        FixKitKatTranslucentSystemBar.fix(this);

        userText = (EditText) findViewById(R.id.text_user);
        pwdText = (EditText) findViewById(R.id.text_pwd);
        loginBtn = (Button) findViewById(R.id.login_btn);

        queryStudentGrade = new QueryStudentGrade();
    }

    @Override
    protected void onStart() {
        super.onStart();
        pwdLayout = (TextInputLayout) findViewById(R.id.pwd_layout);
        userNameLayout = (TextInputLayout) findViewById(R.id.user_layout);
        clickLoginButton = new ClickLoginButton(this);
        loginBtn.setOnClickListener(clickLoginButton);
        userText.addTextChangedListener(new TextChanged(userNameLayout));
        pwdText.addTextChangedListener(new TextChanged(pwdLayout));
        //去除在Android 4.4显示阴影的Bug
        getSupportActionBar().setElevation(0);
        //getSupportActionBar().setHomeAsUpIndicator(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
    }

    private class ClickLoginButton implements View.OnClickListener {

        private AppCompatActivity activity;
        private String username;
        private GradePopupWindow gpw;

        public ClickLoginButton(AppCompatActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onClick(View view) {
            //如果存在登录信息的话直接登录，无需等待
            if(!isLoginInfoChange&&queryStudentGrade.getSchoolYearsList().size()>0){
                gpw.show();
                return;
            }
            //检测密码账号是否为空
            username = userText.getText().toString().trim();
            if (username.equals("") || username.length() < 7) {
                userNameLayout.setError(getString(R.string.user_name_null));
                userNameLayout.setErrorEnabled(true);
                return;
            }
            if (pwdText.getText().toString().equals("")) {
                pwdLayout.setError(getString(R.string.pwd_is_null));
                pwdLayout.setErrorEnabled(true);
                return;
            }

            //异步登录
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int loginState =  queryStudentGrade.login(username, pwdText.getText().toString());
                        switch (loginState) {

                            //登录成功
                            case QueryStudentGrade.LOGIN_OK:
                            isLoginInfoChange = false;
                            getYearsList();
                                break;

                            //账号密码错误
                            case QueryStudentGrade.LOGIN_ACCESS_DENIED:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //登录失败
                                        pwdLayout.setError(getString(R.string.login_access_denied));
                                        pwdLayout.setErrorEnabled(true);
                                    }
                                });
                                break;

                            //账号超过使用权限
                            case QueryStudentGrade.LOGIN_MORE_THAN_ACCESS:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pwdLayout.setError(getString(R.string.login_access_than));
                                        pwdLayout.setErrorEnabled(true);
                                    }
                                });
                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pwdLayout.setError(getString(R.string.login_unknown));
                                        pwdLayout.setErrorEnabled(true);
                                    }
                                });
                                break;
                        }
                    } catch (IOException e) {
                        //网络异常
                        openSnackBarHintRunUI(R.string.grade_network_disconnection);
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }

        /**
         * 获取学年表
         */
        private void getYearsList() {

            new AsyncTask<Void, Void, Integer>() {

                //后台执行来一波，获取学年表
                @Override
                protected Integer doInBackground(Void... voids) {
                    try {
                        if (queryStudentGrade.QueryGradeList())
                            return 0;
                        return -1;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return 1;
                    } catch (QueryStudentGrade.NotLoginException e) {
                        e.printStackTrace();
                        return 2;
                    }
                }

                //回调到UI来一波
                @Override
                protected void onPostExecute(Integer result) {
                    switch (result) {
                        case 0:
                            openSelectListPopupWindow();
                            break;
                        case -1:
                            openSnackBarHint("这不能怪我咯，学校的服务器可能炸了！只能等着学校修复了...");
                            break;
                        case 1:
                            openSnackBarHint(R.string.grade_network_disconnection);
                            break;
                        case 2:
                            openSnackBarHint(R.string.login_exception);
                            break;
                    }
                }
            }.execute();
        }

        /**
         * 新的显示选择选择学年列表对话框
         */
        private void openSelectListPopupWindow() {
                gpw = new GradePopupWindow(activity, queryStudentGrade) {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    final int p = position;
                    //异步执行，弹窗
                    new AsyncTask<GradePopupWindow, Void, Integer>() {
                        //你的成绩单
                        private List<Grade> gradeList = null;
                        private GradePopupWindow popupWindow;

                        //后台执行
                        @Override
                        protected Integer doInBackground(GradePopupWindow... windows) {
                            this.popupWindow = windows[0];
                            Map<String, String> map = queryStudentGrade.getSchoolYearsList();
                            String value = map.get(items[p]);
                            if (value != null) {
                                try {
                                    gradeList = queryStudentGrade.QueryAction(value);
                                    return InfoCode.OK;
                                } catch (QueryStudentGrade.NotLoginException e) {
                                    e.printStackTrace();
                                    Log.e("Lime-Logout", "登录超时或您未登录！");
                                    return InfoCode.FAILED;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Log.e(InfoCode.getCode(InfoCode.NETWORK_DISCONNECTION), InfoText.NETWORK_DISCONNECTION);
                                    return InfoCode.NETWORK_DISCONNECTION;
                                }
                            }
                            return InfoCode.UNKNOW_ERROR;
                        }

                        //回调执行，收尾工作，UI线程
                        @Override
                        protected void onPostExecute(Integer result) {
                            switch (result) {
                                case InfoCode.OK:
                                    if(gradeList.size()==0){
                                        openSnackBarHint(R.string.grades_is_null);
                                        popupWindow.dismiss();
                                        return;
                                    }
                                    FragmentManager fm = getSupportFragmentManager();
                                    // 开启Fragment事务
                                    FragmentTransaction transaction = fm.beginTransaction();
                                    transaction.replace(R.id.query_grade_layout,new GradeFragment(){
                                        /**
                                         * 初始化成绩单
                                         */
                                        @Override
                                        public void initGradesWithListPopupWindow() {
                                            grades = gradeList;
                                            this.listPopupWindow = popupWindow;
                                        }
                                    });
                                    transaction.commit();
                                    //关闭学年选择窗口
                                    popupWindow.dismiss();
                                    break;
                                case InfoCode.FAILED:
                                    openSnackBarHint(R.string.login_exception);
                                    popupWindow.dismiss();
                                    break;
                                case InfoCode.NETWORK_DISCONNECTION:
                                    openSnackBarHint(R.string.grade_network_disconnection);
                                    popupWindow.dismiss();
                                    break;
                                //这是不可能的，我敢肯定
                                case InfoCode.UNKNOW_ERROR:
                                    break;
                            }
                        }
                    }.execute(this);
                }
            };
            gpw.show();
        }

        /**
         * 显示选择选择学年列表对话框已经废弃，请使用openSelectListPopupWindow
         */
        @Deprecated
        private void openSelectListDialog() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.GreenTheme_Dialog_Alert);
                    builder.setTitle(getString(R.string.select_school_year));
                    builder.setItems(queryStudentGrade.getSchoolYearsKeyList(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //openSnackBarHintRunUI("test","dialog:"+dialogInterface.toString()+"\ni:"+i);
                            //new GradeFragment().show(activity.getSupportFragmentManager(), "Lime");
                        }
                    });
                    builder.setAdapter(new ArrayAdapter<String>(activity, R.layout.list_grade_years), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog dialog = builder.create();

                    Window window = dialog.getWindow();
                    //设置属性
                    WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
                    DisplayMetrics outMetrics = new DisplayMetrics();
                    window.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);//获得屏幕宽度和高度
                    layoutParams.gravity = Gravity.BOTTOM;//设置layout在屏幕底端
                    //只有设置背景后才能调整Dialog宽度铺满屏幕
                    window.setBackgroundDrawableResource(R.color.White);//设置背景
                    window.setAttributes(layoutParams);//置入属性

                    ListView dialogListView = dialog.getListView();
                    dialogListView.setBackgroundColor(0XFFFFFFFF);

                    //惊天Bug，必须先显示才能调整高度？反正我不知道什么回事
                    dialog.show();
                    //设置高度、宽度
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, outMetrics.heightPixels >> 1);
                }
            });
        }

    }

    /**
     * 用于显示提示消息，但不做消息处理
     *
     * @param resultId res消息id
     */
    private void openSnackBarHint(int resultId) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.query_grade_layout), resultId, Snackbar.LENGTH_LONG);
        //设置背景色
        View v = snackbar.getView();
        v.setBackgroundColor(getAttributeColor(R.attr.colorPrimary));
        //设置字体颜色
        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(this, R.color.colorTitle));
        snackbar.show();
    }

    /**
     * 用于显示提示消息，但不做消息处理
     *
     * @param message 消息
     */
    private void openSnackBarHint(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.query_grade_layout), message, Snackbar.LENGTH_LONG);
        //设置背景色
        View v = snackbar.getView();
        v.setBackgroundColor(getAttributeColor(R.attr.colorPrimary));
        //设置字体颜色
        TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(this, R.color.colorTitle));
        snackbar.show();
    }

    /**
     * 用于显示提示消息，但不做消息处理，100%保证在UI线程
     *
     * @param message 消息
     */
    private void openSnackBarHintRunUI(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                openSnackBarHint(message);
            }
        });
    }

    /**
     * 用于显示提示消息，但不做消息处理，100%保证在UI线程
     *
     * @param resultId res消息ID
     */
    private void openSnackBarHintRunUI(final int resultId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                openSnackBarHint(resultId);
            }
        });
    }

    /**
     * 内部类，用来控制文本改变时，关闭错误提示
     */
    private class TextChanged implements TextWatcher {

        private TextInputLayout layout;

        public TextChanged(TextInputLayout layout) {
            this.layout = layout;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            layout.setErrorEnabled(false);
            isLoginInfoChange = true;
        }
    }

    /**
     * 获取Attr属性的资源ID
     *
     * @param attr 属性ID
     * @return 颜色值
     */
    private int getAttributeColor(int attr) {
        TypedValue typedValue = new TypedValue();
        this.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }
}
