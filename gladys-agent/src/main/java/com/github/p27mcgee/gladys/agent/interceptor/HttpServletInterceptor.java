package com.github.p27mcgee.gladys.agent.interceptor;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

@Deprecated 
// Context classloading issues killed this approach
public class HttpServletInterceptor {
	
	public static void service(@SuperCall Runnable superService) 
		throws ServletException, IOException {
		
//		System.out.println("HttpServletInterceptor.service() servletInstance = " + servletInstance.toString());
//		System.out.println("HttpServletInterceptor.service() req = " + req.toString());
//		System.out.println("HttpServletInterceptor.service() res = " + resp.toString());
		
		long start = System.nanoTime();
		superService.run();
		long end = System.nanoTime();
		
		logDuration(start, end, "servlet.service() time");		
	}
		
	static void logDuration(long startNanos, long endNanos, String intervalName) {
		System.out.format("%,14d nSec - %s", (endNanos - startNanos), intervalName);
	}
}
