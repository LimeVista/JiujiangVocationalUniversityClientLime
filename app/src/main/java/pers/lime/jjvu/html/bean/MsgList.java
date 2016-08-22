package pers.lime.jjvu.html.bean;

/**
 * Created by Lime(李振宇) on 2016-07-30.
 * 用于新闻消息列表
 */
public class MsgList {
	private String title, address, date;

	public MsgList() {
		super();
	}

	public MsgList(String title, String address, String date) {
		super();
		this.title = title;
		this.address = address;
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		//return "MsgListBean [title=" + title + ", address=" + address + ", date=" + date + "]";
		return toJSON();
	}

	public String toJSON(){
		return "{\"title\":\""+title+"\",\"address\":\"" + address + "\",\"date\":\"" + date+"\"}";
	}

}
