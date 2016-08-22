package priv.lime.html.old;

/**
 * 这只是一个演示类，实际上并没有人会这样用
 * @author Lime(李振宇)
 *
 */
public abstract class MakeHtmlOld {

	/**
	 * 标题
	 */
	protected String title;

	/**
	 * HTML编码
	 */
	protected String charSet;

	/**
	 * 可以用来处理样式和放置在Head的脚本
	 */
	protected StringBuffer insideHead;

	/**
	 * 主体
	 */
	protected StringBuffer body;

	/**
	 * HTML语句规范模块
	 */
	protected final static String STANDARD_HTML5_1 = 
			"<!DOCTYPE html><html><head lang=\"zh-CN\"><meta http-equiv=\"Content-Type\" content=\"text/html; charset=";

	/**
	 * HTML语句规范模块
	 */
	protected final static String STANDARD_HTML5_2 = "\" />\n<title>";

	/**
	 * HTML语句规范模块
	 */
	protected final static String STANDARD_HTML5_3 = "</title>";

	/**
	 * HTML语句规范模块
	 */
	protected final static String STANDARD_HTML5_4 = "</head><body>";

	/**
	 * HTML语句规范模块
	 */
	protected final static String STANDARD_HTML5_5 = "\n</body></html>";

	/**
	 * 初始化
	 */
	public MakeHtmlOld() {
		this("未命名网页", "UTF-8");
	}

	/**
	 * 初始化
	 * 
	 * @param title
	 *            标题
	 * @param charSet
	 *            HTML编码
	 */
	public MakeHtmlOld(String title, String charSet) {
		super();
		this.title = title;
		this.charSet = charSet;
		body = new StringBuffer();
		insideHead = new StringBuffer();
		initBody();
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 *            标题
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 设置HTML编码类型
	 * 
	 * @param charSet
	 *            HTML编码
	 */
	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}

	/**
	 * 添加HTML文本在Head标签中
	 * 
	 * @param html
	 *            HTML文本
	 */
	public void addInsideHead(String html) {
		insideHead.append(html);
	}

	public void addCss(String path) {
		insideHead.append("<link href=\"" + path + 
				"\" rel=\"stylesheet\" type=\"text/css\">");
	}
	
	/**
	 * 初始化主体
	 */
	public abstract void initBody();

	/**
	 * HTML设置主体
	 * @param html 返回主体文本
	 */
	public abstract void setBody(String html);

	/**
	 * 生成HTML文件
	 * 
	 * @return 返回生成的HTML文本
	 */
	public String toHtml() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(STANDARD_HTML5_1);
		buffer.append(charSet);
		buffer.append(STANDARD_HTML5_2);
		buffer.append(title);
		buffer.append(STANDARD_HTML5_3);
		buffer.append(insideHead);
		buffer.append(STANDARD_HTML5_4);
		buffer.append(body);
		buffer.append(STANDARD_HTML5_5);
		return buffer.toString();
	}

	@Override
	public String toString() {
		return toHtml();
	}
}
