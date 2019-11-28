package com.example.gifo.myimageapp;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.gifo.myimageapp.fileprovider.GenericFileProvider;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    final int CAMERA_REQUEST = 1;
    final int GALLERY_REQUEST = 2;
    final int PIC_CROP_CAMERA = 3;
    final int PIC_CROP_GALLERY = 4;
    private Uri picUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Запрос разрешений на чтение и запись
        if (GenericFileProvider.hasWriteDataPermission(this) && GenericFileProvider.hasReadDataPermission(this)) {
            // Создание каталога приложения в случае, когда права уже имеются без запроса
            GenericFileProvider.appDataDirectory(this);
        }

        setTitle("Обрезка изображений");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (String perm : permissions) {
            if (perm.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED) {
                    GenericFileProvider.appDataDirectory(this); // создание каталога приложения
                } else finish();
            }
        }
    }

    public void onClick(View v) {
        try {
            String typeEvent = getResources().getResourceEntryName(v.getId());
            if (typeEvent.equals("clickPhoto")) {

                // Назначаем намерение на камеру
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // Задаём uri для сохранения результата съемки
                picUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName()
                        + ".provider", createImageFile());
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                cameraIntent.putExtra("return-data", true);

                // Запуск камеры
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

            } else if (typeEvent.equals("clickGallery") ) {

                // Назначаем намерение на галерею
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                // Открытие галереи
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            } else if (typeEvent.equals("clickSaved") ) {

                // Переход в папку сохранённых изображений
                Intent openSavedPicturesIntent = new Intent(getApplicationContext(), SavedPicturesActivity.class);
                startActivity(openSavedPicturesIntent);
            }
        } catch (ActivityNotFoundException e) {

            // Выводим сообщение об ошибке
            String errorMessage = "Ошибка приложения!";
            Toast toast = Toast
                    .makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /*
     * Вспомогательная функция
     * Создаёт файл изображения по пути каталога приложения, прописанному в file_paths.xml
     */
    private File createImageFile() {
        return new File(GenericFileProvider.MY_APP_DATA_DERICTORY,
                "IMG_" + String.valueOf(System.currentTimeMillis()) + ".JPG");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            // Возвращаемся от камеры
            if (requestCode == CAMERA_REQUEST) {
                performCrop(CAMERA_REQUEST);

            // Возвращаемся от галереи
            } else if (requestCode == GALLERY_REQUEST && data != null) {
                picUri = data.getData();
                performCrop(GALLERY_REQUEST);

            // Отправляемся на обрезание
            } else if(requestCode > 2) {

                // Получаем изображение
                Bundle extras = data.getExtras();
                Bitmap bmp = extras.getParcelable("data");

                // Сохраняем изображение
                try {
                    final ContentResolver resolver = this.getContentResolver();
                    if (requestCode == PIC_CROP_GALLERY) {
                        picUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName()
                                + ".provider", createImageFile());
                    }
                    OutputStream stream = resolver.openOutputStream(picUri);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream);
                    stream.close();
                } catch (IOException e) {}

                // Открываем сохранёнку
                Intent openSavedPicturesIntent = new Intent(getApplicationContext(), SavedPicturesActivity.class);
                startActivity(openSavedPicturesIntent);
            }
        }
    }

    private void performCrop(int typeCall){
        try {
            // Назначаем намерение на обрзку изображения
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);

            // Открытие активити обрезки в зависимости от источника вызова
            if (typeCall == CAMERA_REQUEST) startActivityForResult(cropIntent, PIC_CROP_CAMERA);
            if (typeCall == GALLERY_REQUEST) startActivityForResult(cropIntent, PIC_CROP_GALLERY);
        }
        catch(ActivityNotFoundException anfe){
            String errorMessage = "Ошибка приложения! Обрезка невозможна!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
