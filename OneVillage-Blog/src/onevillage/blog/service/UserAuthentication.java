package onevillage.blog.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import exception.InternalServerException;

import onevillage.db.MongoConnection;

public class UserAuthentication {

	/**
	 * Check whether user is a registered user in the community
	 * @throws InternalServerException 
	 */

	public static boolean isValidUser(String user_id) throws InternalServerException{

		boolean isValid = false;
		
		DBCollection col = MongoConnection.connectAndGetCollection(MongoConnection.sCollectionUserId);
		DBCursor cur = col.find();
		BasicDBObject query = new BasicDBObject();
		query.append("user_id", user_id);
		cur = col.find(query);
		int size = cur.size();
		if(size > 0){
			isValid = true;
		}
		
		return isValid;
	}
}


