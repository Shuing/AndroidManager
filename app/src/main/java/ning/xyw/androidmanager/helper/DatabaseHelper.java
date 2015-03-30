package ning.xyw.androidmanager.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ning.xyw.androidmanager.App;
import ning.xyw.androidmanager.bean.AppBean;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper instance;
    private static final String DB_NAME = "applications";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "applications";
    private static final String FIELD_ID = "_id";
    private static final String FIELD_LABEL = "label";
    private static final String FIELD_PACKAGENAME = "packagename";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DatabaseHelper getInstance() {
        if (null == instance) {
            instance = new DatabaseHelper(App.getContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "Create table " + TABLE_NAME + "(_id integer primary key autoincrement,label text,packagename text);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public Cursor query(String packagename) {
        SQLiteDatabase db = this.getReadableDatabase();
        String where = FIELD_PACKAGENAME + "=?";
        String[] whereValue = {packagename};
        Cursor cursor = db.query(TABLE_NAME, null, where, whereValue, null, null, " _id desc");
        return cursor;
    }

    public long insert(AppBean appBean) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FIELD_LABEL, appBean.getLabel());
        cv.put(FIELD_PACKAGENAME, appBean.getPackagename());
        long row = db.insert(TABLE_NAME, null, cv);
        return row;
    }

    public void delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = FIELD_ID + "=?";
        String[] whereValue = {Integer.toString(id)};
        db.delete(TABLE_NAME, where, whereValue);
    }

    public void update(int id, AppBean appBean) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = FIELD_ID + "=?";
        String[] whereValue = {Integer.toString(id)};
        ContentValues cv = new ContentValues();
        cv.put(FIELD_LABEL, appBean.getLabel());
        cv.put(FIELD_PACKAGENAME, appBean.getPackagename());
        db.update(TABLE_NAME, cv, where, whereValue);
    }


}
