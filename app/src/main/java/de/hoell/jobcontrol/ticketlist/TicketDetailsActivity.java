package de.hoell.jobcontrol.ticketlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

import de.hoell.jobcontrol.MainActivity;
import de.hoell.jobcontrol.R;

import de.hoell.jobcontrol.historie.Historie_Activity;
import de.hoell.jobcontrol.query.Functions;
import de.hoell.jobcontrol.session.SessionManager;




public class TicketDetailsActivity extends Activity {

 String user,gebiet;
    public static  int Statusnum;
static String ID;
private static final String TAG_SUCCESS = "success";

    AlertDialog.Builder builderSingle;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getActionBar().setDisplayHomeAsUpEnabled(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_details);
        String Status = getIntent().getStringExtra("value_status");
        Button Button_maps = (Button) findViewById(R.id.button_maps);
        Button Button_save = (Button) findViewById(R.id.button_save);
        Button Button_his = (Button) findViewById(R.id.button_his);
        Button Button_rueck = (Button) findViewById(R.id.button_rueck);
        Button Button_info = (Button) findViewById(R.id.button_info);
        Button Button_eskalation = (Button) findViewById(R.id.button_eskalation);


        int DropPos = getIntent().getIntExtra("value_droppos",0);
        final String Firma = getIntent().getStringExtra("value_firma");
        final String Adresse = getIntent().getStringExtra("value_adresse");
        String Standort = getIntent().getStringExtra("value_standort");
        final String Modell = getIntent().getStringExtra("value_modell");//Gerät
        final String Serienummer = getIntent().getStringExtra("value_serienummer");
        String Stoerung = getIntent().getStringExtra("value_stoerung");
        String Ansprechpartner = getIntent().getStringExtra("value_ansprechpartner");
        String Telefonnummer = getIntent().getStringExtra("value_telefonnummer");
        String Angenommen = getIntent().getStringExtra("value_angenommen");
        String Termin = getIntent().getStringExtra("value_termin");
        String Oeffnung = getIntent().getStringExtra("value_oeffnung");
        String Auftragsnr = getIntent().getStringExtra("value_auftragnr");
        String Wvnr = getIntent().getStringExtra("value_wvnr");
        String Fleet = getIntent().getStringExtra("value_fleet");
        String Annahme = getIntent().getStringExtra("value_annahme");
        final String uri = getIntent().getStringExtra("value_uri");
        Statusnum = getIntent().getIntExtra("value_statusnum",0);
        ID = getIntent().getStringExtra("value_id");

        Log.d("INTENTEXTRA:", Status + " " + DropPos + " " + Adresse + " " + Standort + " " + Modell + " " + Serienummer + " " + Stoerung + " " + Ansprechpartner + " " + Telefonnummer + " " + Termin + " " + Annahme);
        Log.e("Statusnum",String.valueOf(Statusnum));
       if (Statusnum<90){
            Spinner spinnerStatus = (Spinner) findViewById(R.id.status_spinner);
            spinnerStatus.setSelection(DropPos);
            spinnerStatus.setVisibility(View.VISIBLE);
        }
        else{
           Spinner spinnerStatus = (Spinner) findViewById(R.id.status_spinner_eskalation);
         spinnerStatus.setSelection(DropPos);
          spinnerStatus.setVisibility(View.VISIBLE);}






        TextView textViewFirma = (TextView) findViewById(R.id.textViewContentFirma);
        textViewFirma.setText(Firma);

        TextView textViewAdresse = (TextView) findViewById(R.id.textViewContentAdresse);
        textViewAdresse.setText(Adresse);

        TextView textViewStandort = (TextView) findViewById(R.id.textViewContentStandort);
        textViewStandort.setText(Standort);

        TextView textViewModell = (TextView) findViewById(R.id.textViewContentModell);
        textViewModell.setText(Modell);

        TextView textViewSerienummer = (TextView) findViewById(R.id.textViewContentSerienummer);
        textViewSerienummer.setText(Serienummer);

        TextView textViewStoerung = (TextView) findViewById(R.id.textViewContentStoerung);
        textViewStoerung.setText(Stoerung);

        TextView textViewAnsprechpartner = (TextView) findViewById(R.id.textViewContentAnsprechpartner);
        textViewAnsprechpartner.setText(Ansprechpartner);

        TextView textViewTelefonnummer = (TextView) findViewById(R.id.textViewContentTelefonnummer);
        textViewTelefonnummer.setText(Telefonnummer);
        Linkify.addLinks(textViewTelefonnummer, Linkify.PHONE_NUMBERS);

        TextView textViewAngenommen = (TextView) findViewById(R.id.textViewContentAngenommen);
        textViewAngenommen.setText(Angenommen);

        TextView textViewTermin = (TextView) findViewById(R.id.textViewContentTermin);
        textViewTermin.setText(Termin);

        TextView textViewOeffnung = (TextView) findViewById(R.id.textViewContentOeffnung);
        if(Oeffnung==null){Oeffnung=" ";}
        textViewOeffnung.setText(Oeffnung);


        TextView textViewAuftragsnr = (TextView) findViewById(R.id.textViewContentAuftragsnr);
        if(Auftragsnr == null){Auftragsnr=" ";}
        textViewAuftragsnr.setText(Auftragsnr);

        TextView textViewWvnr = (TextView) findViewById(R.id.textViewContentWvnr);
        if(Wvnr.equals("null")){Wvnr=" ";}
        textViewWvnr.setText(Wvnr);

        TextView textViewFleet = (TextView) findViewById(R.id.textViewContentFleet);
        if(Fleet.equals("null")){Fleet=" ";}
        textViewFleet.setText(Fleet);

        TextView textViewAnnahme = (TextView) findViewById(R.id.textViewContentAnnahme);
        textViewAnnahme.setText(Annahme);

        Button_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showMap(Uri.parse(uri));
            }
        });






        Button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Functions Function = new Functions();

                if( Function.isNetworkOnline(TicketDetailsActivity.this)){
                    new JSONSaveDetails().execute();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Keine INternet verbindung", Toast.LENGTH_SHORT).show();}
            }
        });

        Button_his.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                TextView textViewSerienummer =  (TextView) findViewById(R.id.textViewContentSerienummer);
                String Serienummer = textViewSerienummer.getText().toString();
                Intent i = new Intent(getApplicationContext(), Historie_Activity.class);
                i.putExtra("value_seriennummer", Serienummer);
                startActivity(i);

            }
        });

        Button_rueck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                TextView textViewFirma = (TextView) findViewById(R.id.textViewContentFirma);
                String Firma = textViewFirma.getText().toString();
                TextView textViewModel = (TextView) findViewById(R.id.textViewContentModell);
                String Model = textViewModel.getText().toString();
                TextView textViewStoerung = (TextView) findViewById(R.id.textViewContentStoerung);
                String Stoerung = textViewStoerung.getText().toString();
                TextView textViewAnsprechpartner = (TextView) findViewById(R.id.textViewContentAnsprechpartner);
                String Ansprechpartner = textViewAnsprechpartner.getText().toString();

                Intent i = new Intent(getApplicationContext(), Rueckmeldung.class);

                i.putExtra("value_firma", Firma);
                i.putExtra("value_model", Model);
                i.putExtra("value_stoerung", Stoerung);
                i.putExtra("value_ansprechpartner", Ansprechpartner);
                i.putExtra("value_id", ID);

                startActivity(i);

            }
        });

        Button_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Functions Function = new Functions();
                if (Function.isNetworkOnline(TicketDetailsActivity.this)) {

                    Intent i = new Intent(getApplicationContext(), InfoActivity.class);
                    i.putExtra("value_id", ID);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Keine INternet verbindung", Toast.LENGTH_LONG).show();
                }

            }
        });

        Button_eskalation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                TextView textViewFirma = (TextView) findViewById(R.id.textViewContentFirma);
                String Firma = textViewFirma.getText().toString();
                TextView textViewModel = (TextView) findViewById(R.id.textViewContentModell);
                String Model = textViewModel.getText().toString();
                TextView textViewStoerung = (TextView) findViewById(R.id.textViewContentStoerung);
                String Stoerung = textViewStoerung.getText().toString();
                TextView textViewAnsprechpartner = (TextView) findViewById(R.id.textViewContentAnsprechpartner);
                String Ansprechpartner = textViewAnsprechpartner.getText().toString();

                Intent i = new Intent(getApplicationContext(), Eskalation.class);

                i.putExtra("value_firma", Firma);
                i.putExtra("value_model", Model);
                i.putExtra("value_stoerung", Stoerung);
                i.putExtra("value_ansprechpartner", Ansprechpartner);
                i.putExtra("value_id", ID);

                startActivity(i);

            }
        });
      builderSingle = new AlertDialog.Builder(
                TicketDetailsActivity.this);
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select One Name:-");
       arrayAdapter = new ArrayAdapter<String>(
                TicketDetailsActivity.this,
                android.R.layout.select_dialog_singlechoice);
        SessionManager session = new SessionManager(this);
        gebiet ="null";
                gebiet=session.getGebiet();
        user=session.getUser();

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ticket_details, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_verschieben) {

            if (!gebiet.equals("null")){


                try {
                    JSONObject gebietstech = new JSONGebTech(gebiet,user).execute().get();
                    int g_success = gebietstech.getInt(TAG_SUCCESS);
                    if (g_success == 1) {
                        JSONArray gebtech = gebietstech.getJSONArray("gebtech");
                        for (int i = 0; i < gebtech.length(); i++) {
                            JSONObject c = gebtech.getJSONObject(i);
                            String techniker= c.getString("kuerzel");
                            arrayAdapter.add(techniker);
                        }

                    }


                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                builderSingle.setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builderSingle.setAdapter(arrayAdapter,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String strName = arrayAdapter.getItem(which);
                                AlertDialog.Builder builderInner = new AlertDialog.Builder(
                                        TicketDetailsActivity.this);

                                Functions Function = new Functions();

                                if( Function.isNetworkOnline(TicketDetailsActivity.this)){
                                    try {
                                        JSONObject verschoben=new JSONVerschieben(ID,strName,user).execute().get();

                                        try {

                                            int success = verschoben.getInt(TAG_SUCCESS);

                                            if (success == 1) {
                                                System.out.println("LOL gehtb sofort"+ success);


                                                builderInner.setMessage("Ticket an " + strName + " verschoben");
                                                builderInner.setTitle("Ticket Verschoben");
                                                builderInner.setPositiveButton("Ok",
                                                        new DialogInterface.OnClickListener() {

                                                            @Override
                                                            public void onClick(
                                                                    DialogInterface dialog,
                                                                    int which) {
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                builderInner.show();

                                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(i);


                                            }
                                            else{

                                                System.out.println("Y U NOT FUNCTION...");

                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    } catch (InterruptedException | ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Keine INternet verbindung", Toast.LENGTH_SHORT).show();}

                            }
                        });
            }else{
                Toast.makeText(getApplicationContext(), "Keine anderen Techniker in deinem Gebiet Verfügbar", Toast.LENGTH_SHORT).show();}

            builderSingle.show();



    /*        Functions Function = new Functions();

            if( Function.isNetworkOnline(TicketDetailsActivity.this)){
                new JSONSaveDetails().execute();
            }
            else {
                Toast.makeText(getApplicationContext(), "Keine INternet verbindung", Toast.LENGTH_SHORT).show();}
    */
        }

        return super.onOptionsItemSelected(item);
    }

    public void showMap(Uri geoLocation) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }



     /**  **/




    public class JSONSaveDetails extends AsyncTask<Integer, Integer, JSONObject> {




        @Override
        protected JSONObject doInBackground(Integer... args) {
            Functions Function = new Functions();
            JSONObject json;
            if (Statusnum<99){
                Spinner spinnerStatus = (Spinner) findViewById(R.id.status_spinner);
                int selected = spinnerStatus.getSelectedItemPosition();

                switch (selected){

                    case 0:
                        // Status = "Unbearbeitet";
                        Statusnum = 10;
                        break;
                    case 1:
                        // Status = "Fahrt";
                        Statusnum = 11;
                        break;
                    case 2:
                        //  Status = "In arbeit";
                        Statusnum = 12;
                        break;
                    case 3:
                        //  Status = "offen";
                        Statusnum = 13;
                        break;

                    case 4:
                        //  Status = "Erledigt";
                        Statusnum = 15;
                        break;
                    case 5:
                        //  Status = "wartet";
                        Statusnum = 16;
                        break;
                    case 6:
                        //   Status = "Ware bestellt";
                        Statusnum = 17;
                        break;
                    case 7:
                        //  Status = "Ware da";
                        Statusnum = 18;
                        break;
                    case 8:
                        //  Status = "Ware benötigt";
                        Statusnum = 19;
                        break;
                    case 9:
                        //  Status = "installiert";
                        Statusnum = 20;
                        break;
                    case 10:
                        // Status = "Eskalation";
                        Statusnum = 99;
                        break;

                }
                // Log.d("Status",Statusnum);


                json = Function.SaveDetails(Statusnum,ID,user);

            }
            else{
                Spinner spinnerStatus = (Spinner) findViewById(R.id.status_spinner_eskalation);
                int selected = spinnerStatus.getSelectedItemPosition();

                switch (selected){

                    case 0:
                        // Status = "Eskalation";
                        Statusnum = 99;
                        break;
                    case 1:
                        // Status = "Eskalation in arbeit";
                        Statusnum = 100;
                        break;
                    case 2:
                        //  Status = "Eskaltion erledigt";
                        Statusnum = 101;
                        break;




                }
                // Log.d("Status",Statusnum);


                json = Function.SaveDetails(Statusnum,ID,user);
            }




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


    private class JSONGebTech extends AsyncTask<Integer, Integer, JSONObject> {
        private String mGebiet,mUser;
        public JSONGebTech(String gebiet,String user) {
            mGebiet =gebiet;
            mUser = user;
        }

        @Override
        protected JSONObject doInBackground(Integer... params) {
            Functions Function = new Functions();
            JSONObject json_gebtech = Function.GebTech(mGebiet,mUser);

            return json_gebtech;
        }
    }

    private class JSONVerschieben extends AsyncTask<Integer, Integer, JSONObject> {
        private String mID ,mTechniker,mUser;
        public JSONVerschieben(String ID,String techniker,String user) {
            mID=ID;
            mTechniker=techniker;
            mUser=user;
        }

        @Override
        protected JSONObject doInBackground(Integer... params) {
            Functions Function = new Functions();
            JSONObject json_gebtech = Function.Verschieben(mID, mTechniker,mUser);

            return json_gebtech;
        }
    }






}
