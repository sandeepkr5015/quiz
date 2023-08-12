package com.quiz.app.repo;

import com.quiz.app.repo.entity.GameSession;
import com.quiz.app.repo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    List<GameSession> findByUser_UserId(Long userId);

}