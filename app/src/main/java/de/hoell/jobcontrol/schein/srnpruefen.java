package de.hoell.jobcontrol.schein;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import de.hoell.jobcontrol.Jobcontrol;
import de.hoell.jobcontrol.R;
import de.hoell.jobcontrol.query.DBManager;

/**
 * Created by Hoell on 10.09.2015.
 */
public class srnpruefen extends Fragment {
    private Context context;
private String mSeriennummer;
    public srnpruefen(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_srnabgleich, container, false);

        Bundle args=getArguments();
        context = rootView.getContext();
        EditText editTextSerienummer =  (EditText) rootView.findViewById(R.id.Serienummer_Eingabe);
        if(args!=null){
            editTextSerienummer.setText(args.getString("value_seriennummer"));
        }



        Log.e("args", "lol "+args);
        Button Button_abgleichen = (Button) rootView.findViewById(R.id.button_abgleich);
        Button Button_abgleich_scan = (Button) rootView.findViewById(R.id.button_abgleich_scan);




        Button_abgleichen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Log.e("button_abgleich","clicked");
                EditText editTextSerienummer =  (EditText) rootView.findViewById(R.id.Serienummer_Eingabe);

                String Seriennummer_eingabe = editTextSerienummer.getText().toString().trim();

                DBManager dbManager = new DBManager(context);
                SQLiteDatabase db = dbManager.getReadableDatabase();
                String query = "SELECT * " +
                        "FROM " +
                        DBManager.TABLE_GERSTAMM +
                        " WHERE " + DBManager.COLUMN_GERNR + " LIKE '" + Seriennummer_eingabe + "' ;";
                Cursor result = db.rawQuery(query, null);

                Log.e("querryyyyy:::", query);


                if (result.moveToFirst()) {

                    String GerNr = result.getString(result.getColumnIndex(DBManager.COLUMN_GERNR));
                    String Firma = result.getString(result.getColumnIndex(DBManager.COLUMN_FIRMA));
                    String Str   = result.getString(result.getColumnIndex(DBManager.COLUMN_STR));
                    String Ort   = result.getString(result.getColumnIndex(DBManager.COLUMN_ORT));
                    String Ger   = result.getString(result.getColumnIndex(DBManager.COLUMN_GER));
                    Log.e("Result", GerNr +" "+ Ger+" "+ Firma+" "+ Str+" "+ Ort);

                    arbeit nextFragment = new arbeit();

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    // Replace whatever is in the fragment_container view with this fragment,
                    // and add the transaction to the back stack so the user can navigate back
                    transaction.replace(R.id.frame_container, nextFragment);
                    transaction.addToBackStack(null);
                    Bundle bundle = new Bundle();
                    bundle.putString("Srn", GerNr);
                    bundle.putString("Firma", Firma);
                    bundle.putString("Str", Str);
                    bundle.putString("Ort", Ort);
                    bundle.putString("Ger", Ger);
                    bundle.putInt("Pos",0);
                    bundle.putInt("TeilePos",0);
                    nextFragment.setArguments(bundle);
                    // Commit the transaction
                    transaction.commit();


                }else {
                    Log.e("ERROR","result is null");
                    Toast.makeText(Jobcontrol.getAppCtx(), "Ungültige Seriennummer Bitte manuellen Schein ausfüllen oder DB updaten?", Toast.LENGTH_SHORT).show();
                }




                /*if (mSeriennummer != null) {
                    if (mSeriennummer.equals(Seriennummer_eingabe)){



                    }
                    else{
                        Toast.makeText(Jobcontrol.getAppCtx(), "Ungültige Seriennummer NEUE Maschine?", Toast.LENGTH_SHORT).show();
                    }
                }else
                {
                    Toast.makeText(Jobcontrol.getAppCtx(), "mSeriennummer null!!!!!!!!", Toast.LENGTH_SHORT).show();
                }*/

            }
        });

        Button_abgleich_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                IntentIntegrator scanIntegrator = new IntentIntegrator(srnpruefen.this);
                scanIntegrator.initiateScan();


               /* Intent qrDroid = new Intent( "la.droid.qr.scan" );
                qrDroid.putExtra("la.droid.qr.complete", true);
                try {
                    startActivityForResult(qrDroid, 0);
                    //ja=true;
                } catch (ActivityNotFoundException activity) {
                    //  ja=false;
                    qrDroidRequired(getActivity());
                }*/
            }
        });


        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (intent != null) {

            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            Log.e("scanningResult", "" + scanningResult);
            if (scanningResult != null) {
                String scanContent = scanningResult.getContents();
                String scanFormat = scanningResult.getFormatName();
                if (scanContent != null && scanFormat.equals("DATA_MATRIX")) {
                    DBManager dbManager = new DBManager(context);
                    SQLiteDatabase db = dbManager.getReadableDatabase();
                    String query = "SELECT * " +
                            "FROM " +
                            DBManager.TABLE_GERSTAMM +
                            " WHERE " + DBManager.COLUMN_GERNR + " LIKE '" + scanContent + "' ;";
                    Cursor result = db.rawQuery(query, null);

                    Log.e("querryyyyy:::", query);


                    if (result.moveToFirst()) {

                        String GerNr = result.getString(result.getColumnIndex(DBManager.COLUMN_GERNR));
                        String Firma = result.getString(result.getColumnIndex(DBManager.COLUMN_FIRMA));
                        String Str = result.getString(result.getColumnIndex(DBManager.COLUMN_STR));
                        String Ort = result.getString(result.getColumnIndex(DBManager.COLUMN_ORT));
                        String Ger = result.getString(result.getColumnIndex(DBManager.COLUMN_GER));
                        Log.e("Result", GerNr + " " + Ger + " " + Firma + " " + Str + " " + Ort);

                        arbeit nextFragment = new arbeit();

                        FragmentTransaction transaction = getFragmentManager().beginTransaction();

                        // Replace whatever is in the fragment_container view with this fragment,
                        // and add the transaction to the back stack so the user can navigate back
                        transaction.replace(R.id.frame_container, nextFragment);
                        transaction.addToBackStack(null);
                        Bundle bundle = new Bundle();
                        bundle.putString("Srn", GerNr);
                        bundle.putString("Firma", Firma);
                        bundle.putString("Str", Str);
                        bundle.putString("Ort", Ort);
                        bundle.putString("Ger", Ger);
                        bundle.putInt("Pos", 0);
                        bundle.putInt("TeilePos", 0);
                        nextFragment.setArguments(bundle);
                        // Commit the transaction
                        transaction.commit();


                    } else {
                        Log.e("ERROR", "result is null");
                        Toast.makeText(Jobcontrol.getAppCtx(), "Ungültige Seriennummer Bitte manuellen Schein ausfüllen oder DB updaten?", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast toast = Toast.makeText(context,
                            "No scan data received!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }


    }
}
