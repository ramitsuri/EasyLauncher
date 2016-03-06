package com.ramitsuri.biglauncher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramitsuri on 3/6/16.
 */
public class SQLHelper extends SQLiteOpenHelper {

    private static SQLHelper instance = null;

    private static final int DATABASE_VERSION =1 ;
    private static final String DATABASE_NAME = "applicationsData";
    private static final String TABLE_APPLICATIONS = "applications";

    private static final String KEY_APP_NAME="appName";
    private static final String KEY_PACKAGE_NAME="packageName";
    private static final String KEY_CLASS_NAME="className";
    private static final String KEY_VERSION_NAME ="versionName";
    private static final String KEY_VERSION_CODE ="versionCode";
    private static final String KEY_TAGS="tags";
    private static final String KEY_ICON="icon";



    private static DataSetObservable dataSetObservable = new DataSetObservable();

    public static SQLHelper getInstance(Context context){
        if(instance==null){
            instance =new SQLHelper(context.getApplicationContext());
        }
        return instance;
    }

    public SQLHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CLIPS_TABLE="CREATE TABLE "+TABLE_APPLICATIONS+"("+
                KEY_APP_NAME+" TEXT, "+
                KEY_PACKAGE_NAME +" TEXT, "+
                KEY_CLASS_NAME+" TEXT, "+
                KEY_VERSION_NAME +" TEXT, " +
                KEY_VERSION_CODE +" TEXT, " +
                KEY_TAGS+" TEXT, "+
                KEY_ICON+" TEXT)";
        db.execSQL(CREATE_CLIPS_TABLE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_APPLICATIONS);
        onCreate(db);
    }

    public void addApplications(ApplicationData applicationData){
        SQLiteDatabase db = this.getWritableDatabase();
        try{

            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_APP_NAME, applicationData.getAppName());
            contentValues.put(KEY_PACKAGE_NAME, applicationData.getPackageName());
            contentValues.put(KEY_CLASS_NAME, applicationData.getClassName());
            contentValues.put(KEY_VERSION_NAME, applicationData.getVersionName());
            contentValues.put(KEY_VERSION_CODE, applicationData.getVersionCode());
            contentValues.put(KEY_TAGS, applicationData.getTags());
            contentValues.put(KEY_ICON, applicationData.getIcon());

            db.insert(TABLE_APPLICATIONS, null, contentValues);
            dataSetObservable.notifyChanged();
        } catch (Exception e) {
            Log.e("Applcuation", "Exception when adding application", e);
        }
    }

    public List<ApplicationData> getAllByStarredFirst(){
        try {
            List<ApplicationData> listClip1=new ArrayList<>();
            List<ApplicationData> listClip2;
            String selectQuery="SELECT * FROM "+TABLE_APPLICATIONS ;
            SQLiteDatabase db=this.getReadableDatabase();
            Cursor cursor=db.rawQuery(selectQuery, null);

        } catch (Exception e) {
            Log.e("ClipSave", "Exception when getting all by starred clips", e);
            return new ArrayList<>();
        }
        return null;
    }

}
