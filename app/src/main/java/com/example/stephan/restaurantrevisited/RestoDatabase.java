package com.example.stephan.restaurantrevisited;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.PreparedStatement;

/**
 * Created by Stephan on 22-11-2017.
 */

public class RestoDatabase extends SQLiteOpenHelper {
    private static RestoDatabase instance;
    // Databse Info
    private static  String DATABASE_NAME = "RestoDatabase";
    private static  Integer DATABASE_VERSION = 1;

    //TABLE NAME
    private static  String TABLE_NAME = "resto";

    //
    private static  String KEY_SQL_ID = "_id";
    private static  String KEY_SQL_NAME = "name";
    private static  String KEY_SQL_PRICE = "price";
    private static  String KEY_SQL_COUNT = "count";
    public Context context;
    public SQLiteDatabase db;
    private Cursor cursor;



    private RestoDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableTodo = "CREATE TABLE " + TABLE_NAME +
                "(" +
                KEY_SQL_ID + " INTEGER PRIMARY KEY, " +
                KEY_SQL_NAME + " TEXT, " +
                KEY_SQL_PRICE + " INTEGER, " +
                KEY_SQL_COUNT + " INTEGER DEFAULT 0"+
                ")";
        sqLiteDatabase.execSQL(createTableTodo);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i != i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }
    public static RestoDatabase getInstance(Context context) {
        if (instance != null) {
            return instance;
        } else {
            return instance = new RestoDatabase(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
    }

    public void addItem(String name){
        db = instance.getWritableDatabase();
        String valueToIncrementBy = "1";
        Log.d(" ordernamekopen", name);

        String[] bindingArgs = new String[] { valueToIncrementBy, name};
        db.execSQL("UPDATE " + TABLE_NAME +
                " SET " + KEY_SQL_COUNT + "= " + KEY_SQL_COUNT + " + ?" +
                " WHERE " + KEY_SQL_NAME + "= ?",bindingArgs);
    }
    public void removeItem(String name){
        db = instance.getWritableDatabase();

        String removeItemsql = "UPDATE " + TABLE_NAME +
                " SET " + KEY_SQL_COUNT + "= 0 " +
                " WHERE " + KEY_SQL_NAME + "= " + name;
        db.execSQL(removeItemsql);

    }

    public void clear(){
        db = instance.getWritableDatabase();
        String clearcountsql =  "UPDATE " + TABLE_NAME +
                                " SET " + KEY_SQL_COUNT + "= 0 " +
                                " WHERE  NOT " + KEY_SQL_COUNT + " = 0";
        Log.d("clearcount", clearcountsql);
        db.execSQL(clearcountsql);
    }

    public Cursor selectAll(){

        String selectall = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_SQL_COUNT + " > 0";
        SQLiteDatabase db = instance.getWritableDatabase();
        cursor = db.rawQuery(selectall, null);
//        if (cursor.moveToFirst()) {
//            do {
//                Log.d("databasecursor", cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));
//            } while (cursor.moveToNext());
//        }
        return cursor;
    }
    public Cursor totalPrice() {
        String totalPricesql = "SELECT SUM( " + KEY_SQL_COUNT + "*" + KEY_SQL_PRICE + ") AS TOTAL FROM " + TABLE_NAME;
        Log.d("tijdelijk", totalPricesql);
        cursor = db.rawQuery(totalPricesql, null);
        return cursor;
    }

    public void databasefill(){
        db = instance.getWritableDatabase();
        RequestQueue queue = Volley.newRequestQueue(context);

        final String url = "https://resto.mprog.nl/menu?category";

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray categories  = response.getJSONArray("items");
                            Log.d("jsonarray 50", response.getJSONArray("items").toString());
                            for (int i = 0; i < categories.length(); i++) {
                                JSONObject subitem = categories.getJSONObject(i);
                                Integer price = subitem.getInt("price");
                                Integer id = subitem.getInt("id");
                                String name = subitem.getString("name");
                                ContentValues data = new ContentValues();
                                data.put(KEY_SQL_ID, id);
                                data.put(KEY_SQL_NAME, name);
                                data.put(KEY_SQL_PRICE, price);
                                db.insert(TABLE_NAME, null, data);
                                Log.d("sqldata", data.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }
}
