package cezary.pokropek.triviaquizgameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import cezary.pokropek.triviaquizgameapp.data.QuestionBank;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new QuestionBank().getQuestions();


    }
}
