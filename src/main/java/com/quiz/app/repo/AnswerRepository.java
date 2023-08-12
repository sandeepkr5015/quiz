package com.quiz.app.repo;

import com.quiz.app.repo.entity.Answer;
import com.quiz.app.repo.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    void deleteByQuestion(Question question);
    List<Answer> findByQuestion_QuestionId(Long questionId);
}
