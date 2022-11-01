package com.example.task_03_01;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static Bitmap pngMale, pngFemale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AssetManager AM = this.getAssets();

        pngMale = getBitmapAsset("male.png", AM);
        pngFemale = getBitmapAsset("female.png", AM);


    }

    private Bitmap getBitmapAsset (String fileName, AssetManager AM)
    {
        /** Получение любого файла изображения из папки 'assets' по имени */
        try
        {
            InputStream IS = AM.open(fileName);
            Bitmap png = BitmapFactory.decodeStream(IS);
            IS.close();
            return png;
        }
        catch (IOException ioe)
        {
            Log.d(TAG, "IOException : " + ioe.getMessage());
        }
        return null;
    }

}