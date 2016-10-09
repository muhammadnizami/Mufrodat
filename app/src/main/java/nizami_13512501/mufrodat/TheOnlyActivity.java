package nizami_13512501.mufrodat;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

public class TheOnlyActivity extends AppCompatActivity {

    Button correctButtonMeaning = null;
    TextView textViewArabicWord;
    Button buttonMeanings [];
    LinkedHashMap<String,String> dictionary = null;
    List<String> dictionary_key_list = null;
    int next_word_index;
    public WordScoreTable wordScoreTable = null;
    Score total_Score;

    public static final int INITIAL_WORD_COUNT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_only);

        textViewArabicWord = (TextView) findViewById(R.id.textViewArabicWord);
        buttonMeanings = new Button[4];
        buttonMeanings[0] = (Button) findViewById(R.id.buttonMeaning1);
        buttonMeanings[1] = (Button) findViewById(R.id.buttonMeaning2);
        buttonMeanings[2] = (Button) findViewById(R.id.buttonMeaning3);
        buttonMeanings[3] = (Button) findViewById(R.id.buttonMeaning4);
    }

    @Override
    protected void onStart(){
        super.onStart();

        initializeDictionary();
        initializeWordScoreTable();
        initializeTotalScore();

        newQuestion();

    }

    @Override
    protected void onDestroy(){
        saveWordScoreTable();
        saveTotalScore();

        super.onDestroy();
    }

    protected void initializeDictionary(){
        String [][] splittedString = getSplittedStringArray(getApplicationContext(),R.array.dictionary_array);
        dictionary = getKeyValueFromSplittedString(splittedString);
        dictionary_key_list = getKeyFromSplittedString(splittedString);
    }

    protected void initializeWordScoreTable(){
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        if (mPrefs.contains("wordScoreTable")) {
            Gson gson = new Gson();
            String json = mPrefs.getString("wordScoreTable", "");
            wordScoreTable = gson.fromJson(json, WordScoreTable.class);
        }else {
            wordScoreTable = new WordScoreTable();
            for (next_word_index=0;next_word_index<INITIAL_WORD_COUNT;){
                addNewWordToScoreTable();
            }
        }
    }

    protected void initializeTotalScore(){
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        if (mPrefs.contains("totalScore")) {
            Gson gson = new Gson();
            String json = mPrefs.getString("totalScore", "");
            total_Score = gson.fromJson(json, Score.class);
        }else {
            total_Score = new Score(0);
        }
    }

    protected void saveWordScoreTable(){
        SharedPreferences  mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(wordScoreTable);
        prefsEditor.putString("wordScoreTable", json);
        prefsEditor.commit();
    }
    protected void saveTotalScore(){
        SharedPreferences  mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(total_Score);
        prefsEditor.putString("totalScore", json);
        prefsEditor.commit();
    }

    protected void addNewWordToScoreTable(){
        if (next_word_index<dictionary_key_list.size()){
            wordScoreTable.insertNewWord(dictionary_key_list.get(next_word_index));
            next_word_index++;
        }
    }

    public void onButtonMeaningClick(View view){
        if (view==correctButtonMeaning)
            onCorrectAnswer();
        else
            onWrongAnswer();
        wordScoreTable.sort();
        newQuestion();
    }

    protected void onCorrectAnswer(){
        showMessage("Jawaban Anda Benar");
        total_Score.giveReward();
        arabicWordScoreItem.score.giveReward();
        if (total_Score.value()>0){
            addNewWordToScoreTable();
        }
    }

    protected void onWrongAnswer(){
        showMessage("Jawaban Anda Salah. Arti dari " + arabicWord + " adalah " + correctAnswer);
        total_Score.givePenalty();
        arabicWordScoreItem.score.givePenalty();
    }

    Toast currentToast = null;
    protected void showMessage(String message){
        if (currentToast !=null){
            currentToast.cancel();
            currentToast = null;
        }
        currentToast = Toast.makeText(this,message,Toast.LENGTH_SHORT);
        currentToast.setGravity(Gravity.TOP,0,0);
        currentToast.show();
    }

    //data for questions
    WordScoreTable.WordScoreItem arabicWordScoreItem = null;
    String arabicWord = "كَلِمَة";
    String correctAnswer = "kata" ;
    String [] wrongAnswers = {"kalimat", "buku", "lorem ipsum"};

    protected void newQuestion(){
        constructNewQuestion();
        displayNewQuestion();
    }

    Random constructQuestionRandom = new Random();
    protected void constructNewQuestion(){
        //this is still for trial
        arabicWordScoreItem = wordScoreTable.whatWordToLearn();
        arabicWord = arabicWordScoreItem.word;
        correctAnswer = dictionary.get(arabicWord);

        for (int i=0;i<wrongAnswers.length;i++){
            int wrongAnswerId;
            String randomArabicWord;
            do {
                wrongAnswerId = constructQuestionRandom.nextInt(dictionary.size());
                randomArabicWord = dictionary_key_list.get(wrongAnswerId);
            } while (randomArabicWord.equals(arabicWord));
            wrongAnswers[i] = dictionary.get(randomArabicWord);
        }
    }

    Random displayQuestionRandom = new Random();
    protected void displayNewQuestion(){
        int correctButtonIdx = displayQuestionRandom.nextInt(4);

        correctButtonMeaning = buttonMeanings[correctButtonIdx];

        textViewArabicWord.setText(arabicWord);
        correctButtonMeaning.setText(correctAnswer);
        for (int i=0;i<correctButtonIdx;i++){
            buttonMeanings[i].setText(wrongAnswers[i]);
        }
        for (int i=correctButtonIdx+1;i<buttonMeanings.length;i++){
            buttonMeanings[i].setText(wrongAnswers[i-1]);
        }
    }

    String [][] getSplittedStringArray(Context ctx, int id){
        String[] array = ctx.getResources().getStringArray(id);
        String [][] splittedString = new String[array.length][];
        for (int i=0;i<array.length;i++) {
            String[] splittedItem = array[i].split("~");
            splittedString[i] = splittedItem;
        }
        return splittedString;
    }

    LinkedHashMap<String, String> getKeyValueFromSplittedString(String [][] splittedArray) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        for (int i=0;i<splittedArray.length;i++){
            result.put(splittedArray[i][0],splittedArray[i][1]);
        }
        return result;
    }

    List<String> getKeyFromSplittedString(String [][] splittedArray) {
        List<String> result = new ArrayList<>(splittedArray.length);
        for (int i=0;i<splittedArray.length;i++){
            result.add(splittedArray[i][0]);
        }
        return result;
    }
}
