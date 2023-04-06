package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuizActivity extends AppCompatActivity {

    private TextView tvQuestion;
    private ListView choicesList;
    private LinkedHashMap<String, List<String>> questions;
    private boolean next = false;
    List<String> choices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tvQuestion = findViewById(R.id.tvQuestion);
        choicesList = findViewById(R.id.choicesList);
        startQuiz();
    }

    private void getQuestions() {
        String question = "";
        List<String> choices = new ArrayList<>();
        questions = new LinkedHashMap<>();
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("QuizFolder/"+ChooseQuizToStartPrompt.chosenTopic+".txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            //creating pattern for matching
            Pattern qPat = Pattern.compile("\\d{1,3}[.] .*");
            Pattern cPat = Pattern.compile("[A-E][.] .*[^*]");
            Matcher Qm, Cm;

            while ((line = reader.readLine()) != null) {
                Qm = qPat.matcher(line);
                Cm = cPat.matcher(line);
                //if line matches..
                if (Qm.find()) {
                    question = line;
                    choices = new ArrayList<>();//declare new arraylist to clear the choices. since new question has been set.
                }
                if (Cm.find()) {
                    choices.add(line);
                }
                if (line.equals("---")){
                    questions.put(question, choices);
                }

            }
            reader.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void startQuiz(){
        getQuestions();
        int items,count=0, questionIndex;
        String question, userAnswer, correctAnswer = "a";

        List<String> questionsKeys = new ArrayList<>(questions.keySet()); //list of keys(questions)
        choices = new ArrayList<>(); //clear out choices because it has value from getQuestions
        List<String> displayChoices = new ArrayList<>();
        Random random = new Random();

        items = questionsKeys.size();

        //Display loop
        questionIndex = random.nextInt(questionsKeys.size()); //get random index from questions keyset

        //pattern for finding correct answer
        Pattern aPat = Pattern.compile("[A-E]. .*\\*");
        Matcher Am;
        //get the correct answer
        for (String a: questions.get(questionsKeys.get(questionIndex))){
            Am = aPat.matcher(a);
            if (Am.find()){
                correctAnswer = a;
            }
        }
        final String answer = correctAnswer;
        //**** show questions and choices ****
        tvQuestion.setText(questionsKeys.get(questionIndex).replaceAll("\\d{1,3}[.]\\s",""));
        //iterate the choices arraylist, remove the last character, add to new arraylist. this is only for display purposes.
        choices = questions.get(questionsKeys.get(questionIndex));
        for(String c: choices){
            c = c.replaceAll(".$","");
            displayChoices.add(c);
        }

        ArrayAdapter<String> choicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayChoices);
        choicesList.setAdapter(choicesAdapter);
        choicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (choices.get(i).equals(answer)){
                    Toast.makeText(QuizActivity.this, "Correct!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(QuizActivity.this, "Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}