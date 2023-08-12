package com.quiz.app.cache;

import com.quiz.app.repo.entity.Answer;
import com.quiz.app.repo.entity.Question;

import java.util.List;
import java.util.Map;

public class GameSessionCache {
    private final List<Question> batchQuestions;
    private final Map<Long, List<Answer>> batchAnswersMap;

    public GameSessionCache(List<Question> batchQuestions, Map<Long, List<Answer>> batchAnswersMap) {
        this.batchQuestions = batchQuestions;
        this.batchAnswersMap = batchAnswersMap;
    }

    public List<Question> getBatchQuestions() {
        return batchQuestions;
    }

    public Map<Long, List<Answer>> getBatchAnswersMap() {
        return batchAnswersMap;
    }
}

