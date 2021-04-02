package com.unipi.panapost.a13033sms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME ="MessageDB";
    public static final String TABLE_NAME ="Messages";
    private static final int DATABASE_VERSION = 1;
    private static final String ID="id";
    private static final String MESSAGE = "message";
    private static String b1 = "Φαρμακείο ή επίσκεψη στον γιατρό ή αιμοδοσία";
    private static String b2 = "Κατάστημα αγαθών πρώτης ανάγκης";
    private static String b3 = "Δημόσια υπηρεσία/Τράπεζα";
    private static String b4 = "Παροχή βοήθειας/Συνοδεία ανήλικων μαθητών";
    private static String b5 = "Μετάβαση σε τελετή κηδείας/Συνοδεία παιδιών σε γονέα";
    private static String b6 = "Άθληση/ Κίνηση με κατοικίδιο ζώo";
    private SQLiteDatabase db;
    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null    , DATABASE_VERSION);
        this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {//called when the database is created for the first time
        db.execSQL("create table "+TABLE_NAME+ "("+ID+" unique,"+MESSAGE+");");
        db.execSQL("insert into messages values ('"+1+"', '"+b1+"')");
        db.execSQL("insert into messages values ('"+2+"','"+b2+"')");
        db.execSQL("insert into messages values ('"+3+"','"+b3+"')");
        db.execSQL("insert into messages values ('"+4+"','"+b4+"')");
        db.execSQL("insert into messages values ('"+5+"','"+b5+"')");
        db.execSQL("insert into messages values ('"+6+"','"+b6+"')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//called when the database needs to be upgraded
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
        onCreate(db);
    }

    public void addmessage(model model){//method for adding a new message
        db = this.getWritableDatabase();//Create and/or open a database that will be used for reading and writing
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.ID, model.getId());
        contentValues.put(DBHelper.MESSAGE, model.getMessage());
        db.insert(TABLE_NAME, null, contentValues);
    }

    public Cursor getData() {//method for creating a cursor in order to get all data from db
        db = this.getWritableDatabase();//Create and/or open a database that will be used for reading and writing
        Cursor cursor = db.rawQuery("select * from Messages order by (id) asc",null);//query for getting all data order by ascending
        return cursor;
    }
    public List<model> getmessages(){//
        String sql  = "select * from " + TABLE_NAME + " order by(id) asc";//query for getting all data order by ascending
        SQLiteDatabase db =this.getWritableDatabase();
        List<model> messages = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            model obj = new model(cursor.getString(0),cursor.getString(1));
            messages.add(obj);
        }
        return messages;
    }

    public void updatemessage(model model){//method for updating
        db =this.getWritableDatabase();//Create and/or open a database that will be used for reading and writing
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.ID,model.getId());
        contentValues.put(DBHelper.MESSAGE,model.getMessage());
        db.update(TABLE_NAME,contentValues,ID + " = ?", new String[]{
                String.valueOf(model.getId())});
    }
    public  void deletemessage(String id){//method for deleting a message
        db =this.getWritableDatabase();
        db.delete(TABLE_NAME, ID + " = ? ", new String[]{String.valueOf(id)});
    }
}

