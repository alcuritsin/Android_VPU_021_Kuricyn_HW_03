package com.example.task_03_01;

import java.util.Calendar;

public class Human {
    //-- Class members -------------------------------
    /** Фамилия */
    public String firstName;
    /** Имя */
    public String lastName;
    /** Пол. true - мужской */
    public boolean gender;
    /** День рождения   */
    public Calendar birthDay;

    //-- Class methods -------------------------------
    public Human(String firstName, String lastName,
                 boolean gender, Calendar birthDay)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDay = birthDay;
    }
    /** Возвращает дату рождения в виде строки dd/mm/yyyy   */
    public String getBirthDayString()
    {
        String str = "";
        int day = this.birthDay.get(Calendar.DAY_OF_MONTH);
        str += ((day < 10)?"0":"") + day + "/";
        int mon = this.birthDay.get(Calendar.MONTH) + 1;
        str += ((mon < 10)?"0":"") + mon + "/";
        str += this.birthDay.get(Calendar.YEAR);
        return str;
    }

    public static Calendar makeCalendar(int day,
                                        int month, int year)
    {
        Calendar C = Calendar.getInstance();
        C.setTimeInMillis(0);
        C.set(Calendar.YEAR, year);
        C.set(Calendar.MONTH, month - 1);
        C.set(Calendar.DAY_OF_MONTH, day);
        return C;
    }
}
