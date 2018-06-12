package com.liwc.service;

import static org.junit.Assert.*;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring-*.xml")
public class SpringActivitiTest {

	private static final Log log = LogFactory.getLog(SpringActivitiTest.class);

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	@Rule
	public ActivitiRule activitiSpringRule;
	
	@Autowired
	private RepositoryService repositoryService;
	
	
	// @Test
	public void myTest() {

		log.info("***************开始测试*********************");

		Assert.assertNotNull(processEngine);
		String processEngineName = processEngine.getName();
		log.info(processEngineName);

		String peVersion = processEngine.VERSION;
		log.info(peVersion);

		log.info("***************测试结束*********************");

	}

	
	
	// @Test
	// @Deployment
	public void simpleProcessTest() {

		log.info("***************开始测试*********************");

		runtimeService.startProcessInstanceByKey("simpleProcess");
		Task task = taskService.createTaskQuery().singleResult();
		assertEquals("My Task", task.getName());

		taskService.complete(task.getId());
		assertEquals(0, runtimeService.createProcessInstanceQuery().count());

		log.info("***************测试结束*********************");
	}

	

	@Test
	@Deployment
	public void financialReportProcessTest() {
		log.info("*****************************************");
		log.info("***************开始测试*********************");
		log.info("*****************************************");

//		org.activiti.engine.repository.Deployment deployment = repositoryService.createDeployment()
//				.addClasspathResource("FinancialReportProcess.bpmn20.xml").deploy();

		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("financialReport");
		assertNotNull(processInstance);
		log.info("processInstanceId:=====>"+processInstance.getName());
		
		
		List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("accountancy").list();
		Assert.assertTrue("任务为空！", tasks.size()!=0);
		
		
		
		log.info("*****************************************");
		log.info("***************测试结束*********************");
		log.info("*****************************************");
	}

}
