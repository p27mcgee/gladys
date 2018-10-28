package com.github.p27mcgee.gladys.agent;

import static net.bytebuddy.matcher.ElementMatchers.hasSuperType;
import static net.bytebuddy.matcher.ElementMatchers.isAbstract;
import static net.bytebuddy.matcher.ElementMatchers.isConstructor;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.none;
import static net.bytebuddy.matcher.ElementMatchers.not;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import java.lang.instrument.Instrumentation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.p27mcgee.gladys.agent.advice.CoyoteWriterAdvice;
import com.github.p27mcgee.gladys.agent.advice.HttpServletAdvice;
import com.github.p27mcgee.gladys.agent.advice.StringConstructorAdvice;
import com.github.p27mcgee.gladys.agent.bootloader.GladysRequestLedger;
import com.github.p27mcgee.gladys.agent.data.GladysRequestLedgerImpl;

import net.bytebuddy.agent.builder.AgentBuilder;

public class GladysAgent {
	
	private static Logger logger = LogManager.getLogger(GladysAgent.class);	

	static private String agentArgs;
	static private Instrumentation instrumentation;

	static {
		ClassLoaderInfoLogger.logClassLoaderInfo(GladysAgent.class);
	}

	public static void premain(String agentArgs, Instrumentation instrumentation) {
		GladysAgent.agentArgs = agentArgs;
		GladysAgent.instrumentation = instrumentation;

		logger.info("GladysAgent is instrumenting the application.");
		logger.info("agentArgs = " + agentArgs);
				
//	 The class loading of Java web applications	and the packaging of 
//	 classes by Spring Boot
// 		https://groups.google.com/forum/#!topic/byte-buddy/lkAvpB1NyME 
//	 seem to make this approach problematic:

		// Use method delegate to instrument requests
//		new AgentBuilder.Default()
//			.type(hasSuperType(named("javax.servlet.http.HttpServlet")))  
//        	.transform(new AgentBuilder.Transformer() {
//        		@Override
// 				public Builder<?> transform(Builder<?> builder, TypeDescription typeDescription,
//						ClassLoader classLoader, JavaModule module) {
//					return builder.method(named("service"))
//						.intercept(MethodDelegation.to(HttpServletInterceptor.class));
//				}
//        	}).installOn(instrumentation);		
		
		// Byte Buddy author Rafael Winterhalter recommends using "Advice" 
		// rather than MethodDelegation so that the advice code is loaded 
		// into the Servlet's context classloader that allows it to access 
		// ServletRequest,  and ServletResponse objects
		new AgentBuilder.Default()
		.with(new InstrumentedBuilderListener())
		.with(new DebugBuilderListener())
		.type(hasSuperType(named("javax.servlet.Servlet")))
        .transform(
            new AgentBuilder.Transformer.ForAdvice()
               .include(HttpServletAdvice.class.getClassLoader())
               .advice(named("service").and(not(isAbstract())), 
            		  HttpServletAdvice.class.getName())
        )
        .installOn(instrumentation);								
		
		logger.info("GladysAgent has applied HttpServlet advice.");
		

		new AgentBuilder.Default()
		.with(new DebugBuilderListener())
        .ignore(none())
        .disableClassFormatChanges()
        .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
        .type(named("java.lang.String"))
        .transform( 
   	        new AgentBuilder.Transformer.ForAdvice()
    	        .include(StringConstructorAdvice.class.getClassLoader())
    	        .advice(isConstructor(), StringConstructorAdvice.class.getName())
		)
        .installOn(instrumentation); 

		logger.info("GladysAgent has applied String constructor advice for String creation counting.");

		// ObjectConstructorAdvice should catch heap allocated for 
		// all objects descendant from java.lang.Object and created with "new"		
		// TODO Heap usage for the following is not be included in the totals:
		// - Array objects
		// - Object created by reflection, e.g. Class.newInstance();
		// - Objects created by Object.clone()
		// - Objects created by deserialization		
		// others?		

		// Measure memory usage for request
		// this breaks the app!!!		
//		new AgentBuilder.Default()
//		.with(new DebugBuilderListener())
//        .ignore(none())
//        .disableClassFormatChanges()
//        .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
//        .type(named("java.lang.Object"))
//        .transform( 
//	        new AgentBuilder.Transformer.ForAdvice()
//    	        .include(ObjectConstructorAdvice.class.getClassLoader())
//    	        .advice(isConstructor(), ObjectConstructorAdvice.class.getName())
//		)
//        .installOn(instrumentation); 
//
//		logger.info("GladysAgent has applied Object constructor advice for heap usage monitoring.");


		// This approach doesn't work for various reasons.
		// One reason is that the application loads/updates various
		// panels with separate requests so there is no consistent place 
		// to put the metrics that would be consistently updated.
		// Also, my hope of using sed-like stream editting to 
		// insert the metrics based on recognizing a pattern in the 
		// output stream won't fly because CoyoteWriter uses 
		// "new IO" and there isn't a clear stream to tap into for
		// monitoring and data insertion.
		
		// Add metrics to rendered output page		
//		new AgentBuilder.Default()
//		.with(new DebugBuilderListener())
//		.type(named("org.apache.catalina.connector.CoyoteWriter"))
//        .transform(
//            new AgentBuilder.Transformer.ForAdvice()
//               .include(CoyoteWriterAdvice.class.getClassLoader())
//               .advice(takesArguments(String.class).and(takesArguments(1)), CoyoteWriterAdvice.class.getName())
//        )
//        .installOn(instrumentation);								
//		
//		logger.info("GladysAgent has applied CoyoteWriter advice.");
		
	}
	
	public static String getAgentArgs() {
		return agentArgs;
	}

	public static void setAgentArgs(String agentArgs) {
		GladysAgent.agentArgs = agentArgs;
	}


	public static Instrumentation getInstrumentation() {
		return instrumentation;
	}

	public static void setInstrumentation(Instrumentation instrumentation) {
		GladysAgent.instrumentation = instrumentation;
	}
	
	public static GladysRequestLedger createGladysRequestLedger() {
		return new GladysRequestLedgerImpl(instrumentation);
	}
}
