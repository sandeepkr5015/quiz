package com.quiz.app.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnswerDTO {

    private String answerText;
    @JsonProperty
    private boolean isCorrect;

    private long answerId;

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(long answerId) {
        this.answerId = answerId;
    }
}
