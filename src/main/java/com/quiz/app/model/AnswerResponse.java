package com.quiz.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnswerResponse {

    @JsonProperty
    private boolean correct;
    private String feedback;

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    @Override
    public String toString() {
        return "AnswerResponse{" +
                "correct=" + correct +
                ", feedback='" + feedback + '\'' +
                '}';
    }
}
