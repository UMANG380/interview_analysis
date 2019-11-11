package com.cg.otm.OnlineTestManagementRestful.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;

import javax.transaction.Transactional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.xssf.usermodel.XSSFRow;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.otm.OnlineTestManagementRestful.dto.OnlineTest;
import com.cg.otm.OnlineTestManagementRestful.dto.Question;
import com.cg.otm.OnlineTestManagementRestful.dto.User;
import com.cg.otm.OnlineTestManagementRestful.exception.ExceptionMessage;
import com.cg.otm.OnlineTestManagementRestful.exception.UserException;
import com.cg.otm.OnlineTestManagementRestful.repository.OnlineTestRepository;
import com.cg.otm.OnlineTestManagementRestful.repository.QuestionRepository;
import com.cg.otm.OnlineTestManagementRestful.repository.UserRepository;
@Service("testservice")
@Transactional
public class OnlineTestServiceImpl implements OnlineTestService{
	@Autowired
	QuestionRepository questionRepository;
	@Autowired
	OnlineTestRepository testRepository;
	
	@Autowired
	UserRepository userRepository;
    
	
	@Override
	public User registerUser(User user) throws UserException {
		User returnedUser = userRepository.save(user);
		if(returnedUser != null)
			return user;
		else {
			throw new UserException(ExceptionMessage.DATABASEMESSAGE);
		}
	}

	
	@Override
	public Question showQuestion(OnlineTest onlineTest, Long questionId) throws UserException {
		Question question = questionRepository.findByQuestionId(questionId);
		if (question == null || !onlineTest.getTestQuestions().contains(question)) {
			throw new UserException(ExceptionMessage.QUESTIONMESSAGE);
		}
		return question;
	}

	@Override
	public Boolean assignTest(Long userId, Long testId) throws UserException {
		User user = userRepository.findByUserId(userId);
		OnlineTest onlineTest = testRepository.findByTestId(testId);
		if (user == null) {
			throw new UserException(ExceptionMessage.USERMESSAGE);
		}
		if (user.getIsAdmin()) {
			throw new UserException(ExceptionMessage.ADMINMESSAGE);
		}
		if (onlineTest == null) {
			throw new UserException(ExceptionMessage.TESTMESSAGE);
		}
		if (onlineTest.getIsTestAssigned()) {
			throw new UserException(ExceptionMessage.TESTASSIGNEDMESSAGE);
		} else {
			user.setUserTest(onlineTest);
			onlineTest.setIsTestAssigned(true);
		}
		testRepository.save(onlineTest);
		userRepository.save(user);
		return true;
	}


	@Override
	public OnlineTest addTest(OnlineTest onlineTest) throws UserException {
		OnlineTest returnedTest = testRepository.save(onlineTest);
		if (returnedTest == null) {
			throw new UserException(ExceptionMessage.DATABASEMESSAGE);
		}
		return returnedTest;
	}

	@Override
	public OnlineTest updateTest(Long testId, OnlineTest onlineTest) throws UserException {
		OnlineTest temp = testRepository.findByTestId(testId);
		if (temp != null) {
			temp.setTestId(testId);
			temp.setTestName(onlineTest.getTestName());
			temp.setTestDuration(onlineTest.getTestDuration());
			temp.setStartTime(onlineTest.getStartTime());
			temp.setEndTime(onlineTest.getEndTime());
			temp.setIsdeleted(onlineTest.getIsdeleted());
			temp.setIsTestAssigned(onlineTest.getIsTestAssigned());
			temp.setTestMarksScored(onlineTest.getTestMarksScored());
			temp.setTestQuestions(onlineTest.getTestQuestions());
			temp.setTestTotalMarks(onlineTest.getTestMarksScored());
			testRepository.save(temp);
			return onlineTest;
		} else
			throw new UserException(ExceptionMessage.TESTMESSAGE);
	}

	
	@Override
	public OnlineTest deleteTest(Long testId) throws UserException {
		OnlineTest returnedTest = testRepository.findByTestId(testId);
		if (returnedTest != null && returnedTest.getIsdeleted() == false) {
			returnedTest.setIsdeleted(true);
		} else {
			throw new UserException(ExceptionMessage.TESTNOTFOUNDMESSAGE);
		}
		return returnedTest;
	}

	@Override
	public Question updateQuestion(Long testId, Long questionId, Question question) throws UserException {
		OnlineTest temp = testRepository.findByTestId(testId);
		if (temp != null) {
			Set<Question> quests = temp.getTestQuestions();
			Question tempQuestion = questionRepository.findByQuestionId(questionId);
			if (tempQuestion != null && quests.contains(tempQuestion)) {
				tempQuestion.setChosenAnswer(question.getChosenAnswer());
				if (tempQuestion.getChosenAnswer() == tempQuestion.getQuestionAnswer()) {
					tempQuestion.setMarksScored(question.getQuestionMarks());
				}
				question.setQuestionId(questionId);
				temp.setTestTotalMarks(
						temp.getTestTotalMarks() - tempQuestion.getQuestionMarks() + question.getQuestionMarks());
				temp.setTestMarksScored(temp.getTestMarksScored() + tempQuestion.getMarksScored());
				tempQuestion.setQuestionMarks(question.getQuestionMarks());
				tempQuestion.setIsDeleted(question.getIsDeleted());
				tempQuestion.setOnlinetest(question.getOnlinetest());
				tempQuestion.setQuestionAnswer(question.getQuestionAnswer());
				tempQuestion.setQuestionId(questionId);
				tempQuestion.setQuestionOptions(question.getQuestionOptions());
				tempQuestion.setQuestionTitle(question.getQuestionTitle());
				questionRepository.save(tempQuestion);
				testRepository.save(temp);
				return question;
			} else
				throw new UserException(ExceptionMessage.QUESTIONMESSAGE);
		} else
			throw new UserException(ExceptionMessage.TESTMESSAGE);
	}

	
	@Override
	public Question deleteQuestion(Long testId, Long questionId) throws UserException {
		OnlineTest temp = testRepository.findByTestId(testId);
		if (temp != null) {
			Set<Question> quests = temp.getTestQuestions();
			Question tempQuestion = questionRepository.findByQuestionId(questionId);
			if (tempQuestion != null && quests.contains(tempQuestion) && tempQuestion.getIsDeleted() == false) {
				temp.setTestTotalMarks(temp.getTestTotalMarks() - tempQuestion.getQuestionMarks());
				testRepository.save(temp);
				tempQuestion.setIsDeleted(true);
				return tempQuestion;
			} else
				throw new UserException(ExceptionMessage.QUESTIONMESSAGE);
		} else
			throw new UserException(ExceptionMessage.TESTMESSAGE);
	}
	
	
	@Override
	public Double getResult(OnlineTest onlineTest) throws UserException {
		Double score = calculateTotalMarks(onlineTest);
		onlineTest.setIsTestAssigned(false);
		testRepository.save(onlineTest);
		return score;
	}

	
	@Override
	public Double calculateTotalMarks(OnlineTest onlineTest) throws UserException {
		Double score = new Double(0.0);
		for (Question question : onlineTest.getTestQuestions()) {
			score = score + question.getMarksScored();
		}
		onlineTest.setTestMarksScored(score);
		testRepository.save(onlineTest);
		return score;
	}
	
	
	@Override
	public User searchUser(Long userId) throws UserException {
		User returnedUser = userRepository.findByUserId(userId);
		if (returnedUser != null) {
			return returnedUser;
		} else {
			throw new UserException(ExceptionMessage.USERMESSAGE);
		}

	}

	
	@Override
	public OnlineTest searchTest(Long testId) throws UserException {
		OnlineTest returnedTest = testRepository.findByTestId(testId);
		if (returnedTest != null) {
			return returnedTest;
		} else {
			throw new UserException(ExceptionMessage.TESTNOTFOUNDMESSAGE);
		}
	}
	

	@Override
	public User updateProfile(User user) throws UserException {

		User returnedUser = userRepository.findById(user.getUserId()).orElse(null);
		if (returnedUser == null) {
			throw new UserException(ExceptionMessage.USERMESSAGE);
		}
		try {
			userRepository.save(user);
			return user;
		}
		catch(Exception e){
			throw new UserException("Username already exists!");
		}
	}
	 
	
	@Override
	public List<User> getUsers() {
		return userRepository.findAll();
	}


	@Override
	public List<OnlineTest> getTests()  {
		// TODO Auto-generated method stub
		ArrayList<OnlineTest> testList = new ArrayList<OnlineTest>( );
		
		 List<OnlineTest> test= testRepository.findAllNotAssignedAndNotDeleted();
		 for (OnlineTest onlineTest : test) {

			 if(onlineTest.getTestQuestions()!=null) {
				 OnlineTest newtest=new OnlineTest();
				   newtest.setTestId(onlineTest.getTestId());
				   newtest.setTestName(onlineTest.getTestName());
				   newtest.setTestDuration(onlineTest.getTestDuration());
				   newtest.setStartTime(onlineTest.getStartTime());
				   newtest.setEndTime(onlineTest.getEndTime());
				   testList.add(newtest);
				 }
			}
		 return testList;
		//return testRepository.findAllNotAssignedAndNotDeleted();
	}

	
	@Override
	public Question searchQuestion(Long questionId) throws UserException {
		Question question = questionRepository.findByQuestionId(questionId);
		if (question != null) {
			return question;
		} else {
			throw new UserException(ExceptionMessage.QUESTIONNOTFOUNDMESSAGE);
		}
	}


	@Override
	public void readFromExcel(long id, String fileName, long time) throws IOException, UserException {
		String UPLOAD_DIRECTORY = "E:\\Excel_Files";
		File dataFile = new File(UPLOAD_DIRECTORY + "\\" + time + fileName);
		FileInputStream fis = new FileInputStream(dataFile);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheetAt(0);    //Read the first sheet
		Row row;
		OnlineTest test = testRepository.findByTestId(id);   //Find the test
		if(test!= null) {
		Double testMarks = test.getTestTotalMarks();
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			row = (Row) sheet.getRow(i);
			String title;
			if (row.getCell(0) == null) {
				throw new UserException(ExceptionMessage.QUESTIONTITLEMESSAGE);   //If Title is not present
			} else { 
				title = row.getCell(0).toString();
			}
			String marks;
			if (row.getCell(1) == null) {
				throw new UserException(ExceptionMessage.QUESTIONMARKSMESSAGE);  //If Marks is not present
			} else {
				marks = row.getCell(1).toString();
				testMarks = testMarks + Double.parseDouble(marks);
			}
			String options;
			if (row.getCell(2) == null) {
				throw new UserException(ExceptionMessage.QUESTIONOPTIONSMESSAGE);   //If Options is not present
			} else {
				options = row.getCell(2).toString();
			}
			String answer;
			if (row.getCell(3) == null) {
				throw new UserException(ExceptionMessage.QUESTIONANSWERMESSAGE);   //If Answer is not present
			} else {
				answer = row.getCell(3).toString();
			}

			Question question = new Question();
			
			if(test == null) {
				throw new UserException(ExceptionMessage.TESTNOTFOUNDMESSAGE);
			}
			test.setTestTotalMarks(testMarks);
			String option[] = new String[4];
			question.setQuestionTitle(title);
			question.setQuestionMarks(Double.parseDouble(marks));
			StringTokenizer token = new StringTokenizer(options, ",");
			int k = 0;
			while (token.hasMoreTokens()) {    //separate the options by splitting with comma
				option[k] = token.nextToken();
				k++;
			}
			question.setQuestionOptions(option);
			question.setQuestionAnswer((int) Double.parseDouble(answer));
			question.setChosenAnswer(0);
			question.setIsDeleted(false);
			question.setMarksScored(new Double(0));
			question.setOnlinetest(test);
			questionRepository.save(question);
		}
		}
		else {
			throw new UserException(ExceptionMessage.NOTESTMESSAGE);
		}
		fis.close();
	}
	
	@Override
	public Question addQuestion(long id, Question question) throws UserException {
		
		OnlineTest test = testRepository.findByTestId(id);
		if(test != null) {
			questionRepository.save(question);
			Set<Question> questions = test.getTestQuestions();
			questions.add(question);
			test.setTestQuestions(questions);
			test.setTestTotalMarks(test.getTestTotalMarks() + question.getQuestionMarks());
			testRepository.save(test);
		}
		else {
			throw new UserException(ExceptionMessage.TESTNOTFOUNDMESSAGE);
		}
		return question;
	}

	@Override
	public User searchUserByName(String name) throws UserException {
		Optional<User> returnedUser = userRepository.findByUserName(name);
		System.out.println(returnedUser.get());
		if(returnedUser.get() != null)
			return returnedUser.get();
		else {
			throw new UserException(ExceptionMessage.USERMESSAGE);
		}
	}

}
