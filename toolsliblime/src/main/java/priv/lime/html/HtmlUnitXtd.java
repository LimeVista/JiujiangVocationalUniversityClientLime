package priv.lime.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Stack;

import priv.lime.algorithm.string.BoyerMoore;

public class HtmlUnitXtd extends HtmlUnit {

	private static final long serialVersionUID = 5353957316819583812L;


	/**
	 * 初始化
	 * @param filePath 文件路径
	 */
	public HtmlUnitXtd(String filePath) {
		data = new ArrayList<DataBean>();
		tags = new Stack<TagBean>();
		reader(new File(filePath));
		analysis();
	}

	public HtmlUnitXtd(byte[] html){
		try {
			this.html = new StringBuffer(new String(html,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			System.err.print("编码转换失败");
			e.printStackTrace();
		}
		data = new ArrayList<DataBean>();
		tags = new Stack<TagBean>();
		analysis();
	}
	
	/**
	 * 读取外部文本(HTML)
	 * @param file 源文件
	 */
	private void reader(File file){
		char[] fileContent;
		{
			fileContent = new char[(int) file.length()];
			FileReader reader = null;
			try {
				reader = new FileReader(file);
				reader.read(fileContent);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		html = new StringBuffer(String.valueOf(fileContent));
	}

	/**
	 * 解析读入的文本(HTML)文件
	 */
	private void analysis() {
		BoyerMoore bm = new BoyerMoore();
		int i=0,j;
		while(i<html.length()){
			i = bm.matchOne("<%", html.toString(), i);
			j=i;
			i= bm.matchOne("%>", html.toString(), i);
			if(j<i)
				tags.push(new TagBean(j, i+2, html.substring(j+2, i).trim()));
		}
	}

	/**
	 * 生成HTML文件(必须在addData()添加所有数据之后使用，使用之后不能再次添加数据(addData()))
	 * @return 返回HTML文本字符串
	 */
	public String makeHtml() {
		DataBean d = null;
		while(data.size()>0&&tags.size()>0){
			TagBean tag =tags.pop();
			for(int i=0;i<data.size();++i){
				d=data.get(i);
				if(d.getKey().equals(tag.tag)){
					html.delete(tag.start, tag.end);
					html.insert(tag.start,d.getValue());
					data.remove(i);
					break;
				}
			}
		}
		return html.toString();
	}
}
