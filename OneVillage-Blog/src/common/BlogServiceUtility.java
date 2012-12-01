package common;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.wink.common.model.multipart.InPart;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;
import onevillage.db.MongoConnection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import exception.InternalServerException;
import exception.NotFoundException;
import java.util.*; 

public class BlogServiceUtility {

	static Logger logger = Logger.getLogger(BlogServiceUtility.class);
	public static DBObject convertBlogToMongo(Blog a) {
		BasicDBObject rtn = new BasicDBObject();
		
		
		if (a.getId() != null)
			rtn.append("blog_id", a.getId());
		
		
		if (a.getTitle() != null)
			rtn.append("title", a.getTitle());
		if (a.getDescription() != null)
			rtn.append("description", a.getDescription());
		if (a.getCreateUserId() != null)
			rtn.append("createUserId", a.getCreateUserId());
		if (a.getCreateDate() != null) 
			rtn.append("createDate",a.getCreateDate());
		
		if (a.getArticleList() != null)
			rtn.append("blogArticles", a.getArticleList());
		else
			rtn.append("blogArticles", "");
		
		return rtn;
	}
	
	
	public static DBObject convertArticleToMongo(Article a) {
		BasicDBObject rtn = new BasicDBObject();
		if (a.getTitle() != null)
			rtn.append("title", a.getTitle());
		if (a.getContent() != null)
			rtn.append("content", a.getContent());
		if (a.getCreateUserId() != null)
			rtn.append("createUserId", a.getCreateUserId());
		if (a.getCreateDate() != null) 
			rtn.append("createDate",a.getCreateDate());
		if (a.getblogId() != null) 
			rtn.append("blogId",a.getblogId());
		
		if (a.getId() != null) 
			rtn.append("articleId",a.getId());
		
		return rtn;
	}
	
	public static List<Blog> translateBlogFromMongo(DBCursor cursor) throws Exception {
		ArrayList<Blog> r = new ArrayList<Blog>();
		
			
		for (int n = 0, N = cursor.size(); n < N; n++) {
			DBObject data = cursor.next();
			Blog a = new Blog();
			
			
			
				
			String v = (String) data.get("title");
			if (v != null)
				a.setTitle(v);
			
			ObjectId articleID = (ObjectId) data.get("_id"); 
			v =  articleID.toStringMongod();
			if (v != null)
				a.setId(v);
			
			v = (String) data.get("description");
			if (v != null)
				a.setDescription(v);

			v = (String) data.get("createUserId");
			if (v != null)
				a.setCreateUserId(v);

			Date d = (Date) data.get("createDate");
			if (d != null)
				a.setCreateDate(d);

			v = (String) data.get("blogArticles");
			if (v != null)
				a.setArticleList(v);
			
			r.add(a);
		}

		return r;
	}
		

	public static List<Article> translateArticleFromMongo(DBCursor cursor) throws Exception {
		ArrayList<Article> r = new ArrayList<Article>();
	
			
		for (int n = 0, N = cursor.size(); n < N; n++) {
			DBObject data = cursor.next();
			Article a = new Article();
			
			String v = (String) data.get("title");
			if (v != null)
				a.setTitle(v);
			
			ObjectId articleID = (ObjectId) data.get("_id"); 
			v =  articleID.toStringMongod();
			if (v != null)
				a.setId(v);
			
			
			v = (String) data.get("content");
			if (v != null)
				a.setContent(v);

			v = (String) data.get("createUserId");
			if (v != null)
				a.setCreateUserId(v);

			Date d = (Date) data.get("createDate");
			if (d != null)
				a.setCreateDate(d);


			v = (String) data.get("blogId");
			if (v != null)
				a.setblogId(v);
			
			r.add(a);
		}

		return r;
	}
	
	
	public static Blog getBlogFromMongo(ObjectId id) throws Exception {
		List<Blog> rtn = null;

		logger.info("BlogServiceUtility: getBlogFromMongo");
		BasicDBObject query = new BasicDBObject();
		query.put("_id", id);

		DBCollection gColl = MongoConnection.connectAndGetCollection(MongoConnection.sCollectionBlog);
		DBCursor cursor = gColl.find(query);

		logger.info("getBlogFromMongo : Total Blogs " +cursor.size());
		if(cursor.size()==0){
			throw new NotFoundException("Blog Not Found");
		}
		
		rtn = translateBlogFromMongo(cursor);

		if(rtn!=null && rtn.size()>0) {
			return rtn.get(0);
		} else {
			throw new InternalServerException();
		}
	}
	
	

	public static Article getArticleFromMongo(String articleId) throws Exception {
		List<Article> rtn = null;
		
		logger.info("****************** BlogServiceUtility: getArticleFromMongo ********************");
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(articleId));

		DBCollection gColl = MongoConnection.connectAndGetCollection(MongoConnection.sCollectionArticle);
		DBCursor cursor = gColl.find(query);

		logger.info("getBlogFromMongo : Total Articles " +cursor.size());
		if(cursor.size()==0){
			throw new NotFoundException("Article Not Found");
		}
		
		rtn = translateArticleFromMongo(cursor);

		if(rtn!=null && rtn.size()>0) {
			return rtn.get(0);
		} else {
			throw new InternalServerException();
		}
	}
	
		
		public static String getTodaysDate(){
		Calendar cal = new GregorianCalendar();
		
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String strTodaysDate = (month + 1) +"-" + day + "-" + year;
		logger.info("Current date : " + day + "/" + (month + 1) + "/" + year);
		return strTodaysDate;
	}
	
	/**
	 * Create Blog JSOn Object
	 * @throws JSONException 
	 */
	public static JSONObject getblogJSONObject(Blog a) throws JSONException {
		
			JSONObject obj = new JSONObject();
			obj.put("blog_id", a.getId());
			obj.put("title", a.getTitle());
			obj.put("description", a.getDescription());
			obj.put("createUserId", a.getCreateUserId());
			obj.put("createDate", a.getCreateDate());
			return obj;
		}

	public static JSONObject getArticleJSONObject(Article p) throws JSONException {

		JSONObject obj = new JSONObject();
		obj.put("id", p.getId());
		obj.put("blog_url", p.getBlogUrl());
		obj.put("article_title", p.getTitle());
		obj.put("blogId", p.getblogId());
		obj.put("content", p.getContent());
		obj.put("createDate", p.getCreateDate());
		obj.put("createUserId", p.getCreateUserId());
		
		
		
		return obj;
	}
	
	public static String getValueInKeyValue(String str) {
		String value = null;
		
		StringTokenizer st = new StringTokenizer(str,"=");
		if (st.hasMoreTokens()) 
			st.nextToken();
		if (st.hasMoreTokens()) 
			value = st.nextToken();
		return value;
	}
	
	public static void main(String[] args) throws Exception {
		BlogServiceUtility utility = new BlogServiceUtility();
		
	} 
	
}
