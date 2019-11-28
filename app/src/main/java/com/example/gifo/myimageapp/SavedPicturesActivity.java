package com.example.gifo.myimageapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;

import com.example.gifo.myimageapp.savedpictures.SavedPicturesAdapter;

import java.io.File;

/**
 * Created by gifo on 27.11.2019.
 */

public class SavedPicturesActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_pictures);

        // Добавляем и показываем в экшен-баре стрелку 'назад'
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Сохраненное");

        context = this;

        GridView gridView = findViewById(R.id.grid_view);

        // Ставим на gridView кастомный адаптер с кастомным слушателем кликов
        SavedPicturesAdapter gridAdapter = new SavedPicturesAdapter(this);
        gridAdapter.setSavedPicturesClickListener(new SavedPicturesAdapter.SavedPicturesClickListener() {
            @Override
            public void onItemClick(View view, int position, File src) {
                Uri picUri = FileProvider.getUriForFile(context, getApplicationContext().getPackageName()
                             + ".provider", src);

                Intent openImageIntent = new Intent(getApplicationContext(), ImageActivity.class);
                openImageIntent.setData(picUri);
                startActivity(openImageIntent);
            }
        });

        // устанавливаем адаптер через экземпляр класса ImageAdapter
        gridView.setAdapter(gridAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        // Открываем главный экран (через интент - ввиду случайных возвратов на следующий экран)
        Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}
