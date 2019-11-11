package com.cg.otm.OnlineTestManagementRestful.controller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cg.otm.OnlineTestManagementRestful.dto.AssignTestData;
import com.cg.otm.OnlineTestManagementRestful.dto.OnlineTest;
import com.cg.otm.OnlineTestManagementRestful.dto.Question;
import com.cg.otm.OnlineTestManagementRestful.dto.User;
import com.cg.otm.OnlineTestManagementRestful.exception.UserException;
import com.cg.otm.OnlineTestManagementRestful.service.OnlineTestService;
import com.cg.otm.OnlineTestManagementRestful.view.PDFView;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class TestManagementController {
	@Autowired 
	OnlineTestService testservice;

	private static final Logger logger = LoggerFactory.getLogger(TestManagementController.class);


	/*Mapping for the page to display after add test form is submitted*/
	@PostMapping(value = "/addtest")
	public ResponseEntity<String> addTest(@RequestBody OnlineTest test) {
		OnlineTest testOne = new OnlineTest();
		try {
			System.out.println("Inside addTest");
			Set<Question> question = new HashSet<Question>();
			testOne.setTestName(test.getTestName());
			testOne.setTestTotalMarks(new Double(0));
			testOne.setTestDuration(test.getTestDuration());
			testOne.setStartTime(test.getStartTime());
			testOne.setEndTime(test.getEndTime());
			testOne.setTestMarksScored(new Double(0));
			testOne.setIsdeleted(false);
			testOne.setIsTestAssigned(false);
			testOne.setTestQuestions(question);
			testservice.addTest(testOne);
			logger.info("Test Added!");
			logger.info("Test Created By - "+testOne.getCreatedBy() +" on date "+testOne.getCreationDate());
		} catch (UserException e) {
			return new ResponseEntity<String>(JSONObject.quote("Data not added"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(JSONObject.quote("Test added successfully"),HttpStatus.OK);

	}
	
	@PostMapping(value="/addsinglequestion")
	public ResponseEntity<?> addSingleQuestion(@RequestParam("testid") long id, @ModelAttribute("question") Question question){
		OnlineTest test;
		try {
			logger.info("Entered add single question method");
			test = testservice.searchTest(id);
			if(test != null) {
				Question addQuestion = new Question();
				addQuestion.setQuestionTitle(question.getQuestionTitle());
				addQuestion.setQuestionOptions(question.getQuestionOptions());
				addQuestion.setQuestionAnswer(question.getQuestionAnswer());
				addQuestion.setIsDeleted(false);
				addQuestion.setChosenAnswer(0);
				addQuestion.setMarksScored(0.0);
				addQuestion.setQuestionMarks(question.getQuestionMarks());
				addQuestion.setOnlinetest(test);
				testservice.addQuestion(id, addQuestion);
				logger.info("Question added Successfully");
				logger.info("Question Created By - "+addQuestion.getCreatedBy() +" on date "+addQuestion.getCreationDate());
				return new ResponseEntity<String>(JSONObject.quote("Question added Successfully"), HttpStatus.OK);
			}
		} catch (UserException e) {
			logger.error(e.getMessage());
			return new ResponseEntity<String>(JSONObject.quote(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		logger.error("Error in adding question");
		return new ResponseEntity<String>(JSONObject.quote("Question could not be added"), HttpStatus.INTERNAL_SERVER_ERROR); 
	}


	@PostMapping(value = "/addquestionsubmit", consumes = "multipart/form-data")
	public ResponseEntity<?> addQuestion(@RequestParam("testid") long id, @RequestParam("exfile") MultipartFile file) {
		try {
			logger.info("Entered add question method");
			String UPLOAD_DIRECTORY = "E:\\Excel_Files";
			String fileName = file.getOriginalFilename();
			File pathFile = new File(UPLOAD_DIRECTORY);
			if (!pathFile.exists()) {  //If the given path does not exist then create the directory
				pathFile.mkdir();
			}

			long time = new Date().getTime();
			pathFile = new File(UPLOAD_DIRECTORY + "\\" + time + fileName); //appending time to filename so that files cannot have same name
			file.transferTo(pathFile);    //Transfer the file to the given path
			testservice.readFromExcel(id, fileName, time);
			logger.info("Question added successfully");
		} catch (UserException | IOException e) {
			logger.error(e.getMessage());
			return new ResponseEntity<String>(JSONObject.quote(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(JSONObject.quote("Questions Added Successfully!"), HttpStatus.OK);
	}
	

	@RequestMapping(value = "/showalltests", method = RequestMethod.GET)
	public ResponseEntity<List<OnlineTest>> showTest() {
		logger.info("Entered Show Test method");
		List<OnlineTest> testList = testservice.getTests();
		if(testList ==null) {
			logger.info("No test Printed");
			return new ResponseEntity<List<OnlineTest>>(HttpStatus.NO_CONTENT);
		}
		else {
			logger.info("Test displayed successfully");
			return new ResponseEntity<List<OnlineTest>>(testList, HttpStatus.OK);
		}
	}

	
	@GetMapping(value="/showusers")
	public ResponseEntity<?> getAllUsers(){
		logger.info("Entered Show User method");
		List<User> userone=  testservice.getUsers();
		if(userone.isEmpty()) {
			logger.info("No users");
			return new ResponseEntity<String>(JSONObject.quote("No user details present"),HttpStatus.BAD_REQUEST);
			
		}else {
			logger.info("Users displayed successfully");
			List<User> users = new ArrayList<User>();
			userone.forEach(user->{
				user.setUserTest(null);
				if(!user.getIsDeleted() && !user.getIsAdmin())
					users.add(user);
			});
			return new ResponseEntity<List<User>>(users,HttpStatus.OK);
		}
		
	}

	
	@DeleteMapping(value = "removetestsubmit")
	public ResponseEntity<?> removeTest(@RequestParam("testid") long id) {
		try {
			logger.info("Entered remove test method");
			OnlineTest deleteTest = testservice.searchTest(id);
			OnlineTest deletedTest = testservice.deleteTest(deleteTest.getTestId());
			if(deletedTest != null) {
				logger.info("test deleted successfully");
				logger.info("Question Deleted By - "+deletedTest.getLastModifiedBy() +" on date "+deletedTest.getLastModifiedBy());
				return new ResponseEntity<String>(JSONObject.quote("Test Deleted Successfully"),HttpStatus.OK);
			}
			else {
				logger.info("Test id doesnt exist");
				return new ResponseEntity<String>(JSONObject.quote("Test id doesnt exist"),HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (UserException e) {
			logger.error(e.getMessage());
			return new ResponseEntity<String>(JSONObject.quote("Test id doesnt exist"), HttpStatus.BAD_REQUEST);
		}
	}


	@DeleteMapping(value = "removequestionsubmit")
	public ResponseEntity<?> removeQuestion(@RequestParam("questionid") long id) {
		logger.info("Entered remove question method");

		try {
			Question question = testservice.searchQuestion(id);
			testservice.deleteQuestion(question.getOnlinetest().getTestId(), question.getQuestionId());
			logger.info("Question Deleted By - "+question.getLastModifiedBy() +" on date "+question.getLastModifiedBy());
			logger.info("Question deleted Successfully");
		} catch (UserException e) {
			logger.error(e.getMessage());
			return new ResponseEntity<String>(JSONObject.quote(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(JSONObject.quote("Question deleted successfully!"), HttpStatus.OK);
	}

	
	@GetMapping(value = "/givetest")
	public ResponseEntity<?> showQuestion(@RequestParam("userid") long userId) {
		logger.info("Entered Give test method");
		User currentUser;
		try {
			currentUser = testservice.searchUser(userId);
			if (currentUser.getUserTest() == null) {
				logger.info("No Test was assigned");
				return new ResponseEntity<String>(JSONObject.quote("No Test Assigned"), HttpStatus.BAD_REQUEST);
			} else {
				Set<Question> questions =new HashSet<Question>();
				if (0 < currentUser.getUserTest().getTestQuestions().toArray().length) {
					logger.info("Sent all questions");
					currentUser.getUserTest().getTestQuestions().forEach(quest->{
						Question addQuestion = new Question();
						addQuestion.setQuestionId(quest.getQuestionId());
						OnlineTest test = quest.getOnlinetest();
						test.setTestQuestions(null);
						addQuestion.setOnlinetest(test);
						addQuestion.setQuestionAnswer(quest.getQuestionAnswer());
						addQuestion.setIsDeleted(false);
						addQuestion.setQuestionMarks(quest.getQuestionMarks());
						addQuestion.setMarksScored(0.0);
						addQuestion.setChosenAnswer(0);
						addQuestion.setQuestionOptions(quest.getQuestionOptions());
						addQuestion.setQuestionTitle(quest.getQuestionTitle());
						questions.add(addQuestion);
					});
					return new ResponseEntity<Set<Question>>(questions, HttpStatus.OK);
				} else {
					logger.info("Test didn't contain any questions");
					return new ResponseEntity<String>("No Questions in the test", HttpStatus.NO_CONTENT);
				}
			}

		} catch (UserException e) {
			logger.error(e.getMessage());
			return new ResponseEntity<String>(JSONObject.quote("User id was incorrect"), HttpStatus.NO_CONTENT);
		}
	}

	
	@PutMapping(value = "/givetest")
	public ResponseEntity<?> submitQuestion(@RequestBody List<Question> questions) {
		logger.info("Entered Give test method");
		questions.forEach(quest->{
			try {
				testservice.updateQuestion(quest.getOnlinetest().getTestId(), quest.getQuestionId(),quest);
				logger.info("Question Deleted By - "+quest.getLastModifiedBy() +" on date "+quest.getLastModifiedBy());
			} catch (UserException e) {
				logger.error(e.getMessage());
			}
		});
		return new ResponseEntity<String>(JSONObject.quote("Test submitted Successfully"), HttpStatus.OK);
	}




	@PostMapping(value = "assign")
	public ResponseEntity<?> assignTest(@RequestBody AssignTestData data) {
		try {
			logger.info("Entered assign test method");
			testservice.assignTest(data.getUserId(), data.getTestId());
			logger.info("Test assigned successfully");
			return new ResponseEntity<String>(JSONObject.quote("Test assigned successfully!"), HttpStatus.OK);
			
		} catch (UserException e) {
			logger.error(e.getMessage());
			return new ResponseEntity<String>(JSONObject.quote(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
	

	@GetMapping(value="getresult")
	public ResponseEntity<?> getResult(@RequestParam("userid") long userId){
		try {
			logger.info("Entered get result method");
			User user=testservice.searchUser(userId);
			if(user.getUserTest()!= null) {
				OnlineTest test=testservice.searchTest(user.getUserTest().getTestId());
				Double marksScored=test.getTestMarksScored();
				logger.info("Result displayed successfully");
				return new ResponseEntity<String>(JSONObject.quote(  marksScored + ""),HttpStatus.OK);
			}
			else {
				logger.error("No Test Assinged yet");
				return new ResponseEntity<String>(JSONObject.quote("No Test Assigned yet"), HttpStatus.BAD_REQUEST);
			}
		}catch(UserException e) {
			logger.error(e.getMessage());
			return new ResponseEntity<String>(JSONObject.quote("Test details cannot be found"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

	@GetMapping(value = "/updatetestinput")
	public ResponseEntity<?> updateTest(@RequestParam("testid") long id) {
		
		OnlineTest test;
		try {
			test = testservice.searchTest(id);
			OnlineTest testOne = new OnlineTest();
			Set<Question> questions = new HashSet<Question>();
			testOne.setTestId(test.getTestId());
			testOne.setTestName(test.getTestName());
			testOne.setTestDuration(test.getTestDuration());
			testOne.setStartTime(test.getStartTime());
			testOne.setEndTime(test.getEndTime());
			testOne.setIsdeleted(false);
			testOne.setTestMarksScored(new Double(0));
			testOne.setTestTotalMarks(new Double(0));
			testOne.setTestQuestions(questions);
			testOne.setIsTestAssigned(false);
			
			return new ResponseEntity<OnlineTest>(testOne,HttpStatus.OK);
		} catch (UserException e) {
			logger.error(e.getMessage());
			return new ResponseEntity<String>(JSONObject.quote("Test not found"), HttpStatus.NO_CONTENT);
		}
	}

	@PutMapping(value = "/updatetestinput")
	public ResponseEntity<?> actualUpdate(@RequestBody OnlineTest test) {
		OnlineTest testOne;
		try {
			testOne = testservice.searchTest(test.getTestId());
			testOne.setTestId(test.getTestId());
			testOne.setTestName(test.getTestName());
			testOne.setTestDuration(test.getTestDuration());
			testOne.setStartTime(test.getStartTime());
			testOne.setEndTime(test.getEndTime());
			testOne.setIsdeleted(false);
			System.out.println(testOne.getTestQuestions());
			testOne.setIsTestAssigned(false);
			testservice.updateTest(test.getTestId(), testOne);
			logger.info("Test Updated By - "+testOne.getLastModifiedBy() +" on date "+testOne.getLastModifiedBy());
			return new ResponseEntity<String>(JSONObject.quote("Test Updated Successfully"), HttpStatus.OK);
		} catch (UserException e) {
			logger.error(e.getMessage());
			return new ResponseEntity<String>(JSONObject.quote(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@PutMapping(value = "/updatequestionsubmit")
	public ResponseEntity<?> actualUpdate(@RequestBody Question question) {
		logger.info("Entered Update question method");
		OnlineTest test;
		Question questionOne;
		try {
			test = testservice.searchTest(question.getOnlinetest().getTestId());
			questionOne = new Question();
			questionOne.setQuestionId(question.getQuestionId());
			questionOne.setQuestionTitle(question.getQuestionTitle());
			questionOne.setQuestionOptions(question.getQuestionOptions());
			questionOne.setQuestionAnswer(question.getQuestionAnswer());
			questionOne.setQuestionMarks(question.getQuestionMarks());
			questionOne.setChosenAnswer(0);
			questionOne.setIsDeleted(false);
			questionOne.setMarksScored(new Double(0));
			questionOne.setOnlinetest(test);
			testservice.updateQuestion(question.getOnlinetest().getTestId(), question.getQuestionId(), questionOne);
			logger.info("question updated successfully");
			logger.info("Question Updated By - "+questionOne.getLastModifiedBy() +" on date "+questionOne.getLastModifiedBy());
		} catch (UserException e) {
			logger.error(e.getMessage());
			return new ResponseEntity<String>(JSONObject.quote("Question could not be updated!"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(JSONObject.quote("Question updated successfully!"), HttpStatus.OK);
	}



	
	@PutMapping(value = "/updateuser")
	public ResponseEntity<?> updateUser(@ModelAttribute("user") User user) {
		logger.info("Entered update user method");
		User userOne;
		try {
			userOne = testservice.searchUser(user.getUserId());
			userOne.setUserName(user.getUserName());
			userOne.setUserPassword(user.getUserPassword());
			userOne.setIsDeleted(false);
			testservice.updateProfile(userOne);
			logger.info("User successfully updated");		
			logger.info("User Updated By - "+userOne.getLastModifiedBy() +" on date "+userOne.getLastModifiedBy());
			return new ResponseEntity<String>(JSONObject.quote("User successfully Updated"), HttpStatus.OK);
		}
		catch(UserException e) {
			logger.error(e.getMessage());
			return new ResponseEntity<String>(JSONObject.quote(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
		catch(Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity<String>(JSONObject.quote("Username already exists!"), HttpStatus.BAD_REQUEST);
		}
	}


	@GetMapping(value = "/listquestionsubmit")
	public ResponseEntity<?> submitListQuestion(@RequestParam("testId") long testId) {
		try {
			OnlineTest test = testservice.searchTest(testId);
			List<Long> qlist = new ArrayList<Long>();
			List<Question> questionList = new ArrayList<Question>();
			Set<Question> questions = test.getTestQuestions();
			questions.forEach(question->{
				if(question.getIsDeleted()!=true) {
					qlist.add(question.getQuestionId());
					Question foundQuestion;
						try {
							foundQuestion = testservice.searchQuestion(question.getQuestionId());
							Question newQuestion = new Question();
							newQuestion.setQuestionId(foundQuestion.getQuestionId());
							newQuestion.setQuestionTitle(foundQuestion.getQuestionTitle());
							newQuestion.setQuestionOptions(foundQuestion.getQuestionOptions());
							newQuestion.setQuestionMarks(foundQuestion.getQuestionMarks());
							questionList.add(newQuestion);
						} catch (UserException e) {
							logger.error("Question not found");
						}
						
					}			
				}
			);
			return new ResponseEntity<List<Question>>(questionList, HttpStatus.OK);
		} catch (UserException e) {
			return new ResponseEntity<String>(JSONObject.quote(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value="/searchquestion")
	public ResponseEntity<?> searchQuestion(@RequestParam("id") long id) {
		try {
			Question question = testservice.searchQuestion(id);
			if(question != null && question.getIsDeleted()==false) {
				Question newQuestion = new Question();
				OnlineTest test = question.getOnlinetest();
				test.setTestQuestions(null); 
				newQuestion.setQuestionId(id);
				newQuestion.setChosenAnswer(0);
				newQuestion.setIsDeleted(false);
				newQuestion.setMarksScored(0.0);
				newQuestion.setQuestionTitle(question.getQuestionTitle());
				newQuestion.setQuestionOptions(question.getQuestionOptions());
				newQuestion.setQuestionAnswer(question.getQuestionAnswer());
				newQuestion.setQuestionMarks(question.getQuestionMarks());
				newQuestion.setOnlinetest(test);
				return new ResponseEntity<Question>(newQuestion, HttpStatus.OK);
			}
		} catch (UserException e) {
			return new ResponseEntity<String>(JSONObject.quote(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(JSONObject.quote("Question not found!"), HttpStatus.INTERNAL_SERVER_ERROR);		
	}
	
	@GetMapping(value="/searchuser")
	public ResponseEntity<?> getUser(@RequestParam("name") String username) {
		try {
			System.out.println(username);
			User user = testservice.searchUserByName(username);
			if(user != null) {
				User returnedUser = new User();
				returnedUser.setIsAdmin(user.getIsAdmin());
				returnedUser.setIsDeleted(user.getIsDeleted());
				returnedUser.setUserId(user.getUserId());
				returnedUser.setUserName(user.getUserName());
				returnedUser.setUserPassword(user.getUserPassword());
				returnedUser.setUserTest(null);
				return new ResponseEntity<User>(returnedUser, HttpStatus.OK);
			}
		} catch (UserException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(JSONObject.quote("User not found!"), HttpStatus.INTERNAL_SERVER_ERROR);		
	}
	

	@GetMapping(value = "/resultpdf", produces = "application/pdf")
	public ResponseEntity<String> download( HttpServletRequest request,
            HttpServletResponse response, @RequestParam("userid")Long userId) {
		String filePath;
		try {
			logger.info("Entered Result Pdf");
			User user = testservice.searchUser(userId);
			OnlineTest test = user.getUserTest();
			if(test != null) {
				Map<String,Object> model = new HashMap<String, Object>();
				model.put("test", test);
				filePath=new PDFView().GetPdf(model);
		        ServletContext context = request.getServletContext();      
		        File downloadFile = new File(filePath);
		        FileInputStream inputStream = new FileInputStream(downloadFile);
		        String mimeType = context.getMimeType(filePath);
		        if (mimeType == null) {
		            mimeType = "application/octet-stream";
		        }
		        logger.info("MIME type: " + mimeType);
		        response.setContentType(mimeType);
		        response.setContentLength((int) downloadFile.length());
		        String headerKey = "Content-Disposition";
		        String headerValue = String.format("filename=\"%s\"",
		                downloadFile.getName());
		        response.setHeader(headerKey, headerValue);
		        ServletOutputStream outStream = response.getOutputStream();
		        byte[] buffer = new byte[4096];
		        int bytesRead = -1;
		        while ((bytesRead = inputStream.read(buffer)) != -1) {
		            outStream.write(buffer, 0, bytesRead);
		        }
		        inputStream.close();
		        outStream.close();
			}
			else {
				return new ResponseEntity<String>("Error",HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (DocumentException | UserException | IOException e) {
			logger.error("Error Generating Result");
			return new ResponseEntity<String>("Error",HttpStatus.INTERNAL_SERVER_ERROR);
		}catch(Exception e){
			
			logger.error("Error Generating Result");
			return new ResponseEntity<String>("Error",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		logger.info("Returning result");
		return new ResponseEntity<String>("Error",HttpStatus.OK);
	}
}
