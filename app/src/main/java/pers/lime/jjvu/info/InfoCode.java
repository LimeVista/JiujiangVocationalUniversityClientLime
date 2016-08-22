package pers.lime.jjvu.info;

/**
 * Created by Lime(李振宇) on 2016-07-30.
 * 用于集合表达代码
 */
public class InfoCode {

    /**
     * 标识符
     */
    public static final String TAG = "Lime";

    /**
     * 错误
     */
    public static final int ERROR = -1;

    /**
     * 成功、确定
     */
    public static final int OK = 0;

    /**
     * 消息
     */
    public static final int INFO = 1;

    /**
     * 加载中
     */
    public static final int LOADING = 2;

    /**
     * 失败
     */
    public static final  int FAILED = 3;

    /**
     * 网络中断、未连接
     */
    public static final int NETWORK_DISCONNECTION = 4;

    /**
     * 网络超时
     */
    public static final int NETWORK_TIMEOUT = 5;

    /**
     * 未知异常
     */
    public static final int UNKNOW_ERROR = 6;

    /**
     * 返回代码
     * @param code 代码编号
     * @return 返回代码文本
     */
    public static String getCode(int code){
        return TAG + "-" + code;
    }
}
