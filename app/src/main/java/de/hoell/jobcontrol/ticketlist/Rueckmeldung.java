package de.hoell.jobcontrol.ticketlist;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import de.hoell.jobcontrol.MainActivity;
import de.hoell.jobcontrol.R;
import de.hoell.jobcontrol.Start;
import de.hoell.jobcontrol.query.Functions;

public class Rueckmeldung extends Activity {
    private static final String TAG_SUCCESS = "success";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rueckmeldung);

        Button Button_RueckSave = (Button) findViewById(R.id.button_save_rueck);

        final String Firma = getIntent().getStringExtra("value_firma");
        String Model = getIntent().getStringExtra("value_model");//Gerät
        String Stoerung = getIntent().getStringExtra("value_stoerung");
        final String Ansprechpartner = getIntent().getStringExtra("value_ansprechpartner");
       final String ID = getIntent().getStringExtra("value_id");

        final String user;

        user = de.hoell.jobcontrol.Start.user;

        TextView textViewRueckFirma = (TextView) findViewById(R.id.textViewRueckFirma);
        textViewRueckFirma.setText(Firma);
        TextView textViewRueckModell = (TextView) findViewById(R.id.textViewRueckModel);
        textViewRueckModell.setText(Model);
        TextView textViewRueckStoerung = (TextView) findViewById(R.id.textViewRueckStoerung);
        textViewRueckStoerung.setText(Stoerung);
        TextView textViewRueckAnsprechpartner = (TextView) findViewById(R.id.textViewRueckAnsprech);
        textViewRueckAnsprechpartner.setText(Ansprechpartner);

        Button_RueckSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Functions Function = new Functions();
                if( Function.isNetworkOnline(Rueckmeldung.this)){

                TextView editTextAnsprechpartner =  (TextView) findViewById(R.id.Ansprechpartner_Content);
                String editAnsprech = editTextAnsprechpartner.getText().toString();
                TextView editTextInfo =  (TextView) findViewById(R.id.Info_Content);
                String editInfo = editTextInfo.getText().toString();

                new JSONSaveRueck(ID,editAnsprech,editInfo, user).execute();}  else {
                    Toast.makeText(getApplicationContext(), "Keine INternet verbindung", Toast.LENGTH_LONG).show();}



            }
        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rueckmeldung, menu);
        return true;
    }

    public class JSONSaveRueck extends AsyncTask<Integer, Integer, JSONObject> {

        private String mID,mAnsprech,mInfo,mUser;
        public JSONSaveRueck(String id, String ansprech, String info,String user) {
            mID=id;
            mAnsprech=ansprech;
            mInfo=info;
            mUser=user;
        }

        @Override
        protected JSONObject doInBackground(Integer... args) {
            Functions Function = new Functions();

            JSONObject json = Function.SaveRueck(mID, mAnsprech,mInfo,mUser);

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
                    System.out.println("LOL gehtb sofort"+ success);

                    Toast.makeText(getApplicationContext(), "Rückmeldung Erfolgreich", Toast.LENGTH_LONG).show();


                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);




                }
                else{

                    System.out.println("Y U NOT FUNCTION...");

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
