package com.quiz.app.controller;

import com.quiz.app.model.dto.AnswerDTO;
import com.quiz.app.model.dto.QuestionDTO;
import com.quiz.app.repo.AnswerRepository;
import com.quiz.app.repo.QuestionRepository;
import com.quiz.app.repo.UserRepository;
import com.quiz.app.repo.entity.Answer;
import com.quiz.app.repo.entity.User;
import com.quiz.app.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }


    @PostMapping("/add-question")
    public ResponseEntity<String> addQuestion(@RequestParam(name = "image", required = false) MultipartFile image,
                                              @RequestParam("questionText") String questionText) {
        String response = service.addQuestion(questionText, image);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/edit-question/{id}")
    public ResponseEntity<String> editQuestion(@PathVariable Long id, @RequestBody QuestionDTO questionDetails) {
        String response = service.editQuestion(id, questionDetails);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-question/{questionId}")
    public ResponseEntity<String> deleteQuestion(@PathVariable Long questionId) {
        String response = service.deleteQuestion(questionId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/add-answers/{questionId}")
    public ResponseEntity<List<Answer>> addAnswers(@PathVariable Long questionId,
                                             @RequestBody List<AnswerDTO> answers) {
        List<Answer> response = service.addAnswer(questionId, answers);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PutMapping("/edit-answer/{answerId}")
    public ResponseEntity<String> editAnswer(@PathVariable Long answerId,
                                             @RequestBody AnswerDTO updatedAnswer) {
      String response = service.updateAnswer(answerId, updatedAnswer);

      return ResponseEntity.ok(response);
    }

}

