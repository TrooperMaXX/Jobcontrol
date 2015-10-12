package de.hoell.jobcontrol.ticketlist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.hoell.jobcontrol.Jobcontrol;
import de.hoell.jobcontrol.MainActivity;
import de.hoell.jobcontrol.R;
import de.hoell.jobcontrol.query.Functions;
import de.hoell.jobcontrol.session.SessionManager;

public class NewInfo extends Activity {
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_info);
        Button Button_InfoSave = (Button) findViewById(R.id.button_info_save);
        final String ID = getIntent().getStringExtra("value_id");
        SessionManager session =new SessionManager(Jobcontrol.getAppCtx());
        final String user=session.getUser();




        Button_InfoSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Functions Function = new Functions();
                if( Function.isNetworkOnline(NewInfo.this)){

                    TextView editTextAnsprechpartner =  (TextView) findViewById(R.id.Ansprechpartner_Info_Content);
                    String editInfoAnsprech = editTextAnsprechpartner.getText().toString();
                    TextView editTextInfo =  (TextView) findViewById(R.id.Info_Info_ontent);
                    String editInfoInfo = editTextInfo.getText().toString();

                    try {
                        new JSONSaveInfo(ID,editInfoAnsprech,editInfoInfo, user).execute().get(30000, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                }  else {
                    Toast.makeText(getApplicationContext(), "Keine INternet verbindung", Toast.LENGTH_LONG).show();}



            }
        });

    }


    public class JSONSaveInfo extends AsyncTask<Integer, Integer, JSONObject> {

        private String mID,mAnsprech,mInfo,mUser;
        private ProgressDialog pDialog ;
        public JSONSaveInfo(String id, String ansprech, String info,String user) {
            mID=id;
            mAnsprech=ansprech;
            mInfo=info;
            mUser=user;
        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewInfo.this);
            pDialog.setMessage("Sending ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Integer... args) {
            Functions Function = new Functions();

            JSONObject json = Function.SaveInfo(mID, mAnsprech,mInfo,mUser);

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

                    Toast.makeText(getApplicationContext(), "Info Erfolgreich gesendet", Toast.LENGTH_LONG).show();

                    pDialog.dismiss();

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);




                }
                else{

                    System.out.println("Y U NOT FUNCTION...");
                    pDialog.dismiss();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
