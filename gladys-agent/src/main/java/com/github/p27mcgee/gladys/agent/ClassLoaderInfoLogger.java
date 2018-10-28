package com.github.p27mcgee.gladys.agent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClassLoaderInfoLogger {

	private static Logger logger = LogManager.getLogger(ClassLoaderInfoLogger.class);	
	
	public static final int INDENT_SPACES = 4;
	public static final String SPACES = 
			"                                                                                                          ";	
	
	public static void logClassLoaderInfo(Class<?> clazz) {
		if (logger.isDebugEnabled()) {
			logClassLoaderInfo(clazz.getSimpleName(), clazz.getClassLoader());
		}
	}
	
	private static void logClassLoaderInfo(String name, ClassLoader loader) {
		logClassLoaderInfo(name, loader, 0);		
	}
	
	private static void logClassLoaderInfo(String name, ClassLoader loader, int depth) {
		if (logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append(name);
			sb.append(SPACES.substring(0, depth * INDENT_SPACES));
			sb.append(" loader[").append(System.identityHashCode(loader)).append("] ");
			sb.append(" ").append(loader.getClass().getName());
			for (int i = depth; i > 0; --i) {
				sb.append("/parent");	
			}
			
			logger.debug(sb.toString());
			
			ClassLoader parentLoader = loader.getParent();
			if (parentLoader != null) {
				logClassLoaderInfo(name, parentLoader, ++depth);
			}
		}
	}

}
