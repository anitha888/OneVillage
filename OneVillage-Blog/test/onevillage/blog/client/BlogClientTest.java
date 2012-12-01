
package onevillage.blog.client;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import onevillage.blog.service.UserAuthentication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BlogClientTest {

	
	BlogClient client;
	String m_blogId;
	String m_blogtitle = "My Techno-Managerial Blog";
	String m_description = "Describes fundamentals of Technology and Management";
	String m_userId="ra.one";
	Date m_date = null;
	String m_articletitle ="Cloud Computing";
	String m_articlecontent ="Information about Cloud Computing";
	String m_articleId = null;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		client = new BlogClient();
		m_blogId = null;
		DateFormat formatter = new SimpleDateFormat("mm-dd-yyyy");
		
		try {
			String strDate = getTodaysDate();
			 m_date = formatter.parse(strDate);
			
		} catch (Exception e) {
			
		}		
	}

	@After
	public void tearDown() throws Exception {
	}

	private static String getTodaysDate(){
		Calendar cal = new GregorianCalendar();
		
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String strTodaysDate = (month + 1) +"-" + day + "-" + year;
		System.out.println("Current date : " + day + "/" + (month + 1) + "/" + year);
		return strTodaysDate;
	} 
	
	@Test
	public final void testCreateBlog() {
		 
		
		
		m_blogId  = client.createBlog(m_blogtitle, m_description, m_userId, m_date);
		
		assertNotNull("Blog Created", m_blogId);
		 
	} 

	@Test
	public final void testDeleteBlog()  throws ParseException {
		
		// Create a blog
		testCreateBlog();
		
		assertNotNull("Blog Created", m_blogId);
		
		// Delete a blog
		boolean retValue = client.deleteBlogOrArticle(m_blogId, m_userId,null);
		
		assertTrue("Blog Deleted succuessfully ", retValue);
	}

	@Test
	public final void testGetAllBlogs()   throws ParseException {
		
		// Create a blog
		testCreateBlog();
		
		assertNotNull("Blog Created", m_blogId);
		// Get all Blogs withinit
		
		JSONArray blogs = client.getAllBlogs();
				
		
		boolean length = false;
		if(blogs.length() > 0)
			 length = true;
		
		assertTrue("Got all blogs", length);

	} 
	
	@Test
	public final void testGetBlog()throws ParseException {
		// Create a blog
		testCreateBlog();
		
		assertNotNull("Blog Created", m_blogId);
		
		// Get a blog
		JSONArray blog = client.getBlog(m_blogId);
						
		boolean length = false;
		if(blog.length() > 0)
			 length = true;
		
		assertTrue("Got a blog", length);		
	}  


	@Test
	public final void createArticleInTheblog() {
		
		
		testCreateBlog();
		
		assertNotNull("Blog Created", m_blogId);
		String strJSONArticleID;
		strJSONArticleID  = client.createArticleInTheblog(m_blogId,m_articletitle,m_articlecontent,m_userId );
		// Parse to get the article ID
		try {
				JSONObject json = new JSONObject(strJSONArticleID);
				m_articleId =json.getString("article_id");
			}
		 catch (JSONException e) {
			 m_articleId = null;
				e.printStackTrace();
				
			}
		
		assertNotNull("Article Id returned", m_articleId);
	} 
	
	@Test
	public final void testGetarticleFromBlog() {
		
		// Create a blog and an article
		createArticleInTheblog();
		
		assertNotNull("Blog Created", m_blogId);
		
		JSONArray article = client.getarticleFromBlog(m_blogId,m_articleId);
		
		boolean length = false;
		
		
		if(article != null && article.length() > 0)
			 length = true;
		
		assertTrue("Got a article", length);
		
	}

	@Test
	public final void testGetAllArticlesFromBlog() {
		// Create a blog and an article
		createArticleInTheblog();
		client.createArticleInTheblog(m_blogId,m_articletitle,m_articlecontent,m_userId );
		client.createArticleInTheblog(m_blogId,m_articletitle,m_articlecontent,m_userId );
		JSONArray articles = client.getArticlesFromBlog(m_blogId);

		boolean length = false;
		if(articles.length() > 0)
			 length = true;
		
		assertTrue("Got a articles", length);	
	} 

	@Test
	public final void testDeleteArticleFromblog() {
		
		// Create a Blog and Create an article in the blog
		createArticleInTheblog();
		// Delete Article from the blog
		boolean bDeleteArticle = client.deleteArticleFromblog(m_blogId, m_userId, m_articleId);
		assertTrue("Deleted Blog", bDeleteArticle=true);
	}
	
}	
		
	
		
	
		


	
