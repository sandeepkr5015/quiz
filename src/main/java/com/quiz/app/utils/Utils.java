package com.quiz.app.utils;

import com.quiz.app.model.dto.AnswerDTO;
import com.quiz.app.model.dto.QuestionDTO;
import com.quiz.app.model.dto.UserDTO;
import com.quiz.app.repo.entity.Answer;
import com.quiz.app.repo.entity.Question;
import com.quiz.app.repo.entity.User;

public class Utils {

    public static Question questionDtoToEntity(QuestionDTO questionDTO) {
        Question question = new Question();
        question.setQuestionText(questionDTO.getQuestionText());
        question.setImageUrl(questionDTO.getImageUrl());
        return question;
    }

    public static QuestionDTO questionEntityToDto(Question question) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setQuestionText(question.getQuestionText());
        questionDTO.setImageUrl(question.getImageUrl());
        questionDTO.setQuestionId(questionDTO.getQuestionId());
        return questionDTO;
    }


    public static Answer answerDtoToEntity(AnswerDTO answerDTO, Question question) {
        Answer answer = new Answer();
        answer.setAnswerText(answerDTO.getAnswerText());
        answer.setCorrect(answerDTO.isCorrect());
        answer.setQuestion(question);
        return answer;
    }

    public static User userDtoToEntity(UserDTO userDTO) {
        User user = new User();

        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());
        user.setPasswordHash(userDTO.getPasswordHash());
        return user;
    }

    public static AnswerDTO answerEntityToDto(Answer answer) {
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setAnswerText(answer.getAnswerText());
        answerDTO.setCorrect(answer.isCorrect());
        answerDTO.setAnswerId(answer.getAnswerId());
        return answerDTO;
    }


}
