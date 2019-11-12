
package com.cg.InterviewAnalysisRestful.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cg.InterviewAnalysisRestful.dto.Question;



public interface QuestionRepository extends JpaRepository<Question, Long>{

	public Question findByQuestionId(Long questionId);
}