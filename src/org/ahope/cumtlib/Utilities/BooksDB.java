package org.ahope.cumtlib.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BooksDB extends SQLiteOpenHelper {  
    private final static String DATABASE_NAME = "books.db";  
    private final static int DATABASE_VERSION = 1;  
    public final static String TABLE_NAME = "books_table";  
    public final static String BOOK_ID = "book_id";  
    public final static String BOOK_NAME = "book_name";  
    public final static String BORROW_TIME = "borrow_time";
    public final static String BACK_TIME = "back_time";
    public final static String BORROW_TIMES = "borrow_times";
    public final static String CNO = "con";
  
    public BooksDB(Context context) {  
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }  
  
    public BooksDB(Context context, String name, CursorFactory factory,  
            int version) {  
        super(context, name, factory, version);  
        // TODO Auto-generated constructor stub  
    }  
  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
        // TODO Auto-generated method stub  
        String sql = "CREATE TABLE " + TABLE_NAME + "(" + BOOK_ID  
                + " INTEGER primary key autoincrement, " + BOOK_NAME + " text, "  
                + BORROW_TIME + " text, "+ BACK_TIME + " text, "+ BORROW_TIMES + " text, "+ CNO +" text);";  
        db.execSQL(sql);  
    }  
  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
        // TODO Auto-generated method stub  
        String sql = "DROP TABLE IF EXISTS" + TABLE_NAME;  
        db.execSQL(sql);  
        onCreate(db);  
    }  
  
    public Cursor select() {  
        SQLiteDatabase db = this.getReadableDatabase();  
        Cursor cursor = db  
                .query(TABLE_NAME, null, null, null, null, null, null);
        return cursor;  
    }  
  
    public long insert(String bookname, String borrow_time,String back_time,String borrow_times,String cno) {  
        SQLiteDatabase db = this.getWritableDatabase();  
        ContentValues cv = new ContentValues();  
        cv.put(BOOK_NAME, bookname);  
        cv.put(BORROW_TIME, borrow_time);
        cv.put(BACK_TIME, back_time);
        cv.put(BORROW_TIMES, borrow_times);
        cv.put(CNO, cno);
        long row = db.insert(TABLE_NAME, null, cv); 
        db.close();
        return row;  
    }
    
    public void update(int id, String bookname, String borrow_time,String back_time,String borrow_times,String cno) {  
        SQLiteDatabase db = this.getWritableDatabase();  
        String where = BOOK_ID + "=?";  
        String[] whereValue = { Integer.toString(id) };  
  
        ContentValues cv = new ContentValues();  
        cv.put(BOOK_NAME, bookname);  
        cv.put(BORROW_TIME, borrow_time);
        cv.put(BACK_TIME, back_time);
        cv.put(BORROW_TIMES, borrow_times);
        cv.put(CNO, cno);  
        db.update(TABLE_NAME, cv, where, whereValue);  
    }  
}  