package de.hoell.jobcontrol.historie;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
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

import de.hoell.jobcontrol.R;
import de.hoell.jobcontrol.adapter.SpecialAdapter;
import de.hoell.jobcontrol.query.Functions;

/**
 * Created by Hoell on 11.02.2015.
 */
public class Historie_Activity extends ListActivity {
    private static final String TAG_SUCCESS = "success";
    ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

    private int anfang;
    private String Seriennummer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        anfang=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historie);
        Button Button_next = (Button) findViewById(R.id.button_next);
        Seriennummer = getIntent().getStringExtra("value_seriennummer");

//||Seriennummer!= null
        if (!Seriennummer.equals("99")){
            Log.e("anfang","anfangstart"+anfang);
            new JSONHistorie(getApplicationContext(),Seriennummer,anfang).execute();
        }
        else{
            Toast.makeText(this, "Ungültige Seriennummer NEUE Maschine?", Toast.LENGTH_SHORT).show();
        }

        Button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anfang =anfang+15;
                Log.e("anfang","anfangclicked"+anfang);
                new JSONHistorie(getApplicationContext(),Seriennummer,anfang).execute();


            }
        });
    }









    private class JSONHistorie extends AsyncTask<Integer, Integer, JSONObject> {


        private Context mContext;
        private String mSeriennummer;
        private int mAnfang;
        private ProgressDialog pDialog ;
        private   int letzter;
        public JSONHistorie(Context context, String Seriennummer, int anfang){
            mContext = context;
            mSeriennummer =Seriennummer;
            mAnfang=anfang;
            Log.e("anfang","anfanginhistoreieasync"+anfang);

        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(Historie_Activity.this);
            pDialog.setMessage("Working ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Integer... args) {
            Functions Function = new Functions();

            JSONObject json = Function.Historie(mSeriennummer,mAnfang);



            Log.d("Create Response", json.toString());
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonh) {



            if (mAnfang==0){

            }else{
                letzter=mylist.size();


            }


            try {

                int success = jsonh.getInt(TAG_SUCCESS);

                if (success == 1) {
                    JSONObject jsonobj_his = jsonh.getJSONObject("historie");
                   JSONArray auanrs = jsonh.getJSONArray("auanrs");
                     Log.e("IS MIR SCH..EGAL", "länge "+ auanrs.length() );

                    for (int i = 0; i < auanrs.length() ; i++) {

                        String whatisinit = jsonobj_his.optString(auanrs.getString(i));

                        if (!whatisinit .equals("")){
                            Log.e(String.valueOf(i)+" lel","lol "+whatisinit);

                            JSONObject auanr = new JSONObject(whatisinit);
                            int testzaehler=0;

                            for (int n = 0; n <500; n++) {


                                if (!auanr.optString((String.valueOf(n))) .equals("")){
                                    HashMap<String, String> map = new HashMap<String, String>();


                                    JSONObject json_datech = new JSONDatech(auanrs.getString(i)).execute().get();
                                    JSONArray jsona_datech = json_datech.getJSONArray("datech");
                                    JSONObject jsonaa_datech = jsona_datech.getJSONObject(0);

                                    String auatechnr = jsonaa_datech.getString("auatechnr");

                                    String z1 = jsonaa_datech.getString("Z1");
                                    String z2 = jsonaa_datech.getString("Z2");

                                    String auadatum = jsonaa_datech.getString("auaaufdatumjmt");

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.GERMAN);
                                    SimpleDateFormat edf = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMAN);
                                    Date datum = sdf.parse(auadatum);
                                    String formattedDate = edf.format(datum);


                                    map.put("Datum", formattedDate.toString());
                                    map.put("Technr","Tech: "+ auatechnr);
                                    map.put("Z1","Sw: "+z1);
                                    map.put("Z2","F: "+z2);

                                    JSONObject text = auanr.getJSONObject(String.valueOf(n));
                                    String text1 = text.getString("text1");
                                    String text2 = text.getString("text2");
                                    String text3 = text.getString("text3");
                                    String text4 = text.getString("text4");
                                    String textges= text1+" "+text2+" "+text3+" "+text4;

                                    int posnr =n;

                                    map.put("Texte", textges);
                                    mylist.add(map);
                                    Log.e("Maptest","MAP"+map);
                                    System.out.println("AuftragsNR: "+auanrs.getString(i)+" posnr: "+posnr+" Technickernummer: "+auatechnr+" datum: "+formattedDate + " Texte: "+text1+"  "+text2+"  "+text3+"  "+text4);



                                }else {
                                    if (testzaehler>=3){
                                        Log.e("testzaehler","leeres dings"+testzaehler);
                                        break;

                                    }else{
                                        testzaehler=testzaehler+1;
                                    }
                                }
                            }



                        }else{
                        }

                    }



                 Log.e("COMMERZ query", jsonh.toString());
                    System.out.println("LOL COMMERZ RULZZZZ"+ success);



                }
                else{
                    Toast.makeText(mContext, "Keine Historie verfügbar", Toast.LENGTH_SHORT).show();
                    System.out.println("Y U NOT FUNCTION...");

                }

            } catch (JSONException | InterruptedException | ExecutionException | ParseException e) {
                e.printStackTrace();
            }
            //TODO:
            Log.e("mylist", mylist.toString());
            setListAdapter(new SpecialAdapter(mContext, mylist, R.layout.row_his,
                    new String[] {"Datum","Technr","Z1","Z2", "Texte" }, new int[] {R.id.DATUM_CELL, R.id.TECHNR_CELL, R.id.Z1_CELL,R.id.Z2_CELL, R.id.TXT_CELL, }));
            setSelection (letzter);
            pDialog.dismiss();


        }






    }
    private class JSONDatech extends AsyncTask<Integer, Integer, JSONObject> {
        private String mAuanr;
        public JSONDatech(String auftragsnr) {
           mAuanr =auftragsnr;
        }

        @Override
        protected JSONObject doInBackground(Integer... params) {
            Functions Function = new Functions();
            JSONObject json_datech = Function.DaTech(String.valueOf(mAuanr));

            return json_datech;
        }
    }

}
