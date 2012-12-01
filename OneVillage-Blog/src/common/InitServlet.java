package common;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class InitServlet extends HttpServlet{
	
	static Logger logger = Logger.getLogger(InitServlet.class);
	
	static Properties prop;
	static String baseUrl;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		/*Get the real path corresponding to the given virtual path
		In this case it will return actual path to which a request of the form http://localhost:8080/OneVillage/
		would be mapped*/
		
		logger.info("*** INIT Servlet ***");
		
		baseUrl = config.getServletContext().getRealPath("/");
		logger.info("Base URL = " + baseUrl);
				
		prop = new Properties();
		String fileName = "OneVillage.config"; 		
		
		try {
		    InputStream is =  this.getClass().getClassLoader().getResourceAsStream(fileName);	
			prop.load(is);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("IOException getting properties. " + e.getMessage());
			logger.error("PROPERTIES NOT LOADED!!");
		}
		logger.info("getProperty(\"blogUrl\") = " + getProperty("blogUrl"));
		baseUrl = getProperty("blogUrl");
		logger.info("Base URL = " + baseUrl);
		logger.info(prop.size() + " Properties loaded");
	}
	
	public static String getProperty(String key) {
		String value = prop.getProperty(key);
		logger.debug("Key: " + key + " Value: " + value);
		return value;
	}
	
	public static String getBaseUrl() {
		return baseUrl;
	}
		
}
