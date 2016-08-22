package pers.lime.jjvu.html.bean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lime(李振宇) on 2016-07-30.
 * 用于存储文章
 */
public class Article {
	private String title,other,content;
	
	public Article() {
		super();
	}

	public Article(String title, String other, String content) {
		super();
		this.title = title;
		this.other = other;
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate(){
		Matcher matcher = Pattern.compile("\\d{4}-\\d{2}-\\d{2}[ ]+\\d{2}:\\d{2}").matcher(other);
		if(matcher.find()){
			return matcher.group();
		}
		return "";
	}

	@Override
	public String toString() {
		return "Article [Title=" + title + ", Other=" + other + ", Content=" + content + "]";
	}
}
