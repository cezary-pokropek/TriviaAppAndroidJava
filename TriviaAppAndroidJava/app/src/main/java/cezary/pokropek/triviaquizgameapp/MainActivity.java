package cezary.pokropek.triviaquizgameapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;

import cezary.pokropek.triviaquizgameapp.data.AnswerListAsyncResponse;
import cezary.pokropek.triviaquizgameapp.data.QuestionBank;
import cezary.pokropek.triviaquizgameapp.model.Question;
import cezary.pokropek.triviaquizgameapp.model.Score;
import cezary.pokropek.triviaquizgameapp.util.Prefs;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView questionTextview;
    private TextView questionCounterTextview;
    private Button trueButton;
    private Button falseButton;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;
    private TextView scoreTextView;
    private TextView highestScoreTextView;

    private int scoreCounter = 0;
    private Score score;
    private Prefs prefs;

    private SoundPool soundPool;
    private int sound_correct, sound_false;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        score = new Score(); // score obj

        prefs = new Prefs(MainActivity.this);

        scoreTextView = findViewById(R.id.score_text);
        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.prev_button);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        questionCounterTextview = findViewById(R.id.counter_text);
        questionTextview = findViewById(R.id.question_textView);
        highestScoreTextView = findViewById(R.id.high_score);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);

        scoreTextView.setText("Current Score: " + String.valueOf(score.getScore()));

        //get previous state
        currentQuestionIndex = prefs.getState();

        highestScoreTextView.setText("Highest Score: " + String.valueOf(prefs.getHighScore()));

        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {

                questionTextview.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                questionCounterTextview.setText(currentQuestionIndex + " / " + questionList.size());
//                Log.d("Inside", "processFinished: " + questionArrayList);
            }
        });
//        Log.d("Main", "onCreate: " + questionList);

        // Sound clip multimedia

        @SuppressLint({"NewApi", "LocalSuppress"}) AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(audioAttributes)
                .build();

        sound_correct = soundPool.load(this,R.raw.correct, 1);
        sound_false = soundPool.load(this, R.raw.defeat_two, 1);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prev_button:
                currentQuestionIndex = ((currentQuestionIndex + (questionList.size() - 1)) % questionList.size());
                updateQuestion();
                break;
            case R.id.next_button:
                goNext();
                break;
            case R.id.true_button:
                checkAnswer(true);
                updateQuestion();
                goNext();
                break;
            case R.id.false_button:
                checkAnswer(false);
                updateQuestion();
                goNext();
                break;

        }

    }

    private void checkAnswer(boolean userChooseCorrect) {
        boolean answerIsTrue = questionList.get(currentQuestionIndex).isAnswerTrue();
        int toastMessageId = 0;
        if (userChooseCorrect == answerIsTrue) {
            soundPool.play(sound_correct, 1, 1, 0, 0,1);
            addPoints();
            fadeView();
            toastMessageId = R.string.correct_answer;
        } else {
            soundPool.play(sound_false, 1, 1, 0, 0,1);
            decrementPoints();
            shakeAnimation();
            toastMessageId = R.string.wrong_answer;
        }
        Toast.makeText(MainActivity.this, toastMessageId,
                Toast.LENGTH_SHORT)
                .show();
    }

    private void updateQuestion() {
        Log.d("Current", "onClick: " + currentQuestionIndex);
        questionTextview.setText(questionList.get(currentQuestionIndex).getAnswer());
        questionCounterTextview.setText(currentQuestionIndex + " / " + questionList.size());
    }

    private void fadeView() {
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


    }

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.shake_animation);
        final CardView cardView = findViewById(R.id.cardView);
        cardView.startAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }

    private void goNext() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
        updateQuestion();
    }

    private void addPoints() {
        scoreCounter += 100;
        score.setScore(scoreCounter);
        scoreTextView.setText("Current Score: " + String.valueOf(score.getScore()));

        Log.d("Score: ", "addPoints: " + score.getScore());
    }

    private void decrementPoints() {
        scoreCounter -= 100;
        if (scoreCounter > 0) {
            score.setScore(scoreCounter);
            scoreTextView.setText("Current Score: " + String.valueOf(score.getScore()));
        } else {
            scoreCounter = 0;
            score.setScore(scoreCounter);
            scoreTextView.setText("Current Score: " + String.valueOf(score.getScore()));
            Log.d("Score Invalid", "decrementPoints: " + score.getScore());
        }

        Log.d("Score: ", "addPoints: " + score.getScore());
    }

    @Override
    protected void onPause() {
        prefs.saveHighScore(score.getScore());
        prefs.setState(this.currentQuestionIndex);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }



    }
