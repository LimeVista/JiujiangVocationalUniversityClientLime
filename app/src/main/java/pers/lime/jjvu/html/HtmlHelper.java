package pers.lime.jjvu.html;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pers.lime.jjvu.html.bean.Article;
import pers.lime.jjvu.html.bean.MsgList;

/**
 * Create by Lime(李振宇) on 2016.07.12
 * @version 1.2
 * @author Lime(李振宇)
 * <p>用于处理HTML文件</p>
 */
public class HtmlHelper {

	/**
	 * 缓冲区大小
	 */
	public final static int BUFFER_SIZE = 1024;

	/**
	 * HTML编码
	 */
	public final static String CODE_TYPE = "UTF-8";

	/**
	 * 尽可能少的匹配，非贪婪模式
	 */
	private final static String LIST_REG = "\\<a\\s*href\\s*=\\s*\"/templet/default/ShowArticle\\.jsp\\?id=[\\d]+\"\\s*target\\s*=\\s*\"_blank\"\\s*>\\s*<span([\\s\\S]*?)<\\/nobr>";

	private final static String ARTICLE_REG = "\\<font.?class.?=.?\"con\"([\\s\\S]*?)<\\/font>";
	
	/**
	 * 获取HTML源文件
	 * 
	 * @param urlString
	 *            源文件路径（HTML文件编码默认为UTF-8）
	 * @return 返回HTML字符串
	 * @throws IOException
	 *             各种异常，包括URL是否错误，IOException
	 */
	public static String getHtml(String urlString) throws IOException {
		return getHtml(urlString, CODE_TYPE);
	}

	/**
	 * /** 获取HTML源文件
	 * 
	 * @param urlString
	 *            源文件路径
	 * @param charSet
	 *            HTML文件编码
	 * @return 返回HTML字符串
	 * @throws IOException
	 *             各种异常，包括URL格式化异常、IOException
	 */
	public static String getHtml(String urlString, String charSet) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		conn.setReadTimeout(6*1000);
		// 通过输入流获取html数据
		InputStream inStream = conn.getInputStream();
		// 得到html的二进制数据
		byte[] data = readInputStream(inStream);
		String html = new String(data, charSet);
		inStream.close();
		return html;
	}

	/**
	 * 捕获栏目内的新闻或消息列表
	 * @param html 源HTML文件
	 * @return 返回捕获后的消息bean集合
	 */
	public static List<MsgList> catchList(String html) {
		Matcher matcher = Pattern.compile(LIST_REG).matcher(html);
		// List<String> list = new ArrayList<String>();
		List<MsgList> list = new ArrayList<MsgList>();
		String sMatcher = null;
		while (matcher.find()) {
			sMatcher = matcher.group();
			list.add(cutListString(sMatcher));
		}
		return list;
	}
	
	/**
	 * 捕获文章有效消息
	 * @param html 源HTML文件
	 * @return 返回捕获的文章bean消息
	 */
	public static Article catchArticle(String html){
		Matcher matcher = Pattern.compile(ARTICLE_REG).matcher(html);
		Article article = new Article();
		//获得正文
		if(matcher.find()){
			String content = matcher.group();
			//修复图片
			content = content.replaceAll("src.?=.?\"", "src=\"http://www.person.lime.jjvu.jx.cn");
			content = content.replaceAll("href.?=.?\"\\/", "href=\"http://www.person.lime.jjvu.jx.cn/");
			article.setContent(content);
		}
		//获得标题
		matcher = Pattern.compile("\\<div.*?id\\s*=\\s*\"ri_zwbt\".*?>[\\s\\S]*?</div>").matcher(html);
		if(matcher.find())
			article.setTitle(killAllTag(matcher.group()).trim());
		matcher = Pattern.compile("发布[\\s\\S]*?时间[\\s\\S]*?点击率:[\\d]+").matcher(html);
		if(matcher.find())
			article.setOther(matcher.group());
		return article;
	}
	
	/**
	 * 根据路径生成符合HTML条件的链接
	 * @param src 路径
	 * @return 返回符合HTML的JavaSrcipt链接
	 */
	public static String execHtmlJavaScript(String src) {
		return "<script src=\"" + src + 
				"\" type=\"text/javascript\"></script>";
	}

	/**
	 * 从输入流中读取数据，并转换为Byte数组
	 * 
	 * @param inStream
	 *            待操作的输入流
	 * @return Byte数组形式的html文件
	 * @throws IOException
	 *             各种异常，包括IOException
	 */
	public static byte[] readInputStream(InputStream inStream) throws IOException {
		// 字节缓冲流
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[BUFFER_SIZE];
		int len = 0;
		// 循环读取
		while ((len = inStream.read(buffer)) != -1)
			outStream.write(buffer, 0, len);
		return outStream.toByteArray();
	}

	/**
	 * 干掉所有HTML标签
	 * @param content 需要干掉标签的源文本
	 * @return 干掉后的字符串
	 */
	public static String killAllTag(String content) {
		return content.replaceAll("\\<.*?>", "");
	}

	/**
	 * 分割栏目消息列表
	 * @param content 元文本
	 * @return 分割后的消息bean
	 */
	private static MsgList cutListString(String content) {
		String address_reg = "/templet/default/ShowArticle\\.jsp\\?id=[\\d]+";
		Pattern pattern = Pattern.compile(address_reg);
		Matcher matcher = pattern.matcher(content);
		MsgList ml = new MsgList();
		if (matcher.find())
			ml.setAddress(matcher.group());
		content = killAllTag(content);
		//去掉所有换行
		content = content.replaceAll("[\\r\\n]", "");
		int len = content.length();
		ml.setTitle(content.substring(0, len - 10).trim());
		ml.setDate(content.substring(len - 10));
		return ml;
	}
}
