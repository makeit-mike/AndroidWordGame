package csc.game.word;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

public class DB extends SQLiteOpenHelper {

    public static final String DB_NAME = "aWordGame";

    public static final String TABLE_LEVEL_WORDS = "levelWords";
    public static final String ID = "id";
    public static final String WORD = "word";
    public static final String LEVEL = "level";
    public static final String CATEGORY = "status";
    public static final int CATEGORY_LVL = 0;
    public static final int CATEGORY_MAIN = 1;
    public static final int CATEGORY_EXTRA = 2;

    private Context context;
    private SQLiteDatabase db;

    public DB(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
    }

    public void restart() {
        context.deleteDatabase(DB_NAME);
    }

    public void writeDB(){
        db = this.getWritableDatabase();
    }

    public void readDB(){
        db = this.getReadableDatabase();
    }

    public void closeDB(){
        if (db!=null)
            db.close();
    }

    public void insertData(SQLiteDatabase db, String word, int level, int category){
        ContentValues cv = new ContentValues();
        cv.put(WORD, word);
        cv.put(LEVEL, level);
        cv.put(CATEGORY, category);
        db.insert(TABLE_LEVEL_WORDS, null, cv);
    }

    public Cursor getLevelData(int level){
        Cursor cursor =
                db.rawQuery("select * from "+TABLE_LEVEL_WORDS+" where "+LEVEL+"="+level, null);
        return cursor;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "create table " + TABLE_LEVEL_WORDS + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WORD + " TEXT, " +
                        LEVEL + " INTEGER, " +
                        CATEGORY + " INTEGER " +
                        ")"
        );
        Log.d("mylog", "create");
        initDB(MainActivity.TOTAL_LEVEL, sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private void initDB(int levelCount, SQLiteDatabase db){
        ArrayList[] allWords = WordsForLevelsGenerator.findLevelRandomWords(levelCount, context);

        ArrayList lvlWords = allWords[0];
        ArrayList<ArrayList<String>> mainWords = allWords[1];
        ArrayList<ArrayList<String>> extraWords = allWords[2];

        for (int i=0; i<lvlWords.size(); i++){
            insertData(db, (String) lvlWords.get(i), i+1, CATEGORY_LVL);
            for (String str : mainWords.get(i)){
                insertData(db, str, i+1, CATEGORY_MAIN);
            }
            for (String str : extraWords.get(i)){
                insertData(db, str, i+1, CATEGORY_EXTRA);
            }
        }
        Log.d("mylog", "done");
    }
}
