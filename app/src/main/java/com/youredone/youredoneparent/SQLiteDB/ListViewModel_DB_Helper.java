package com.youredone.youredoneparent.SQLiteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.youredone.youredoneparent.Model.ListViewModel;

import java.util.ArrayList;

public class ListViewModel_DB_Helper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "Child_database";
    private static final String TABLE_VALUE = "CHILD_INFO";

    public ListViewModel_DB_Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create ListViewModel table
        String CREATE_LISTVIEWMODEL_TABLE = "CREATE TABLE "+TABLE_VALUE+"( "
                +"ID INTEGER PRIMARY KEY AUTOINCREMENT, " +"name TEXT, "+"passcode TEXT, "+"status INTEGER DEFAULT 0, "+"child_id TEXT)";

        // create ListViewModel table
        db.execSQL(CREATE_LISTVIEWMODEL_TABLE);
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
    private static final String KEY_STATUS = "status";
    private static final String KEY_CHILD_ID = "child_id";

    private static final String[] COLUMNS = {KEY_ID,KEY_NAME,KEY_PASSCODE,KEY_STATUS,KEY_CHILD_ID};

    public void add_listViewModel(ListViewModel model){

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME,model.name);
        values.put(KEY_PASSCODE,model.passcode);
        values.put(KEY_STATUS,model.status);
        values.put(KEY_CHILD_ID,model.child_id);
        // 3. insert
        db.insert(TABLE_VALUE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public ListViewModel getModel(int index){

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
        ListViewModel temp = new ListViewModel();
        temp.name = cursor.getString(1);
        temp.passcode = cursor.getString(2);
        temp.status = cursor.getInt(3) > 0;
        temp.child_id = cursor.getString(4);

        db.close();
        // 5. return book
        return temp;
    }

    // Get All Books
    public ArrayList<ListViewModel> getAllModels() {
        ArrayList<ListViewModel> models=new ArrayList<ListViewModel>();

        // 1. build the query
        String query = "SELECT  * FROM "+TABLE_VALUE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        ListViewModel temp_value = null;
        if (cursor.moveToFirst()) {
            do {
                temp_value = new ListViewModel();
                temp_value.name = cursor.getString(1);
                temp_value.passcode = cursor.getString(2);
                temp_value.status = cursor.getInt(3) > 0;
                temp_value.child_id = cursor.getString(4);
                models.add(temp_value);
            } while (cursor.moveToNext());
        }
        db.close();

        // return books
        return models;
    }
    // Updating single book
    public int update_model(ListViewModel model) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        /* 2. create ContentValues to add key "column"/value */
        ContentValues values = new ContentValues();
        values.put(KEY_NAME,model.name);
        values.put(KEY_PASSCODE,model.passcode);
        values.put(KEY_STATUS,model.status);
        values.put(KEY_CHILD_ID,model.child_id);
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
    public void delete_model(ListViewModel model) {

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
