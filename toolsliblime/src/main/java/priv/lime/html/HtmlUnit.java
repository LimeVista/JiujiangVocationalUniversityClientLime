package priv.lime.html;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Stack;

public abstract class HtmlUnit implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * HTML字符串
	 */
	protected StringBuffer html = null;

	/**
	 * 自定义HTML中的标签对，类似JSP的作用
	 */
	protected Stack<TagBean> tags = null;

	/**
	 * 用于存储需要添加到HTML文件中的数据集合
	 */
	protected List<DataBean> data = null;

	/**
	 * 用于存储标签对的类
	 * 
	 * @author Lime Date:2016.07.17
	 * @version 0.1
	 */
	public static class TagBean implements Serializable {
		private static final long serialVersionUID = 5599881580152728275L;

		public TagBean(int start, int end, String tag) {
			super();
			this.start = start;
			this.end = end;
			this.tag = tag;
		}

		/**
		 * 标签起始位置
		 */
		public int start;

		/**
		 * 标签结束位置
		 */
		public int end;

		/**
		 * 标签名
		 */
		public String tag;

		@Override
		public String toString() {
			return "TagBean [Start=" + start + ", End=" + end + ", Tag=" + tag + "]";
		}
	}

	/**
	 * 用于存储添加数据的类
	 * 
	 * @author Lime Date:2016.07.17
	 * @version 0.1
	 */
	public static class DataBean implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 2619388386560009590L;
		/**
		 * 数据名
		 */
		private String key;
		/**
		 * 值
		 */
		private String value;

		public DataBean() {
			super();
		}

		/**
		 * 初始化
		 * 
		 * @param key
		 *            键名
		 * @param value
		 *            值
		 */
		public DataBean(String key, String value) {
			super();
			this.key = key;
			this.value = value;
		}

		/**
		 * 获得键名
		 * 
		 * @return 返回键名
		 */
		public String getKey() {
			return key;
		}

		/**
		 * 设置键名
		 * 
		 * @param key
		 *            键名
		 */
		public void setKey(String key) {
			this.key = key;
		}

		/**
		 * 获得键值
		 * 
		 * @return 返回键值
		 */
		public String getValue() {
			return value;
		}

		/**
		 * 设置键值
		 * 
		 * @param value
		 *            键值
		 */
		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "HtmlBean [key=" + key + ", value=" + value + "]";
		}
	}

	/**
	 * 添加数据
	 * @param key 数据键名
	 * @param value 数据键值
	 */
	public void addData(String key, String value) {
		data.add(new DataBean(key, value));
	}

	/**
	 * 添加数据
	 * @param data DataBean类型数据
	 */
	public void addData(DataBean data) {
			this.data.add(data);
	}
	
	/**
	 * 添加数据
	 * @param data DataBean类型数据
	 */
	public void addData(DataBean... data) {
		for(DataBean db : data)
			this.data.add(db);
	}

	/**
	 * 清除所有添加数据
	 */
	public void clearData(){
		data.clear();
	}
	
	/**
	 * 删除添加的数据
	 * @param key 键值
	 * @return 返回删除的数据，没有此数据的话，返回null
	 */
	public DataBean removeData(String key){
		for(int i=0 ;i<data.size();i++){
			if(data.get(i).getKey().equals(key))
				return data.remove(i);
		}
		return null;
	}
	
	/**
	 * 生成HTML文件(必须在addData()添加所有数据之后使用，使用之后不能再次添加数据(addData()))
	 * @return 返回HTML文本字符串
	 */
	public abstract String makeHtml();
	
	/**
	 * 深度克隆此类
	 * @return 返回克隆类
	 * @throws Exception
	 */
	public HtmlUnit deepClone() throws Exception {
		// 序列化
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(this);

		// 反序列化
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bis);
		return (HtmlUnit)ois.readObject();
	}
	
}
