package com.liverpoolfaithful.app.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class BookmarkSaver extends SQLiteOpenHelper {



    private static final String DATABASE_NAME = "sourov.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "bookmark_library";
    private static final String COLUMN_ID = "_id";

    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CAT_NAME = "cat_name";
    private static final String COLUMN_IMAGE_LINK = "image_link";
    private static final String COLUMN_POST_ID = "post_id";
    private static final String COLUMN_SELF_URL = "self_url";
    private static final String COLUMN_DATE = "_date";
    private static final String COLUMN_CAT_ID = "cat_id";

    public BookmarkSaver(@Nullable Activity activity) {
        super(activity, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query =
                "CREATE TABLE " + TABLE_NAME +
                        " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TITLE + " TEXT, " +
                        COLUMN_CAT_NAME + " TEXT, " +
                        COLUMN_IMAGE_LINK + " TEXT, " +
                        COLUMN_POST_ID + " TEXT, " +
                        COLUMN_SELF_URL + " TEXT, " +
                        COLUMN_DATE + " TEXT, " +
                        COLUMN_CAT_ID + " TEXT);";

        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public String addBookmark(String title,String catName,  String id, String selfUrl,  String feature_image,String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_CAT_NAME, catName);
        cv.put(COLUMN_POST_ID, id);
        cv.put(COLUMN_SELF_URL, selfUrl);;
        cv.put(COLUMN_IMAGE_LINK, feature_image);
        cv.put(COLUMN_DATE, date);

        long result= db.insert(TABLE_NAME,null,cv);



        String msg;
        if (result==-1){
            msg = "failed to add to bookmark";
        }else {
            msg = "successfully added to bookmark";
        }

        return  msg;
    }

    public Cursor readAllBookmark(){
        String query = "SELECT * FROM "+ TABLE_NAME;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor= null;
        if (database!=null){
            cursor = database.rawQuery(query,null);
        }
        return cursor;
    }

    public boolean IfBookmarkExists(String id){
        boolean exists = false;
        String query = "SELECT * FROM "+ TABLE_NAME+ " WHERE " + COLUMN_POST_ID+" = " + "'" + id + "'";
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor;
        if (database!=null){
            cursor = database.rawQuery(query,null);
            if(cursor.moveToFirst()){
                exists = true;
            }else{
                exists = false;
            }

        }
        return exists;

    }

    public String deleteBookmarkPost(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result= db.delete(TABLE_NAME, COLUMN_POST_ID + "=" + id, null);
        String msg;
        if (result==-1){
          msg = "failed to delete from bookmark";
        }else {
            msg = "successfully deleted from bookmark";
        }

        return  msg;

    }
}