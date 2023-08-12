package com.quiz.app.exception.handler;

import com.quiz.app.exception.AnswerNotFoundException;
import com.quiz.app.exception.FileUploadFailedException;
import com.quiz.app.exception.InvalidSessionException;
import com.quiz.app.exception.QuestionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileUploadFailedException.class)
    public ResponseEntity<String> handleFileNotFoundException(FileUploadFailedException e) {
        // Log the exception
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(QuestionNotFoundException.class)
    public ResponseEntity<String> handleQuestionNotFoundException(QuestionNotFoundException e) {
        // Log the exception
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(AnswerNotFoundException.class)
    public ResponseEntity<String> handleAnswerNotFoundException(AnswerNotFoundException e) {
        // Log the exception
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(InvalidSessionException.class)
    public ResponseEntity<String> invalidSessionException(InvalidSessionException e) {
        // Log the exception
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

}
