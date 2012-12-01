package onevillage.blog.service;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.wink.common.model.multipart.InMultiPart;
import org.json.JSONArray;
import org.json.JSONObject;

//Sets the path to base URL + /Blog
@Path("/blog")
public interface BlogService   {
	
	//To create an blog by issuing a POST method  
	
	@Path("/create")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public JSONObject createBlog(String a);

	//delete  an blog by issuing a DELETE method
	// delete  a article in a blog by issuing a DELETE method
	@Path("/{blog_id}")
	@DELETE
	public Response delete(@PathParam("blog_id") String blogId, @QueryParam("user_id") String userId, @QueryParam("article_id") String articleId);
	
	//get all the blogs by issuing a GET method   
	//get a perticular blog by  issuing a GET method
	
	@GET
	public JSONArray getBlogs(@QueryParam("blog_id") String blogId);
	
	
	
	//To create an article by issuing a POST method  
	@Path("/{blog_id}/article")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public JSONObject createArticle(@PathParam("blog_id") String blogId, String b);

	
	//get all the articles by issuing a GET method 
	
	@Path("/{blog_id}/article")
	@GET
	public JSONArray getArticles(@PathParam("blog_id") String blogId);
	
	
	//get a particular article of a blog by issuing a GET method  
	
	@Path("/{blog_id}")
	@GET
	public JSONArray getArticle(@PathParam("blog_id") String blogId,@QueryParam("article_id") String articleId);
	
	

	/*//delete  a article by issuing a DELETE method    */
	
	@Path("/{blog_id}/article/{article_id}/{user_id}")
	@DELETE
	public Response deleteArticle(@PathParam("blog_id") String blogId, @QueryParam("article_id") String articleId,@QueryParam("user_id") String userId); 
	
			
			
}
