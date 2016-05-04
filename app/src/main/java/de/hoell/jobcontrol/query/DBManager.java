package de.hoell.jobcontrol.query;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import de.hoell.jobcontrol.Jobcontrol;
import de.hoell.jobcontrol.MainActivity;
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
    public final static String COLUMN_LOHNARTNR="artnr";
    public final static String COLUMN_BEZEICHNUNG="bez";
    public final static String COLUMN_WVT="wvt";


    private static final String TABLE_SCHEIN = "schein";
    private static final String COLUMN_SRN = "srn";
    private static final String COLUMN_TICKETNR = "ticketnr";
    private static final String COLUMN_ERROR = "error";
    private static final String COLUMN_POSART = "posart";
    //public final static String COLUMN_ARTNR="artnr";
    private static final String COLUMN_DATUM = "datum";
    private static final String COLUMN_TECHNR = "technr";
    private static final String COLUMN_MENGE_AW = "mengeaw";
    private static final String COLUMN_WEGAW = "wegaw";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_PRUEFEN = "pruefen";

    public final static String TABLE_ZAEHLER="zaehler";
    //private static final String COLUMN_ID = "id";
    //private static final String COLUMN_TICKETNR = "ticketnr";
    public final static String COLUMN_Z1="z1";
    public final static String COLUMN_Z2="z2";

    public final static String TABLE_VDE="vde";
    private static final String COLUMN_RPE = "rpe";
    private static final String COLUMN_RISO = "riso";
    private static final String COLUMN_LEAK = "leak";

    public final static String TABLE_UNTERSCHRIFT="unterschrift";
    //private static final String COLUMN_ID = "id";
    //private static final String COLUMN_TICKETNR = "ticketnr";
    public final static String COLUMN_UNTERSCHRIFT="unterschrift";
    // Database creation sql statement
    // Indexes should not be used on small tables. © Tutorials Point
    private static final String ART_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ARTSTAMM + "(" +
                    "artid INTEGER PRIMARY KEY AUTOINCREMENT , " +
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
                    COLUMN_LOHNARTNR + " INTEGER PRIMARY KEY, " +
                    COLUMN_BEZEICHNUNG + " TEXT , " +
                    COLUMN_WVT + " INTEGER );";

    private static final String SCHEIN_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SCHEIN + "(" +
                    COLUMN_SRN + " TEXT , " +
                    COLUMN_TICKETNR + " INTEGER , " +
                    COLUMN_ERROR + " TEXT , " +
                    COLUMN_POSART + " INTEGER , " +
                    COLUMN_ARTNR + " TEXT , " +
                    COLUMN_DATUM + " TEXT , " +
                    COLUMN_TECHNR + " INTEGER , " +
                    COLUMN_MENGE_AW + " INTEGER , " +
                    COLUMN_WEGAW + " INTEGER , " +
                    COLUMN_TEXT + " TEXT , "+
                    COLUMN_PRUEFEN + " BOOLEAN ," +
                    COLUMN_UEBERTRAGEN+ " BOOLEAN NOT NULL DEFAULT '0' );";

    private static final String ZAEHLER_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ZAEHLER + "(" +
                    COLUMN_SRN + " TEXT , " +
                    COLUMN_TICKETNR + " INTEGER , " +
                    COLUMN_Z1 + " INTEGER , " +
                    COLUMN_Z2 + " INTEGER ," +
                    COLUMN_UEBERTRAGEN+ " BOOLEAN NOT NULL DEFAULT '0' );";

    private static final String VDE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_VDE + "(" +
                    COLUMN_SRN + " TEXT , " +
                    COLUMN_TICKETNR + " INTEGER , " +
                    COLUMN_RPE + " DOUBLE , " +
                    COLUMN_RISO + " DOUBLE , " +
                    COLUMN_LEAK + " DOUBLE," +
                    COLUMN_UEBERTRAGEN+ " BOOLEAN NOT NULL DEFAULT '0' );";

    private static final String UNTERSCHRIFT_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_UNTERSCHRIFT + "(" +
                    COLUMN_SRN + " TEXT , " +
                    COLUMN_TICKETNR + " INTEGER , " +
                    COLUMN_UNTERSCHRIFT+ " LONGBLOB ," +
                    COLUMN_UEBERTRAGEN+ " BOOLEAN NOT NULL DEFAULT '0' );";

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("DATABASE_CREATE", ART_CREATE);
        this.getWritableDatabase().execSQL(ART_CREATE);
        Log.d("DATABASE_CREATE", GER_CREATE);
        this.getWritableDatabase().execSQL(GER_CREATE);
        Log.d("DATABASE_CREATE", LOHN_CREATE);
        this.getWritableDatabase().execSQL(LOHN_CREATE);
        Log.d("DATABASE_CREATE", SCHEIN_CREATE);
        this.getWritableDatabase().execSQL(SCHEIN_CREATE);
        Log.d("DATABASE_CREATE", ZAEHLER_CREATE);
        this.getWritableDatabase().execSQL(ZAEHLER_CREATE);
        Log.d("DATABASE_CREATE", VDE_CREATE);
        this.getWritableDatabase().execSQL(VDE_CREATE);
        Log.d("DATABASE_CREATE", UNTERSCHRIFT_CREATE);
        this.getWritableDatabase().execSQL(UNTERSCHRIFT_CREATE);

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


    public static void dropAll( SQLiteDatabase sdb){

        Log.d("DATABASE_DROP", "DROP TABLE " + TABLE_ARTSTAMM);
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
        sdb.execSQL("DROP TABLE " + TABLE_UNTERSCHRIFT);

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

            String columns = COLUMN_GERNR + ", "+ COLUMN_GER + ", " +COLUMN_FIRMA + ", " +COLUMN_STR + ", " +COLUMN_ORT + ", " +COLUMN_STANDORT+ ", " +COLUMN_Z1 + ", " +COLUMN_Z2 + ", " +COLUMN_ZANZ;
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
                    // Log.d("INSERT: ",sb.toString());
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
            Log.i("erfolgreich", "fillLohnDB");
            pDialog.dismiss();

        }

    }

    public static class FillScheinDB extends AsyncTask<String, String, String[]> {

        private Context mContext;
        private Bundle mBundle;
        private boolean mLoeschen=false;

        private ProgressDialog pDialog;
        public FillScheinDB (Context context,Bundle Bundle){
            mContext = context;
            mBundle = Bundle;

        }
        public FillScheinDB (Context context,Bundle Bundle,boolean loeschen){
            mContext = context;
            mBundle = Bundle;
            mLoeschen=loeschen;

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
        protected String[] doInBackground(String... args) {


            SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();
            sdb.execSQL(SCHEIN_CREATE);



            String columns = COLUMN_SRN + ", "+ COLUMN_TICKETNR + ", " +COLUMN_ERROR +  ", " +COLUMN_POSART + ", "
                    + COLUMN_ARTNR+ ", " +COLUMN_DATUM + ", " +COLUMN_TECHNR + ", " +COLUMN_MENGE_AW + ", " +COLUMN_WEGAW + ", " +COLUMN_TEXT + ", " + COLUMN_PRUEFEN;
            String str1 = "INSERT INTO " + TABLE_SCHEIN + " (" + columns + ") values(";



            sdb.beginTransaction();

            String Srn = mBundle.getString("Srn");
            int pruefen = mBundle.getInt("pruefen");
            String TicketID="";
            if ( mBundle.containsKey("TicketID")) {

                TicketID= mBundle.getString("TicketID");

            }
            String Error = mBundle.getString("Error");

            for ( int i=0; i <= mBundle.getInt("Pos");i++) {


                int Posart = 1;
                String ArtNr = mBundle.getString("LohnArtNr" + String.valueOf(i));
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMAN);
                Date Datum= null;
                String formated="null";
                try {
                    Datum = sdf.parse( mBundle.getString("Datum" + String.valueOf(i)));
                    formated= new SimpleDateFormat("yyyy-MM-dd",Locale.GERMAN).format(Datum);
                    Log.d("formated",formated);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String Techniker=mBundle.getString("TechNr" + String.valueOf(i));
                String MengeAW=mBundle.getString("AW" + String.valueOf(i));
                String WegAW=mBundle.getString("WEG" + String.valueOf(i));
                String Text = mBundle.getString("Arbeit" + String.valueOf(i));


                String values="'" + Srn+"','"+TicketID+"','"+ Error+"','"+Posart+"','"+ArtNr+"','"+formated +"','"+Techniker+"','"+MengeAW+"','"+WegAW+"','"+Text+"','"+pruefen+"');";

                String abfrage=str1+values;
                Log.d("INSERT: ",abfrage);
                sdb.execSQL(abfrage);

            }

            for ( int t=0; t <= mBundle.getInt("TeilePos");t++){
                if ( mBundle.containsKey("Anz"+ String.valueOf(t))) {
                    String columnst = COLUMN_SRN + ", "+ COLUMN_TICKETNR + ", " +COLUMN_ERROR +  ", " +COLUMN_POSART + ", "
                            + COLUMN_ARTNR+ ", " +COLUMN_MENGE_AW + ", " +COLUMN_TEXT+ ", " +COLUMN_PRUEFEN;
                    String str1t = "INSERT INTO " + TABLE_SCHEIN + " (" + columnst + ") values(";
                    int Posart = 2;
                    String MengeAW=mBundle.getString("Anz" + String.valueOf(t));

                    String ArtNr = mBundle.getString("ArtNr" + String.valueOf(t));

                    String Bez = mBundle.getString("Bez" + String.valueOf(t));

                    String values="'" + Srn+"','"+TicketID+"','"+Error+"','"+Posart+"','"+ArtNr+"','"+ MengeAW+"','"+ Bez+"','"+pruefen+"');";

                    String abfrage=str1t+values;
                    Log.d("INSERT: ",abfrage);
                    sdb.execSQL(abfrage);
                }
            }

            /*********************Zähler**********************************************************************************/
            sdb.execSQL(ZAEHLER_CREATE);
            String columnsz = COLUMN_SRN + ", "+ COLUMN_TICKETNR + ", " +COLUMN_Z1 + ", " +COLUMN_Z2;
            String str1z = "INSERT INTO " + TABLE_ZAEHLER + " (" + columnsz + ") values(";

            String Sw =mBundle.getString("Sw");
            String Farb =mBundle.getString("Farb");

            String valuesz="'" + Srn+"','"+TicketID+"','"+Sw +"','"+Farb+"');";

            String abfragez=str1z+valuesz;
            Log.d("INSERT: ",abfragez);
            sdb.execSQL(abfragez);

            /********************VDE**************************************************************************************/

            if(mBundle.getBoolean("VDE")){
                sdb.execSQL(VDE_CREATE);
                String columnst = COLUMN_SRN + ", "+ COLUMN_TICKETNR + ", " +COLUMN_RPE +  ", " +COLUMN_RISO + ", " +COLUMN_LEAK;
                String str1t = "INSERT INTO " + TABLE_VDE + " (" + columnst + ") values(";

                String RPE =mBundle.getString("RPE");
                String RISO =mBundle.getString("RISO");
                String LEAK =mBundle.getString("ILEAK");



                String values="'" + Srn+"','"+TicketID+"','"+RPE+"','"+RISO+"','"+LEAK+"');";

                String abfrage=str1t+values;
                Log.d("INSERT: ",abfrage);
                sdb.execSQL(abfrage);
            }

            /*********************Unterschrift**********************************************************************************/
            sdb.execSQL(UNTERSCHRIFT_CREATE);
            String columnsu = COLUMN_SRN + ", "+ COLUMN_TICKETNR + ", " +COLUMN_UNTERSCHRIFT;
            String str1u = "INSERT INTO " + TABLE_UNTERSCHRIFT + " (" + columnsu + ") values(";

            String BLOB = mBundle.getString("BLOB");

            String valuesu="'" + Srn+"','"+TicketID+"','"+ BLOB+"');";

            String abfrageu=str1u+valuesu;
            Log.d("INSERT: ",abfrageu);
            sdb.execSQL(abfrageu);

            /************************************************************************************************************/





            sdb.setTransactionSuccessful();
            sdb.endTransaction();
            String[] ids=new String[2];
            ids[0]=Srn;
            ids[1]=TicketID;
            return ids;
        }


        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String[] ids) {
            // dismiss the dialog after the file was downloaded
            //mCSV.delete();
            Log.i("erfolgreich", "fillScheinDB");
            //TODO: Daten an den Server übertragen
            new UebertrageDaten(mContext,mLoeschen,ids).execute();
            pDialog.dismiss();

        }

    }
    public static class UebertrageDaten extends AsyncTask<String, String, String> {

        private Context mContext;
        private boolean mLoeschen;
        private String[] mIds;


        private ProgressDialog pDialog;
        public UebertrageDaten (Context context,boolean loeschen,String[] ids){
            mContext = context;
            mLoeschen=loeschen;
            mIds=ids;
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
            String where = " WHERE uebertragen = '0';";
            SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();
            sdb.beginTransaction();

            Log.d("GET: ",selectfrom+TABLE_SCHEIN+where);

            Cursor scheinresult = sdb.rawQuery(selectfrom+TABLE_SCHEIN,null);



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







            /*********************Zähler**********************************************************************************/
            Log.d("GET: ",selectfrom+TABLE_ZAEHLER+where);

            Cursor zaehlerresult = sdb.rawQuery(selectfrom+TABLE_ZAEHLER,null);



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

            Cursor vderesult = sdb.rawQuery(selectfrom+TABLE_VDE,null);



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

            Cursor unterresult = sdb.rawQuery(selectfrom+TABLE_UNTERSCHRIFT,null);



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





        Log.v("postdata",postparams.toString());




            CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, postparams, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject json) {
                    Log.d("Response Volley: ", json.toString());
                    //  showProgress(false);
                    try {
                        if (json.getInt("success")==1) {

                            Log.d("übertragung", "succsess wub wub");
                            SQLiteDatabase sdb = new DBManager(mContext).getWritableDatabase();
                            sdb.beginTransaction();
                            String abfrage= "UPDATE "+TABLE_SCHEIN+" SET uebertragen='1' WHERE uebertragen='0';"+
                                            "UPDATE "+TABLE_ZAEHLER+" SET uebertragen='1' WHERE uebertragen='0';"+
                                            "UPDATE "+TABLE_VDE+" SET uebertragen='1' WHERE uebertragen='0';"+
                                            "UPDATE "+TABLE_UNTERSCHRIFT+" SET uebertragen='1' WHERE uebertragen='0';";
                            Log.d("UPDATE: ",abfrage);

                            Cursor updateresult = sdb.rawQuery(abfrage,null);

                            while (updateresult.moveToNext()) {



                               Log.i("UPDATERESULT",updateresult.toString());

                            }

                            updateresult.close();
                            sdb.setTransactionSuccessful();
                            sdb.endTransaction();

                            if(mLoeschen){

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

                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError response) {
                    Log.d("Response: ", response.toString());

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
}