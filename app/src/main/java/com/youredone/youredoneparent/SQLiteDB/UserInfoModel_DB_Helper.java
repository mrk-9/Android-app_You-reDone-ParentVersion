package com.youredone.youredoneparent.SQLiteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.youredone.youredoneparent.Model.UserInfro;

import java.util.ArrayList;

/**
 * Created by win on 5/4/2016.
 */
public class UserInfoModel_DB_Helper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "UserInfo_database";
    private static final String TABLE_VALUE = "UserInfo_INFO";

    public UserInfoModel_DB_Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create ListViewModel table
        String CREATE_USERINFO_TABLE = "CREATE TABLE "+TABLE_VALUE+"( "
                +"ID INTEGER PRIMARY KEY AUTOINCREMENT, " +"name TEXT, "+"passcode TEXT, "+"password TEXT, "+"child_id TEXT, "+"total_time LONG)";

        // create ListViewModel table
        db.execSQL(CREATE_USERINFO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS books");

        // create fresh books table
        this.onCreate(db);
    }

    // ListViewModel Table Columns names
    private static final String KEY_ID = "ID";
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSCODE = "passcode";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_CHILD_ID = "child_id";
    private static final String KEY_TOTAL_TIME = "total_time";

    private static final String[] COLUMNS = {KEY_ID,KEY_NAME,KEY_PASSCODE,KEY_PASSWORD,KEY_CHILD_ID,KEY_TOTAL_TIME};

    public void add_UserInfoModel(UserInfro userInfro){

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME,userInfro.name);
        values.put(KEY_PASSCODE,userInfro.passcode);
        values.put(KEY_PASSWORD,userInfro.password);
        values.put(KEY_CHILD_ID,userInfro.child_id);
        values.put(KEY_TOTAL_TIME,userInfro.total_Time);
        // 3. insert
        db.insert(TABLE_VALUE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public UserInfro getUser(int index){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_VALUE, // a. table
                        COLUMNS, // b. column names
                        " ID = ?", // c. selections
                        new String[] { String.valueOf(index) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        UserInfro temp = new UserInfro();
        temp.name = cursor.getString(1);
        temp.passcode = cursor.getString(2);
        temp.password = cursor.getString(3);
        temp.child_id = cursor.getString(4);
        temp.total_Time = cursor.getLong(5);

        db.close();
        // 5. return book
        return temp;
    }

    // Get All Books
    public ArrayList<UserInfro> getAllUserInfos() {
        ArrayList<UserInfro> userInfos=new ArrayList<UserInfro>();

        // 1. build the query
        String query = "SELECT  * FROM "+TABLE_VALUE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        UserInfro temp_value = null;
        if (cursor.moveToFirst()) {
            do {
                temp_value = new UserInfro();
                temp_value.name = cursor.getString(1);
                temp_value.passcode = cursor.getString(2);
                temp_value.password = cursor.getString(3);
                temp_value.child_id = cursor.getString(4);
                temp_value.total_Time = cursor.getLong(5);

                userInfos.add(temp_value);
            } while (cursor.moveToNext());
        }
        db.close();

        // return books
        return userInfos;
    }
    // Updating single book
    public int update_model(UserInfro model) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        /* 2. create ContentValues to add key "column"/value */
        ContentValues values = new ContentValues();
        values.put(KEY_NAME,model.name);
        values.put(KEY_PASSCODE, model.passcode);
        values.put(KEY_PASSWORD, model.password);
        values.put(KEY_CHILD_ID, model.child_id);
        values.put(KEY_TOTAL_TIME, model.total_Time);
        // 3. updating row
        int i = db.update(TABLE_VALUE, //table
                values, // column/value
                KEY_NAME+" = ?", // selections
                new String[] { model.name}); //selection args
        // 4. close
        db.close();

        return i;

    }

    // Deleting single book
    public void delete_model(UserInfro model) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. delete
        db.delete(TABLE_VALUE,
                KEY_NAME+" = ?",
                new String[] { String.valueOf(model.name) });

        // 3. close
        db.close();
    }
    public void delete_all(){
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. delete
        db.delete(TABLE_VALUE,null,null);
        // 3. close
        db.close();
    }
}
