package com.example.news_kursovoy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getNews();
    }

    void getNews(){
        NewsApiClient newsApiClient = new NewsApiClient("5263be172f6d45a8b2277c997986f3b0");
        newsApiClient.getTopHeadlines(
                new TopHeadlinesRequest.Builder()
                        .language("ru")
                        .build(),
                new NewsApiClient.ArticlesResponseCallback() {
                    @Override
                    public void onSuccess(ArticleResponse response) {
                       response.getArticles().forEach((a)->{
                           Log.i("Article", a.getTitle());
                       });

                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.i("GOT Failure", throwable.getMessage());

                    }
                }
        );
    }
}