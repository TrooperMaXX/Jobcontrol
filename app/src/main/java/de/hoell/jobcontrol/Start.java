package de.hoell.jobcontrol;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.hoell.jobcontrol.query.Functions;
import de.hoell.jobcontrol.session.SessionManager;
import de.hoell.jobcontrol.ticketlist.TicketDetailsActivity;


public class Start extends Activity {

    Button btnLogin;
    EditText InputName;
    EditText InputPass;
    CheckBox InputCheck;
    public static String user;
    private  String password;
    SessionManager session;
    TicketDetailsActivity online = new TicketDetailsActivity();



   // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ERROR = "error";
    private static final String TAG_MESSAGE = "message";





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        session = new SessionManager(this);
        //Importing all assets like buttons, text fields
        InputName = (EditText) findViewById(R.id.login_name);
        InputPass = (EditText) findViewById(R.id.login_pass);
        btnLogin = (Button) findViewById(R.id.button);
        InputCheck = (CheckBox) findViewById(R.id.checkBox);

        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {


                new JSONLogin().execute();

            }
        });



        if (session.isUserLoggedIn()){


            user = session.getUser();

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);

            // closing this screen
            finish();

        }

    }


    private class JSONLogin extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(Start.this);
            pDialog.setMessage("Sending Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected JSONObject doInBackground(String... args) {

            user = InputName.getText().toString();
            password = InputPass.getText().toString();

            Functions Function = new Functions();
            JSONObject json = Function.loginUser(user, password);

            // check for login response
            // check log cat fro response
            Log.d("Create Response", json.toString());
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully logged in

                    if (InputCheck.isChecked()){

                        System.out.println("Checkbox is gesetz daten werden gespeichert ");

                        session.saveSession(user, password);
                    }
                    else{
                        System.out.println("Checkbox is leer daten werden NICHT gespeichert");
                    }

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);


                    // closing this screen
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Falscher Login-Name / Falsches Passwort!!!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }








    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_info) {
            Toast.makeText(getApplicationContext(), "Version 0.0.6", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
