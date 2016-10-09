package nizami_13512501.mufrodat;

import java.io.Serializable;


public class Score implements Serializable{
    public static final long PENALTY = 5;
    public static final long REWARD = 1;

    protected long score;
    public Score(){
        score = 0;
    }
    public Score(long score){
        this.score = score;
    }

    public long value(){
        return score;
    }

    public String toString(){
        return Long.toString(score);
    }

    public void givePenalty(){
        score -= PENALTY;
    }

    public void giveReward(){
        score += REWARD;
    }

    public long getScore(){
        return score;
    }

    public void setScore(long score){
        this.score = score;
    }
}
