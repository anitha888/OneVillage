package common;

import java.util.Date;
import java.util.HashSet; 

public class Blog{
	private String id;
	private String title;
	private String description;
	private String createUserId;
	private Date createDate;
	private String articleIdsStringList=null;  

	public String getId() {
		return id;
	}
	public void setId(String blogID) {
		this.id = blogID;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public void setArticleList(String articleIdsStringList) {
		this.articleIdsStringList = articleIdsStringList;
	}
	public String getArticleList() {
		return articleIdsStringList; 
	}  

}
