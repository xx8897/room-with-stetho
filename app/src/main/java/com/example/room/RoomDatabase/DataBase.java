package com.example.room.RoomDatabase;


import android.content.Context;

//import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
//import androidx.room.migration.Migration;
//import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {MyData.class},version = 1,exportSchema = true)//資料綁定的Getter-Setter,資料庫版本,是否將資料導出至文件
public abstract class DataBase extends RoomDatabase {
    public static final String DB_NAME = "RecordData.db";//資料庫名稱
    private static volatile DataBase instance;

    public static synchronized DataBase getInstance(Context context){
        if(instance == null){
            instance = create(context);//創立新的資料庫
        }
        return instance;
    }

    private static DataBase create(final Context context){
        return Room.databaseBuilder(context,DataBase.class,DB_NAME).build();
    }
    public abstract DataUao getDataUao();//設置對外接口
}

