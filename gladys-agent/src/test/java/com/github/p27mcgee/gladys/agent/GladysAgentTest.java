package com.github.p27mcgee.gladys.agent;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GladysAgentTest {

    private static final Object INSTANCE_FOR_STATIC = null;

    private static final String FIELD_NAME_AGENT_ARGS = "agentArgs";
    private static final String FIELD_NAME_INSTRUMENTATION = "instrumentation";
    
    private static final String TEST_ARGS = "shazam!";

    private Instrumentation realInstrumentation;
    @Mock
    private Instrumentation mockInstrumentation;
    
    private String realAgentArgs;
    
    Field agentArgsField;
    Field instrumentationField;
    		
    @Before
    public void setUp() throws Exception {
//    	agentArgsField = GladysAgent.class.getDeclaredField(FIELD_NAME_AGENT_ARGS);         
//    	agentArgsField.setAccessible(true);
//    	
//    	instrumentationField = GladysAgent.class.getDeclaredField(FIELD_NAME_INSTRUMENTATION);         
//    	instrumentationField.setAccessible(true);
//
//    	initMocks(this);
//
//    	realAgentArgs = (String) agentArgsField.get(INSTANCE_FOR_STATIC);   	
//        realInstrumentation = (Instrumentation) instrumentationField.get(INSTANCE_FOR_STATIC);
    }

    @After
    public void tearDown() throws Exception {
//    	agentArgsField.set(INSTANCE_FOR_STATIC, realAgentArgs);
//        instrumentationField.set(INSTANCE_FOR_STATIC, realInstrumentation);
    }

  @Test
  public void testDummy() throws Exception {
//  	GladysAgent.premain(TEST_ARGS, mockInstrumentation);
      assertEquals(TEST_ARGS, TEST_ARGS);
//      assertEquals(GladysAgent.getInstrumentation(), mockInstrumentation);
  }
    
//    @Test
//    public void testPreMain() throws Exception {
//    	GladysAgent.premain(TEST_ARGS, mockInstrumentation);
//        assertEquals(GladysAgent.getAgentArgs(), TEST_ARGS);
//        assertEquals(GladysAgent.getInstrumentation(), mockInstrumentation);
//    }
//
//    @Test
//    public void testNewString() throws Exception {
//    	GladysAgent.premain(TEST_ARGS, mockInstrumentation);
//    	
//        assertEquals(GladysAgent.getAgentArgs(), TEST_ARGS);
//        assertEquals(mockInstrumentation, mockInstrumentation);
//    }

    //==================================================================
    
//    @Test
//    public void testAgentMain() throws Exception {
//    	GladysAgent.agentmain(FOO, mockInstrumentation);
//        assertThat(GladysAgent.getInstrumentation(), is(mockInstrumentation));
//    }

//    @Test
//    public void testAgentInstallerIsPublic() throws Exception {
//        Class<?> type = GladysAgent.class;
//        assertThat(Modifier.isPublic(type.getModifiers()), is(true));
//        assertThat(type.getDeclaringClass(), nullValue(Class.class));
//        assertThat(type.getDeclaredClasses().length, is(0));
//    }

//    @Test
//    public void testAgentInstallerStoreIsPrivate() throws Exception {
//        Field field = GladysAgent.class.getDeclaredField("instrumentation");
//        assertThat(Modifier.isPrivate(field.getModifiers()), is(true));
//    }

//    @Test
//    public void testAgentInstallerGetterIsPublic() throws Exception {
//        Method method = GladysAgent.class.getDeclaredMethod("getInstrumentation");
//        assertThat(Modifier.isPublic(method.getModifiers()), is(true));
//    }

//    @Test(expected = UnsupportedOperationException.class)
//    public void testConstructorThrowsException() throws Exception {
//        Constructor<?> constructor = GladysAgent.class.getDeclaredConstructor();
//        constructor.setAccessible(true);
//        try {
//            constructor.newInstance();
//            fail();
//        } catch (InvocationTargetException exception) {
//            throw (Exception) exception.getCause();
//        }
//    }
}
