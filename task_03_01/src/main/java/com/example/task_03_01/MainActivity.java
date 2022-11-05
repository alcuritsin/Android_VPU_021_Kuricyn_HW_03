package com.example.task_03_01;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ListView lvHumans;

    private static Bitmap pngMale, pngFemale;

    private static AlertDialog alertDialog;

    private static EditText etFirstNameDialog, etLastNameDialog;
    private static ImageView ivGenderDialog;
    private static TextView tvGenderValueDialog;
    private static DatePicker dpBirthDialog;
    private String dialogMode = null;

    /** Цвет фона не выбранного элемента */
    private int nrmColor = Color.TRANSPARENT;
    /** Цвет фона выбранного элемента */
    private int slctColor = Color.rgb(0xE2,0xA7, 0x6F);
    /** Индекс выбранного элемента */
    private static int curItem = -1;

    /** Ссылка на виджет текущего выбранного элемента списка */
    private static View curView = null;

    private static Human curHuman = null;

    private static ArrayList<Human> humans;

    ArrayAdapter<Human> adapter;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-- Читаем иконки для контактов, в зависимости от гендера
        AssetManager AM = this.getAssets();

        pngMale = getBitmapAsset("male.png", AM);
        pngFemale = getBitmapAsset("female.png", AM);

        //-- Создание коллекции псевдо случайных людей.
        humans = new ArrayList<>();

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
        adapter = new ArrayAdapter<Human>(this, R.layout.human_item, R.id.tvFirstNameDialog, humans) {
            @Override
            public View getView (int position, View convertView, ViewGroup parent)
            {
                View view = super.getView(position, convertView, parent);

                //--    Получение ссылки на объект 'human'
                Human H = this.getItem(position);

                //--    Получение ссылок на виджеты, для отображения информации
                TextView tvLastName = (TextView) view.findViewById(R.id.tvLastNameDialog);
                TextView tvFirstName = (TextView) view.findViewById(R.id.tvFirstNameDialog);
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
                    MainActivity.this.curHuman = this.getItem(position);
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

        //----
        initAlertDialog();



    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        //-- Добавление меню
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (menu instanceof MenuBuilder)
        {
            MenuBuilder m = (MenuBuilder) menu;
            //!! Проблема с отображением иконок меню.
            m.setOptionalIconsVisible(true);
        }

        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int year = 0, month = 0, day = 0;

        switch (item.getItemId())
        {
            case R.id.menuRemoveHuman:
                if (curItem == -1) return false;

                Toast.makeText(this, String.valueOf(curItem), Toast.LENGTH_SHORT).show();

                humans.remove(curItem);
                curItem = -1;
                curView = null;
                curHuman = null;

                adapter.notifyDataSetChanged();

                return true;

            case R.id.menuEditHuman:
                if (curItem == -1) return false;
                //-- Режим открытия диалогового окна на редактирование
                dialogMode = "edit";

                curHuman = humans.get(curItem);

                etFirstNameDialog.setText(curHuman.firstName);
                etLastNameDialog.setText(curHuman.lastName);

                if (curHuman.gender)
                {
                    ivGenderDialog.setImageBitmap(pngMale);
                    tvGenderValueDialog.setText(R.string.genderMale);
                }
                else
                {
                    ivGenderDialog.setImageBitmap(pngFemale);
                    tvGenderValueDialog.setText(R.string.genderFemale);
                }

                ivGenderDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeGender();
                    }
                });

                year = curHuman.birthDay.get(Calendar.YEAR);
                month = curHuman.birthDay.get(Calendar.MONTH);
                day = curHuman.birthDay.get(Calendar.DAY_OF_MONTH);

                break;

            case R.id.menuAddHuman:
                //-- Режим открытия диалогового окна на добавление
                dialogMode = "add";

                curItem = -1;
                curView = null;
                curHuman = new Human();

                etFirstNameDialog.setText("");
                etLastNameDialog.setText("");
                ivGenderDialog.setImageBitmap(pngMale);
                tvGenderValueDialog.setText(R.string.genderMale);

                Calendar C = Calendar.getInstance();
                year = C.get(Calendar.YEAR);
                month = C.get(Calendar.MONTH);
                day = C.get(Calendar.DAY_OF_MONTH);

                break;
        }


        ivGenderDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeGender();
            }
        });

        dpBirthDialog.updateDate(year, month, day);

        alertDialog.show();

        return true;
    }



    private int getRandomNumberInRange (int min, int max)
    {
        if (min >= max)
        {
            throw new IllegalArgumentException("'max' must be grater than 'min'");
        }


//        Random rnd = new Random();
//        return rnd.nextInt(((max - min) + 1) + min);
        max -= min;
        return (int) (Math.random() * ++max) + min;
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

    private void initAlertDialog(){
        //--    Создание объекта 'Builder'
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Dialog);

        //--    Формирование заголовка окна и его содержимого
        builder.setTitle("Редактирование сотрудника");

        LayoutInflater inflater = this.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_human_content, null, false);

        etFirstNameDialog = (EditText) view.findViewById(R.id.etFirstNameDialog);
        etLastNameDialog = (EditText) view.findViewById(R.id.etLastNameDialog);
        ivGenderDialog = (ImageView) view.findViewById(R.id.ivGenderDialog);
        tvGenderValueDialog = (TextView) view.findViewById(R.id.tvGenderValueDialog);
        dpBirthDialog = (DatePicker) view.findViewById(R.id.dpBirthDialog);

        builder.setView(view);

        //--    Назначение кнопок
        builder.setPositiveButton("Приментить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                
                int year = dpBirthDialog.getYear();
                int month = dpBirthDialog.getMonth();
                int day = dpBirthDialog.getDayOfMonth();

                Human newHuman = new Human(
                        etFirstNameDialog.getText().toString(),
                        etLastNameDialog.getText().toString(),
                        getResources().getString(R.string.genderMale) == tvGenderValueDialog.getText(),
                        Human.makeCalendar(day, ++month, year)
                );

                switch (dialogMode)
                {
                    case "edit":
                        humans.set(curItem, newHuman);
                        break;
                    case "add":
                        humans.add(newHuman);
                        curItem = -1;
                        curHuman = null;
                        curView = null;
                        break;
                }
                
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialog = builder.create();

    }

    private void changeGender(){
        if (curHuman.gender)
        {
            curHuman.gender = false;
            ivGenderDialog.setImageBitmap(pngFemale);
            tvGenderValueDialog.setText(R.string.genderFemale);
        }
        else
        {
            curHuman.gender = true;
            ivGenderDialog.setImageBitmap(pngMale);
            tvGenderValueDialog.setText(R.string.genderMale);
        }
    }
    //TODO: Сериализация/десериализация списка в фай
    //TODO: Загрузка при старте
    //TODO: Добавить аннотации
    //TODO: Навести порядок в коде. Удалить комментарии 'debug'
}