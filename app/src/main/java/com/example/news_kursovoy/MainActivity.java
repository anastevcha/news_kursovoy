package com.example.news_kursovoy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import androidx.appcompat.widget.SearchView;


import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerView;
    List<Article> articleList = new ArrayList<>();
    NewsRecyclerAdapter adapter;
    LinearProgressIndicator progressIndicator;
    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7;
    SearchView searchView;
    private String selectedCountry = "ru"; // По умолчанию русский

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.news_recycler_view);
        progressIndicator = findViewById(R.id.progress_bar);
        searchView = findViewById(R.id.search_view);
        btn1 = findViewById(R.id.btn_1);
        btn2 = findViewById(R.id.btn_2);
        btn3 = findViewById(R.id.btn_3);
        btn4 = findViewById(R.id.btn_4);
        btn5 = findViewById(R.id.btn_5);
        btn6 = findViewById(R.id.btn_6);
        btn7 = findViewById(R.id.btn_7);

        Button selectCountryButton = findViewById(R.id.select_country_button);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);

        selectCountryButton.setOnClickListener(v -> {
            showCountryDialog(); // Вызов метода для отображения диалогового окна выбора страны
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getNews("GENERAL", query, selectedCountry);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        setupRecyclerView(selectedCountry);
        getNews("GENERAL", null, selectedCountry);
    }


    void setupRecyclerView(String selectedCountry) {
        this.selectedCountry = selectedCountry;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsRecyclerAdapter(articleList);
        recyclerView.setAdapter(adapter);
    }


    void changeInProgress(boolean show) {
        if (show)
            progressIndicator.setVisibility(View.VISIBLE);
        else
            progressIndicator.setVisibility(View.INVISIBLE);
    }

    void getNews(String category, String query, String selectedCountry) {
        if (selectedCountry == null || selectedCountry.isEmpty()) {

            return;
        }

        changeInProgress(true);
        NewsApiClient newsApiClient = new NewsApiClient("dec0c079f50649f78b215e6852f81a9d");
        String englishCategory = translateCategoryToEnglish(category);
        newsApiClient.getTopHeadlines(
                new TopHeadlinesRequest.Builder()
                        .country(selectedCountry)
                        .category(englishCategory)
                        .q(query)
                        .build(),
                new NewsApiClient.ArticlesResponseCallback() {
                    @Override
                    public void onSuccess(ArticleResponse response) {
                        runOnUiThread(() -> {
                            changeInProgress(false);
                            articleList = response.getArticles();
                            adapter.updateData(articleList);
                            adapter.notifyDataSetChanged();
                        });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.i("GOT Failure", throwable.getMessage());
                    }
                }
        );
    }



    public String translateCategory(String englishCategory) {
        String russianCategory = "";
        switch (englishCategory) {
            case "GENERAL":
                russianCategory = "Главное";
                break;
            case "BUSINESS":
                russianCategory = "Бизнес";
                break;
            case "ENTERTAINMENT":
                russianCategory = "Развлечения";
                break;
            case "HEALTH":
                russianCategory = "Здоровье";
                break;
            case "SCIENCE":
                russianCategory = "Наука";
                break;
            case "SPORTS":
                russianCategory = "Спорт";
                break;
            case "TECHNOLOGY":
                russianCategory = "Технологии";
                break;
            default:
                russianCategory = englishCategory; // если категория не найдена, возвращаем как есть
                break;
        }
        return russianCategory;
    }

    public String translateCategoryToEnglish(String russianCategory) {
        String englishCategory = "";
        switch (russianCategory) {
            case "Главное":
                englishCategory = "GENERAL";
                break;
            case "Бизнес":
                englishCategory = "BUSINESS";
                break;
            case "Развлечения":
                englishCategory = "ENTERTAINMENT";
                break;
            case "Здоровье":
                englishCategory = "HEALTH";
                break;
            case "Наука":
                englishCategory = "SCIENCE";
                break;
            case "Спорт":
                englishCategory = "SPORTS";
                break;
            case "Технологии":
                englishCategory = "TECHNOLOGY";
                break;
            default:
                englishCategory = russianCategory; // если категория не найдена, возвращаем как есть
                break;
        }
        return englishCategory;
    }

    private void showCountryDialog() {
        final String category = "GENERAL"; // Определение переменной category
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите страну");
        String[] countries = {"США", "Россия", "Бразилия", "Япония", "Франция"};
        builder.setItems(countries, (dialog, which) -> {
            String selectedCountry;
            switch (which) {
                case 0:
                    selectedCountry = "us";
                    break;
                case 1:
                    selectedCountry = "ru";
                    break;
                case 2:
                    selectedCountry = "br";
                    break;
                case 3:
                    selectedCountry = "jp";
                    break;
                case 4:
                    selectedCountry = "fr";
                    break;
                default:
                    selectedCountry = "";
                    break;
            }
            this.selectedCountry = selectedCountry;
            String query = getQueryForCountry(selectedCountry);
            getNews(category, query, selectedCountry);
            setupRecyclerView(selectedCountry);
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private String getQueryForCountry(String selectedCountry) {
        switch (selectedCountry) {
            case "США":
                return "country=us";
            case "Россия":
                return "country=ru";
            case "Бразилия":
                return "country=br";
            case "Япония":
                return "country=jp";
            case "Франция":
                return "country=fr";
            default:
                return "";
        }
    }


    public void onClick(View v) {
        Button btn = (Button) v;
        String englishCategory = btn.getText().toString();
        String category = translateCategory(englishCategory);

        getNews(category, null, selectedCountry);
        setupRecyclerView(selectedCountry);
    }
}