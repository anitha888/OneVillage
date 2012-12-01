package onevillage.blog.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.core.MediaType;



import org.apache.log4j.Logger;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.common.model.multipart.BufferedOutMultiPart;
import org.apache.wink.common.model.multipart.OutPart;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;
public class BlogClient {

static Logger logger = Logger.getLogger(BlogClient.class);
	
	// private String endPoint = "http://localhost:8080/OneVillage/blog";
	private static String endPoint = "http://localhost:8082/OneVillage";


	/**
	 * 
	 */
	public BlogClient() {
		// TODO Auto-generated constructor stub
	}

	
	public static String createBlog(String blogtitle, String description, String userId, Date createDate) 
	{
		SimpleDateFormat dateformat = new SimpleDateFormat("MM-dd-yyyy");
		String blogId = null;
		try { 

			JSONObject obj = new JSONObject();
			obj.put("title", blogtitle);
			obj.put("description",description );
			obj.put("user_id", userId);
			obj.put("created_date", dateformat.format(createDate));
			String jsonString=obj.toString();
			String url = endPoint+"/blog/create";

			//create the rest client instance
			RestClient client = new RestClient();

			// create the resource instance to interact with
			Resource resource = client.resource(url);			
			
			
			 
					
			//issue the request
			String str = resource.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class,jsonString);
			
			
			
			
			JSONObject returnJson = new JSONObject(str);
			blogId = (String)returnJson.get("blog_id");
			
			logger.info("Blog " + blogtitle + " created with id " + blogId);
			
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		return blogId;
	}
	
	
	/*  method for create areticle  */
	
	public static String createArticleInTheblog(String blogId, String articletitle,String articlecontent,String userId) {
		boolean returnValue = false;
		String article_info=null;
		DateFormat formatter = new SimpleDateFormat("mm-dd-yyyy");
		Date date = null;
		try {
			String strDate = getTodaysDate();
			 date = formatter.parse(strDate);
			
		} catch (Exception e) {
			
		}
		
		try {
			
			
			JSONObject obj = new JSONObject();
			obj.put("title",articletitle);	
			obj.put("content",articlecontent);
			obj.put("createUserId", userId);
			obj.put("created_date", date);
			String jsonString=obj.toString();

			
			
			
			
			String url = endPoint+"/blog/"+blogId+"/article";

			
			//create the rest client instance
			RestClient client = new RestClient(); 

			// create the resource instance to interact with
			Resource resource = client.resource(url);

			//issue the request
			
			article_info= resource.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class,jsonString);
			//System.out.println("Response = " + response);
			
			
			logger.info("Article created in the blog " + blogId  + "  with article ID is  "+article_info);
			
			
		} catch (ClientWebException e) {
			e.printStackTrace();
			logger.error("Exception caught " + e.getResponse().getStatusCode() + " " + e.getResponse().getMessage());
			
		} catch (Exception e) {
			logger.error("Exception caught " + e.getMessage());
			e.printStackTrace(); 
		}

		return article_info;
	}

		

	public static boolean deleteBlogOrArticle(String blogId, String userId, String articleId) {
		
		boolean returnValue = false;
		
		try {
			
			String url = endPoint+"/blog/"+blogId+"?user_id="+userId +"&article_id="+articleId;

			//create the rest client instance
			RestClient client = new RestClient(); 

			// create the resource instance to interact with
			Resource resource = client.resource(url);

			

			//issue the request
			String response = resource.delete(String.class);
			
			logger.info("Blog is deleted  " + blogId);
			

			returnValue = true;
		} catch (ClientWebException e) {
			logger.error("Exception caught " + e.getResponse().getStatusCode() + " " + e.getResponse().getMessage());
		} catch (Exception e) {
			logger.error("Exception caught " + e.getMessage());
			e.printStackTrace(); 
		}

		return returnValue;
	} 

	
	public static JSONArray getAllBlogs() {
		
		JSONArray blogs = null;
		try {
			String url = endPoint+"/blog/";

			//create the rest client instance
			RestClient client = new RestClient(); 

			// create the resource instance to interact with
			Resource resource = client.resource(url);

			//issue the request
			String b =  resource.accept(MediaType.APPLICATION_JSON).get(String.class);
			
			
			blogs = new JSONArray(b);
			logger.info("All Blogs: " + blogs);

		} catch (ClientWebException e) {
			System.out.println("Exception caught " + e.getResponse().getStatusCode() + " " + e.getResponse().getMessage());
		} catch (Exception e) {
			System.out.println("Exception caught " + e.getMessage());
			e.printStackTrace(); 
		}
		return blogs;
	}
	
	public static JSONArray getBlog(String blogId) {
		JSONArray blog = null;
		try {
			String url = endPoint+"/blog?blog_id="+blogId;

			//create the rest client instance
			RestClient client = new RestClient(); 

			// create the resource instance to interact with
			Resource resource = client.resource(url);

			//issue the request
			String b =  resource.accept(MediaType.APPLICATION_JSON).get(String.class);
			
			
			blog = new JSONArray(b);
			logger.info("Perticular blog is " + blogId  );

		} catch (ClientWebException e) {
			System.out.println("Exception caught " + e.getResponse().getStatusCode() + " " + e.getResponse().getMessage());
		} catch (Exception e) {
			System.out.println("Exception caught " + e.getMessage());
			e.printStackTrace(); 
		}
		return blog;
	}
	
	/*
	 * Delete Article from the blog
	 * Required items: BlogId, ArticleId and UserId
	 */
	public static boolean deleteArticleFromblog(String blogId, String userId, String articleId) {
		boolean returnValue = false;

		try {
			
			String url = endPoint+"/blog/"+blogId+"?user_id="+userId+ "&article_id="+ articleId;
			
			//create the rest client instance
			RestClient client = new RestClient(); 

			// create the resource instance to interact with
			Resource resource = client.resource(url);

			//issue the request
			String response = resource.delete(String.class);
			logger.info("deleted Article From the Blog  " + articleId);

			returnValue = true;
		} catch (ClientWebException e) {
			logger.error("Exception caught " + e.getResponse().getStatusCode() + " " + e.getResponse().getMessage());
		} catch (Exception e) {
			logger.error("Exception caught " + e.getMessage());
			e.printStackTrace(); 
		}

		return returnValue;
	}
	
	/*
	 * Get Article From the Blog
	 * Based on Blog Id and Article Id
	 */
	public static JSONArray getarticleFromBlog(String blog_id, String articleId) {
		JSONArray articles = null;
		try {
				String url = null;
				if(articleId == null)
				{
					
					// Time to get getArticles...
					url = endPoint+"/blog/" +blog_id +"/article";
				}
				else
				{
					// Time to get 
					url = endPoint+"/blog/" +blog_id +"?article_id=" + articleId;
				}
	
				//create the rest client instance
				RestClient client = new RestClient(); 
	
				// create the resource instance to interact with
				Resource resource = client.resource(url);
	
				//issue the request
				String b =  resource.accept(MediaType.APPLICATION_JSON).get(String.class);
				
				
				articles = new JSONArray(b);
				
				logger.info("Article in blog " + blog_id  + "is " + articles + " "+b);

		} catch (ClientWebException e) {
			logger.error("Exception caught " + e.getResponse().getStatusCode() + " " + e.getResponse().getMessage());
		} catch (Exception e) {
			logger.error("Exception caught " + e.getMessage());
			e.printStackTrace(); 
		}
		return articles;
	}
	
	/*
	 * Get All Articles from the Blog
	 * Required Input: BlogId
	 */
	public static JSONArray getArticlesFromBlog(String blogId) {
		JSONArray articles = null;
		try {
			String url = endPoint+"/blog/" +blogId+"/article/";

			//create the rest client instance
			RestClient client = new RestClient(); 

			// create the resource instance to interact with
			Resource resource = client.resource(url);

			//issue the request
			String b =  resource.accept(MediaType.APPLICATION_JSON).get(String.class);
			
			
			articles = new JSONArray(b);
			logger.info("All articles in blog " + blogId  +"are : " + "total " +articles.length()+" " +articles);

		} catch (ClientWebException e) {
			logger.error("Exception caught " + e.getResponse().getStatusCode() + " " + e.getResponse().getMessage());
		} catch (Exception e) {
			logger.error("Exception caught " + e.getMessage());
			e.printStackTrace(); 
		}
		return articles;
	}
	
	private static String getTodaysDate(){
		Calendar cal = new GregorianCalendar();
		
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String strTodaysDate = (month + 1) +"-" + day + "-" + year;
		
		return strTodaysDate;
	} 
	
	public static void main(String[] args) {
	
		Date createDate = null;
		String strTodaysDate = getTodaysDate();
		
		logger.info("Starting Ruby Conversion ....:)" + strTodaysDate);
		DateFormat formatter = new SimpleDateFormat("mm-dd-yyyy");
		try {
			createDate = formatter.parse(strTodaysDate);
			
		} catch(Exception e) {
			
		}
		String blogId = createBlog("My Tech Blog","Tech Blog Description","ra.one",createDate );
		String articleInfo = createArticleInTheblog(blogId, "tech article","tech article info","ra.one");
		String articleId=null;
		try {
			JSONObject json = new JSONObject(articleInfo);
			articleId =json.getString("article_id");
		}
	 catch (JSONException e) {
		 articleId = null;
			e.printStackTrace();			
		}
		JSONArray article =getarticleFromBlog(blogId,articleId);
boolean length = false;
		
		logger.info(article);
		
		if(article != null && article.length() > 0)
			 length = true;
		
	}
	

	} 

