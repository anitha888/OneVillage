package onevillage.blog.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import onevillage.db.BlogDbService;
import onevillage.blog.service.UserAuthentication;

import org.apache.log4j.Logger;
import org.apache.wink.common.model.multipart.InMultiPart;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import common.Blog;
import common.Article;
import common.BlogServiceUtility;


import exception.InternalServerException;
import exception.NotFoundException;

public class BlogServiceImpl implements BlogService {

	private static BlogServiceImpl svc;
	Logger logger = Logger.getLogger(BlogServiceImpl.class);
	BlogDbService dbService;
	
	public BlogServiceImpl() {
		dbService = new BlogDbService();
	}
	
	public synchronized static BlogServiceImpl getInstance() {
		if (svc == null) {
			svc = new BlogServiceImpl();
		}
		return svc;
	}

	@Override
	public JSONObject createArticle(String blogId, String jsonString) {

		logger.info("************** In createArticle **********************");
		logger.info("In createArticle -- blogId" + blogId + "jsonString" + jsonString);
		JSONObject returnJson = null;
		
		String article_id = null;
		try {
			JSONObject json = new JSONObject(jsonString);
			
			article_id = dbService.addArticleToBlog(jsonString,blogId);
			returnJson = new JSONObject();
			returnJson.put("article_id", article_id); 
			
			
		} catch (JSONException e) {
			logger.error(e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		} catch (ParseException e) {
			logger.error("date in not valid " + e.getMessage());
			logger.error(e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
			
		} catch (InternalServerException e) {
			logger.error(e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
		 catch (Exception e) {
				logger.error(e.getClass().getName() + " " + e.getMessage());
				e.printStackTrace();
				throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
			}

		
		
		return returnJson;
	}

	
	@Override
	public JSONObject createBlog(String jsonString)
	{
		logger.info("************** In createBlog **********************");
		JSONObject returnJson = null;
		String blog_id = null;
		
		try {
			JSONObject json = new JSONObject(jsonString);
			
			if (!UserAuthentication.isValidUser(json.getString("user_id"))) {
				throw new WebApplicationException(Status.NOT_FOUND);
			}
			
			Blog blog= new Blog();
			
			blog.setTitle(json.getString("title"));
			blog.setDescription(json.getString("description"));
			blog.setCreateUserId(json.getString("user_id"));
			
					
			Date created_date = new Date();
			created_date = convertInJavaDate(json.getString("created_date"));
			
			blog.setCreateDate(created_date);
			
			
			logger.info("In createBlog "+json);
			
			blog_id = dbService.createBlog(blog);
			returnJson = new JSONObject();
			returnJson.put("blog_id", blog_id);
			
		} catch (JSONException e) {
			logger.error(e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		} catch (ParseException e) {
			logger.error("date in not valid " + e.getMessage());
			logger.error(e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
			
		} catch (InternalServerException e) {
			logger.error(e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}

		//return json.toString();
		
		return returnJson;
	}

	
	@Override
	public Response delete(String blogId, String userId, String articleId) {
		logger.info("************** In delete **********************");
		logger.info("blogId=" + blogId);
		logger.info("userId=" + userId);
		logger.info("articleId=" + articleId);
		try {
			if(userId == null) {
				return Response.status(Status.BAD_REQUEST).build();
			}
			
			if (!UserAuthentication.isValidUser(userId)) {
				throw new NotFoundException("User Not Found");
			}
			
			if(articleId != null && articleId.length() > 4) {
				
				deleteArticleFromBlog(blogId, userId, articleId);
			} else {
				logger.info("************** To Be  deleteBlog **********************");
				deleteBlog(blogId, userId, articleId);
			}
			
			

		} catch (NotFoundException ne) {
			logger.error("NotFoundException " + ne.getMessage());
			
			return Response.status(Status.NOT_FOUND).build();
		} catch (InternalServerException ise) {
			logger.error("Internal Server Error) " + ise.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} catch (Exception e) {
			logger.error("Internal Server Error) " + e.getMessage());
			
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	
		return Response.ok().build();
	}
	

	
	private void deleteBlog(String blogId, String userId, String articleId) throws Exception {
		dbService.deleteBlog(blogId, userId);		
	}
	
	public void deleteArticleFromBlog(String blogId, String userId, String articleId) throws Exception {
		dbService.deleteArticleFromBlog(blogId, userId, articleId);
}

	@Override
	public JSONArray getArticle(String blogId, String articleId) {
		// Get Article from the BLOG
		
		logger.info("In getArticle -- blogId --- " + blogId + "----articleId --- " + articleId);
		
		JSONArray articleArray = new JSONArray();

		try {
			if(articleId != null) {
				//Article Id is given
				logger.info("Get Article From Blog -- Case Article Id is given" + articleId);
				logger.info("Before DBService::GetArticle");
				
				Article article = dbService.getArticleFromBlog(blogId, articleId);
				
				
				
				
				if (article!= null) {
					logger.info(article.getblogId() + "---" + article.getContent() + "---" + article.getContent() + "----" + article.getTitle());
					logger.info(article);
					articleArray.put(BlogServiceUtility.getArticleJSONObject(article));
						
					logger.info("blog url" + article.getBlogUrl());
				}
				else
				{
					logger.info("ARTICLE NULL **************");
				}
			}
			else {
				logger.info("Get Article From Blog -- case No Article ID -- NULL");
				logger.info("Before Calling DBService::GetAllArticles");
				
				List<Article> articleList = dbService.getAllArticlesFromBlog(blogId);
				logger.info("After Calling DBService::GetAllArticles From Blog");
				
				// Now that we have articles from Blog -- lets check the length and other properties to confirm code working
				
				for (Article p: articleList) {
					logger.info(p.getblogId() + "---" + p.getContent() + "---" + p.getContent() + "----" + p.getTitle());
					articleArray.put(BlogServiceUtility.getArticleJSONObject(p));
				}
			}
			return articleArray;

		} catch (JSONException e) {
			logger.error(e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
			throw new WebApplicationException();
		} catch (Exception e) {
			logger.error(e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
			throw new WebApplicationException();
		}
	}
	
	
	@Override
	public JSONArray getBlogs(String blogId) {

		JSONArray blogArray = new JSONArray();
		List<Blog> blogs = null;

		try {
			
			
			if(blogId != null) {
				blogs = dbService.getBlog(blogId);
			}
			else
			{
				blogs = dbService.getAllblogs();
				
				
			}
			for (Blog a: blogs) {
				blogArray.put(BlogServiceUtility.getblogJSONObject(a));
			}	
									
			
			
		} catch (Exception e) {
			logger.error(e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
			throw new WebApplicationException();
		}

		return blogArray;
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

	
	@Override
	public JSONArray getArticles(String blogId) {
		
		
		JSONArray articleArray = new JSONArray();
		try {
			// getArticle with ArticleId Null would return all the Articles
			articleArray = getArticle(blogId,null);
		
	} catch (Exception e) {
		logger.error(e.getClass().getName() + " " + e.getMessage());
		e.printStackTrace();
		throw new WebApplicationException();
	}

		return articleArray;
	}


	@Override
	public Response deleteArticle(String blogId, String articleId, String userId) {
		// TODO Auto-generated method stub
		return null;
	}
		
	
}
