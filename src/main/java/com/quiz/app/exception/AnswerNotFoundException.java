package com.quiz.app.exception;

public class AnswerNotFoundException extends RuntimeException {

    public AnswerNotFoundException(String message) {
        super(message);
    }
}
