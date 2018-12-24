package com.example.air.locationalarm;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DBVersion = 1;
    private static final String DBName = "Reminders";
    private static final String TableName = "Reminder";
    private static final String ID = "id";
    private static final String Col_Title = "Title";
    private static final String Col_Detail= "Detail"; //correct option
    //private static final String Long = "opta"; //option a
    //private static final String Lat = "optb"; //option b
    public DatabaseHelper(Context context) {
        super(context, DBName, null, DBVersion);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL = "CREATE TABLE IF NOT EXISTS " +TableName+ " ( "
                +ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +Col_Title+
                " TEXT, " + Col_Detail + " TEXT);";
        db.execSQL(SQL);
    }
    public void addReminder(Reminder reminder){
        SQLiteDatabase dbase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_Title, reminder.getTitle());
        contentValues.put(Col_Detail, reminder.getDetail());
        dbase.insert(TableName, null,contentValues);
        dbase.close();
    }
    public List<Reminder> getReminders() {
        List<Reminder> reminderList = new ArrayList<Reminder>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TableName;
        SQLiteDatabase dbase=this.getReadableDatabase();
        Cursor cursor = dbase.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = new Reminder();
                reminder.setID(cursor.getInt(0));
                reminder.setTitle(cursor.getString(1));
                reminder.setDetail(cursor.getString(2));
                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }
        // return quest list
        return reminderList;
    }
    public Reminder getSaved(int id){
        SQLiteDatabase dbase = this.getReadableDatabase();
        Reminder reminder = null;
        String SQL = "Select * From " +TableName+ " Where " +ID+ " = " +id+ ";";
        Cursor cursor = dbase.rawQuery(SQL,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        if(cursor != null && cursor.getCount()>0){
            reminder = new Reminder(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
        }
        return reminder;
    }
    public void editReminder(Reminder r){
        SQLiteDatabase dbase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String where = ID+ "=?";
        String [] whereArgs = new String[] {String.valueOf(r.getID())};
        cv.put(Col_Title, r.getTitle());
        cv.put(Col_Detail,r.getDetail());
        dbase.update(TableName, cv, where, whereArgs);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TableName);
        onCreate(db);
    }

}
