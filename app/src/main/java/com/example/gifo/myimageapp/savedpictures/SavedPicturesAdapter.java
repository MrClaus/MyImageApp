package com.example.gifo.myimageapp.savedpictures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import static com.example.gifo.myimageapp.fileprovider.GenericFileProvider.MY_APP_DATA_DERICTORY;

/**
 * Created by gifo on 27.11.2019.
 */

public class SavedPicturesAdapter extends BaseAdapter {

    private Context mContext;
    private Integer mSizeImg;
    public File[] mThumbIds = null;

    // Чтобы памяти меньше ело
    private ArrayList<ImageView> imgViewList = new ArrayList<>();

    /*
     * Объявляем кастомный интерфейс для прослушки события клика
     * на каждый элемент списка адаптера
     */
    private SavedPicturesClickListener itemClickListener = null;
    public interface SavedPicturesClickListener {
        void onItemClick(View view, int position, File src);
    }

    public SavedPicturesAdapter(Context context) {
        mContext = context;
        init();
    }

    // Заполняем наш GridView данными
    private void init() {

        // Вычисляем размер плитки GridView
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width = display.getWidth();
        if (width < 720) mSizeImg = 64;
        else if (width < 1080) mSizeImg = 128;
        else if (width < 1440) mSizeImg = 256;
        else mSizeImg = 320;

        // Инициализируем список изображений (формата *.JPG) из каталога приложения
        String path = MY_APP_DATA_DERICTORY.substring(0, MY_APP_DATA_DERICTORY.length());
        File directory = new File(path);
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jpg");
            }
        });

        if (files != null) {
            mThumbIds = new File[files.length];
            for (int i = 0; i < files.length; i++)
                mThumbIds[i] = files[i];
        }
    }

    // Задаёт слушатель события клика на элементы адаптера
    public void setSavedPicturesClickListener(SavedPicturesClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getCount() {
        return (mThumbIds != null) ? mThumbIds.length : 0;
    }

    @Override
    public Object getItem(int position) {
        return (mThumbIds != null) ? mThumbIds[position] : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (position <= imgViewList.size() - 1) return imgViewList.get(position);
        else {
            final ImageView imageView = new ImageView(mContext);

            Bitmap bitmap = BitmapFactory.decodeFile(mThumbIds[position].getAbsolutePath());
            imageView.setImageBitmap(bitmap);

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(mSizeImg, mSizeImg));

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onItemClick(v, position, mThumbIds[position]);
                }
            });

            imgViewList.add(position, imageView);
            return imageView;
        }
    }
}
