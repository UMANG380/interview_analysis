
package com.cg.otm.OnlineTestManagementRestful.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cg.otm.OnlineTestManagementRestful.dto.Question;



public interface QuestionRepository extends JpaRepository<Question, Long>{

	public Question findByQuestionId(Long questionId);
}