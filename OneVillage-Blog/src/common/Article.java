package common;

import java.util.Date;
import java.util.HashSet;

public class Article {
	private String id;
	private String title;
	private String content;
	private String createUserId;
	private Date createDate;
	private String blogUrl;
	private String blogId;
	
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	
	public void setblogId(String id) {
		this.blogId = id;
	}
	
	public String getblogId() {
		return blogId;
	}
	
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	public String getTitle() {
		return title;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	
	
	public String getContent() {
		return content;
	}
	
	
	public String getCreateUserId() {
		return createUserId;
	}
	
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	
	public Date getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}	
	
	public void setBlogUrl(String blogUrl) {
		this.blogUrl = blogUrl;
	}
	
	
	 public String getBlogUrl() {
		return blogUrl;
	} 
	
		
}
