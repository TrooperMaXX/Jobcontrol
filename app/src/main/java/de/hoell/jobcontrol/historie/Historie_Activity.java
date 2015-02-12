package de.hoell.jobcontrol.historie;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hoell.jobcontrol.R;
import de.hoell.jobcontrol.adapter.SpecialAdapter;
import de.hoell.jobcontrol.query.Functions;

/**
 * Created by Hoell on 11.02.2015.
 */
public class Historie_Activity extends ListActivity {
    private static final String TAG_SUCCESS = "success";
    ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historie);
        String Seriennummer = getIntent().getStringExtra("value_seriennummer");

        new JSONHistorie(getApplicationContext(),Seriennummer).execute();
    }









    private class JSONHistorie extends AsyncTask<Integer, Integer, JSONObject> {


        private Context mContext;
        private String mSeriennummer;
        public JSONHistorie (Context context,String Seriennummer){
            mContext = context;
            mSeriennummer =Seriennummer;

        }

        @Override
        protected JSONObject doInBackground(Integer... args) {
            Functions Function = new Functions();

            JSONObject json = Function.Historie(mSeriennummer);


            // check for login response
            // check log cat fro response
            Log.d("Create Response", json.toString());
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonh) {

           // ListView list = (ListView) findViewById(android.R.id.list);




            try {

                int success = jsonh.getInt(TAG_SUCCESS);

                if (success == 1) {
                    JSONObject jsonobj_his = jsonh.getJSONObject("historie");


                    for (int i = 90200; i <90211; i++) {
                        //  String jsonobj_num =  jsonobj_his.getJSONObject(String.valueOf(i)).toString();
                        String whatisinit = jsonobj_his.optString((String.valueOf(i)));

                        if (!whatisinit .equals("")){
                            Log.e(String.valueOf(i)+" lel","lol "+whatisinit);
                            int auftragsnr=i;
                            JSONObject auanr = new JSONObject(whatisinit);


                            for (int n = 0; n <11; n++) {


                                if (!auanr.optString((String.valueOf(n))) .equals("")){
                                    HashMap<String, String> map = new HashMap<String, String>();







                                    JSONObject text = auanr.getJSONObject(String.valueOf(n));
                                    String text1 = text.getString("text1");
                                    String text2 = text.getString("text2");
                                    String text3 = text.getString("text3");
                                    String text4 = text.getString("text4");
                                    String textges= text1+" "+text2+" "+text3+" "+text4;
                                    int posnr =n;
                                    map.put("Auftragsnr", String.valueOf(auftragsnr));
                                    map.put("Posnr",String.valueOf(posnr));
                                    map.put("Texte", textges);
                                    mylist.add(map);
                                    Log.e("Maptest","MAP"+map);
                                    System.out.println("AuftragsNR: "+auftragsnr+" posnr: "+posnr+" Texte: "+text1+"  "+text2+"  "+text3+"  "+text4);



                                }
                            }



                        }else{System.out.println(String.valueOf(i) + "lol" + whatisinit + "nix?"); }
                        /*TODO:*/
                    }

                   // SimpleAdapter mHistorie = new SimpleAdapter(mContext, mylist, R.layout.row_his,
                    //        new String[] {"Auftragsnr", "Posnr", "Texte"}, new int[] {R.id.AUANR_CELL, R.id.POS_CELL, R.id.TXT_CELL});

                  //  list.setAdapter(mHistorie);

                 Log.e("COMMERZ query", jsonh.toString());
                    System.out.println("LOL COMMERZ RULZZZZ"+ success);
                   /* JSONObject Historie =jsonh.getJSONObject("historie");
                    int laenge=Historie.length();
                    Log.e("HISTORIELAENGE","laenge: "+laenge);

                    for (int i = 0; i < Historie.length(); i++) {
                        JSONObject auanr = Historie.getJSONObject(String.valueOf(i+1));

                        for (int n = 0; n < auanr.length(); n++) {
                            JSONObject text = Historie.getJSONObject(String.valueOf(n));
                            String text1 = text.getString("text1");
                            String text2 = text.getString("text2");
                            String text3 = text.getString("text3");
                            String text4 = text.getString("text4");
                            String textges= text1+"\n"+text2+"\n"+text3+"\n"+text4;
                            int posnr =n;
                           System.out.println("posnr: "+posnr+"Texte: "+textges);
                        }


                    }*/


                }
                else{

                    System.out.println("Y U NOT FUNCTION...");

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //TODO:
            Log.e("mylist", mylist.toString());
            setListAdapter(new SpecialAdapter(mContext, mylist, R.layout.row_his,
                    new String[] {"Auftragsnr", "Posnr", "Texte"}, new int[] {R.id.AUANR_CELL, R.id.POS_CELL, R.id.TXT_CELL}));


        }






    }


}
