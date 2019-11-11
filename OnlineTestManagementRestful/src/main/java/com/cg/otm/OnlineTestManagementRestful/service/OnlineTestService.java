package com.cg.otm.OnlineTestManagementRestful.service;

import java.io.IOException;
import java.util.List;

import com.cg.otm.OnlineTestManagementRestful.dto.OnlineTest;
import com.cg.otm.OnlineTestManagementRestful.dto.Question;
import com.cg.otm.OnlineTestManagementRestful.dto.User;
import com.cg.otm.OnlineTestManagementRestful.exception.UserException;

public interface OnlineTestService {
	public OnlineTest addTest(OnlineTest onlineTest) throws UserException;
	public OnlineTest updateTest(Long testId, OnlineTest onlineTest) throws UserException;
	public OnlineTest deleteTest(Long testId) throws UserException;
	public Question updateQuestion(Long testId, Long questionId, Question question) throws UserException;
	public Question deleteQuestion(Long testId, Long questionId) throws UserException;
	public User registerUser(User user) throws UserException;
	public Question showQuestion(OnlineTest onlineTest, Long questionId) throws UserException;
	public Question searchQuestion(Long questionId) throws UserException;
	public Boolean assignTest(Long userId, Long testId) throws UserException;
	public Double getResult(OnlineTest onlineTest) throws UserException;
	public Double calculateTotalMarks(OnlineTest onlineTest) throws UserException;
	public OnlineTest searchTest(Long testId) throws UserException;
	public User searchUser(Long userId) throws UserException;
	public List<User> getUsers();
	public List<OnlineTest> getTests();
	public User updateProfile(User user) throws UserException;
	public void readFromExcel(long id, String fileName, long time) throws IOException, UserException;
	public Question addQuestion(long id, Question question) throws UserException;
	public User searchUserByName(String name) throws UserException;
}
