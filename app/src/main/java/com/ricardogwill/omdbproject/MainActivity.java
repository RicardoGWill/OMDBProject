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

    private TextView movieTitleTextView, infoTextView, moreInfoTextView, ratingsTextView;
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
        moreInfoTextView = findViewById(R.id.more_info_textView);
        ratingsTextView = findViewById(R.id.ratings_textView);
        parseButton = findViewById(R.id.parse_button);
        movieTitleEditText = findViewById(R.id.movie_title_editText);
        moviePosterImageView = findViewById(R.id.movie_poster_imageView);

        requestQueue = Volley.newRequestQueue(this);

        parseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingsTextView.setText("Ratings:  \n\n");
                jsonParse();
                movieTitleEditText.setText("");
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
                    // Below is to bring out the Strings not embedded in Objects {} or Arrays {}.
                    String titleJSONString = response.getString("Title");
                    String yearJSONString = response.getString("Year");
                    String ratedJSONString = response.getString("Rated");
                    String runtimeJSONString = response.getString("Runtime");
                    String directorJSONString = response.getString("Director");
                    String genreJSONString = response.getString("Genre");
                    String actorsJSONString = response.getString("Actors");
                    String plotJSONString = response.getString("Plot");
                    String writerJSONString = response.getString("Writer");

                    // This sets the "movieTitleTextView".
                    movieTitleTextView.setText("");
                    movieTitleTextView.append(titleJSONString);

                    // This sets the "infoTextView".
                    infoTextView.setText("");
                    infoTextView.append(
                            "Year Released:  " + yearJSONString + "\n" +
                            "Rating:  " + ratedJSONString + "\n" +
                            "Runtime:  " + runtimeJSONString + "\n" +
                            "Director:  " + directorJSONString
                    );

                    // This sets the "moreInfoTextView".
                    moreInfoTextView.setText("");
                    moreInfoTextView.append(
                            "Genre:  " + genreJSONString + "\n\n" +
                            "Actors:  " + actorsJSONString + "\n\n" +
                            "Plot:  " + plotJSONString + "\n\n" +
                            "Written by:  " + writerJSONString + "\n"
                    );

                            // This sets the Imageview to the movie poster.
                    String posterJSONString = response.getString("Poster");
                    Picasso.get().load(posterJSONString).into(moviePosterImageView);

                    // This is for the JSON "Ratings" Array - Ratings: [...]
                    JSONArray jsonArray = response.getJSONArray("Ratings");
                    // This gets the three Objects within the "Ratings" Array.
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject ratingsJSONObject = jsonArray.getJSONObject(i);
                        // "Source" and "Value" are Strings within the three Objects inside the "Ratings" Array.
                        String nameString = ratingsJSONObject.getString("Source");
                        String capitalString = ratingsJSONObject.getString("Value");

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