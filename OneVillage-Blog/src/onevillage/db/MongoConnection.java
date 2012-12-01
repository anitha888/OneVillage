package onevillage.db;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;


import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import common.InitServlet;

import exception.InternalServerException;

public class MongoConnection {
static Logger logger = Logger.getLogger(MongoConnection.class);
	
	// mongodb setup - each collection should be thought of as a database
	public static final String sCollectionBlog = "mongo.blogCollection";
	public static final String sCollectionArticle = "mongo.articleCollection";
	public static final String sCollectionUserId = "mongo.userIdCollection";
	public static final String sDBBlog = "mongo.dbBlog";
	public static final String sDBUser = "mongo.dbUser";
	public static final String sHost = "mongo.host";
	public static final String sDBArticle = "mongo.dbArticle";
	private static DBCollection blogCollection;
	private static DBCollection articleCollection;
	private static DBCollection userIdCollection;
	
	//private static Properties props;
	
	public  MongoConnection() {}
	
	
	/**
	 * connection setup
	 * 
	 * @param props
	 */
	static {
	
	}

	/**
	 * 
	 */
	public void release() {
		blogCollection = null;
		userIdCollection = null;
		articleCollection = null;
	}
	
	/**
	 * just like a jdbc connect - get me a connection to the data
	 * 
	 * @return
	 * @throws Exception 
	 */
	public static DBCollection connectAndGetCollection(String collectionName) throws InternalServerException{

		DBCollection collection = null;
		Mongo m;
		DB db = null;
		try {
		if (sCollectionBlog.equals(collectionName)) {
			if (blogCollection != null && blogCollection.getName() != null)
				return blogCollection; 
			else 
				m = new Mongo(InitServlet.getProperty(sHost));
				db = m.getDB(InitServlet.getProperty(sDBBlog));
		} else if (sCollectionUserId.equals(collectionName)) {
			if (userIdCollection != null && userIdCollection.getName() != null)
				return userIdCollection;
			else 
				m = new Mongo(InitServlet.getProperty(sHost));
				db = m.getDB(InitServlet.getProperty(sDBUser));
		} else if (sCollectionArticle.equals(collectionName)) {
			if (articleCollection != null && articleCollection.getName() != null)
				return articleCollection;
			else 
				m = new Mongo(InitServlet.getProperty(sHost));
				db = m.getDB(InitServlet.getProperty(sDBArticle));
			
		}
			collection = db.getCollection(InitServlet.getProperty(collectionName));
		} catch (UnknownHostException e) {
			logger.error("UnknownHostException " + e.getMessage());
			e.printStackTrace();
			throw new InternalServerException(e.getMessage());
		} catch (MongoException e) {
			logger.error("MongoException " + e.getMessage());
			e.printStackTrace();
			throw new InternalServerException(e.getMessage());
		}
		
		if (collection == null)
			throw new InternalServerException("Missing collection: " + collectionName);

		return collection;
	}
	
	/*public static GridFS connectAndGetGridFS() throws UnknownHostException, MongoException{
		
		Mongo m = new Mongo(InitServlet.getProperty(sHost));

		DB db = m.getDB(InitServlet.getProperty(sDBBlog));

		//Create GridFS object
		GridFS fs = new GridFS(db);
		return fs; 
	} */

}
