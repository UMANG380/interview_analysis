package com.cg.InterviewAnalysisRestful.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cg.InterviewAnalysisRestful.dto.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	public User findByUserId(Long userId);
	public Optional<User> findByUserName(String username);
}
