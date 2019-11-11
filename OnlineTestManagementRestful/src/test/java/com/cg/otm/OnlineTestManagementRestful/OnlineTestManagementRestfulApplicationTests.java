package com.cg.otm.OnlineTestManagementRestful;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.test.context.junit4.SpringRunner;

import com.cg.otm.OnlineTestManagementRestful.dto.OnlineTest;
import com.cg.otm.OnlineTestManagementRestful.dto.Question;
import com.cg.otm.OnlineTestManagementRestful.dto.User;
import com.cg.otm.OnlineTestManagementRestful.exception.UserException;
import com.cg.otm.OnlineTestManagementRestful.service.OnlineTestService;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class OnlineTestManagementRestfulApplicationTests {
	
	@Autowired
	TestRestTemplate restTemplate;
	
	@Autowired
	OnlineTestService onlineTestService;
	

	@Test
	public void testAddUserUrl() {
		User user=restTemplate.getForObject("/adduser", User.class);
		assertThat(user);
	}
	@Test
	public void testAddTestUrl() {
		OnlineTest onlineTest=restTemplate.getForObject("/addtest", OnlineTest.class);
		assertThat(onlineTest);
	}
	@Test
	public void testAddQuestionUrl() {
		Question question=restTemplate.getForObject("/addquestionsubmit", Question.class);
		assertThat(question);
	}
	
	@Test
	public void testRemoveTestUrl() {
		OnlineTest onlineTest=restTemplate.getForObject("/removetestsubmit",OnlineTest.class);
		assertThat(onlineTest);
	}
	@Test
	public void testRemoveQuestionUrl() {
		Question question=restTemplate.getForObject("/removequestionsubmit",Question.class);
		assertThat(question);
	}
	
	@Test
	public void testUpdateUserUrl() {
		User user=restTemplate.getForObject("/updateuser",User.class);
		assertThat(user);
	}
	
	@Test
	public void testUpdateQuestionUrl() {
		Question question=restTemplate.getForObject("/updatequestionsubmit",Question.class);
		assertThat(question);
	}
	
	
	
	
	@Test
	public void testShowUsersUrl() {
	List<User> userList=restTemplate.getForObject("/showusers",List.class);
	assertThat(userList);
		
	}
	
	@Test
	public void testShowTestsUrl() {
		List<OnlineTest> testList= restTemplate.getForObject("/showalltests", List.class);
		assertThat(testList);
	}
	
	@Test
	public void testShowQuestionsUrl() {
	List<Question> questionList= restTemplate.getForObject("/listquestionsubmit", List.class);
	assertThat(questionList);
	}
	
	@Test
	public void checkUserData() {
		assertEquals(13, onlineTestService.getUsers().size());
	
	}
	
	@Test
	public void checkTestData() {
		assertEquals(0, onlineTestService.getTests().size());
	}
	
	
	
	
	@Test
	public void registerUserUnitTest() throws UserException  {
		User addedUser = new User(null, "Priya Tiwary", "Priya@1234", null, new Boolean(false));
		User registeredUser = onlineTestService.registerUser(addedUser);
		assertEquals(registeredUser, onlineTestService.searchUser(addedUser.getUserId()));
	}
	
	
	@Test
	public void chosenAnswerUnitTest() throws UserException {
		OnlineTest test = onlineTestService.searchTest(Long.valueOf(1));
		Question question = onlineTestService.showQuestion(test, Long.valueOf(1));
		assertTrue(question.getChosenAnswer()>=1 && question.getChosenAnswer()<=4);
		assertFalse(question.getChosenAnswer()>=5);
	}
	
	@Test
	public void assignTestUnitTest() throws UserException{
		assertTrue( onlineTestService.assignTest(Long.valueOf(2), Long.valueOf(2)));
	}
	
	@Test
	public void searchTestUnitTest() throws UserException {
	assertEquals("C Programming", onlineTestService.searchTest(Long.valueOf(3)).getTestName());
	}
	@Test
	public void searchUserUnitTest() throws UserException {
	assertEquals("Priya Tiwary", onlineTestService.searchUser(Long.valueOf(10)).getUserName());
	}
	@Test
	public void searchQuestiontUnitTest() throws UserException {
	assertEquals("Programming", onlineTestService.searchQuestion(Long.valueOf(1)).getQuestionTitle());
	}
	
}