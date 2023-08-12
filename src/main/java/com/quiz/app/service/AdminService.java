package com.quiz.app.service;

import com.quiz.app.exception.AnswerNotFoundException;
import com.quiz.app.exception.FileUploadFailedException;
import com.quiz.app.exception.QuestionNotFoundException;
import com.quiz.app.model.dto.AnswerDTO;
import com.quiz.app.model.dto.QuestionDTO;
import com.quiz.app.model.dto.UserDTO;
import com.quiz.app.repo.AnswerRepository;
import com.quiz.app.repo.QuestionRepository;
import com.quiz.app.repo.UserRepository;
import com.quiz.app.repo.entity.Answer;
import com.quiz.app.repo.entity.Question;
import com.quiz.app.repo.entity.User;
import com.quiz.app.utils.Utils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.quiz.app.utils.Utils.questionDtoToEntity;

@Service
public class AdminService {

    private final AmazonS3Service amazonS3Service;

    private final QuestionRepository questionRepository;

    private final AnswerRepository answerRepository;

    private final UserRepository userRepository;

    public AdminService(AmazonS3Service amazonS3Service, QuestionRepository questionRepository, AnswerRepository answerRepository, UserRepository userRepository) {
        this.amazonS3Service = amazonS3Service;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
    }

    public QuestionDTO saveQuestion(QuestionDTO questionDTO) {
        Question questionEntity = questionDtoToEntity(questionDTO);
        questionRepository.save(questionEntity);
        return questionDTO;
    }

    public String addQuestion(String questionText, MultipartFile image) {

        try {
            String imageUrl = null;
            if (image != null && !image.isEmpty()) {
                imageUrl = amazonS3Service.uploadFile(image);
            }

            Question question = new Question();
            question.setQuestionText(questionText);
            question.setImageUrl(imageUrl);

            questionRepository.save(question);

            return "Question added successfully";
        } catch (IOException e) {
            throw new FileUploadFailedException("Failed to upload image: " + e.getMessage());
        }
    }

    public List<Answer> addAnswer(Long questionId, List<AnswerDTO> answers) {
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            List<Answer> answerEntities = answers.stream().map(a -> Utils.answerDtoToEntity(a, question)).collect(Collectors.toList());
            return answerRepository.saveAll(answerEntities);
        } else {
            throw new QuestionNotFoundException("Question not found");
        }
    }

    public String editQuestion(Long id, QuestionDTO questionDetails) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            if (questionDetails.getQuestionText() != null)
                question.setQuestionText(questionDetails.getQuestionText());

            if (questionDetails.getImageUrl() != null)
                question.setImageUrl(questionDetails.getImageUrl());
            questionRepository.save(question);
            return "Question edited successfully";
        } else {
            throw new QuestionNotFoundException("Question not found");
        }
    }

    @Transactional
    public String deleteQuestion(Long questionId) {
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            answerRepository.deleteByQuestion(question);
            questionRepository.deleteById(questionId);
            return "Question and answers deleted successfully";
        } else {
            throw new QuestionNotFoundException("Question not found");
        }
    }

    public String updateAnswer(Long answerId, AnswerDTO updatedAnswer) {
        Optional<Answer> optionalAnswer = answerRepository.findById(answerId);
        if (optionalAnswer.isPresent()) {
            Answer answer = optionalAnswer.get();
            answer.setAnswerText(updatedAnswer.getAnswerText());
            answer.setCorrect(updatedAnswer.isCorrect());
            answerRepository.save(answer);
            return "Answer updated successfully";
        } else {
            throw new AnswerNotFoundException("Answer not found");
        }
    }


    public User addUser(UserDTO userDTO) {
        return userRepository.save(Utils.userDtoToEntity(userDTO));
    }
}
