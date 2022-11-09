package com.example.task_03_01;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;

public class JSONHelper {
    //--    Класс для работы с  JSON
    private static final String FILE_NAME = "data.json";

    static boolean exportToJSON (Context context, ArrayList<Human> dataList) {
        //--    Экспорт в json
        Gson gson = new Gson();
        DataItems dataItems = new DataItems();
        dataItems.setHumans(dataList);
        String jsonString = gson.toJson(dataItems);

        FileHelper fileHelper = new FileHelper();

        return fileHelper.exportToFile(jsonString, FILE_NAME, context);

    }

    static ArrayList<Human> importFromJSON(Context context){
        //--    Импорт из json
        FileHelper fileHelper = new FileHelper();

        String jsonString = fileHelper.importFromFile(FILE_NAME, context);

        if (jsonString == null) return null;

        Gson gson = new Gson();

        DataItems dataItems = gson.fromJson(jsonString, DataItems.class);

        return dataItems.getHumans();

    }

    private static class DataItems{
        //--    Вспомогательный класс, для десериализации
        private ArrayList<Human> humans;

        ArrayList<Human> getHumans(){
            return humans;
        }

        void setHumans (ArrayList<Human> humans){
            this.humans = humans;
        }
    }
}
