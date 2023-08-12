package com.quiz.app.model;

public class GameStatistics {

    private int userScore;
    private long timeTaken;
    private int lifelineUsage;

    public GameStatistics(int userScore, long timeTaken, int lifelineUsage) {
        this.userScore = userScore;
        this.timeTaken = timeTaken;
        this.lifelineUsage = lifelineUsage;
    }

    public int getUserScore() {
        return userScore;
    }

    public void setUserScore(int userScore) {
        this.userScore = userScore;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public int getLifelineUsage() {
        return lifelineUsage;
    }

    public void setLifelineUsage(int lifelineUsage) {
        this.lifelineUsage = lifelineUsage;
    }
}
