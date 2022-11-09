package com.example.task_03_01;

import android.content.Context;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class FileHelper {
    //--    Класс для работы с файлами

    public boolean exportToFile(String content, String fileName, Context context){
        try
        {
            //-- Создание приватного файла приложения --------
            FileOutputStream FOS = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            //-- Запись данных -------------------------------
            String str = content;
            byte[] a = str.getBytes("UTF8");
            FOS.write(a, 0, a.length);
            FOS.close();
            System.out.println("FileHelper: export done!");
        }
        catch (FileNotFoundException fnfe)
        {
            //-- Генерируется методом openFileOutput ---------
            Toast.makeText(context, "FileNotFoundException : " + fnfe.getMessage(), Toast.LENGTH_SHORT).show();
            System.out.println("FileHelper: FNFE " + fnfe.getMessage());
        }
        catch (UnsupportedEncodingException uee)
        {
            //-- Генерируется методом getBytes ---------------
            Toast.makeText(context, "UnsupportedEncodingException : " + uee.getMessage(), Toast.LENGTH_SHORT).show();
            System.out.println("FileHelper: UEE " + uee.getMessage());
        }
        catch (IOException ioe)
        {
            //-- Генерируется методом write ------------------
            Toast.makeText(context, "IOException : " + ioe.getMessage(), Toast.LENGTH_SHORT).show();
            System.out.println("FileHelper: IOE " + ioe.getMessage());
        }
        return true;
    }
    
    public String importFromFile(String fileName, Context context){
        /*
         * Чтение данных из приватного файла приложения
         * -----------------------------------------------
         */

        String str = "";
        try
        {
            //-- Открытие приватного файла приложения --------
            FileInputStream FIS = context.openFileInput(fileName);
            //-- Чтение данных из файла ----------------------
            byte[] b = new byte[1024];
            int cnt = FIS.read(b, 0, b.length);
            FIS.close();
            str = new String(b, 0, cnt, "UTF8");
            //            Log.d(TAG, "Прочитано из файла : " + str);
            //Toast.makeText(context, "Прочитано из файла : " + str, Toast.LENGTH_SHORT).show();
            System.out.println("FileHelper: import done!");

        }
        catch (FileNotFoundException fnfe)
        {
            //-- Генерируется методом openFileInput ----------
            //            Log.d(TAG, "FileNotFoundException : " +
            //                    fnfe.getMessage());
            Toast.makeText(context, "FileNotFoundException : " + fnfe.getMessage(), Toast.LENGTH_SHORT).show();
            System.out.println("FileHelper: FNFE " + fnfe.getMessage());
            return null;
        }
        catch (IOException ioe)
        {
            //-- Генерируется методом read -------------------
            //            Log.d(TAG, "IOException : " + ioe.getMessage());
            Toast.makeText(context, "IOException : " + ioe.getMessage(), Toast.LENGTH_SHORT).show();
            System.out.println("FileHelper: IOE " + ioe.getMessage());
        }
        return str;
    }
}
