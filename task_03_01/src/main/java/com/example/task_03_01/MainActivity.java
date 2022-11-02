package com.example.task_03_01;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ListView lvHumans;

    private static Bitmap pngMale, pngFemale;

    /** Цвет фона не выбранного элемента */
    private int nrmColor = Color.TRANSPARENT;
    /** Цвет фона выбранного элемента */
    private int slctColor = Color.rgb(0xE2,0xA7, 0x6F);
    /** Индекс выбранного элемента */
    private int curItem = -1;

    /** Ссылка на виджет текущего выбранного элемента списка */
    private View curView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //-- Читаем иконки для контактов, в зависимости от гендера
        AssetManager AM = this.getAssets();

        pngMale = getBitmapAsset("male.png", AM);
        pngFemale = getBitmapAsset("female.png", AM);

        //-- Создание коллекции псевдо случайных людей.
        ArrayList<Human> humans = new ArrayList<>();

        for (int i = 0; i < 50; i++)
        {
            //--  Псевдо случайный человек :)

            boolean gender = getRandomNumberInRange(0,1) == 0 ? true : false;
            Calendar birth = new GregorianCalendar(
                    getRandomNumberInRange(1980, 2010),
                    getRandomNumberInRange(0, 11),
                    getRandomNumberInRange(1, 31));

            humans.add(
                    new Human(
                            "Фамилия_" + (i < 10 ? "0" : "") + String.valueOf(i),
                            "Имя_" + (i < 10 ? "0" : "") + String.valueOf(i),
                            gender,
                            birth)
            );
        }

        this.lvHumans = (ListView) this.findViewById(R.id.lvHumans);

        //--    Создание адаптера Данных
        ArrayAdapter<Human> adapter = new ArrayAdapter<Human>(this, R.layout.human_item, R.id.tvFirstName, humans) {
            @Override
            public View getView (int position, View convertView, ViewGroup parent)
            {
                View view = super.getView(position, convertView, parent);

                //--    Получение ссылки на объект 'human'
                Human H = this.getItem(position);

                //--    Получение ссылок на виджеты, для отображения информации
                TextView tvLastName = (TextView) view.findViewById(R.id.tvLastName);
                TextView tvFirstName = (TextView) view.findViewById(R.id.tvFirstName);
                TextView tvBirthDay = (TextView) view.findViewById(R.id.tvBirthDay);

                ImageView ivIcon = (ImageView) view.findViewById(R.id.ivIcon);

                //--    Запись в виджеты информации
                tvLastName.setText(H.lastName);
                tvFirstName.setText(H.firstName);
                tvBirthDay.setText(H.getBirthDayString());

                if (H.gender) {
                    ivIcon.setImageBitmap(pngMale);
                } else {
                    ivIcon.setImageBitmap(pngFemale);
                }

                //--    Подсветка отмеченного элемента списка
                if (position == MainActivity.this.curItem)
                {
                    view.setBackgroundColor(MainActivity.this.slctColor);
                    MainActivity.this.curView = view;
                }
                else
                {
                    view.setBackgroundColor(MainActivity.this.nrmColor);
                }

                return view;
            }
        };

        //--    Назначение Адаптера Данных списку
        this.lvHumans.setAdapter(adapter);

        //--    Назначение обработчика события клика по элементу списка
        this.lvHumans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //--    Снимаем выделение с предыдущего выделеного элемента
                if (MainActivity.this.curItem != -1)
                {
                    MainActivity.this.curView.setBackgroundColor(MainActivity.this.nrmColor);
                }

                //--    Установка выделения на текущий элемент списка
                MainActivity.this.curItem = position;
                MainActivity.this.curView = view;
                MainActivity.this.curView.setBackgroundColor(MainActivity.this.slctColor);
            }
        });
    }


    private int getRandomNumberInRange (int min, int max)
    {
        if (min >= max)
        {
            throw new IllegalArgumentException("'max' must be grater than 'min'");
        }
        Random rnd = new Random();
        return rnd.nextInt(((max - min) + 1) + min);
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