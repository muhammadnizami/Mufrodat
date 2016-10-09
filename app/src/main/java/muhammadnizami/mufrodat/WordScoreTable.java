package muhammadnizami.mufrodat;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

public class WordScoreTable implements Serializable {

    public static final long INITIAL_SCORE = 0;
    public static final float WORD_PROBABILITY = 0.75f;

    public class WordScoreItem implements Serializable, Comparable<WordScoreItem>{
        public String word;
        public Score score;

        public WordScoreItem(){
            word = "";
            score = new Score(INITIAL_SCORE);
        }

        public WordScoreItem(String word){
            this.word = word;
            score = new Score(INITIAL_SCORE);
        }

        @Override
        public int compareTo(@NonNull WordScoreItem o) {
            return Long.compare(score.value(),o.score.value());
        }

        public String toString(){
            return word + "->" + score;
        }

        public long getScoreValue(){
            return score.value();
        }
    }

    LinkedList<WordScoreItem> tab;

    public WordScoreTable(){
        tab = new LinkedList<>();
    }

    public void insertNewWord(String word){
        tab.add(new WordScoreItem(word));
    }

    Random random = new Random();
    public WordScoreItem whatWordToLearn(){
        ListIterator itr = tab.listIterator();

        while(itr.hasNext()){
            float r = random.nextFloat();
            if (r < WORD_PROBABILITY){
                return (WordScoreItem) itr.next();
            }else{
                itr.next();
            }
        }

        itr.previous();
        return (WordScoreItem) itr.next();
    }

    /**
     * this has to be called everytime a WordScoreItem's score is updated
     */
    @TargetApi(Build.VERSION_CODES.N)
    public void sort(){
        Collections.sort(tab);
    }

    public String toString(){
        return tab.toString();
    }

}
