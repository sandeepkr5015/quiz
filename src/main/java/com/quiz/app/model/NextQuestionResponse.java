package com.quiz.app.model;

import com.quiz.app.model.dto.AnswerDTO;
import com.quiz.app.model.dto.QuestionDTO;

import java.util.List;

public class NextQuestionResponse {
    private QuestionDTO question;
    private List<AnswerDTO> answers;
    private int questionNumber;

    public QuestionDTO getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDTO question) {
        this.question = question;
    }

    public List<AnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDTO> answers) {
        this.answers = answers;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    @Override
    public String toString() {
        return "NextQuestionResponse{" +
                "question=" + question +
                ", answers=" + answers +
                ", questionNumber=" + questionNumber +
                '}';
    }
}
