package cezary.pokropek.triviaquizgameapp.data;

import java.util.ArrayList;

import cezary.pokropek.triviaquizgameapp.model.Question;

public interface AnswerListAsyncResponse {
    void processFinished(ArrayList<Question> questionArrayList);

}
