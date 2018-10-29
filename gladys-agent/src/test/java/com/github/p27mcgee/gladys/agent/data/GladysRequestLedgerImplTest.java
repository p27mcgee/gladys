package com.github.p27mcgee.gladys.agent.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.p27mcgee.gladys.agent.GladysAgent;

@RunWith(MockitoJUnitRunner.class)
public class GladysRequestLedgerImplTest {
	private static final String FIELD_NAME_INSTRUMENTATION = "instrumentation";
	
    @Mock
    private Instrumentation mockInstrumentation;    
    
	Field instrumentationField;
	
	@Before
	public void setUp() throws Exception {
		instrumentationField = GladysRequestLedgerImpl.class.getDeclaredField(FIELD_NAME_INSTRUMENTATION);
		instrumentationField.setAccessible(true);
		
		initMocks(this);
	}
	
	@Test
	public void testCreation() throws IllegalArgumentException, IllegalAccessException {

		long beforeConstruct = System.nanoTime();
		GladysRequestLedgerImpl grli = 
			new GladysRequestLedgerImpl(mockInstrumentation);
		long afterConstruct = System.nanoTime();
		
		Instrumentation grliInstrumentation = (Instrumentation)
				instrumentationField.get(grli);
		
		assertEquals(grliInstrumentation, mockInstrumentation);
		
		assertTrue(grli.getStartNanos() >= beforeConstruct);
		assertTrue(grli.getStartNanos() <= afterConstruct);	
		
		assertEquals(grli.getThreadName(), Thread.currentThread().getName());	
		
		assertNotNull(grli.getUniqueId());
		assertNotEquals(grli.getUniqueId(),
			new GladysRequestLedgerImpl(mockInstrumentation).getUniqueId());
	}
	
	@Test
	public void testComplete() throws IllegalArgumentException, IllegalAccessException {
		GladysRequestLedgerImpl grli = 
			new GladysRequestLedgerImpl(mockInstrumentation);
				
		assertFalse(grli.isComplete());
		
		grli.enteringServlet("dummy");
		try {
			Thread.currentThread().sleep(1);
		} catch (InterruptedException e) {
		}
		long beforeComplete = System.nanoTime();
		grli.exitingServlet("dummy");
		long afterComplete = System.nanoTime();

		assertTrue(grli.isComplete());
		assertTrue(grli.getEndNanos() >= beforeComplete);
		assertTrue(grli.getEndNanos() <= afterComplete);	
	}
	
	@Test
	public void testServletForwarding() throws IllegalArgumentException, IllegalAccessException {
		GladysRequestLedgerImpl grli = 
			new GladysRequestLedgerImpl(mockInstrumentation);
				
		assertFalse(grli.isComplete());
		
		grli.enteringServlet("servlet1");
		grli.enteringServlet("servlet2");
		try {
			Thread.currentThread().sleep(1);
		} catch (InterruptedException e) {
		}
		grli.exitingServlet("servlet2");
		assertFalse(grli.isComplete());
		long beforeComplete = System.nanoTime();
		grli.exitingServlet("servlet1");
		long afterComplete = System.nanoTime();

		assertTrue(grli.isComplete());
		assertTrue(grli.getDurationNanos() > 0);
		assertTrue(grli.getEndNanos() >= beforeComplete);
		assertTrue(grli.getEndNanos() <= afterComplete);	
	}
	
	@Test
	public void testBadServletNesting() throws IllegalArgumentException, IllegalAccessException {
		GladysRequestLedgerImpl grli = 
			new GladysRequestLedgerImpl(mockInstrumentation);
				
		assertFalse(grli.isComplete());
		assertNull(grli.getInstrumentationExceptionMessage());
		grli.enteringServlet("servlet1");
		grli.enteringServlet("servlet2");
		assertFalse(grli.isComplete());
		assertNull(grli.getInstrumentationExceptionMessage());
		grli.exitingServlet("servlet1");
		assertNotNull(grli.getInstrumentationExceptionMessage());
	}
	
	@Test
	public void testStringAndClassCounting() throws IllegalArgumentException, IllegalAccessException {
		String httpMethod = "GET";
		String url = "http://localhost:8080/index.html";
		
		GladysRequestLedgerImpl grli = 
			new GladysRequestLedgerImpl(mockInstrumentation);
				
		assertEquals(grli.getStringCreateCount(), 0);
		assertEquals(grli.getClassLoadCount(), 0);

		grli.setHttpMethod(httpMethod);
		grli.setUrl(url);
		grli.enteringServlet("servlet");
		
		grli.logClassLoaded("someclass");
		grli.logStringCreate();
		grli.logStringCreate();
		grli.logClassLoaded("someotherclass");
		grli.logStringCreate();

		grli.exitingServlet("servlet");
		
		assertEquals(grli.getStringCreateCount(), 3);
		assertEquals(grli.getClassLoadCount(), 2);	
		
		String summary = grli.createSummary();
//		System.out.println(summary);
		assertTrue(summary.contains(httpMethod + " " + url));
		assertTrue(summary.contains("requestId: " + grli.getUniqueId()));  
		assertTrue(summary.contains(" 3 Strings created"));
		assertTrue(summary.contains(" 2 Classes loaded"));
	}

}
