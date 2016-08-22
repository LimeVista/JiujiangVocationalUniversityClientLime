package pers.lime.jjvu.html;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pers.lime.jjvu.html.bean.Grade;

/**
 * 用于查询成绩
 * <p>Last 2016.08.06</p>
 * @author Lime(李振宇)
 * @version 1.0
 */
public class QueryStudentGrade {

	/**
	 * Session [0]为id，[1]为path
	 */
	private String[] sessionId;

	/**
	 * HTTP URL连接
	 */
	private HttpURLConnection connection;

	/**
	 * 登录成功标志Tag
	 */
	public static final int LOGIN_OK = -1;

	/**
	 * 用户名或密码错误
	 */
	public static final int LOGIN_ACCESS_DENIED = 4913;

	/**
	 * 超出使用权限
	 */
	public static final int LOGIN_MORE_THAN_ACCESS = 4919;

	/**
	 * 用户名
	 */
	private String userName;

	/**
	 * 学年表
	 */
	protected volatile Map<String, String> mapSchoolYears;

	/**
	 * 登录URL接口
	 */
	protected final String LOGIN_URL = "http://111.75.149.188:9966/jjdc2008/Logon.do?method=logonBySSO";

	/**
	 * 登录URL接口2
	 */
	protected final String LOGIN_URL_2 = "http://111.75.149.188:9966/jjdc2008/Logon.do?method=logon";

	/**
	 * 查询成绩选项卡URL接口
	 */
	protected final String QUERY_SELECT_URL = "http://111.75.149.188:9966/jjdc2008/jiaowu/cjgl/queryXsccj.jsp";

	//举个栗子http://111.75.149.188:9966/jjdc2008/jiaowu/cjgl/xscjcxAction.do?method=getXscjxx&xnxqh=2015-2016-2&xh=2014040201111
	protected final String QUERY_ACTION_URL = "http://111.75.149.188:9966/jjdc2008/jiaowu/cjgl/xscjcxAction.do?method=getXscjxx&xnxqh=";
	
	/**
	 * 初始化
	 */
	public QueryStudentGrade() {
		sessionId = null;
		userName = "";
		mapSchoolYears = new TreeMap<String, String>(new Comparator<String>() {			
			/**
			 * Key倒序排列
			 */
			@Override
			public int compare(String o1, String o2) {
				return o2.compareTo(o1);
			}
		});
	}

	public int login(String userName, String pwd) throws IOException {
		URL url = new URL(LOGIN_URL + "&USERNAME=" + userName + "&PASSWORD=" + pwd);
		// 创建连接
		connection = (HttpURLConnection) url.openConnection();
		// 设置链接超时、Head、请求方式
		connection.setRequestMethod("POST");
		connection.addRequestProperty("CONTENT-TYPE", "application/x-www-form-urlencoded");
		connection.setConnectTimeout(5 * 1000);
		// 用来处理GZIP压缩导致长度Bug，然而在这里并没有什么卵用
		// connection.setRequestProperty("Accept-Encoding", "identity");
		// 连接
		connection.connect();
		// 状态码
		int code = connection.getResponseCode();
		// 请求长度
		int contentLen = connection.getContentLength();

		this.userName = userName;
		connection.disconnect();
		if (code == 200 && contentLen == LOGIN_OK) {
			// 获得Session的ID和path
			String session_value = connection.getHeaderField("Set-Cookie");
			sessionId = session_value.split(";");
			return LOGIN_OK;
		} else {
			sessionId = null;
			url = new URL(LOGIN_URL_2 + "&USERNAME=" + userName + "&PASSWORD=" + pwd);
			// 创建连接
			connection = (HttpURLConnection) url.openConnection();
			// 设置链接超时、Head、请求方式
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(5 * 1000);
			// 连接
			connection.connect();
			// 请求长度
			contentLen = connection.getContentLength();
			connection.disconnect();
			return contentLen;
		}
	}

	/**
	 * 生成查询成绩学年列表
	 * 
	 * @return 是否成功
	 * @throws IOException
	 *             IO异常
	 * @throws NotLoginException
	 *             登录异常
	 */
	public boolean QueryGradeList() throws IOException, NotLoginException {
		// 说明你忘了登录
		if (sessionId == null)
			throw new NotLoginException();

		URL url = new URL(QUERY_SELECT_URL);
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		// 运行的时候，把登录读取的session的值设置上
		connection.setRequestProperty("Cookie", sessionId[0]);
		connection.setConnectTimeout(5 * 1000);
		// 把HTML文件下载下来
		InputStream in = connection.getInputStream();
		// HTML文件长度
		int len = connection.getContentLength();
		int code = connection.getResponseCode();

		if (code != 200 || len < 4000)
			return false;
		else {
			//读取数据
			byte[] bytes = HtmlHelper.readInputStream(in);
			String html = new String(bytes, "UTF-8");
			// 得到列表
			Matcher matcher = Pattern.compile("\"[\\d]{4}-[\\d]{4}-[12]").matcher(html);

			mapSchoolYears.put("在校所有成绩", "");
			String value;
			while (matcher.find()) {
				value = matcher.group().substring(1);
				mapSchoolYears.put(value, value);
			}
			return mapSchoolYears.size() > 1;
		}
	}

	/**
	 * 执行成绩查询
	 * @param schoolYear 学年
	 * @return 成绩单
	 * @throws NotLoginException
	 * @throws IOException
	 */
	public List<Grade> QueryAction(String schoolYear) throws NotLoginException, IOException {
		// 说明你忘了登录
		if (sessionId == null)
			throw new NotLoginException();
		URL url = new URL(QUERY_ACTION_URL+schoolYear+"&xh="+userName);
			
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		// 运行的时候，把登录读取的session的值设置上
		connection.setRequestProperty("Cookie", sessionId[0]);
		connection.setConnectTimeout(5 * 1000);
		// 把HTML文件下载下来
		InputStream in = connection.getInputStream();
		
		byte[] bytes = null;
		try {
			bytes = HtmlHelper.readInputStream(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String html = new String(bytes, "UTF-8");
		//把成绩单取出来
		Matcher matcher = Pattern.compile("<table.*?id=mxh.*?>[\\s\\S]*?</table>").matcher(html);
		html = html.replaceAll("&nbsp;", "");

		if(matcher.find())
			html = matcher.group();
		matcher = Pattern.compile("<tr.*?>[\\s\\S]*?</tr>").matcher(html);
		//列出每个科目成绩
		List<String> grade_list = new ArrayList<String>();
		//第一个结果不要
		matcher.find();
		//matcher.group();
		while(matcher.find())
			grade_list.add(matcher.group());
		//录入Grade
		List<Grade> gradeList= new ArrayList<Grade>();
		
		for(String i : grade_list){
			matcher = Pattern.compile("<td.*?>[\\s\\S]*?</td>").matcher(i);
			List<String> list = new ArrayList<String>();
			while(matcher.find())
				list.add(HtmlHelper.killAllTag(matcher.group()).trim());
			
			gradeList.add(new Grade(list.get(3),
					list.get(5),
					list.get(12),
					list.get(13),
					Integer.valueOf(list.get(7)),
					getScore(list.get(8)),
					getScore(list.get(9)),
					getScore(list.get(10)),
					getScore(list.get(11))));
		}
		return gradeList;
	}

	/**
	 * 获取学年列表
	 * 
	 * @return 学年列表
	 */
	public Map<String, String> getSchoolYearsList() {
		return mapSchoolYears;
	}

	/**
	 * 获取学年键值列表
	 * @return 键值列表
     */
	public String[] getSchoolYearsKeyList(){
		String[] s = new String[mapSchoolYears.size()];
		mapSchoolYears.keySet().toArray(s);
		return s;
	}
	

	/**
	 * 未登录异常,触发此异常表明您未进行登录！
	 * <p>
	 * 2016.08.06
	 * </p>
	 * 
	 * @author Lime(李振宇)
	 * @version 0.1
	 */
	public class NotLoginException extends Exception {
		private static final long serialVersionUID = -6183625888764105240L;

		public NotLoginException() {
			super("你未进行登录！请先运行Login方法登录再使用此方法！");
		}

		public NotLoginException(String msg) {
			super(msg);
		}
	}
	
	/**
	 * 把HTML字符串中的成绩数值转换为单精度浮点类型成绩
	 * @param strScore 字符串类型分数
	 * @return 返回分数
	 */
	private float getScore(String strScore){
		if(strScore.equals("")||strScore.equals("&nbsp;"))
			return -1;
		return Float.valueOf(strScore);
	}
}
