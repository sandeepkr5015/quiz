package com.quiz.app.repo;

import com.quiz.app.repo.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    //List<Question> findTop10ByOrderByIdAsc();
}

