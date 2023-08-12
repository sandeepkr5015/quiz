package com.quiz.app.model;

import com.quiz.app.model.dto.AnswerDTO;
import com.quiz.app.model.dto.QuestionDTO;
import com.quiz.app.repo.entity.Question;

import java.util.List;

public class StartQuizSessionResponse {

    private QuestionDTO question;

    private int remainingTimeInSec;

    private long sessionId;

    private List<AnswerDTO> answers;


    public QuestionDTO getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDTO question) {
        this.question = question;
    }

    public int getRemainingTimeInSec() {
        return remainingTimeInSec;
    }

    public void setRemainingTimeInSec(int remainingTimeInSec) {
        this.remainingTimeInSec = remainingTimeInSec;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public List<AnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDTO> answers) {
        this.answers = answers;
    }
}
