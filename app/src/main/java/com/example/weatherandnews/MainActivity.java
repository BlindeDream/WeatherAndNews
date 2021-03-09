package com.example.weatherandnews;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.weatherandnews.api.ApiClient;
import com.example.weatherandnews.api.ApiInterface;
import com.example.weatherandnews.models.Article;
import com.example.weatherandnews.models.News;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements  SwipeRefreshLayout.OnRefreshListener {

    public static final String API_KEY = "bbb9edc064264f2381021ab7619f1340";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> articles = new ArrayList<>();
    private Adapter adapter;
    private String TAG = MainActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout errorLayout;
    private ImageView errorImage;
    private TextView errorTitle, errorMessage;
    private Button btnRetry;


    String City = "London";
    //Your Key
    String Key = "2a20d2a71c5a0737f88861d43949d630";

    String url1 = "https://samples.openweathermap.org/data/2.5/weather?q=London&appid=439d4b804bc8187953eb36d2a8c26a02";


    TextView txtCity, txtTime, txtValue, txtValueFeelLike, txtValueHumidity, txtValueVision;

    String nameIcon = "10d";

    EditText editText;

    Button btnLoading;

    ImageView imgIcon;

    Button button_1;
    Button button_2;

    RelativeLayout relativeLayoutMain;
    RelativeLayout relativeLayout;

    LinearLayout linearLayoutMenu;



    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;

            URL url;

            HttpURLConnection httpURLConnection;

            InputStream inputStream;

            try {
                Log.i("LINK", strings[0]);
                url = new URL(strings[0]);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                inputStream = httpURLConnection.getInputStream();

                bitmap = BitmapFactory.decodeStream(inputStream);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            String result = "";

            URL url;

            HttpURLConnection httpURLConnection;

            InputStream inputStream;

            InputStreamReader inputStreamReader;

            try {

                url = new URL(strings[0]);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                inputStream = httpURLConnection.getInputStream();

                inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();

                while (data != -1) {

                    result += (char) data;

                    data = inputStreamReader.read();

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    public void loading(View view) {
        editText.setVisibility(View.INVISIBLE);
        btnLoading.setVisibility(View.INVISIBLE);
        linearLayoutMenu.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.VISIBLE);


        City = editText.getText().toString();

        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + City + "&units=metric&appid=" + Key;

        DownloadTask downloadTask = new DownloadTask();

        try {

            String result = "abc";

            result = downloadTask.execute(url).get();

            JSONObject jsonObject = new JSONObject(result);

            JSONObject main = jsonObject.getJSONObject("main");

            String temp = main.getString("temp");

            String humidity = main.getString("humidity");

            String feel_like = main.getString("feels_like");

            String visibility = jsonObject.getString("visibility");

            nameIcon = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");

            Long time = jsonObject.getLong("dt");

            String sTime = new SimpleDateFormat("dd-M-yyyy hh:mm:ss", Locale.ENGLISH)
                    .format(new Date(time * 1000));

            txtCity.setText(City);

            txtValue.setText(temp + "°");

            txtValueVision.setText(visibility);

            txtValueHumidity.setText(humidity);

            txtValueFeelLike.setText(feel_like);

            txtTime.setText(sTime);

            DownloadImage downloadImage = new DownloadImage();

            String urlIcon = " https://openweathermap.org/img/wn/" + nameIcon + "@2x.png";

            Bitmap bitmap = downloadImage.execute(urlIcon).get();

            imgIcon.setImageBitmap(bitmap);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        onLoadingSwipeRefresh("");

        errorLayout = findViewById(R.id.errorLayout);
        errorImage = findViewById(R.id.errorImage);
        errorTitle = findViewById(R.id.errorTitle);
        errorMessage = findViewById(R.id.errorMessage);
        btnRetry = findViewById(R.id.btnRetry);


        editText = findViewById(R.id.edt_input);

        txtCity = findViewById(R.id.txtCity);

        txtTime = findViewById(R.id.txtTime);

        txtValue = findViewById(R.id.txtValue);

        txtValueFeelLike = findViewById(R.id.txtValueFeelLike);

        txtValueHumidity = findViewById(R.id.txtValueHumidity);

        txtValueVision = findViewById(R.id.txtValueVision);

        imgIcon = findViewById(R.id.imgIcon);

        btnLoading = findViewById(R.id.btnLoading);

        button_1 = findViewById(R.id.button_1);

        button_2 = findViewById(R.id.button_2);

        relativeLayout = findViewById(R.id.rlWeather);

        relativeLayoutMain = findViewById(R.id.rlMain_Ac);

        linearLayoutMenu = findViewById(R.id.menu);



        View.OnClickListener oclBtnN = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.INVISIBLE);
                editText.setVisibility(View.INVISIBLE);
                btnLoading.setVisibility(View.INVISIBLE);


            }
        };
        View.OnClickListener oclBtnW = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setVisibility(View.INVISIBLE);
                editText.setVisibility(View.VISIBLE);
                btnLoading.setVisibility(View.VISIBLE);

            }
        };
        button_2.setOnClickListener(oclBtnN);

        button_1.setOnClickListener(oclBtnW);

    }
    public void LoadJson(final String keyword){

        errorLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        String country = Utils.getCountry();
        String language = Utils.getLanguage();

        Call<News> call;

        if (keyword.length() > 0 ){
            call = apiInterface.getNewsSearch(keyword, language, "publishedAt", API_KEY);
        } else {
            call = apiInterface.getNews(country, API_KEY);
        }

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful() && response.body().getArticle() != null){

                    if (!articles.isEmpty()){
                        articles.clear();
                    }

                    articles = response.body().getArticle();
                    adapter = new Adapter(articles, MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    initListener();


                    swipeRefreshLayout.setRefreshing(false);


                } else {

                    swipeRefreshLayout.setRefreshing(false);

                    String errorCode;
                    switch (response.code()) {
                        case 404:
                            errorCode = "404 not found";
                            break;
                        case 500:
                            errorCode = "500 server broken";
                            break;
                        default:
                            errorCode = "unknown error";
                            break;
                    }

                    showErrorMessage(
                            R.drawable.no_result,
                            "No Result",
                            "Please Try Again!\n"+
                                    errorCode);

                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                showErrorMessage(
                        R.drawable.oops,
                        "Oops..",
                        "Network failure, Please Try Again\n"+
                                t.toString());
            }
        });

    }



    private void initListener(){

        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ImageView imageView = view.findViewById(R.id.img);
                Intent intent = new Intent(MainActivity.this, NewsDetailActivity.class);

                Article article = articles.get(position);
                intent.putExtra("url", article.getUrl());
                intent.putExtra("title", article.getTitle());
                intent.putExtra("img",  article.getUrlToImage());
                intent.putExtra("date",  article.getPublishedAt());
                intent.putExtra("source",  article.getSource().getName());
                intent.putExtra("author",  article.getAuthor());

                Pair<View, String> pair = Pair.create((View)imageView, ViewCompat.getTransitionName(imageView));
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        MainActivity.this,
                        pair
                );


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    startActivity(intent, optionsCompat.toBundle());
                }else {
                    startActivity(intent);
                }

            }
        });

    }



    @Override
    public void onRefresh() {
        LoadJson("");
    }

    private void onLoadingSwipeRefresh(final String keyword){

        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        LoadJson(keyword);
                    }
                }
        );

    }

    private void showErrorMessage(int imageView, String title, String message){

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
        }

        errorImage.setImageResource(imageView);
        errorTitle.setText(title);
        errorMessage.setText(message);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoadingSwipeRefresh("");
            }
        });

    }
}