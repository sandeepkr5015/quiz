package com.quiz.app.service;

import com.quiz.app.cache.GameSessionCache;
import com.quiz.app.config.GameSessionCacheService;
import com.quiz.app.exception.AnswerNotFoundException;
import com.quiz.app.exception.QuestionNotFoundException;
import com.quiz.app.model.AnswerResponse;
import com.quiz.app.model.GameStatistics;
import com.quiz.app.model.NextQuestionResponse;
import com.quiz.app.model.StartQuizSessionResponse;
import com.quiz.app.model.UserGameStatistics;
import com.quiz.app.model.dto.AnswerDTO;
import com.quiz.app.repo.AnswerRepository;
import com.quiz.app.repo.GameSessionRepository;
import com.quiz.app.repo.QuestionRepository;
import com.quiz.app.repo.entity.Answer;
import com.quiz.app.repo.entity.GameSession;
import com.quiz.app.repo.entity.Question;
import com.quiz.app.repo.entity.User;
import com.quiz.app.utils.Utils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GameService {


    private final QuestionRepository questionRepository;

    private final GameSessionRepository gameSessionRepository;


    private final AnswerRepository answerRepository;

    private final CacheManager cacheManager;

    private final GameSessionCacheService gameSessionCacheService;


    public GameService(QuestionRepository questionRepository, GameSessionRepository gameSessionRepository, AnswerRepository answerRepository, CacheManager cacheManager, GameSessionCacheService gameSessionCacheService) {

        this.questionRepository = questionRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.answerRepository = answerRepository;
        this.cacheManager = cacheManager;
        this.gameSessionCacheService = gameSessionCacheService;
    }

    public GameStatistics endGame(Long gameId) {
        // Retrieve the game session from the database
        GameSession game = gameSessionRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with id: " + gameId));

        // Calculate game statistics
        int userScore = 10 - game.getScore();

        long timeTaken = calculateTimeTaken(game.getStartTime(), game.getEndTime());
        boolean isFiftyFiftyUsed = game.isLifeline5050Used();
        boolean isAddTimeUsed = game.isLifeline10sUsed();

        int lifelineUsage = getLifeLineUsage(isFiftyFiftyUsed, isAddTimeUsed);


        // Update game statistics
        game.setScore(userScore);
        game.setTimeTaken(timeTaken);
        // Save the updated game session
        gameSessionRepository.save(game);

        gameSessionCacheService.clearGameSessionCache(game.getSessionId());


        // Create and return game statistics
        return new GameStatistics(userScore, timeTaken, lifelineUsage);
    }

    public UserGameStatistics getUserStatistics(Long userId) {

        List<GameSession> games = gameSessionRepository.findByUser_UserId(userId);

        if (games == null || games.isEmpty()) {
            throw new EntityNotFoundException("Game not found for user id: " + userId);
        }

        int sum = 0;
        List<Integer> scores = games.stream().map(GameSession::getScore).collect(Collectors.toList());
        List<Long> sessions = games.stream().map(GameSession::getSessionId).distinct().collect(Collectors.toList());

        for (int value : scores) {
            sum += value;
        }


        UserGameStatistics statistics = new UserGameStatistics();
        statistics.setAverageScore(sum / scores.size());
        statistics.setTotalGamesPlayed(sessions.size());
        return statistics;
    }

    private int getLifeLineUsage(boolean isFiftyFiftyUsed, boolean isAddTimeUsed) {
        int used = 0;
        if (isFiftyFiftyUsed) {
            used++;
        }
        if (isAddTimeUsed) {
            used++;
        }
        return used;
    }

    // Example method to calculate time taken in milliseconds
    private long calculateTimeTaken(LocalDateTime startTime, LocalDateTime endTime) {
        return ChronoUnit.SECONDS.between(startTime, endTime);
    }


    public StartQuizSessionResponse startGame(String username, User user) {


        // Fetch a batch of questions (adjust the logic as needed)
        List<Question> batchQuestions = questionRepository.findAll();

        if (batchQuestions.isEmpty()) {
            throw new QuestionNotFoundException("No questions available");
        }

        // Fetch answers for the batch of questions
        Map<Long, List<Answer>> batchAnswersMap = new HashMap<>();
        for (Question question : batchQuestions) {
            List<Answer> answers = answerRepository.findByQuestion_QuestionId(question.getQuestionId());
            batchAnswersMap.put(question.getQuestionId(), answers);
        }

        // Create a new game session
        GameSession gameSession = new GameSession();
        gameSession.setUser(user);
        gameSession.setCurrentQuestion(batchQuestions.get(0));
        gameSession.setStartTime(LocalDateTime.now());
        gameSession.setScore(0);
        gameSession.setLifeline10sUsed(false);
        gameSession.setLifeline5050Used(false);
        gameSession.setCompleted(false);
        gameSession.setEndTime(LocalDateTime.now().plusSeconds(16));
        gameSession.setAskedQuestionIds(batchQuestions.get(0).getQuestionId() + ",");

        GameSession saved = gameSessionRepository.save(gameSession);

        // Cache the batch of questions and answers for the session
        GameSessionCache sessionCache = new GameSessionCache(batchQuestions, batchAnswersMap);
        putToCache(saved.getSessionId(), sessionCache);

        List<AnswerDTO> answers = batchAnswersMap.get(batchQuestions.get(0).getQuestionId()).stream().map(Utils::answerEntityToDto).collect(Collectors.toList());
        StartQuizSessionResponse response = new StartQuizSessionResponse();
        response.setQuestion(Utils.questionEntityToDto(batchQuestions.get(0)));
        response.setRemainingTimeInSec(15);
        response.setSessionId(saved.getSessionId());
        response.setAnswers(answers);

        return response;
    }

    public void putToCache(Long gameSessionId, GameSessionCache gameSessionCache) {
        Cache cache = cacheManager.getCache("gameSessionCache");
        if (cache != null) {
            cache.put(gameSessionId, gameSessionCache);
        }
    }

    public NextQuestionResponse getNextQuestion(Long gameId, GameSession gameSession) {


        // Get the sessionCache from the cache
        GameSessionCache sessionCache = gameSessionCacheService.getGameSessionCache(gameId);

        List<Long> askedQuestions = getCommaSeparated(gameSession.getAskedQuestionIds());

        Question nextQuestion = getNextQuestionLogic(sessionCache, askedQuestions);

        if (nextQuestion == null) {
            gameSession.setCompleted(true);
            gameSession.setEndTime(LocalDateTime.now());
            gameSessionRepository.save(gameSession);
            throw new QuestionNotFoundException("No question found");
        }


        // Fetch answers for the next question
        List<Answer> answers = answerRepository.findByQuestion_QuestionId(nextQuestion.getQuestionId());

        // Update the game session with the next question
        gameSession.setCurrentQuestion(nextQuestion);
        gameSession.setAskedQuestionIds(gameSession.getAskedQuestionIds() + nextQuestion.getQuestionId() + ",");
        gameSession.setEndTime(LocalDateTime.now().plusSeconds(16));
        gameSessionRepository.save(gameSession);

        NextQuestionResponse response = new NextQuestionResponse();
        response.setQuestion(Utils.questionEntityToDto(nextQuestion));
        response.setAnswers(answers.stream().map(Utils::answerEntityToDto).collect(Collectors.toList()));
        response.setQuestionNumber(askedQuestions.size() + 1);

        return response;
    }

    private List<Long> getCommaSeparated(String commaSeparated) {

        List<Long> numbersList = new ArrayList<>();
        String[] numberStrings = commaSeparated.split(",");

        for (String numberString : numberStrings) {
            try {
                long number = Long.parseLong(numberString);
                numbersList.add(number);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format: " + numberString);
            }
        }

        return numbersList;
    }

    public List<AnswerDTO> getModifiedAnswers(GameSession gameSession, Long questionId) {
        if (gameSession.isLifeline5050Used()) {
            return new ArrayList<>();
        }
        List<Answer> answers = answerRepository.findByQuestion_QuestionId(questionId);

        List<AnswerDTO> modifiedAnswerOptions = new ArrayList<>();

        for (Answer answer : answers) {
            if (answer.isCorrect()) {
                modifiedAnswerOptions.add(Utils.answerEntityToDto(answer));
            }
        }

        for (Answer answer : answers) {
            if (!answer.isCorrect()) {
                modifiedAnswerOptions.add(Utils.answerEntityToDto(answer));
                break;
            }
        }
        return modifiedAnswerOptions;
    }


    private Question getNextQuestionLogic(GameSessionCache sessionCache, List<Long> askedQuestions) {
        List<Question> allQuestions = sessionCache.getBatchQuestions();

        // Check if there are questions left to ask
        if (allQuestions.isEmpty()) {
            return null;
        }

        for (Question question : allQuestions) {
            if (!askedQuestions.contains(question.getQuestionId())) {
                return question;
            }
        }
        return null;
    }

    public AnswerResponse checkAnswer(GameSession gameSession, Long questionId, Long answerId) {
        List<Answer> answers = answerRepository.findByQuestion_QuestionId(questionId);

        if (answers == null || answers.isEmpty()) {
            throw new AnswerNotFoundException("No answer found");
        }

        Answer answer = answers.stream().filter(a -> a.getAnswerId().equals(answerId)).findAny().orElse(null);
        boolean isCorrect = false;
        AnswerResponse response = new AnswerResponse();
        if (answer != null) {
            if (!answer.isCorrect()) {
                response.setFeedback("Wrong answer");
            } else {
                response.setFeedback("Correct answer");
                isCorrect = true;
            }
        }
        response.setCorrect(isCorrect);
        return response;
    }
}


