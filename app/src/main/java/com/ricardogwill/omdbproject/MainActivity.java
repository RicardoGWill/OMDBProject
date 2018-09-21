package com.ricardogwill.omdbproject;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView movieTitleTextView, infoTextView, ratingsTextView;
    EditText movieTitleEditText;
    ImageView moviePosterImageView;
    Button parseButton;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieTitleTextView = findViewById(R.id.movie_title_textView);
        infoTextView = findViewById(R.id.info_textView);
        ratingsTextView = findViewById(R.id.ratings_textView);
        parseButton = findViewById(R.id.parse_button);
        movieTitleEditText = findViewById(R.id.movie_title_editText);
        moviePosterImageView = findViewById(R.id.movie_poster_imageView);

        requestQueue = Volley.newRequestQueue(this);

        parseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingsTextView.setText("");
                jsonParse();
            }
        });

    }

    private void jsonParse() {

        String movieTitleEditTextString = movieTitleEditText.getText().toString();
        String movieTitleEditTextMultiWordCapableString = movieTitleEditTextString.replaceAll(" ", "%20");

        String url = "http://www.omdbapi.com/?t=" + movieTitleEditTextMultiWordCapableString + "&apikey=3a82d9df";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // This is to bring out the Strings not embedded in Objects {} or Arrays {}.
                    String titleJSONString = response.getString("Title");
                    String yearJSONString = response.getString("Year");
                    String ratedJSONString = response.getString("Rated");
                    String runtimeJSONString = response.getString("Runtime");

                    movieTitleTextView.setText("");
                    movieTitleTextView.append(titleJSONString);

                    infoTextView.setText("");
                    infoTextView.append(
                            "Year Released: " + yearJSONString + "\n" +
                            "Rating: " + ratedJSONString + "\n" +
                            "Runtime: " + runtimeJSONString
                    );

                    // This sets the Imageview to the movie poster.
                    String posterJSONString = response.getString("Poster");
                    Picasso.get().load(posterJSONString).into(moviePosterImageView);

                    // This is for the JSON "Ratings" Array - Ratings: [...]
                    JSONArray jsonArray = response.getJSONArray("Ratings");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject statesJSONObject = jsonArray.getJSONObject(i);

                        String nameString = statesJSONObject.getString("Source");
                        String capitalString = statesJSONObject.getString("Value");

                        ratingsTextView.append(nameString + "\n" +
                                "★ " + capitalString + "\n\n");
                    }

                    // This (still in the "try" block, makes the keyboard disappear upon pressing the button.
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(movieTitleEditText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Movie Not Found.", Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

}