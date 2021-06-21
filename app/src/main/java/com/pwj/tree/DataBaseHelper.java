package com.pwj.tree;

import android.content.Context;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;

import com.pwj.helloya.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jun on 2018/6/14.
 */

public class DataBaseHelper extends SQLiteOpenHelper {


    private static String DB_NAME = "pwjcs.db";
    //private static String PACKAGENAME = "com.pwj.helloya";
    //private static String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/"
    //        + PACKAGENAME + "/databases";
    private SQLiteDatabase myDataBase;
    private final Context context;

    public DataBaseHelper(Context context){
        super(context, DB_NAME, null, 3);
        this.context = context;
    }


    public SQLiteDatabase importDatabase(){
        String dirPath = "/data/data/com.pwj.helloya/databases";
        File dir = new File(dirPath);
        if (!dir.exists()){
            dir.mkdirs();
        }

        File file = new File(dir, "pwjcs.db");
        try {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            InputStream is = context.getResources().openRawResource(R.raw.pwjcs);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while((len = is.read(buffer)) != -1){
                fos.write(buffer, 0, len);
            }
            fos.flush();
            fos.close();
            is.close();

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dirPath + "/" + "pwjcs.db", null);
        return sqLiteDatabase;

    }


    public SQLiteDatabase openDatabase() throws SQLException {
        myDataBase = importDatabase();
        Log.d("JUN", "db init sucess...");
        //Cursor cursor = myDataBase.rawQuery("select * from pwj_user limit 1,5",null);
        //cursor.moveToFirst();
        return myDataBase;
    }

    @Override
    public synchronized  void close(){
        if (myDataBase != null){
            myDataBase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
