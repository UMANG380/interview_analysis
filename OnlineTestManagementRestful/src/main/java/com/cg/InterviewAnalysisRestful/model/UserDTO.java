
package com.cg.InterviewAnalysisRestful.model;


public class UserDTO {

	private String username;
	private String password;
	private Boolean isAdmin;
	private Boolean isDeleted;
	
	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "UserDTO [userName=" + username + ", userPassword=" + password + ", isAdmin=" + isAdmin
				+ ", isDeleted=" + isDeleted + "]";
	}
	
	
}
