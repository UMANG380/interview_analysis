package com.cg.otm.OnlineTestManagementRestful.dto;

public class AssignTestData {

	private Long userId;
	private Long testId;
	public AssignTestData() {
		super();
	}
	public AssignTestData(Long userId, Long testId) {
		super();
		this.userId = userId;
		this.testId = testId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getTestId() {
		return testId;
	}
	public void setTestId(Long testId) {
		this.testId = testId;
	}
	@Override
	public String toString() {
		return "AssignTestData [userId=" + userId + ", testId=" + testId + "]";
	}
	
}
