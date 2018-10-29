package com.github.p27mcgee.gladys.agent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.p27mcgee.gladys.agent.data.GladysRequestLedgerImpl;

@RunWith(MockitoJUnitRunner.class)
public class GladysAgentTest {

	private static final Object INSTANCE_FOR_STATIC = null;

	private static final String FIELD_NAME_AGENT_ARGS = "agentArgs";
	private static final String FIELD_NAME_INSTRUMENTATION = "instrumentation";

	private static final String TEST_ARGS = "shazam!";

	@Mock
	private Instrumentation mockInstrumentation;

	Field agentArgsField;
	Field instrumentationField;

	@Before
	public void setUp() throws Exception {
		agentArgsField = GladysAgent.class.getDeclaredField(FIELD_NAME_AGENT_ARGS);
		agentArgsField.setAccessible(true);

		instrumentationField = GladysAgent.class.getDeclaredField(FIELD_NAME_INSTRUMENTATION);
		instrumentationField.setAccessible(true);

		initMocks(this);

		agentArgsField.set(INSTANCE_FOR_STATIC, TEST_ARGS);
		instrumentationField.set(INSTANCE_FOR_STATIC, mockInstrumentation);
	}

	@After
	public void tearDown() throws Exception {
		agentArgsField.set(INSTANCE_FOR_STATIC, null);
		instrumentationField.set(INSTANCE_FOR_STATIC, null);
	}

	@Test
	public void testGetters() throws Exception {
//		I can't make GladysAgent.premain() happy with mockInstrumentation  		
//		GladysAgent.premain(TEST_ARGS, mockInstrumentation);
		assertEquals(GladysAgent.getAgentArgs(), TEST_ARGS);
		assertEquals(GladysAgent.getInstrumentation(), mockInstrumentation);
	}

	@Test
	public void testCreateGladysRequestLedger() throws Exception {
        assertEquals(GladysAgent.createGladysRequestLedger().getClass(), 
        		GladysRequestLedgerImpl.class);       		
        assertTrue(GladysRequestLedgerImpl.class.equals(
        	GladysAgent.createGladysRequestLedger().getClass()));       		
	}
}
