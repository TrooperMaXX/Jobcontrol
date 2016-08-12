package de.hoell.jobcontrol.query;


import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hoell.jobcontrol.session.SessionManager;

public class DBManager extends SQLiteOpenHelper {

    final static String DATABASE_NAME="Jobcontrol";
    public static final int DATABASE_VERSION = 1;

    public final static String TABLE_ARTSTAMM="artstamm";
    public final static String COLUMN_ARTNR="artnr";
    public final static String COLUMN_BESCHREIBUNG="beschreibung";
    public final static String COLUMN_EAN="artean";
    public final static String COLUMN_VK="artvk";
    public final static String COLUMN_UEBERTRAGEN="uebertragen";


    public static final String TABLE_GERSTAMM = "gerstamm";
    public static final String COLUMN_GERNR = "gernr";
    public static final String COLUMN_FIRMA = "firma";
    public static final String COLUMN_STR = "str";
    public static final String COLUMN_ORT = "ort";
    public static final String COLUMN_GER = "ger";
    public static final String COLUMN_STANDORT = "standort";
    public static final String COLUMN_ZANZ = "zaehleranz";



    public final static String TABLE_LOHNART="lohnart";
    public final static String COLUMN_REIHENFOLGEID="reihenfolgeID";
    public final static String COLUMN_LOHNARTNR="artnr";
    public final static String COLUMN_BEZEICHNUNG="bez";
    public final static String COLUMN_WVT="wvt";

    public final static String COLUMN_TIMESTAMP="timestamp";
    public final static String TABLE_POSITIONEN="positionen";
    private static final String COLUMN_POSID = "posid";
    private static final String COLUMN_SCHEINID = "scheinid";
    private static final String COLUMN_POSART = "posart";
    private static final String COLUMN_DATUM = "datum";
    private static final String COLUMN_TECHNR = "technr";
    private static final String COLUMN_MENGE_AW = "mengeaw";
    private static final String COLUMN_WEGAW = "wegaw";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_PRUEFEN = "pruefen";
    public final static String COLUMN_SYNCID="syncid";

    public final static String TABLE_ZAEHLER="zaehler";
    private static final String COLUMN_ZAEHLERID = "zaehlerid";
    public final static String COLUMN_Z1="z1";
    public final static String COLUMN_Z2="z2";

    public final static String TABLE_VDE="vde";
    private static final String COLUMN_VDEID = "vdeid";
    private static final String COLUMN_RPE = "rpe";
    private static final String COLUMN_RISO = "riso";
    private static final String COLUMN_LEAK = "leak";

    public final static String TABLE_UNTERSCHRIFT="unterschrift";
    private static final String COLUMN_UNTERSCHRIFTID = "unterschriftid";
    private static final String COLUMN_KLARNAME = "klarname";
    public final static String COLUMN_UNTERSCHRIFT="unterschrift";

    private static final String TABLE_SCHEIN = "schein";
    private static final String COLUMN_SRN = "srn";
    private static final String COLUMN_TICKETNR = "ticketnr";
    private static final String COLUMN_ERROR = "error";
    private static final String COLUMN_ANSPRECHPARTNER = "ansprechpartner";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_BEMERKUNG = "bemerkung";
    public final static String COLUMN_ABGESCHLOSSEN="abgeschlossen";
    // Database creation sql statement
    // Indexes should not be used on small tables. © Tutorials Point
    private static final String ART_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ARTSTAMM + "(" +
                    "artid INTEGER PRIMARY KEY  , " +
                    COLUMN_ARTNR + " TEXT , " +
                    COLUMN_BESCHREIBUNG + " TEXT , " +
                    COLUMN_EAN + " TEXT ,"+
                    COLUMN_VK + " DOUBLE );";

    private static final String GER_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_GERSTAMM + "(" +
                    COLUMN_GERNR + " TEXT PRIMARY KEY, " +
                    COLUMN_GER + " TEXT ,"+
                    COLUMN_FIRMA + " TEXT , " +
                    COLUMN_STANDORT + " TEXT , " +
                    COLUMN_STR + " TEXT ,"+
                    COLUMN_ORT + " TEXT ,"+
                    COLUMN_Z1 + " INT , " +
                    COLUMN_Z2 + " INT ,"+
                    COLUMN_ZANZ + " INT );";

    private static final String LOHN_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_LOHNART + "(" +
                    COLUMN_REIHENFOLGEID + " INTEGER PRIMARY KEY, " +
                    COLUMN_LOHNARTNR + " INTEGER, " +
                    COLUMN_BEZEICHNUNG + " TEXT , " +
                    COLUMN_WVT + " INTEGER );";

    private static final String POSITIONEN_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_POSITIONEN + "(" +
                    COLUMN_POSID + " INTEGER  NOT NULL  PRIMARY KEY , " +
                    COLUMN_SCHEINID + " INTEGER , " +
                    COLUMN_POSART + " INTEGER , " +
                    COLUMN_ARTNR + " TEXT , " +
                    COLUMN_DATUM + " TEXT , " +
                    COLUMN_TECHNR + " INTEGER , " +
                    COLUMN_MENGE_AW + " INTEGER , " +
                    COLUMN_WEGAW + " INTEGER , " +
                    COLUMN_TEXT + " TEXT , "+
                    COLUMN_PRUEFEN + " BOOLEAN ," +
                    COLUMN_UEBERTRAGEN+ " BOOLEAN NOT NULL DEFAULT '0'," +
                    COLUMN_SYNCID + " VARCHAR(255)UNIQUE ," +
                    COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL   );";

    private static final String ZAEHLER_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ZAEHLER + "(" +
                    COLUMN_ZAEHLERID + " INTEGER  NOT NULL  PRIMARY KEY , " +
                    COLUMN_SCHEINID + " INTEGER UNIQUE, " +
                    COLUMN_Z1 + " INTEGER , " +
                    COLUMN_Z2 + " INTEGER ," +
                    COLUMN_UEBERTRAGEN+ " BOOLEAN NOT NULL DEFAULT '0'," +
                    COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL   );";

    private static final String VDE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_VDE + "(" +
                    COLUMN_VDEID + " INTEGER  NOT NULL  PRIMARY KEY , " +
                    COLUMN_SCHEINID + " INTEGER UNIQUE, " +
                    COLUMN_RPE + " DOUBLE , " +
                    COLUMN_RISO + " DOUBLE , " +
                    COLUMN_LEAK + " DOUBLE," +
                    COLUMN_UEBERTRAGEN+ " BOOLEAN NOT NULL DEFAULT '0' ," +
                    COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL   );";

    private static final String UNTERSCHRIFT_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_UNTERSCHRIFT + "(" +
                    COLUMN_UNTERSCHRIFTID + " INTEGER  NOT NULL  PRIMARY KEY , " +
                    COLUMN_SCHEINID + " INTEGER UNIQUE, " +
                    COLUMN_UNTERSCHRIFT+ " LONGBLOB ," +
                    COLUMN_UEBERTRAGEN+ " BOOLEAN NOT NULL DEFAULT '0' ," +
                    COLUMN_KLARNAME + " TEXT , " +
                    COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL   );";

    private static final String SCHEIN_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SCHEIN + "(" +
                    COLUMN_SCHEINID + " INTEGER PRIMARY KEY," +
                    COLUMN_SRN + " TEXT , " +
                    COLUMN_TICKETNR + " INTEGER , " +
                    COLUMN_TECHNR + " INTEGER , " +
                    COLUMN_ANSPRECHPARTNER + " TEXT , " +
                    COLUMN_ERROR + " TEXT , " +
                    COLUMN_EMAIL + " TEXT , " +
                    COLUMN_BEMERKUNG + " TEXT , " +
                    COLUMN_ABGESCHLOSSEN+ " BOOLEAN NOT NULL DEFAULT '0' ," +
                    COLUMN_UEBERTRAGEN+ " BOOLEAN NOT NULL DEFAULT '0'," +
                    COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL   );";

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("DATABASE_CREATE", ART_CREATE);
        this.getWritableDatabase().execSQL(ART_CREATE);
        Log.d("DATABASE_CREATE", GER_CREATE);
        this.getWritableDatabase().execSQL(GER_CREATE);
        Log.d("DATABASE_CREATE", LOHN_CREATE);
        this.getWritableDatabase().execSQL(LOHN_CREATE);
        Log.d("DATABASE_CREATE", POSITIONEN_CREATE);
        this.getWritableDatabase().execSQL(POSITIONEN_CREATE);
        Log.d("DATABASE_CREATE", ZAEHLER_CREATE);
        this.getWritableDatabase().execSQL(ZAEHLER_CREATE);
        Log.d("DATABASE_CREATE", VDE_CREATE);
        this.getWritableDatabase().execSQL(VDE_CREATE);
        Log.d("DATABASE_CREATE", UNTERSCHRIFT_CREATE);
        this.getWritableDatabase().execSQL(UNTERSCHRIFT_CREATE);
        Log.d("DATABASE_CREATE", SCHEIN_CREATE);
        this.getWritableDatabase().execSQL(SCHEIN_CREATE);
        this.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        /*sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTSTAMM + ";");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_GERSTAMM + ";");*/
        onCreate(sqLiteDatabase);
    }


    public static void dropAll( Context context){
        context.deleteDatabase(DATABASE_NAME);

        /*Log.d("DATABASE_DROP", "DROP TABLE " + TABLE_ARTSTAMM);
        sdb.execSQL("DROP TABLE " + TABLE_ARTSTAMM);
        Log.d("DATABASE_DROP", "DROP TABLE " + TABLE_GERSTAMM);
        sdb.execSQL("DROP TABLE " + TABLE_GERSTAMM);
        Log.d("DATABASE_DROP", "DROP TABLE " + TABLE_LOHNART);
        sdb.execSQL("DROP TABLE " + TABLE_LOHNART);
        Log.d("DATABASE_DROP", "DROP TABLE " + TABLE_SCHEIN);
        sdb.execSQL("DROP TABLE " + TABLE_SCHEIN);
        Log.d("DATABASE_DROP", "DROP TABLE " + TABLE_ZAEHLER);
        sdb.execSQL("DROP TABLE " + TABLE_ZAEHLER);
        Log.d("DATABASE_DROP", "DROP TABLE " + TABLE_VDE);
        sdb.execSQL("DROP TABLE " + TABLE_VDE);
        Log.d("DATABASE_DROP", "DROP TABLE " + TABLE_UNTERSCHRIFT);
        sdb.execSQL("DROP TABLE " + TABLE_UNTERSCHRIFT);*/

    }

    public static void UpdateSchein(Context mContext, int ScheinId, String bemerkung,String email){

        SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();

        String update= "UPDATE "+TABLE_SCHEIN+
                " SET "+
                COLUMN_BEMERKUNG+" = '"+bemerkung+ "'," +
                COLUMN_EMAIL+" = '"+email+ "'" +

                " WHERE "+COLUMN_SCHEINID +" = "+ScheinId;


        sdb.beginTransaction();

        Log.d("UPDATE: ", update);
        sdb.execSQL(update);
        sdb.setTransactionSuccessful();
        sdb.endTransaction();
        sdb.close();



    }

    public static void InsterUnterschrift(Context context, int scheinId, String klarname, String BLOB) {
        SQLiteDatabase sdb = new DBManager(context).getWritableDatabase();

        String update= "INSERT OR REPLACE INTO "+TABLE_UNTERSCHRIFT+
                " ( "+COLUMN_SCHEINID + ", " + COLUMN_UNTERSCHRIFT+ ", " + COLUMN_KLARNAME+") VALUES("
        +"'" + scheinId + "','" + BLOB + "','" + klarname + "');";

        sdb.beginTransaction();

        Log.d("UPDATE: ", update);
        sdb.execSQL(update);
        sdb.setTransactionSuccessful();
        sdb.endTransaction();
        sdb.close();

        new UebertrageDaten(context,scheinId).execute();
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
            pDialog.setCancelable(false);
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
                    // Log.d("INSERT: ",sb.toString());
                    sdb.execSQL(sb.toString());
                }
            } catch (IOException e) {
                return "false";
            }
            sdb.setTransactionSuccessful();
            sdb.endTransaction();
            sdb.close();



            return null;
        }


        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String succsess) {
            // dismiss the dialog after the file was downloaded
            mCSV.delete();
            Log.i("erfolgreich", "fillARTDB");
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
            pDialog.setCancelable(false);
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

            String columns = COLUMN_GERNR + ", "+ COLUMN_FIRMA + ", " +COLUMN_STR + ", " +COLUMN_ORT + ", " + COLUMN_GER+ ", " +COLUMN_STANDORT+ ", " +COLUMN_Z1 + ", " +COLUMN_Z2 + ", " +COLUMN_ZANZ;
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
                    sb.append(str[5] + "','");
                    sb.append(str[6] + "','");
                    sb.append(str[7] + "','");
                    sb.append(str[8] + "'");

                    sb.append(str2);
                    //Log.d("INSERT: ",sb.toString());
                    sdb.execSQL(sb.toString());
                }
            } catch (IOException e) {
                return "false";
            }
            sdb.setTransactionSuccessful();
            sdb.endTransaction();
            sdb.close();


            return "true";
        }


        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String succsess) {
            // dismiss the dialog after the file was downloaded
            mCSV.delete();
            Log.i("erfolgreich","fillAGERDB");
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
            pDialog.setCancelable(false);
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

            String columns = COLUMN_REIHENFOLGEID+ ", "+ COLUMN_LOHNARTNR + ", "+ COLUMN_BEZEICHNUNG+ ", " +COLUMN_WVT;
            String str1 = "INSERT INTO " + TABLE_LOHNART + " (" + columns + ") values(";
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
                    // Log.d("INSERT: ",sb.toString());
                    sdb.execSQL(sb.toString());
                }
            } catch (IOException e) {
                return "false";
            }
            sdb.setTransactionSuccessful();
            sdb.endTransaction();
            sdb.close();



            return null;
        }


        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String succsess) {
            // dismiss the dialog after the file was downloaded
            mCSV.delete();
            Log.i("erfolgreich", "fillLohnDB");
            pDialog.dismiss();

        }

    }

    public static class FillScheinDB extends AsyncTask<String, String, Integer> {

        private Context mContext;
        private Bundle mBundle;
        private int mScheinID;

        private ProgressDialog pDialog;

        public FillScheinDB (Context context,Bundle bundle,int scheinid){
            mContext = context;
            mBundle = bundle;
            mScheinID =scheinid;

        }
        /**
         * Before starting background thread Show Progress Bar Dialog
         * */


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Speichere Schein");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected Integer doInBackground(String... args) {

            Log.d("in background", "speichereSchein");
            SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();
            /*********************Schein**********************************************************************************/

            String columns = COLUMN_SCHEINID + ", " + COLUMN_SRN + ", " + COLUMN_TICKETNR + ", " + COLUMN_TECHNR + ", " + COLUMN_ANSPRECHPARTNER + ", " + COLUMN_ERROR + ", " + COLUMN_EMAIL + ", " + COLUMN_BEMERKUNG;
            String str1s = "INSERT OR REPLACE INTO " + TABLE_SCHEIN + " (" + columns + ") values(";


            sdb.beginTransaction();

            String Srn = mBundle.getString("Srn");
            String TicketID = "";
            if (mBundle.containsKey("TicketID")) {

                TicketID = mBundle.getString("TicketID");

            }
            SessionManager session = new SessionManager(mContext);
            String Technr = String.valueOf(session.getTechNum());
            String Error = mBundle.getString("Error");
            String Ansprechpartner = mBundle.getString("Name");

            String email = "";
            if (mBundle.containsKey("email")) {

                email = mBundle.getString("email");

            }
            String bemerkung = "";
            if (mBundle.containsKey("bemerkung")) {

                bemerkung = mBundle.getString("bemerkung");

            }
            String values = "'" + mScheinID + "','" + Srn + "','" + TicketID + "','" + Technr + "','" + Ansprechpartner + "',COALESCE((SELECT " + COLUMN_ERROR + " FROM " + TABLE_SCHEIN + " WHERE " + COLUMN_SCHEINID + " = " + mScheinID + "),'') || ' ;" + Error + "'," +
                    "'" + email + "'," +
                    "COALESCE((SELECT " + COLUMN_BEMERKUNG + " FROM " + TABLE_SCHEIN + " WHERE " + COLUMN_SCHEINID + " = " + mScheinID + "), '') || ' ;" + bemerkung + "');";

            String abfrage = str1s + values;
            Log.d("INSERT: ", abfrage);
            sdb.execSQL(abfrage);


            /*********************Positionen**********************************************************************************/

            String columnp = COLUMN_SCHEINID + ", " + COLUMN_POSART + ", " + COLUMN_ARTNR + ", " + COLUMN_DATUM + ", " + COLUMN_TECHNR + ", " + COLUMN_MENGE_AW + ", " + COLUMN_WEGAW + ", " + COLUMN_TEXT + ", " + COLUMN_PRUEFEN+ ", " + COLUMN_SYNCID;
            String str1p = "INSERT INTO " + TABLE_POSITIONEN + " (" + columnp + ") values(";


            int pruefen = mBundle.getInt("pruefen");


            for (int i = 0; i <= mBundle.getInt("Pos"); i++) {


                int Posart = 1;
                String ArtNr = mBundle.getString("LohnArtNr" + String.valueOf(i));
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMAN);
                Date Datum = null;
                String formated = "null";
                try {
                    Datum = sdf.parse(mBundle.getString("Datum" + String.valueOf(i)));
                    formated = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN).format(Datum);
                    Log.d("formated", formated);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String Techniker = mBundle.getString("TechNr" + String.valueOf(i));
                String MengeAW = mBundle.getString("AW" + String.valueOf(i));
                String WegAW = mBundle.getString("WEG" + String.valueOf(i));
                String Text = mBundle.getString("Arbeit" + String.valueOf(i));

                String syncid=session.getID();

                String valuep = "'" + mScheinID + "','" + Posart + "','" + ArtNr + "','" + formated + "','" + Techniker + "','" + MengeAW + "','" + WegAW + "','" + Text + "','" + pruefen+ "','" + syncid + "');";

                String abfragep = str1p + valuep;
                Log.d("INSERT: ", abfragep);
                sdb.execSQL(abfragep);


            }

            for (int t = 0; t <= mBundle.getInt("TeilePos"); t++) {
                if (mBundle.containsKey("Anz" + String.valueOf(t))) {
                    String columnst = COLUMN_SCHEINID + ", " + COLUMN_POSART + ", "
                            + COLUMN_ARTNR + ", " + COLUMN_MENGE_AW + ", " + COLUMN_TECHNR + ", " + COLUMN_TEXT + ", " + COLUMN_PRUEFEN+ ", " + COLUMN_SYNCID;
                    String str1t = "INSERT INTO " + TABLE_POSITIONEN + " (" + columnst + ") values(";
                    int Posart = 2;
                    String MengeAW = mBundle.getString("Anz" + String.valueOf(t));

                    String ArtNr = mBundle.getString("ArtNr" + String.valueOf(t));

                    String Bez = mBundle.getString("Bez" + String.valueOf(t));
                    String Techniker = mBundle.getString("TechNr0");

                    String syncid=session.getID();

                    String valuet = "'" + mScheinID + "','" + Posart + "','" + ArtNr + "','" + MengeAW + "','" + Techniker + "','" + Bez + "','" + pruefen+ "','" + syncid + "');";

                    String abfraget = str1t + valuet;
                    Log.d("INSERT: ", abfraget);
                    sdb.execSQL(abfraget);

                }
            }

            /*********************Zähler**********************************************************************************/


            String columnsz = COLUMN_SCHEINID + ", " + COLUMN_Z1 + ", " + COLUMN_Z2;
            String str1z = "INSERT OR REPLACE INTO " + TABLE_ZAEHLER + " (" + columnsz + ") values(";

            String Sw = mBundle.getString("Sw");
            String Farb = mBundle.getString("Farb");

            String valuesz = "'" + mScheinID + "','" + Sw + "','" + Farb + "')";

            String abfragez = str1z + valuesz;
            Log.d("INSERT: ", abfragez);
            sdb.execSQL(abfragez);

            /********************VDE**************************************************************************************/

            if (mBundle.getBoolean("VDE")) {
                sdb.execSQL(VDE_CREATE);
                String columnv = COLUMN_SCHEINID + ", " + COLUMN_RPE + ", " + COLUMN_RISO + ", " + COLUMN_LEAK;
                String str1v = "INSERT OR REPLACE INTO " + TABLE_VDE + " (" + columnv + ") values(";

                String RPE = mBundle.getString("RPE");
                String RISO = mBundle.getString("RISO");
                String LEAK = mBundle.getString("ILEAK");


                String valuev = "'" + mScheinID + "','" + RPE + "','" + RISO + "','" + LEAK + "'); ";

                String abfragev = str1v + valuev;
                Log.d("INSERT: ", abfragev);
                sdb.execSQL(abfragev);

            }

            /*********************Unterschrift**********************************************************************************/
            if (mBundle.containsKey("BLOB")) {

            String columnsu = COLUMN_SCHEINID + ", " + COLUMN_UNTERSCHRIFT+ ", " + COLUMN_KLARNAME;
            String str1u = "INSERT OR REPLACE INTO " + TABLE_UNTERSCHRIFT + " (" + columnsu + ") values(";

            String BLOB = mBundle.getString("BLOB");
                String Klarname = mBundle.getString("Klarname");

            String valuesu = "'" + mScheinID + "','" + BLOB + "','" + Klarname + "');";

            String abfrageu = str1u + valuesu;
            Log.d("INSERT: ", abfrageu);
            sdb.execSQL(abfrageu);
        }
        /************************************************************************************************************/





            sdb.setTransactionSuccessful();
            sdb.endTransaction();
            sdb.close();
            return mScheinID;
        }


        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(Integer scheinid) {
            //dismiss the dialog after the file was downloaded
            //mCSV.delete();
            Log.i("erfolgreich", "fillScheinDB");
           /* //TODO: Daten an den Server übertragen wenn scheinid !=0
            if (scheinid !=0){
                Log.d("fillScheinDB","daten sollen übertragen werden mit id: "+scheinid);
               new UebertrageDaten(mContext,scheinid).execute();
            }else
            {   //TODO: spater wieder nach der id fragen wenn erfolgreich dann übertragen
                Log.i("Scheinid==0","schein wird nich übertragen weil noch keine id");
            }*/

            pDialog.dismiss();

        }

    }

    public static class UebertrageDaten extends AsyncTask<String, String, String> {

        private Context mContext;
        private int mId;


        private ProgressDialog pDialog;
        public UebertrageDaten (Context context,int id){
            mContext = context;

            mId=id;
        }
        /**
         * Before starting background thread Show Progress Bar Dialog
         * */


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Übertrage Schein");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(final String... args) {

            final RequestQueue queue = MyVolley.getRequestQueue();

            String url = "https://hoell.syno-ds.de:55443/job/android/index.php";

            Map<String, String> postparams = new HashMap<String, String>();
            postparams.put("tag", "schein");


            String selectfrom = "SELECT * FROM ";
            String where = " WHERE scheinid = '"+mId+"' ;";
            SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();
            sdb.beginTransaction();

            Log.d("GET: ",selectfrom+TABLE_SCHEIN+where);

            Cursor scheinresult = sdb.rawQuery(selectfrom+TABLE_SCHEIN+where,null);


            JSONArray schein = new JSONArray();
            JSONObject row;


            while (scheinresult.moveToNext()) {
                row=new JSONObject();
                for (int c=0; c<scheinresult.getColumnCount();c++){
                    Log.d("getColumnName",scheinresult.getColumnName(c)+ " " + scheinresult.getString(c));
                    try {
                        row.put(scheinresult.getColumnName(c),scheinresult.getString(c));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                schein.put(row);




            }
            postparams.put("scheindata", String.valueOf(schein));


            scheinresult.close();


            /*********************Positionen**********************************************************************************/
            Log.d("GET: ",selectfrom+TABLE_POSITIONEN+where);

            Cursor posresult = sdb.rawQuery(selectfrom+TABLE_POSITIONEN+where,null);



            JSONArray positionen = new JSONArray();
            JSONObject prow;


            while (posresult.moveToNext()) {
                prow=new JSONObject();
                for (int c=0; c<posresult.getColumnCount();c++){
                    Log.d("getColumnName",posresult.getColumnName(c)+ " " + posresult.getString(c));
                    try {
                        prow.put(posresult.getColumnName(c),posresult.getString(c));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                positionen.put(prow);



            }
            postparams.put("posdata", String.valueOf(positionen));


            posresult.close();




            /*********************Zähler**********************************************************************************/
            Log.d("GET: ",selectfrom+TABLE_ZAEHLER+where);

            Cursor zaehlerresult = sdb.rawQuery(selectfrom+TABLE_ZAEHLER+where,null);



            JSONArray zaehler = new JSONArray();
            JSONObject zrow;


            while (zaehlerresult.moveToNext()) {
                zrow=new JSONObject();
                for (int c=0; c<zaehlerresult.getColumnCount();c++){
                    Log.d("getColumnName",zaehlerresult.getColumnName(c)+ " " + zaehlerresult.getString(c));
                    try {
                        zrow.put(zaehlerresult.getColumnName(c),zaehlerresult.getString(c));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                zaehler.put(zrow);



            }
            postparams.put("zaehlerdata", String.valueOf(zaehler));


            zaehlerresult.close();
            /********************VDE**************************************************************************************/

            Log.d("GET: ",selectfrom+TABLE_VDE+where);

            Cursor vderesult = sdb.rawQuery(selectfrom+TABLE_VDE+where,null);



            JSONArray vde = new JSONArray();
            JSONObject vrow;


            while (vderesult.moveToNext()) {
                vrow=new JSONObject();
                for (int c=0; c<vderesult.getColumnCount();c++){
                    Log.d("getColumnName",vderesult.getColumnName(c)+ " " + vderesult.getString(c));
                    try {
                        vrow.put(vderesult.getColumnName(c),vderesult.getString(c));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                vde.put(vrow);



            }
            postparams.put("vdedata", String.valueOf(vde));


            vderesult.close();

            /*********************Unterschrift**********************************************************************************/
            Log.d("GET: ",selectfrom+TABLE_UNTERSCHRIFT+where);

            Cursor unterresult = sdb.rawQuery(selectfrom+TABLE_UNTERSCHRIFT+where,null);



            JSONArray unterschrift = new JSONArray();
            JSONObject urow;


            while (unterresult.moveToNext()) {
                urow=new JSONObject();
                for (int c=0; c<unterresult.getColumnCount();c++){
                    Log.d("getColumnName unt",unterresult.getColumnName(c)+ " " + unterresult.getString(c));
                    try {
                        urow.put(unterresult.getColumnName(c),unterresult.getString(c));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                unterschrift.put(urow);



            }
            postparams.put("unterschriftdata", String.valueOf(unterschrift));


            unterresult.close();

            /************************************************************************************************************/





            sdb.setTransactionSuccessful();
            sdb.endTransaction();
            sdb.close();




        Log.v("postdata",postparams.toString());




            CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, postparams, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject json) {
                    Log.d("Response Volley: ", json.toString());
                    //  showProgress(false);
                    try {
                        if (json.getInt("success")==1) {

                            Log.d("übertragung", "succsess wub wub");
                            /*SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();
                            sdb.beginTransaction();
                            String abfrage= "UPDATE "+TABLE_SCHEIN+" SET uebertragen='1' WHERE scheinid = '"+mId+"' AND uebertragen = '0';"+
                                            "UPDATE "+TABLE_POSITIONEN+" SET uebertragen='1' WHERE scheinid = '"+mId+"' AND uebertragen = '0';"+
                                            "UPDATE "+TABLE_ZAEHLER+" SET uebertragen='1' WHERE scheinid = '"+mId+"' AND uebertragen = '0';"+
                                            "UPDATE "+TABLE_VDE+" SET uebertragen='1' WHERE scheinid = '"+mId+"' AND uebertragen = '0';"+
                                            "UPDATE "+TABLE_UNTERSCHRIFT+" SET uebertragen='1' WHERE scheinid = '"+mId+"' AND uebertragen = '0';";
                            Log.d("UPDATE: ",abfrage);

                            Cursor updateresult = sdb.rawQuery(abfrage,null);

                            while (updateresult.moveToNext()) {



                               Log.i("UPDATERESULT",updateresult.toString());

                            }

                            updateresult.close();
                            sdb.setTransactionSuccessful();
                            sdb.endTransaction();
                            sdb.close();*/
                           /* if(mLoeschen){

                                sdb.beginTransaction();
                                String delete=  "DELETE FROM "+TABLE_SCHEIN+" WHERE `srn`='"+mIds[0]+"' AND `ticketnr` = '"+mIds[1]+"';" +
                                                "DELETE FROM "+TABLE_ZAEHLER+" WHERE `srn`='"+mIds[0]+"' AND `ticketnr` = '"+mIds[1]+"';" +
                                                "DELETE FROM "+TABLE_VDE+" WHERE `srn`='"+mIds[0]+"' AND `ticketnr` = '"+mIds[1]+"';" +
                                                "DELETE FROM "+TABLE_UNTERSCHRIFT+" WHERE `srn`='"+mIds[0]+"' AND `ticketnr` = '"+mIds[1]+"';";
                                Cursor delresult = sdb.rawQuery(delete,null);

                                while (delresult.moveToNext()) {



                                    Log.i("delresult",delresult.toString());

                                }

                                delresult.close();
                                sdb.setTransactionSuccessful();
                                sdb.endTransaction();

                                final String index = "https://hoell.syno-ds.de:55443/job/android/index.php";

                                Map<String, String> postparams = new HashMap<String, String>();
                                postparams.put("tag", "savedetails");
                                postparams.put("user", new SessionManager(Jobcontrol.getAppCtx()).getUser());
                                postparams.put("status", "15");
                                postparams.put("id", mIds[1]);
                                postparams.put("xml", "true");
                                Log.d("Volley Params: ", postparams.toString());


                                CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, index, postparams, new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject json) {
                                        Log.d("Response Volley: ", json.toString());

                                        try {
                                            if(json.getInt("success")==1){
                                                Intent i = new Intent(Jobcontrol.getAppCtx(), MainActivity.class);
                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                mContext.startActivity(i);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError response) {
                                        Log.d("onErrorResponse: ", response.toString());
                                        Toast.makeText(mContext, "ERROR "+response.toString() , Toast.LENGTH_SHORT).show();

                                    }
                                });

                                queue.add(jsObjRequest);

                            }*/


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError response) {
                    Log.e("Response: ", response.toString());

                    Toast.makeText(mContext, "Übertragung fehl geschlagen", Toast.LENGTH_SHORT).show();
                }
            });

            queue.add(jsObjRequest);

            return null;
        }


        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String succsess) {

            Log.d("erfolgreich", "ÜbertrageScheinDB");

            pDialog.dismiss();

        }

    }

    public static class SyncScheinDB extends AsyncTask<String, String, Integer> {

        private Context mContext;
        private JSONObject mSchein,mVde,mUnterschrift,mZaehler;
        private JSONArray mArbeit,mTeile;

        private ProgressDialog pDialog;

        public SyncScheinDB (Context context,JSONObject Schein, JSONObject Vde,JSONObject Unterschrift,JSONObject Zaehler,JSONArray Arbeit,JSONArray Teile){
            mContext = context;
            mSchein = Schein;
            mVde = Vde;
            mUnterschrift = Unterschrift;
            mZaehler = Zaehler;
            mArbeit = Arbeit;
            mTeile = Teile;

        }
        /**
         * Before starting background thread Show Progress Bar Dialog
         * */


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Synce Schein");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected Integer doInBackground(String... args) {
            try {
                int ScheinId= mSchein.getInt("scheinid");
                Map<String, String> DBData=DBManager.GetSchein(mContext,ScheinId);
                JSONObject DBSchein=new JSONObject(DBData.get("scheindata"));
                Log.d("DBSchein",""+DBSchein);
                JSONObject DBVde=new JSONObject(DBData.get("vdedata"));
                Log.d("DBVde",""+DBVde);
                JSONObject DBUnterschrift=new JSONObject(DBData.get("unterschriftdata"));
                Log.d("DBUnterschrift",""+DBUnterschrift);
                JSONArray DBTeile =new JSONArray(DBData.get("teiledata"));
                Log.d("DBTeile",""+DBTeile);
                JSONArray DBArbeit =new JSONArray(DBData.get("arbeitdata"));
                Log.d("DBArbeit",""+DBArbeit);
                JSONObject DBZaehler=new JSONObject(DBData.get("zaehlerdata"));
                Log.d("DBZaehler",""+DBZaehler);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                        Locale.GERMAN);
                //TODO:wenn schein nich im handy abfangen !!!!!!!!!!!!!!!
                if(!DBSchein.isNull("scheinid")&&mSchein!=null){


                    if(format.parse(mSchein.getString("timestamp")).after(format.parse(DBSchein.getString("timestamp")))){

                        SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();

                       String updateschein= "UPDATE "+TABLE_SCHEIN+
                                            " SET "+
                                               COLUMN_ANSPRECHPARTNER+" = '"+mSchein.getString("ansprechpartner")+ "'," +
                                               COLUMN_ERROR+" = '"+mSchein.getString("error")+ "'," +
                                               COLUMN_EMAIL+" = '"+mSchein.getString("email")+ "'," +
                                               COLUMN_BEMERKUNG+" = '"+mSchein.getString("bemerkung")+ "'," +
                                               COLUMN_TIMESTAMP+" = '"+mSchein.getString("timestamp")+ "'" +
                                            " WHERE "+COLUMN_SCHEINID +" = "+ScheinId;


                        sdb.beginTransaction();

                        Log.d("UPDATE: ", updateschein);
                        sdb.execSQL(updateschein);
                        sdb.setTransactionSuccessful();
                        sdb.endTransaction();
                        sdb.close();
                    }
                }else if (DBSchein.isNull("scheinid")&&mSchein!=null){
                    SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();

                    String insertschein= "INSERT INTO "+TABLE_SCHEIN+
                            "  ( "+COLUMN_SCHEINID + ", " + COLUMN_SRN + ", " + COLUMN_TICKETNR + ", " + COLUMN_TECHNR + ", " + COLUMN_ANSPRECHPARTNER + ", " + COLUMN_ERROR + ", " + COLUMN_EMAIL + ", " + COLUMN_BEMERKUNG + ", " + COLUMN_TIMESTAMP+")VALUES("+
                            " '"+mSchein.getString("scheinid")+ "'," +
                            " '"+mSchein.getString("srn")+ "'," +
                            " '"+mSchein.getString("ticketnr")+ "'," +
                            " '"+mSchein.getString("technr")+ "'," +
                            " '"+mSchein.getString("ansprechpartner")+ "'," +
                            " '"+mSchein.getString("error")+ "'," +
                            " '"+mSchein.getString("email")+ "'," +
                            " '"+mSchein.getString("bemerkung")+ "'," +
                            " '"+mSchein.getString("timestamp")+ "')";


                    sdb.beginTransaction();

                    Log.d("INSERT: ", insertschein);
                    sdb.execSQL(insertschein);
                    sdb.setTransactionSuccessful();
                    sdb.endTransaction();
                    sdb.close();

                }
                if(!DBUnterschrift.isNull("scheinid")&&mUnterschrift!=null){

                    if(format.parse(mUnterschrift.getString("timestamp")).after(format.parse(DBUnterschrift.getString("timestamp")))){

                    SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();

                    String updateunterschrift= "UPDATE "+TABLE_UNTERSCHRIFT+
                            " SET "+
                            COLUMN_UNTERSCHRIFT+" = '"+mUnterschrift.getString("unterschrift")+ "'," +
                            COLUMN_KLARNAME+" = '"+mUnterschrift.getString("klarname")+ "'," +
                            COLUMN_TIMESTAMP+" = '"+mUnterschrift.getString("timestamp")+ "'" +
                            " WHERE "+COLUMN_SCHEINID +" = "+ScheinId;


                    sdb.beginTransaction();

                    Log.d("UPDATE: ", updateunterschrift);
                    sdb.execSQL(updateunterschrift);
                    sdb.setTransactionSuccessful();
                    sdb.endTransaction();
                    sdb.close();

                    }
                }else if (DBUnterschrift.isNull("scheinid")&&mUnterschrift!=null){
                    SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();

                    String insertunterschrift= "INSERT INTO "+TABLE_UNTERSCHRIFT+
                            "  ( "+COLUMN_UNTERSCHRIFTID + ", " + COLUMN_SCHEINID + ", " + COLUMN_UNTERSCHRIFT + ", " + COLUMN_KLARNAME + ", " + COLUMN_TIMESTAMP+")VALUES("+
                            " '"+mUnterschrift.getString("unterschriftid")+ "'," +
                            " '"+mUnterschrift.getString("scheinid")+ "'," +
                            " '"+mUnterschrift.getString("unterschrift")+ "'," +
                            " '"+mUnterschrift.getString("klarname")+ "'," +
                            " '"+mUnterschrift.getString("timestamp")+ "')" ;

                    sdb.beginTransaction();

                    Log.d("INSERT: ", insertunterschrift);
                    sdb.execSQL(insertunterschrift);
                    sdb.setTransactionSuccessful();
                    sdb.endTransaction();
                    sdb.close();

                }
                if(!DBVde.isNull("scheinid")&&mVde!=null){
                    if(format.parse(mVde.getString("timestamp")).after(format.parse(DBVde.getString("timestamp")))){

                        SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();

                        String updatevde= "UPDATE "+TABLE_VDE+
                                " SET "+
                                COLUMN_RISO+" = '"+mVde.getString("riso")+ "'," +
                                COLUMN_RPE+" = '"+mVde.getString("rpe")+ "'," +
                                COLUMN_LEAK+" = '"+mVde.getString("leak")+ "'," +
                                COLUMN_TIMESTAMP+" = '"+mVde.getString("timestamp")+ "'" +
                                " WHERE "+COLUMN_SCHEINID +" = "+ScheinId;


                        sdb.beginTransaction();

                        Log.d("UPDATE: ", updatevde);
                        sdb.execSQL(updatevde);
                        sdb.setTransactionSuccessful();
                        sdb.endTransaction();
                        sdb.close();

                    }
                }else if (DBVde.isNull("scheinid")&&mVde!=null){
                    SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();

                    String insertvde= "INSERT INTO "+TABLE_VDE+
                            "  ( "+COLUMN_VDEID + ", " + COLUMN_SCHEINID + ", " + COLUMN_RPE + ", " + COLUMN_RISO + ", " + COLUMN_LEAK + ", " + COLUMN_TIMESTAMP+")VALUES("+
                            " '"+mVde.getString("vdeid")+ "'," +
                            " '"+mVde.getString("scheinid")+ "'," +
                            " '"+mVde.getString("rpe")+ "'," +
                            " '"+mVde.getString("riso")+ "'," +
                            " '"+mVde.getString("leak")+ "'," +
                            " '"+mVde.getString("timestamp")+ "')";


                    sdb.beginTransaction();

                    Log.d("INSERT: ", insertvde);
                    sdb.execSQL(insertvde);
                    sdb.setTransactionSuccessful();
                    sdb.endTransaction();
                    sdb.close();

                }
                if(!DBZaehler.isNull("scheinid")&&mZaehler!=null){
                    if(format.parse(mZaehler.getString("timestamp")).after(format.parse(DBZaehler.getString("timestamp")))){

                        SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();

                        String updatezaehler= "UPDATE "+TABLE_ZAEHLER+
                                " SET "+
                                COLUMN_Z1+" = '"+mZaehler.getString("z1")+ "'," +
                                COLUMN_Z2+" = '"+mZaehler.getString("z2")+ "'," +
                                COLUMN_TIMESTAMP+" = '"+mZaehler.getString("timestamp")+ "'" +
                                " WHERE "+COLUMN_SCHEINID +" = "+ScheinId;


                        sdb.beginTransaction();

                        Log.d("UPDATE: ", updatezaehler);
                        sdb.execSQL(updatezaehler);
                        sdb.setTransactionSuccessful();
                        sdb.endTransaction();
                        sdb.close();

                    }
                }else if (DBZaehler.isNull("scheinid")&&mZaehler!=null){
                    SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();

                    String insertzaehler= "INSERT INTO "+TABLE_ZAEHLER+
                            "  ( "+COLUMN_ZAEHLERID + ", " + COLUMN_SCHEINID + ", " + COLUMN_Z1 + ", " + COLUMN_Z2 + ", " + COLUMN_TIMESTAMP+")VALUES("+
                            " '"+mZaehler.getString("zaehlerid")+ "'," +
                            " '"+mZaehler.getString("scheinid")+ "'," +
                            " '"+mZaehler.getString("z1")+ "'," +
                            " '"+mZaehler.getString("z2")+ "'," +
                            " '"+mZaehler.getString("timestamp")+ "')";


                    sdb.beginTransaction();

                    Log.d("INSERT: ", insertzaehler);
                    sdb.execSQL(insertzaehler);
                    sdb.setTransactionSuccessful();
                    sdb.endTransaction();
                    sdb.close();

                }
                if(mArbeit!=null) {


                    for (int i = 0; i < mArbeit.length(); i++) {
                        SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();

                        JSONObject Arbeit = mArbeit.getJSONObject(i);

                        String insertarbeit = "INSERT OR REPLACE INTO " + TABLE_POSITIONEN +
                                "  ( " + COLUMN_SCHEINID + ", " + COLUMN_POSART + ", " + COLUMN_ARTNR + ", " + COLUMN_DATUM + ", " + COLUMN_TECHNR + ", " + COLUMN_MENGE_AW + ", " + COLUMN_WEGAW + ", " + COLUMN_TEXT + ", " + COLUMN_PRUEFEN+ ", " + COLUMN_SYNCID +  ", " + COLUMN_TIMESTAMP + ")VALUES(" +
                                " '" + Arbeit.getString("scheinid") + "'," +
                                " '" + Arbeit.getString("posart") + "'," +
                                " '" + Arbeit.getString("artnr") + "'," +
                                " '" + Arbeit.getString("datum") + "'," +
                                " '" + Arbeit.getString("technr") + "'," +
                                " '" + Arbeit.getString("mengeaw") + "'," +
                                " '" + Arbeit.getString("wegaw") + "'," +
                                " '" + Arbeit.getString("text") + "'," +
                                " '" + Arbeit.getString("pruefen") + "'," +
                                " '" + Arbeit.getString("syncid") + "'," +
                                " '" + Arbeit.getString("timestamp") + "')";


                        sdb.beginTransaction();

                        Log.d("INSERT: ", insertarbeit);
                        sdb.execSQL(insertarbeit);
                        sdb.setTransactionSuccessful();
                        sdb.endTransaction();
                        sdb.close();

                    }
                }

                if(mTeile!=null) {


                    for (int i = 0; i < mTeile.length(); i++) {
                        SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();

                        JSONObject Teile = mTeile.getJSONObject(i);

                        String insertteile = "INSERT OR REPLACE INTO " + TABLE_POSITIONEN +
                                "  ( " + COLUMN_SCHEINID + ", " + COLUMN_POSART + ", "
                                + COLUMN_ARTNR + ", " + COLUMN_MENGE_AW + ", " + COLUMN_TECHNR + ", " + COLUMN_TEXT + ", " + COLUMN_PRUEFEN+ ", " + COLUMN_SYNCID  +  ", " + COLUMN_TIMESTAMP +")VALUES(" +
                                " '" + Teile.getString("scheinid") + "'," +
                                " '" + Teile.getString("posart") + "'," +
                                " '" + Teile.getString("artnr") + "'," +
                                " '" + Teile.getString("mengeaw") + "'," +
                                " '" + Teile.getString("technr") + "'," +
                                " '" + Teile.getString("text") + "'," +
                                " '" + Teile.getString("pruefen") + "'," +
                                " '" + Teile.getString("syncid") + "'," +
                                " '" + Teile.getString("timestamp") + "')";


                        sdb.beginTransaction();

                        Log.d("INSERT: ", insertteile);
                        sdb.execSQL(insertteile);
                        sdb.setTransactionSuccessful();
                        sdb.endTransaction();
                        sdb.close();

                    }
                }


            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
            return null;
        }


        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(Integer scheinid) {
            //dismiss the dialog after the file was downloaded
            //mCSV.delete();
            Log.i("erfolgreich", "sync");


            pDialog.dismiss();

        }

    }

    public static Map<String, String> GetSchein(Context mContext, int mId){




        Map<String, String> postparams = new HashMap<String, String>();


        String selectfrom = "SELECT * FROM ";
        String where = " WHERE scheinid = '"+mId+"'";
        SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();
        sdb.beginTransaction();

        Log.d("GET: ",selectfrom+TABLE_SCHEIN+where);

        Cursor scheinresult = sdb.rawQuery(selectfrom+TABLE_SCHEIN+where,null);


        JSONArray schein = new JSONArray();
        JSONObject row=new JSONObject();


        while (scheinresult.moveToNext()) {
            row=new JSONObject();
            for (int c=0; c<scheinresult.getColumnCount();c++){
                Log.d("getColumnName",scheinresult.getColumnName(c)+ " " + scheinresult.getString(c));
                try {
                    row.put(scheinresult.getColumnName(c),scheinresult.getString(c));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            schein.put(row);




        }
        postparams.put("scheindata", String.valueOf(row));


        scheinresult.close();


        /*********************Teile**********************************************************************************/
        Log.d("GET: ",selectfrom+TABLE_POSITIONEN+where);

        Cursor posresult = sdb.rawQuery(selectfrom+TABLE_POSITIONEN+where+"AND "+COLUMN_POSART + "=2",null);



        JSONArray positionen = new JSONArray();
        JSONObject prow;


        while (posresult.moveToNext()) {
            prow=new JSONObject();
            for (int c=0; c<posresult.getColumnCount();c++){
                Log.d("getColumnName",posresult.getColumnName(c)+ " " + posresult.getString(c));
                try {
                    prow.put(posresult.getColumnName(c),posresult.getString(c));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            positionen.put(prow);



        }
        postparams.put("teiledata", String.valueOf(positionen));


        posresult.close();


        /*********************Arbeit**********************************************************************************/
        Log.d("GET: ",selectfrom+TABLE_POSITIONEN+where);

        Cursor arbeitresult = sdb.rawQuery(selectfrom+TABLE_POSITIONEN+where+"AND "+COLUMN_POSART + "=1",null);



        JSONArray arbeiten = new JSONArray();
        JSONObject arow;


        while (arbeitresult.moveToNext()) {
            arow=new JSONObject();
            for (int c=0; c<arbeitresult.getColumnCount();c++){
                Log.d("getColumnName",arbeitresult.getColumnName(c)+ " " + arbeitresult.getString(c));
                try {
                    arow.put(arbeitresult.getColumnName(c),arbeitresult.getString(c));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            arbeiten.put(arow);



        }
        postparams.put("arbeitdata", String.valueOf(arbeiten));


        arbeitresult.close();




        /*********************Zähler**********************************************************************************/
        Log.d("GET: ",selectfrom+TABLE_ZAEHLER+where);

        Cursor zaehlerresult = sdb.rawQuery(selectfrom+TABLE_ZAEHLER+where,null);



        JSONArray zaehler = new JSONArray();
        JSONObject zrow=new JSONObject();


        while (zaehlerresult.moveToNext()) {
            zrow=new JSONObject();
            for (int c=0; c<zaehlerresult.getColumnCount();c++){
                Log.d("getColumnName",zaehlerresult.getColumnName(c)+ " " + zaehlerresult.getString(c));
                try {
                    zrow.put(zaehlerresult.getColumnName(c),zaehlerresult.getString(c));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            zaehler.put(zrow);



        }
        postparams.put("zaehlerdata", String.valueOf(zrow));


        zaehlerresult.close();
        /********************VDE**************************************************************************************/

        Log.d("GET: ",selectfrom+TABLE_VDE+where);

        Cursor vderesult = sdb.rawQuery(selectfrom+TABLE_VDE+where,null);



        JSONArray vde = new JSONArray();
        JSONObject vrow=new JSONObject();


        while (vderesult.moveToNext()) {
            vrow=new JSONObject();
            for (int c=0; c<vderesult.getColumnCount();c++){
                Log.d("getColumnName",vderesult.getColumnName(c)+ " " + vderesult.getString(c));
                try {
                    vrow.put(vderesult.getColumnName(c),vderesult.getString(c));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            vde.put(vrow);



        }
        postparams.put("vdedata", String.valueOf(vrow));


        vderesult.close();

        /*********************Unterschrift**********************************************************************************/
        Log.d("GET: ",selectfrom+TABLE_UNTERSCHRIFT+where);

        Cursor unterresult = sdb.rawQuery(selectfrom+TABLE_UNTERSCHRIFT+where,null);



        JSONArray unterschrift = new JSONArray();
        JSONObject urow=new JSONObject();


        while (unterresult.moveToNext()) {
            urow=new JSONObject();
            for (int c=0; c<unterresult.getColumnCount();c++){
                Log.d("getColumnName unt",unterresult.getColumnName(c)+ " " + unterresult.getString(c));
                try {
                    urow.put(unterresult.getColumnName(c),unterresult.getString(c));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            unterschrift.put(urow);



        }
        postparams.put("unterschriftdata", String.valueOf(urow));


        unterresult.close();

        /************************************************************************************************************/





        sdb.setTransactionSuccessful();
        sdb.endTransaction();
        sdb.close();




        Log.v("postdata",postparams.toString());


        return postparams;
    }


}

