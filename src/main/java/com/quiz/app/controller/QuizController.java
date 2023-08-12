package com.quiz.app.controller;

import com.quiz.app.config.GameSessionCacheService;
import com.quiz.app.exception.InvalidSessionException;
import com.quiz.app.model.AnswerResponse;
import com.quiz.app.model.GameStatistics;
import com.quiz.app.model.NextQuestionResponse;
import com.quiz.app.model.StartQuizSessionResponse;
import com.quiz.app.model.UserGameStatistics;
import com.quiz.app.model.dto.AnswerDTO;
import com.quiz.app.repo.AnswerRepository;
import com.quiz.app.repo.GameSessionRepository;
import com.quiz.app.repo.QuestionRepository;
import com.quiz.app.repo.UserRepository;
import com.quiz.app.repo.entity.GameSession;
import com.quiz.app.repo.entity.User;
import com.quiz.app.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final UserRepository userRepository;

    private final GameSessionRepository gameSessionRepository;

    private final GameSessionCacheService gameSessionCacheService;

    private final GameService service;

    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);


    public QuizController(UserRepository userRepository, QuestionRepository questionRepository, GameSessionRepository gameSessionRepository, AnswerRepository answerRepository, CacheManager cacheManager, GameSessionCacheService gameSessionCacheService, GameService service) {
        this.userRepository = userRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.gameSessionCacheService = gameSessionCacheService;
        this.service = service;
    }

    @GetMapping("/startGameSession")
    public ResponseEntity<StartQuizSessionResponse> startGAmeSession(@RequestHeader String username) {
        logger.info("Starting new session");
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        StartQuizSessionResponse response = service.startGame(username, user);

        logger.info("Session started");
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/questions/next")
    public ResponseEntity<NextQuestionResponse> getNextQuestion(@RequestHeader Long gameId) {
        logger.info("Fetching next question for session Id :: " + gameId);
        // Fetch the game session from the database
        Optional<GameSession> gameSessionOptional = gameSessionRepository.findById(gameId);
        if (gameSessionOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        GameSession gameSession = gameSessionOptional.get();

        validateSession(gameSession);
        if (gameSession.isCompleted()) {
            return ResponseEntity.badRequest().build();
        }

        NextQuestionResponse response = service.getNextQuestion(gameId, gameSession);

        logger.info("Next question ::" + response);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/questions/answers")
    public ResponseEntity<AnswerResponse> submitAnswer(
            @RequestHeader Long gameId,
            @RequestParam Long questionId,
            @RequestParam Long answerId) {

        logger.info("Checking answer");
        // Fetch the game session from the database
        Optional<GameSession> gameSessionOptional = gameSessionRepository.findById(gameId);
        if (gameSessionOptional.isEmpty() || gameSessionOptional.get().isCompleted()) {
            return ResponseEntity.notFound().build();
        }

        GameSession gameSession = gameSessionOptional.get();
        validateSession(gameSession);

        AnswerResponse response = service.checkAnswer(gameSession, questionId, answerId);

        logger.info("Result is ::" + response);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/questions/{questionId}/lifelines/add-time")
    public ResponseEntity<String> useAddTimeLifeline(
            @RequestHeader Long gameId,
            @PathVariable Long questionId,
            @PathVariable Long currentTimeSec) {
        logger.info("Lifeline add time requested");
        // Fetch the game session from the database
        Optional<GameSession> gameSessionOptional = gameSessionRepository.findById(gameId);
        if (gameSessionOptional.isEmpty() || gameSessionOptional.get().isCompleted()) {
            return ResponseEntity.notFound().build();
        }

        GameSession gameSession = gameSessionOptional.get();

        validateSession(gameSession);

        if (!gameSession.isLifeline10sUsed()) {
            gameSession.setEndTime(gameSession.getEndTime().plusSeconds(10));
            gameSessionRepository.save(gameSession);
            logger.info("Lifeline add time activated");
            return ResponseEntity.ok(currentTimeSec + 10 + "");
        } else
            logger.info("Lifeline already used");
        return ResponseEntity.ok("Lifeline already used");

    }

    @PostMapping("/questions/{questionId}/lifelines/fifty-fifty")
    public ResponseEntity<List<AnswerDTO>> useFiftyFiftyLifeline(
            @RequestHeader Long gameId,
            @PathVariable Long questionId) {

        logger.info("Lifeline fifty-fifty  requested");

        // Fetch the game session from the database
        Optional<GameSession> gameSessionOptional = gameSessionRepository.findById(gameId);
        if (gameSessionOptional.isEmpty() || gameSessionOptional.get().isCompleted()) {
            return ResponseEntity.notFound().build();
        }

        GameSession gameSession = gameSessionOptional.get();

        validateSession(gameSession);

        List<AnswerDTO> modifiedAnswerOptions = service.getModifiedAnswers(gameSession, questionId);
        return ResponseEntity.ok(modifiedAnswerOptions);
    }

    @PostMapping("/{gameId}/end")
    public ResponseEntity<GameStatistics> endGame(@PathVariable Long gameId) {
        GameStatistics gameStatistics = service.endGame(gameId);
        return ResponseEntity.ok(gameStatistics);
    }

    @GetMapping("/statistics/{userId}")
    public ResponseEntity<UserGameStatistics> getUserStatistics(@PathVariable Long userId) {
        UserGameStatistics statistics = service.getUserStatistics(userId);
        return ResponseEntity.ok(statistics);
    }

    private void validateSession(GameSession gameSession) {
        logger.info("Validating session");
        if (LocalDateTime.now().isAfter(gameSession.getEndTime().plusSeconds(16))) {
            gameSession.setCompleted(true);
            gameSession.setEndTime(LocalDateTime.now());
            gameSessionRepository.save(gameSession);
            gameSessionCacheService.clearGameSessionCache(gameSession.getSessionId());
            throw new InvalidSessionException("Invalid Session");
        }
        logger.info("Session is valid");
    }

}