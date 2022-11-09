package com.example.task_03_01;

import static android.content.ContentValues.TAG;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

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

    private static ArrayList<Human> humans = new ArrayList<Human>();

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

        //--    Инициализация 'Alert Dialog - edit human'
        initAlertDialog();

        //--    Привязываем 'ListView' - список сотрудников
        this.lvHumans = (ListView) this.findViewById(R.id.lvHumans);

        //Toast.makeText(this, humans.size(), Toast.LENGTH_SHORT).show();

        //--    Загружаем данные из файла приложения
        loadData();


        if (humans != null) {
            createAdapter();
        }

    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        //--    Добавление меню
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (menu instanceof MenuBuilder)
        {
            MenuBuilder m = (MenuBuilder) menu;
            //!!    Проблема с отображением иконок меню.
            m.setOptionalIconsVisible(true);
        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        //--    Обработка нажатий на элементы меню
        switch (item.getItemId())
        {
            case R.id.menuRemoveHuman:
                //--    Удаление сотрудника из списка
                if (curItem == -1) return false;

                Toast.makeText(this, String.valueOf(curItem), Toast.LENGTH_SHORT).show();

                humans.remove(curItem);
                curItem = -1;
                curView = null;
                curHuman = null;

                adapter.notifyDataSetChanged();
                uploadData();

                return true;

            case R.id.menuEditHuman:
                //--    Редактирование выделенного сотрудника
                if (curItem == -1) return false;

                initAlertDialogEdit();
                break;

            case R.id.menuAddHuman:
                //--    Добавление сотрудника в список
                initAlertDialogAdd();
                break;
        }


        ivGenderDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //--    Клик по битмапу меняет пол
                changeGender();
            }
        });

        alertDialog.show();

        return true;
    }

    private Bitmap getBitmapAsset (String fileName, AssetManager AM)
    {
        //--    Получение любого файла изображения из папки 'assets' по имени
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
        //--    Создание и инициализация 'Alert Dialog'

        //--    Создание объекта 'Builder'
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Black);

        //--    Формирование заголовка окна и его содержимого
        //builder.setTitle("Редактирование сотрудника");

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
                        //--    Режим редактирования
                        humans.set(curItem, newHuman);
                        break;
                    case "add":
                        //--    Режим добавления
                        if (humans == null) humans = new ArrayList<>();
                        humans.add(newHuman);
                        if (humans.size() == 1) createAdapter();
                        curItem = -1;
                        curHuman = null;
                        curView = null;
                        break;
                }

                //--    Увадомляем адаптер об изменениях.
                adapter.notifyDataSetChanged();

                //--    Сразу же записываем обновленный список в файл.
                uploadData();
            }
        });

        builder.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //--    При отмене ничего не делаем :)
            }
        });

        alertDialog = builder.create();

    }

    private void initAlertDialogAdd(){
        //--    Режим открытия диалогового окна на добавление
        dialogMode = "add";

        curItem = -1;
        curView = null;
        curHuman = new Human();

        //--    Инициализация полей в 'Alert Dialog'
        etFirstNameDialog.setText("");
        etLastNameDialog.setText("");
        ivGenderDialog.setImageBitmap(pngMale);
        tvGenderValueDialog.setText(R.string.genderMale);

        Calendar C = Calendar.getInstance();
        int year = C.get(Calendar.YEAR);
        int month = C.get(Calendar.MONTH);
        int day = C.get(Calendar.DAY_OF_MONTH);

        dpBirthDialog.updateDate(year, month, day);
    }

    private void initAlertDialogEdit(){
        //--    Режим открытия диалогового окна на редактирование
        dialogMode = "edit";

        //--    Инициализация полей в 'Alert Dialog'
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

        int year = curHuman.birthDay.get(Calendar.YEAR);
        int month = curHuman.birthDay.get(Calendar.MONTH);
        int day = curHuman.birthDay.get(Calendar.DAY_OF_MONTH);

        dpBirthDialog.updateDate(year, month, day);
    }

    private void changeGender(){
        //--    Смена пола в 'Alert Dialog' :)
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


    private void createAdapter(){
        //--    Создание адаптера Данных для списка сотрудников
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
    }

    public void uploadData(){
        //--    Выгрузка данных в файл
        boolean result = JSONHelper.exportToJSON(this, humans);

        if (result) {
            Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Не удалось сохранить данные", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadData(){
        //--    Загрузка данных из файла
        humans = JSONHelper.importFromJSON(this);

        if(humans !=null) {
            //createAdapter();
            //adapter.notifyDataSetChanged();
            Toast.makeText(this, "Данные востановлены", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Не удалось восстановить данные", Toast.LENGTH_SHORT).show();

            //-- Создание коллекции людей.
            humans = new ArrayList<Human>();

            //--    Так как загрузить данные не удалось (например это первый запуск)
            //--    Предлагается создать сотрудника
            initAlertDialogAdd();

            alertDialog.show();

            //-- Так можно запустить генератор псевдо случайных сотрудников, для тестирования
//            fillListRandom(2);
        }
    }

    private void fillListRandom(int countHuman){
        for (int i = 0; i < countHuman; i++) {
            //--  Псевдо случайный человек :)

            boolean gender = getRandomNumberInRange(0, 1) == 0 ? true : false;
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
    }

    private int getRandomNumberInRange (int min, int max)
    {
        //--    Генератор случайного целого числа.
        //  Был нужен для разработки. Помогал генерировать случайные данные.
        if (min >= max)
        {
            throw new IllegalArgumentException("'max' должен быть больше 'min'");
        }

        max -= min;
        return (int) (Math.random() * ++max) + min;
    }
}