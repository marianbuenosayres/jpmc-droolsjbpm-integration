package org.drools.process.dynamicprocessid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.drools.container.spring.beans.persistence.JPAKnowledgeServiceBean;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author nicolas.loriente
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/conf/dynamic-processid-conf.xml")
@Transactional
@TransactionConfiguration(defaultRollback=true)
public class DynamicProcessIdTest {
	
	public static boolean foundProcessId;
	
	@Resource(name="knowledgeProvider")
	private JPAKnowledgeServiceBean knowledgeProvider;
	
	@Test
	public void shouldLoadSpringContext() { }

	@Test
	public void shouldFindProcessByDynamicId() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put( "subprocessId", "dynamic-processid-subflow" );
		
		startProcess(params);
		
		assertTrue( foundProcessId );
	}
	
	@Test(expected=RuntimeException.class)
	public void shouldNotFindProcessByDynamicId() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put( "subprocessId", "non-existent-processid" );
		
		try {
			startProcess(params);
		} catch ( RuntimeException e ) {
			System.out.println("\n*** message: " + e.getCause().getMessage() + "***\n");
		
			assertEquals( "org.drools.RuntimeDroolsException: Could not find process non-existent-processid", 
					e.getCause().getMessage() );
		
			assertFalse( foundProcessId );
			
			throw e;
		}
	}
	
	private int startProcess(Map<String, Object> params) {
		StatefulKnowledgeSession ksession = knowledgeProvider.newStatefulKnowledgeSession();
		ksession.startProcess("dynamic-processid-main-flow", params);
		int sessionId = ksession.getId();
		ksession.dispose();
		return sessionId;
	}
	
	@Before
	public void resetFoundProcessId() {
		foundProcessId = false;
	}
}
