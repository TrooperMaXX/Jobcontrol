package de.hoell.jobcontrol.ticketlist;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.hoell.jobcontrol.R;
import de.hoell.jobcontrol.adapter.SpecialAdapter;
import de.hoell.jobcontrol.query.Functions;

/**
 * Created by Hoell on 11.02.2015.
 */
public class InfoActivity extends ListActivity {
    private static final String TAG_SUCCESS = "success";
    ArrayList<HashMap<String, String>> infolist = new ArrayList<HashMap<String, String>>();


    private String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ID = getIntent().getStringExtra("value_id");

        try {
            new JSONInfo(ID).execute().get(30000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        ;

        Button Button_newinfo = (Button) findViewById(R.id.button_newinfo);

        Button_newinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {





                Intent i = new Intent(getApplicationContext(), NewInfo.class);
                i.putExtra("value_id", ID);
                startActivity(i);

            }
        });
    }









    private class JSONInfo extends AsyncTask<Integer, Integer, JSONObject> {




        private ProgressDialog pDialog ;
        private String mID;

        public JSONInfo(String id) {
            mID=id;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(InfoActivity.this);
            pDialog.setMessage("Working ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected JSONObject doInBackground(Integer... args) {
            Functions Function = new Functions();
            JSONObject json = Function.Info(ID);

            // check for login response
            // check log cat fro response
            Log.d("Create Response", json.toString());
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            try {

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    JSONArray json_info = json.getJSONArray("info");
                    for (int i = 0; i < json_info.length(); i++) {
                        JSONObject inf = json_info.getJSONObject(i);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
                        SimpleDateFormat edf = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMAN);

                        String Datum_unformatiert =inf.getString("Datum");


                        Date Datum = sdf.parse(Datum_unformatiert);
                        String formatiertes_datum = edf.format(Datum);
                        Log.e("Datums","unformatiert "+Datum_unformatiert+" \n Formatiert "+Datum+" \n StringFormatiert "+formatiertes_datum);
                        String Benutzer= inf.getString("Benutzer");
                        String Ansprech= inf.getString("Kontaktperson");
                        String KonArt= inf.getString("Kontakt_Art");

                        String Info= inf.getString("Information");
                        String L_Benutzer= inf.getString("Letzte_Aederung");
                        String L_Datum_unformatiert= inf.getString("Letzteaenderung_Datum");
                        Date L_Datum = sdf.parse(L_Datum_unformatiert);
                        String formatiertes_l_datum = edf.format(L_Datum);


                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("Datum", formatiertes_datum);
                        map.put("Benutzer", Benutzer);
                        map.put("Ansprech", Ansprech);
                        map.put("KonArt", KonArt);
                        map.put("L_Benutzer", L_Benutzer);
                        map.put("L_Datum", formatiertes_l_datum);
                        map.put("Info", Info);
                        infolist.add(map);


                    }


                }
                else{
                    Toast.makeText(InfoActivity.this, "Keine Info verfügbar", Toast.LENGTH_SHORT).show();
                    System.out.println("Y U NOT FUNCTION...");

                }

            } catch (JSONException |  ParseException e) {
                e.printStackTrace();
            }

            Log.e("Infolist", infolist.toString());
            setListAdapter(new SpecialAdapter(InfoActivity.this, infolist, R.layout.row_info,
                    new String[] {"Datum","Benutzer","Ansprech","KonArt", "L_Benutzer", "L_Datum", "Info" },
                    new int[] {R.id.DATUM_INFO_CELL, R.id.BENUTZER_CELL, R.id.ANSPRECH_CELL,R.id.KONART_CELL, R.id.L_BENUTZER_CELL, R.id.L_DATUM_CELL, R.id.INFO_CELL}));

            pDialog.dismiss();


        }






    }

}
