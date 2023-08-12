package com.quiz.app.repo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_session")
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "current_question_id", nullable = false)
    private Question currentQuestion;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "score")
    private Integer score;

    @Column(name = "lifeline_10s_used")
    private boolean lifeline10sUsed;

    @Column(name = "lifeline_5050_used")
    private boolean lifeline5050Used;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;

    @Column(name = "asked_question_ids")
    private String askedQuestionIds;

    @Column(name = "time_taken")
    private Long timeTaken;

    public Long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public String getAskedQuestionIds() {
        return askedQuestionIds;
    }

    public void setAskedQuestionIds(String askedQuestionIds) {
        this.askedQuestionIds = askedQuestionIds;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public boolean isLifeline10sUsed() {
        return lifeline10sUsed;
    }

    public void setLifeline10sUsed(boolean lifeline10sUsed) {
        this.lifeline10sUsed = lifeline10sUsed;
    }

    public boolean isLifeline5050Used() {
        return lifeline5050Used;
    }

    public void setLifeline5050Used(boolean lifeline5050Used) {
        this.lifeline5050Used = lifeline5050Used;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}

