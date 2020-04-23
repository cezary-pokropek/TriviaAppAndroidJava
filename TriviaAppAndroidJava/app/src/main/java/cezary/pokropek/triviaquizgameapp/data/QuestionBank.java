package cezary.pokropek.triviaquizgameapp.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cezary.pokropek.triviaquizgameapp.controller.AppController;
import cezary.pokropek.triviaquizgameapp.model.Question;

import static cezary.pokropek.triviaquizgameapp.controller.AppController.TAG;

public class QuestionBank {

    private String url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";
    ArrayList<Question> questionArrayList = new ArrayList<>();

    public List<Question> getQuestions(final AnswerListAsyncResponse callBack){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                (JSONArray) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++ ) {
                            try {
                                Question question = new Question();
                                question.setAnswer(response.getJSONArray(i).get(0).toString());
                                question.setAnswerTrue(response.getJSONArray(i).getBoolean(1));

                                // Add question to list
                                questionArrayList.add(question);
                                //Log.d(TAG, "onResponse: " + question.getAnswer());


                                // Log.d(TAG, "onResponse: "+ response.getJSONArray(i).get(0));
                                // Log.d(TAG, "onResponse: "+ response.getJSONArray(i).getBoolean(1));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if(null != callBack) callBack.processFinished(questionArrayList);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);

        return questionArrayList;
    }


}
