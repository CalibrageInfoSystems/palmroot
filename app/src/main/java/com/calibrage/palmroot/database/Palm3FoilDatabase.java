package com.calibrage.palmroot.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.calibrage.palmroot.common.CommonConstants;
import com.calibrage.palmroot.common.CommonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

public class Palm3FoilDatabase extends SQLiteOpenHelper {

    public static final String LOG_TAG = Palm3FoilDatabase.class.getName();
    public final static int DATA_VERSION = 3;
    private final static String DATABASE_NAME = "3foilpalm.sqlite";
    public static String Lock = "dblock";
    private static Palm3FoilDatabase palm3FoilDatabase;
    private static String DB_PATH;
    private static SQLiteDatabase mSqLiteDatabase = null;
    private Context mContext;
    File rootDirectory;

    public Palm3FoilDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATA_VERSION);
        this.mContext = context;
        File dbDirectory = new File(CommonUtils.get3FFileRootPath() + "3F_Database");
        DB_PATH = dbDirectory.getAbsolutePath() + File.separator;
        Log.v("The Database Path", DB_PATH);
    }

    public static synchronized Palm3FoilDatabase getPalm3FoilDatabase(Context context) {
        synchronized (Lock) {
            if (palm3FoilDatabase == null) {
                palm3FoilDatabase = new Palm3FoilDatabase(context);
            }
            return palm3FoilDatabase;
        }

    }

    public static SQLiteDatabase openDataBaseNew() throws SQLException {
        // Open the database
        if (mSqLiteDatabase == null) {
            mSqLiteDatabase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        } else if (!mSqLiteDatabase.isOpen()) {
            mSqLiteDatabase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        }
        return mSqLiteDatabase;
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    /* create an empty database if data base is not existed */
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if (dbExist) {
            //do nothing - database already exist
        } else {
            try {
                copyDataBase();
                Log.v("dbcopied:::", "true");
            } catch (SQLiteException ex) {
                ex.printStackTrace();
                throw new Error("Error copying database");
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error("Error copying database" + e);
            }
            try {
                openDataBase();
            } catch (SQLiteException ex) {
                ex.printStackTrace();
                throw new Error("Error opening database");
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("Error opening database");
            }
        }

    }

    /* checking the data base is existed or not */
    /* return true if existed else return false */
    private boolean checkDataBase() {
        boolean dataBaseExisted = false;
        try {
            String check_Path = DB_PATH + DATABASE_NAME;
            mSqLiteDatabase = SQLiteDatabase.openDatabase(check_Path, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }
        return mSqLiteDatabase != null;
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private void copyDataBase() throws IOException {
        File dbDir = new File(DB_PATH);
        if (!dbDir.exists()) {
            dbDir.mkdir();

        }
        InputStream myInput = mContext.getAssets().open(DATABASE_NAME);
        OutputStream myOutput = new FileOutputStream(DB_PATH + DATABASE_NAME);
        copyFile(myInput, myOutput);

    }



    //FLOG_TRACKING......
    public boolean insertLatLong (double Latitude, double Longitude,String IsActive,String CreatedByUserId, String CreatedDate,String UpdatedByUserId,  String UpdatedDate, String IMEINumber, String ServerUpdatedStatus) {
//        SQLiteDatabase db = this.getWritableDatabase();

        try {
            openDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("UserId",CreatedByUserId);
            contentValues.put(DatabaseKeys.LATITUDE,Latitude);
            contentValues.put(DatabaseKeys.LONGITUDE,Longitude);
            contentValues.put("Address", "Testin");
            contentValues.put("LogDate",UpdatedDate);
            contentValues.put("ServerUpdatedStatus",0);

            mSqLiteDatabase.insert(DatabaseKeys.TABLE_Location_TRACKING_DETAILS, null, contentValues);
            Log.v("userdata","data for user"+contentValues);
        }catch (Exception e){
            Log.v("UserDetails","Data insert failed due to"+e);
        }

        return true;
    }
    public boolean insertLogDetails(String ClientName,String MobileNumber,String Location,String Details,float Latitude,float Longitude,String ServerUpdatedStatus ){
        try {
            openDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("ClientName",ClientName);
            contentValues.put("MobileNumber", MobileNumber);
            contentValues.put("Location",Location);
            contentValues.put("Details",Details);
            contentValues.put("Latitude",Latitude);
            contentValues.put("Longitude",Longitude);
            contentValues.put("CreatedByUserId",CommonConstants.USER_ID);
            contentValues.put("CreatedDate",CommonUtils.getcurrentDateTime(CommonConstants.DATE_FORMAT_DDMMYYYY_HHMMSS));
            contentValues.put("ServerUpdatedStatus",0);

            mSqLiteDatabase.insert(DatabaseKeys.TABLE_VisitLog, null, contentValues);
            Log.v("logdetails","Log Detaails are inserted sucessfully"+contentValues);
        }catch (Exception e){
            Log.v("logdetails","Log Detaails are not inserted"+e);
        }
        return true;
    }

    /* Open the database */
    public void openDataBase() throws SQLException {

        String check_Path = DB_PATH + DATABASE_NAME;
        if (mSqLiteDatabase != null) {
            mSqLiteDatabase.close();
            mSqLiteDatabase = null;
            mSqLiteDatabase = SQLiteDatabase.openDatabase(check_Path, null, SQLiteDatabase.OPEN_READWRITE);
        } else {
            mSqLiteDatabase = SQLiteDatabase.openDatabase(check_Path, null, SQLiteDatabase.OPEN_READWRITE);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void openDatabase() {
        String dbPath = mContext.getDatabasePath(DATABASE_NAME).getPath();
        if(mSqLiteDatabase != null && mSqLiteDatabase.isOpen()) {
            return;
        }
        mSqLiteDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
}