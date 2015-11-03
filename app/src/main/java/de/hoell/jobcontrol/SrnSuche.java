package de.hoell.jobcontrol;

/**
 * Created by Hoell on 16.10.2014.
 */

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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

import de.hoell.jobcontrol.ticketlist.Historie_Activity;

public class SrnSuche extends Fragment {
    Context context;
    public SrnSuche(){}
    boolean ja=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       final View rootView = inflater.inflate(R.layout.fragment_srnsuche, container, false);

        Button Button_such = (Button) rootView.findViewById(R.id.button_suchen);
        Button Button_scan = (Button) rootView.findViewById(R.id.button_scan);
       // Button Button_pdf = (Button) rootView.findViewById(R.id.button_pdf);
        context = rootView.getContext();

        Button_such.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                EditText editTextSerienummer =  (EditText) rootView.findViewById(R.id.editText_Suchen);
                String Serienummer = editTextSerienummer.getText().toString();
                Intent i = new Intent(context, Historie_Activity.class);
                i.putExtra("value_seriennummer", Serienummer);
                startActivity(i);

            }
        });
        Button_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                IntentIntegrator scanIntegrator = new IntentIntegrator(SrnSuche.this);
                scanIntegrator.initiateScan();


                /*
                TO DO: QRDROID
                Intent qrDroid = new Intent( "la.droid.qr.scan" );
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

       /** Button_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Intent intent = new Intent("biz.binarysolutions.fasp.FILL_AND_SIGN");
                Uri data = Uri.fromFile(new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/eSchein.pdf"))));
                intent.setData(data);

                intent.setComponent(
                        new ComponentName(
                                "biz.binarysolutions.fasp",
                                "biz.binarysolutions.fasp.Fill"
                        )
                );


                startActivity(intent);



       }

        });
    **/
        return rootView;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//retrieve scan result

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        Log.e("scanningResult",""+scanningResult);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            if (scanContent != null){


            Toast toast = Toast.makeText(context,
                "SCAN THERE "+scanContent, Toast.LENGTH_SHORT);
            Intent i = new Intent(context, Historie_Activity.class);
            i.putExtra("value_seriennummer", scanContent);
            startActivity(i);
            toast.show();
        }

        }else{
            Toast toast = Toast.makeText(context,
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }

        /*TO DO: QRDROID
        if (intent!= null){
            String result = intent.getExtras().getString("la.droid.qr.result");
            if (result != null) {

                Toast toast = Toast.makeText(context,
                        "SCAN THERE "+result, Toast.LENGTH_SHORT);
                Intent i = new Intent(context, Historie_Activity.class);
                i.putExtra("value_seriennummer", result);
                startActivity(i);
                toast.show();


            }else{
                Toast toast = Toast.makeText(context,
                        "No scan data received!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }*/
    }


    /* TO DO: QRDROID
    public static void qrDroidRequired( final Activity activity ) {
        //Apparently, QR Droid is not installed, or it's previous to version 3.5
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("QR Droid fehlt. Bitte im Market Downloaden")
                .setCancelable(true)
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Download from Market", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://market.android.com/details?id=la.droid.qr.priva")));
                    }
                });
        builder.create().show();
    }*/

}