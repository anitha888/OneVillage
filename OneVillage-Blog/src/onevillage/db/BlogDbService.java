package onevillage.db;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import java.util.Calendar;
import java.text.ParseException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.apache.wink.common.model.multipart.InMultiPart;
import org.apache.wink.common.model.multipart.InPart;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import common.Blog;
import common.InitServlet;
import common.Article;
import common.BlogServiceUtility;







import exception.NotFoundException;
public class BlogDbService {
	
	String BLOG_URL = "blogUrl";

	Logger logger = Logger.getLogger(BlogDbService.class);

	public BlogDbService() {

	}
	
	public String createBlog(Blog blog) {
		// TODO Auto-generated method stub
		ObjectId id = null;
		
		try {
			DBCollection col = MongoConnection.connectAndGetCollection(MongoConnection.sCollectionBlog);
			DBObject dob = BlogServiceUtility.convertBlogToMongo(blog);
			col.insert(dob);
			id = (ObjectId)dob.get( "_id" );

			logger.info("Blog ceated successfully in Mongo. id:" + id.toStringMongod());

		} catch (Exception e) {
			logger.error("Error while adding Blog " + e.getMessage());
			e.printStackTrace();
		}

		

		return id.toStringMongod();


	}
	
	
	public String createArticle(Article article) {
		// TODO Auto-generated method stub
		
		ObjectId id = null;
		logger.info("Inside create Article");

		try {
			DBCollection col = MongoConnection.connectAndGetCollection(MongoConnection.sCollectionArticle);
					
			DBObject dob = BlogServiceUtility.convertArticleToMongo(article);
			col.insert(dob);
			id = (ObjectId)dob.get( "_id" );

			logger.info("Article ceated successfully in Mongo. id:" + id.toStringMongod());

		} catch (Exception e) {
			logger.error("Error while adding Article " + e.getMessage());
			e.printStackTrace();
		}

		logger.info("Article added " + article);

		return id.toStringMongod();
	}
	


	public String addArticleToBlog( String article_json_string, String blogId) throws Exception {
		
		logger.info(article_json_string);
		logger.info(blogId);
		
		// First, create article and add it to Mongo
		// This provides unique db id
		logger.info("addArticleToBlob -- inside");
		ObjectId id = new ObjectId(blogId);
		//get blog from the database
		Blog blog = BlogServiceUtility.getBlogFromMongo(id);
		Article newArticleObject = new Article();
		String articleId = null;
		JSONObject returnJson = null;
		try {
			JSONObject json = new JSONObject(article_json_string);
			newArticleObject.setTitle(json.getString("title"));
			newArticleObject.setContent(json.getString("content"));
			newArticleObject.setCreateUserId(json.getString("createUserId"));
			newArticleObject.setBlogUrl(InitServlet.getProperty(BLOG_URL) + newArticleObject.getTitle());
			DateFormat formatter = new SimpleDateFormat("mm-dd-yyyy");
			Date date = null;
			try {
				 date = formatter.parse(BlogServiceUtility.getTodaysDate());
				
			} catch (Exception e) {
				
			}
			// add blob id too...
			newArticleObject.setblogId(blogId);
			
			//Date created_date = new Date();
			// created_date = convertInJavaDate(json.getString("created_date"));
			blog.setCreateDate(date);
			newArticleObject.setCreateDate(date);
			logger.info("AddArticleToBlog---title" + newArticleObject.getTitle());
			logger.info("Calling Store Articles...");
			// time to call create article so that article would be inserted in the MongoDB collection...
			
			articleId = createArticle(newArticleObject); 
			
			
		}catch (JSONException e) {
			logger.error(e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
		
		
		// Second, add the newly created artcle id to the blog array collection
		// Through this, we can make an association between Article and Blog
		
		String articlesIDStringList= null;
		
		 
		if(blog.getArticleList() != null) {
			articlesIDStringList = blog.getArticleList();
		}  
		// at this point, we have inserted our foreign reference into Blog article collection list...
		logger.info("Added Newly created article into blog collection" + articleId);
		articlesIDStringList += articleId +",";
		// Now, write blog back to db
		logger.info("adding article id back to blog collection - closing loop");
		//ObjectMapper mapper = new ObjectMapper();

		String jsonString = articlesIDStringList;//mapper.writeValueAsString(articlesIDStringList);

		logger.info("JSON String" + jsonString);
		DBCollection col = MongoConnection.connectAndGetCollection(MongoConnection.sCollectionBlog);
		
		logger.info("article add - json string" + jsonString);
		BasicDBObject update = new BasicDBObject("blogArticles", jsonString );
		BasicDBObject set = new BasicDBObject("$set", update);

		BasicDBObject query = new BasicDBObject();
		query.put("_id", id);
		DBCursor cursor = col.find(query);
		while (cursor.hasNext()) {
			col.update(cursor.next(), set);
		}
		
		logger.info("Blog updated, articleId = " + articleId);
		
		return articleId;
	}


	public void deleteBlog(String blogId, String userId) throws Exception {

		logger.info("IndbService: deleteBlog, Blogid before " +blogId + " userId " + userId);
		try {
			//convert the blogId from String to ObjectId, throws IllegalArgumentException if the blogId is not valid
			ObjectId id = new ObjectId(blogId);
			logger.info("IndbService: deleteBlog, Blogid is " + id.toString());
			//get the blog from database
			Blog blog = BlogServiceUtility.getBlogFromMongo(id);

			//check if the userId matches blog owner's userId
			if(!userId.equals(blog.getCreateUserId())) {
				throw new NotFoundException("User not found " + userId + " != " + blog.getCreateUserId());
			}


			// get article map from the blog
			logger.info("Now remove Article from Article Hash..." + blog.getArticleList());
			StringTokenizer strArticlesIDList = new StringTokenizer(blog.getArticleList(),",");
			boolean bRemove = false;
			String newArticlesIDList = "";
			logger.info("strArticlesIDList..." + strArticlesIDList);
			
			while(strArticlesIDList.hasMoreElements()){
				String strArticleIDToRemove = strArticlesIDList.nextElement().toString();
				// Now remove the article
				
				ObjectId id_article = new ObjectId(strArticleIDToRemove);

				DBCollection colArticle = MongoConnection.connectAndGetCollection(MongoConnection.sCollectionArticle);
				BasicDBObject deleteArticleValues = new BasicDBObject();


				deleteArticleValues.append("_id", id_article);

				deleteArticleValues.append("createUserId", userId);

				logger.info("Article Collection Size before delete=" + colArticle.find().size());
				System.out.println("Article Collection Size before delete=" + colArticle.find().size());
				colArticle.remove(deleteArticleValues);
				logger.info("Article Collection Size after delete=" + colArticle.find().size());
				System.out.println("Article Collection Size after delete=" + colArticle.find().size());
				logger.info("Article is removed.");
			}
									

			DBCollection col = MongoConnection.connectAndGetCollection(MongoConnection.sCollectionBlog);
			BasicDBObject deleteValues = new BasicDBObject();

			deleteValues.append("_id", id);

			deleteValues.append("createUserId", userId);

			logger.info(col.find().size());
			col.remove(deleteValues);
			logger.info(col.find().size());

		} catch (IllegalArgumentException le) {
			throw new NotFoundException(le.getMessage());
		}
	}

	
	/**
	 * Get All Blogs
	 * @return
	 * @throws Exception 
	 */
	public List<Blog> getAllblogs() throws Exception {
		List<Blog> blogList = null;
		DBCollection col = MongoConnection.connectAndGetCollection(MongoConnection.sCollectionBlog);
		DBCursor cursor = col.find();

		blogList = BlogServiceUtility.translateBlogFromMongo(cursor);
		if(blogList == null || blogList.size() == 0)
		{
			throw new NotFoundException("No Blog found");
		}

		return blogList;	
	}
	
	
public List<Blog> getBlog(String blogId) throws Exception {
		
	
	Blog blog = null;
	List<Blog> blogList = new ArrayList<Blog>();
		try {
			ObjectId id = new ObjectId(blogId);

			blog = BlogServiceUtility.getBlogFromMongo(id);
			
			if(blog == null) {
				throw new NotFoundException("Blog Not found");
			}
			blogList.add(blog);
			return blogList;
			
		}
		catch (IllegalArgumentException le) {
			throw new NotFoundException(le.getMessage());
		}
	     
	}



	
	public List<Article> getAllArticlesFromBlog(String blogId) throws Exception {
		
		
		List<Article> articleList = new ArrayList<Article>();
		try {
			ObjectId id = new ObjectId(blogId);
			logger.info("Before getting Blog from Mongo DB");
			Blog blog = BlogServiceUtility.getBlogFromMongo(id);
			
			if(blog == null)
			{
				throw new NotFoundException("No Blog found");
			}
			
			logger.info("got Blog for a given Blog ID -- " + blogId);
			// get articles will return article object id
			// dereference the article id and go to the article db collection and retrieve actual article object
			
			// get article ids 
			logger.info("get all article ids...");
			
			
			if(blog.getArticleList() == null) {
				throw new NotFoundException("article not found ");
			} else {
				
				logger.info("Blog has articles.... ");
				
				logger.info(blog.getArticleList());
				StringTokenizer strArticlesList = new StringTokenizer(blog.getArticleList() ,",");	
				logger.info("Lets loop though the article collection..." + strArticlesList.countTokens());
				
				while(strArticlesList.hasMoreElements()){
					logger.info("get article id...");
					// get article id
					String article_id = strArticlesList.nextElement().toString();
					// now we got the article id, time to get the Article List from the MongoDB
					logger.info("got article id --- " + article_id);
					logger.info("get Article Object for the article_id---" + article_id);
					
					Article a = BlogServiceUtility.getArticleFromMongo(article_id);
					if(a != null)
					{
						logger.info("SUCCESS - got article from the blog [id=" + a.getId() +"]");
						
						a.setBlogUrl(BLOG_URL + a.getTitle());
						articleList.add(a);
					}
					else
					{
						logger.error("FAILED to get Article from the blog --- :(");
					}
					
				}
			}
			return articleList;

		}
		catch (IllegalArgumentException le) {
			throw new NotFoundException(le.getMessage());
		}
	
	}
public Article getArticleFromBlog(String blogId, String articleId) throws Exception {
	
	Article oArticle = null;
	
	logger.info("Before Calling DBService::GetAllArticles From Blog");
	List<Article> articleList = getAllArticlesFromBlog(blogId);
	logger.info("After Calling DBService::GetAllArticles From Blog");
	
	// Now that we have articles from Blog -- lets check the length and other properties to confirm code working
	
	for (Article p: articleList) {
		logger.info("ARTICLE NULL SYNDROME");
		logger.info(p.toString());
		/*if(p.getId() == articleId)
		{
			oArticle = p;
			
		}*/
		oArticle = p;
		logger.info(oArticle.getblogId() + "---" + oArticle.getContent() + "---" + oArticle.getContent() + "----" + oArticle.getTitle());
		
	}
	return oArticle;
}
		
public void deleteArticleFromBlog(String blogId, String userId,String articleId) throws Exception {
	
	
	
	logger.info("Before Calling DBService::GetAllArticles From Blog");
	List<Article> articleList = getAllArticlesFromBlog(blogId);
	logger.info("Done Calling DBService::GetAllArticles From Blog");
	
	// Now that we have articles from the Blog, loop through each of them and delete the matched ones...
	logger.info("we have articles from the Blog, loop through each of them and delete the matched ones...");
		for (Article p: articleList) {
			
			logger.info("Check Article ID the one to be deleted...");
		if(p.getId().compareTo(articleId) == 0)
		{
			logger.info("Yes!");
			// got the Article... :)
			
			// remove article id from the Article Map
			ObjectId id = new ObjectId(blogId);			
			//get the blog from database
			Blog blog = BlogServiceUtility.getBlogFromMongo(id);
			
			logger.info("Get Blog for the Article ID");
			// get article map from the blog
			logger.info("Now remove Article from Article Hash..." + blog.getArticleList());
			StringTokenizer strArticlesIDList = new StringTokenizer(blog.getArticleList(),",");
			boolean bRemove = false;
			String newArticlesIDList = "";
			logger.info("strArticlesIDList..." + strArticlesIDList);
			
			while(strArticlesIDList.hasMoreElements()){
				String strIDCompare = strArticlesIDList.nextElement().toString();
				logger.info("Compare between Strings ...[strIDCompare=" + strIDCompare + "] && [articleId=" + articleId + "]");
				if(strIDCompare.compareTo(articleId) == 0)
				{
					bRemove = true;
					logger.info("Remove = "+bRemove);
				}
				else
				{
					logger.info("Not match -- assemble string");
					newArticlesIDList += strIDCompare + ",";
					logger.info(newArticlesIDList);
				}
				
			}
			if(bRemove)
			{
				logger.info("blog.getArticles()=" + blog.getArticleList());
				logger.info("bRemove=" + bRemove);				
				blog.setArticleList(newArticlesIDList);
				logger.info("blog.getArticles()=" + blog.getArticleList());
				
				// Now Write Blog back to MongoDB
				
				 
				DBCollection col = MongoConnection.connectAndGetCollection(MongoConnection.sCollectionBlog);
				BasicDBObject query = new BasicDBObject();
				
				query.put("_id", id); 
				// find blog id
				DBCursor cursor = col.find(query);
				if(cursor.size() == 0){
					throw new NotFoundException("Blog Not Found");
				}
				logger.info("cursor.size()=" + cursor.size());
				BasicDBObject rtn = new BasicDBObject();
				
				if (blog.getTitle() != null)
				{
					rtn.append("title", blog.getTitle());
					logger.info("title=" + blog.getTitle());
				}
				if (blog.getDescription() != null)
					rtn.append("description", blog.getDescription());
				
				if (blog.getCreateUserId() != null)
					rtn.append("createUserId", blog.getCreateUserId());
				if (blog.getCreateDate() != null) 
					rtn.append("createDate",blog.getCreateDate());
				
					rtn.append("blogArticles",blog.getArticleList());
				DBObject obj = cursor.next();
				
				BasicDBObject set = new BasicDBObject("$set", rtn);
				logger.info("cursor.hasNext()=" + cursor.hasNext());
				if (cursor.hasNext()  == false) {
					col.update(obj , set);
					logger.info("Cursor updated");
				}
		
				// Now remove the article
				
				ObjectId id_article = new ObjectId(articleId);

				DBCollection colArticle = MongoConnection.connectAndGetCollection(MongoConnection.sCollectionArticle);
				BasicDBObject deleteValues = new BasicDBObject();


				deleteValues.append("_id", id_article);

				deleteValues.append("createUserId", userId);

				logger.info("Article Collection Size before delete=" + colArticle.find().size());
				System.out.println("Article Collection Size before delete=" + colArticle.find().size());
				colArticle.remove(deleteValues);
				logger.info("Article Collection Size after delete=" + colArticle.find().size());
				System.out.println("Article Collection Size after delete=" + colArticle.find().size());
				logger.info("Article is removed.");
			}
			 
		}
		
	}	
			
	DBCollection col = MongoConnection.connectAndGetCollection(MongoConnection.sCollectionArticle);
	BasicDBObject deleteValues = new BasicDBObject();

	deleteValues.append("_id", articleId);
	

	logger.info("Before remove size = " + col.find().size());
	col.remove(deleteValues);
	logger.info("After remove size = " + col.find().size());
	}	

//Method to check validity of date
	private Date convertInJavaDate(String d) throws ParseException {
		logger.info("convertInJavaDate - date" + d);
		Date date = null;
			if(d.compareTo("today") == 0)
			{
				// Ruby on Rails sending Date as "Today" 
				// Lets convert the date
				
				logger.info("Starting Ruby Conversion ....:)");
				
				String strTodaysDate = getTodaysDate();
				
				logger.info("Starting Ruby Conversion ....:)" + strTodaysDate);
				DateFormat formatter = new SimpleDateFormat("mm-dd-yyyy");
				date = formatter.parse(strTodaysDate);
				
				
				
			}
			else
			{
				DateFormat formatter = new SimpleDateFormat("mm-dd-yyyy");
				date = formatter.parse(d);
			}
			
			logger.info(date);
			
			return date;
	}
	private String getTodaysDate(){
		Calendar cal = new GregorianCalendar();
		
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String strTodaysDate = (month + 1) +"-" + day + "-" + year;
		logger.info("Current date : " + day + "/" + (month + 1) + "/" + year);
		return strTodaysDate;
	} 
	

}
