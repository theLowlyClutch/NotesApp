package com.example.editablenotepad.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.editablenotepad.models.Note;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "notes_manager";
    private static final String TABLE_NAME = "notes";

    // Coloumn Names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_NOTE = "note";
    private static final String KEY_DATE = "date";

    // Coloumn Combinations
    private static final String[] COLS_ID_TITLE_NOTE = new String[] {KEY_ID,KEY_TITLE,KEY_NOTE,KEY_DATE};


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NAME + " ( "
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT"+", "
                + KEY_TITLE + " TEXT NOT NULL"+ ", "
                + KEY_NOTE + " TEXT"+ ", "
                + KEY_DATE + " TEXT NOT NULL"
                + ")";

        Log.d(TAG,CREATE_NOTES_TABLE);

        db.execSQL(CREATE_NOTES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String DROP_TABLE = "DROP TABLE IF EXISTS "+ TABLE_NAME;

        Log.d(TAG,DROP_TABLE);

        db.execSQL(DROP_TABLE);

        onCreate(db);

    }

    //CRUD OPERATIONS

    public void addNote(Note note) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_NOTE, note.getNote());
        values.put(KEY_DATE, getCurrentDateTime());

        db.insert(TABLE_NAME,null,values);
        db.close();
    }

    public void updateNote(Note noteToBeUpdated) {
        System.out.println("In DB Update: " + noteToBeUpdated.getNote());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, noteToBeUpdated.getId());
        values.put(KEY_TITLE, noteToBeUpdated.getTitle());
        values.put(KEY_NOTE, noteToBeUpdated.getNote());
        values.put(KEY_DATE, getCurrentDateTime());
        String whereClause = KEY_ID +"=?";
        String whereArgs[] = {String.valueOf(noteToBeUpdated.getId())};

        System.out.printf("contentValues: " + values.toString());
        db.update(TABLE_NAME, values, whereClause, whereArgs);

        db.close();
    }

    public void deleteNote(Note noteToBeDeleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = KEY_ID +"=?";
        String whereArgs[] = {String.valueOf(noteToBeDeleted.getId())};

//        System.out.printf("contentValues: " + values.toString());
        db.delete(TABLE_NAME, whereClause, whereArgs);

        db.close();
    }

    public Note getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.query(TABLE_NAME,COLS_ID_TITLE_NOTE,KEY_ID +"=?",new String[]{String.valueOf(id)},null,null,null,null);
        if (c != null) {
            c.moveToFirst();
        }
        db.close();

        Log.d(TAG,"Get Note Result "+ c.getString(0)+","+c.getString(1)+","+c.getString(2));
        Note note = new Note(Integer.parseInt(c.getString(0)),c.getString(1),c.getString(2));
        return note;
    }

    public List<Note> getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<Note> noteList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME,COLS_ID_TITLE_NOTE,null,null,null,null,null);


        if (cursor!= null && cursor.moveToFirst()) {

            do {
                Note note = new Note();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setTitle(cursor.getString(1));
                note.setNote(cursor.getString(2));
                note.setDate(cursor.getString(3));
                noteList.add(note);

            } while (cursor.moveToNext());


        }
        db.close();
        return noteList;

    }

    private String getCurrentDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date();
        return formatter.format(date);
    }
}