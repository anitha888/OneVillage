package onevillage.blog.service;


import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;


public class BlogServiceApp extends Application {
	
	private Set<Object> svc_singletons = new HashSet<Object>();

	/**
	 * Per-request service classes are instantiated for each request, and
	 * disposed of after the request is processed.
	 */
	private Set<Class<?>> svc_classes = new HashSet<Class<?>>();
	
	public BlogServiceApp() {
		svc_singletons.add(BlogServiceImpl.getInstance());
	}
	
	@Override
	public Set<Object> getSingletons() {
		return svc_singletons;
	}

	@Override
	public Set<Class<?>> getClasses() {
		return svc_classes;
	}

}
