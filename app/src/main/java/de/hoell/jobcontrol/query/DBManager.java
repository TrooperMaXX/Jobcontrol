package de.hoell.jobcontrol.query;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DBManager extends SQLiteOpenHelper {

    final static String DATABASE_NAME="Jobcontrol";
    public static final int DATABASE_VERSION = 1;

    public final static String TABLE_ARTSTAMM="artstamm";
    public final static String COLUMN_ARTNR="artnr";
    public final static String COLUMN_BESCHREIBUNG="beschreibung";
    public final static String COLUMN_EAN="artean";
    public final static String COLUMN_VK="artvk";


    public static final String TABLE_GERSTAMM = "gerstamm";
    public static final String COLUMN_GERNR = "gernr";
    public static final String COLUMN_FIRMA = "firma";
    public static final String COLUMN_STR = "str";
    public static final String COLUMN_ORT = "ort";
    public static final String COLUMN_GER = "ger";
    public static final String COLUMN_STANDORT = "standort";


    public final static String TABLE_LOHNART="lohnart";
    public final static String COLUMN_LOHNARTNR="artnr";
    public final static String COLUMN_BEZEICHNUNG="bez";
    public final static String COLUMN_WVT="wvt";


    // Database creation sql statement
    // Indexes should not be used on small tables. © Tutorials Point
    private static final String ART_CREATE =
            "CREATE TABLE " + TABLE_ARTSTAMM + "(" +
                    COLUMN_ARTNR + " TEXT PRIMARY KEY, " +
                    COLUMN_BESCHREIBUNG + " TEXT , " +
                    COLUMN_EAN + " TEXT ,"+
                    COLUMN_VK + " DOUBLE  );";
    private static final String GER_CREATE =
            "CREATE TABLE " + TABLE_GERSTAMM + "(" +
                    COLUMN_GERNR + " TEXT PRIMARY KEY, " +
                    COLUMN_GER + " TEXT ,"+
                    COLUMN_FIRMA + " TEXT , " +
                    COLUMN_STANDORT + " TEXT , " +
                    COLUMN_STR + " TEXT ,"+
                    COLUMN_ORT + " TEXT );";

    private static final String LOHN_CREATE =
            "CREATE TABLE " + TABLE_LOHNART + "(" +
                    COLUMN_LOHNARTNR + " INT PRIMARY KEY, " +
                    COLUMN_BEZEICHNUNG + " TEXT , " +
                    COLUMN_WVT + " INT );";

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("DATABASE_CREATE", ART_CREATE);
        db.execSQL(ART_CREATE);
        Log.e("DATABASE_CREATE", GER_CREATE);
        db.execSQL(GER_CREATE);
        Log.e("DATABASE_CREATE", LOHN_CREATE);
        db.execSQL(LOHN_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        /*sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTSTAMM + ";");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_GERSTAMM + ";");*/
        onCreate(sqLiteDatabase);
    }




    public static class FillArtDB extends AsyncTask<String, String, String> {

        private Context mContext;
        private File mCSV;
        private ProgressDialog pDialog;
        public FillArtDB (Context context,File csv){
            mContext = context;
            mCSV=csv;

        }
        /**
         * Before starting background thread Show Progress Bar Dialog
         * */


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Verarbeite Datenbank Daten");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setIndeterminate(true);
            pDialog.show();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... args) {



            SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();
            sdb.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTSTAMM + ";");
            sdb.execSQL(ART_CREATE);
            FileReader file = null;
            try {
                file = new FileReader(mCSV);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            BufferedReader buffer = new BufferedReader(file);
            String line = "";

            String columns = COLUMN_ARTNR + ", "+ COLUMN_BESCHREIBUNG + ", " +COLUMN_EAN + ", " +COLUMN_VK;
            String str1 = "INSERT INTO " + TABLE_ARTSTAMM + " (" + columns + ") values(";
            String str2 = ");";

            sdb.beginTransaction();

            try {
                while ((line = buffer.readLine()) != null) {
                    StringBuilder sb = new StringBuilder(str1);
                    String[] str = line.split(",");
                    sb.append("'" + str[0] + "','");
                    sb.append(str[1] + "','");
                    sb.append(str[2] + "','");
                    sb.append(str[3] + "'");

                    sb.append(str2);
                    // Log.e("INSERT: ",sb.toString());
                    sdb.execSQL(sb.toString());
                }
            } catch (IOException e) {
                return "false";
            }
            sdb.setTransactionSuccessful();
            sdb.endTransaction();




            return null;
        }


        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String succsess) {
            // dismiss the dialog after the file was downloaded
            mCSV.delete();
            Log.e("erfolgreich", "fillARTDB");
            pDialog.dismiss();

        }

    }




    public static class FillGerDB extends AsyncTask<String, String, String> {

        private Context mContext;
        private File mCSV;
        private ProgressDialog pDialog;
        public FillGerDB (Context context,File csv){
            mContext = context;
            mCSV=csv;

        }
        /**
         * Before starting background thread Show Progress Bar Dialog
         * */


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Verarbeite Datenbank Daten");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setIndeterminate(true);
            pDialog.show();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... args) {

            SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();
            sdb.execSQL("DROP TABLE IF EXISTS " + TABLE_GERSTAMM + ";");
            sdb.execSQL(GER_CREATE);

            FileReader file = null;
            try {
                file = new FileReader(mCSV);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            BufferedReader buffer = new BufferedReader(file);
            String line = "";

            String columns = COLUMN_GERNR + ", "+ COLUMN_STANDORT + ", " +COLUMN_FIRMA + ", " +COLUMN_STR + ", " +COLUMN_ORT + ", " +COLUMN_GER;
            String str1 = "INSERT INTO " + TABLE_GERSTAMM + " (" + columns + ") values(";
            String str2 = ");";

            sdb.beginTransaction();

            try {
                while ((line = buffer.readLine()) != null) {
                    StringBuilder sb = new StringBuilder(str1);
                    String[] str = line.split(",");
                    sb.append("'" + str[0] + "','");
                    sb.append(str[1] + "','");
                    sb.append(str[2] + "','");
                    sb.append(str[3] + "','");
                    sb.append(str[4] + "','");
                    sb.append(str[5] + "'");

                    sb.append(str2);
                    //Log.e("INSERT: ",sb.toString());
                    sdb.execSQL(sb.toString());
                }
            } catch (IOException e) {
                return "false";
            }
            sdb.setTransactionSuccessful();
            sdb.endTransaction();



            return "true";
        }


        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String succsess) {
            // dismiss the dialog after the file was downloaded
            mCSV.delete();
            Log.e("erfolgreich","fillAGERDB");
           pDialog.dismiss();

        }

    }

    public static class FillLohnDB extends AsyncTask<String, String, String> {

        private Context mContext;
        private File mCSV;
        private ProgressDialog pDialog;
        public FillLohnDB (Context context,File csv){
            mContext = context;
            mCSV=csv;

        }
        /**
         * Before starting background thread Show Progress Bar Dialog
         * */


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Verarbeite Datenbank Daten");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setIndeterminate(true);
            pDialog.show();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... args) {



            SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();
            sdb.execSQL("DROP TABLE IF EXISTS " + TABLE_LOHNART + ";");
            sdb.execSQL(LOHN_CREATE);
            FileReader file = null;
            try {
                file = new FileReader(mCSV);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            BufferedReader buffer = new BufferedReader(file);
            String line = "";

            String columns = COLUMN_LOHNARTNR + ", "+ COLUMN_BEZEICHNUNG+ ", " +COLUMN_WVT;
            String str1 = "INSERT INTO " + TABLE_LOHNART + " (" + columns + ") values(";
            String str2 = ");";

            sdb.beginTransaction();

            try {
                while ((line = buffer.readLine()) != null) {
                    StringBuilder sb = new StringBuilder(str1);
                    String[] str = line.split(",");
                    sb.append("'" + str[0] + "','");
                    sb.append(str[1] + "','");
                    sb.append(str[2] + "'");

                    sb.append(str2);
                    // Log.e("INSERT: ",sb.toString());
                    sdb.execSQL(sb.toString());
                }
            } catch (IOException e) {
                return "false";
            }
            sdb.setTransactionSuccessful();
            sdb.endTransaction();




            return null;
        }


        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String succsess) {
            // dismiss the dialog after the file was downloaded
            mCSV.delete();
            Log.e("erfolgreich", "fillLohnDB");
            pDialog.dismiss();

        }

    }


}