package com.example.gifo.myimageapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import static com.example.gifo.myimageapp.fileprovider.GenericFileProvider.MY_APP_DATA_DERICTORY;

/**
 * Created by gifo on 27.11.2019.
 */

public class ImageActivity extends AppCompatActivity {

    private Context context;
    private AlertDialog.Builder mDialog;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        context = this;

        // Добавляем и показываем в экшен-баре стрелку 'назад'
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Добавляем диалоговое окно удаления изображения
        initImgRemoveDialog();

        setTitle("Просмотр");

        // Загружаем контент
        mUri = getIntent().getData();
        Bitmap image = null;
        try {
            image = MediaStore.Images.Media.getBitmap(getContentResolver(), mUri);
        } catch (IOException e) {}

        ImageView picView = findViewById(R.id.current_image);
        picView.setImageBitmap(image);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.icon_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) mDialog.show(); // клик по значку удаления
        else onBackPressed();                                       // клик по значку назад
        return true;
    }

    private void initImgRemoveDialog() {
        String title = "Удаление";
        String message = "Удалить безвозвратно данное изображение?";
        String button1String = "Да";
        String button2String = "Отмена";

        mDialog = new AlertDialog.Builder(context);
        mDialog.setTitle(title);
        mDialog.setMessage(message);
        mDialog.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(context, "Удаление...", Toast.LENGTH_LONG).show();

                String fileName = new File(mUri.getPath()).getName();
                File file = new File(MY_APP_DATA_DERICTORY, fileName);
                if (file.delete()) {
                    Toast.makeText(context, "Изображение удалено!", Toast.LENGTH_LONG).show();

                    // Открываем сохранёнку
                    Intent openSavedPicturesIntent = new Intent(getApplicationContext(), SavedPicturesActivity.class);
                    startActivity(openSavedPicturesIntent);
                }
                else Toast.makeText(context, "Ошибка удаления!", Toast.LENGTH_LONG).show();
            }
        });

        mDialog.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

            }
        });

        mDialog.setCancelable(true);
    }
}
