package com.quiz.app.model.dto;

public class QuestionDTO {

    private String questionText;

    private String imageUrl;

    private Long questionId;

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    @Override
    public String toString() {
        return "QuestionDTO{" +
                "questionText='" + questionText + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", questionId=" + questionId +
                '}';
    }
}
