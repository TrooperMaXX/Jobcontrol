package de.hoell.jobcontrol;

/**
 * Created by Hoell on 16.10.2014.
 */

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import de.hoell.jobcontrol.historie.Historie_Activity;

public class SrnSuche extends Fragment {
    Context context;
    public SrnSuche(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       final View rootView = inflater.inflate(R.layout.fragment_srnsuche, container, false);

        Button Button_such = (Button) rootView.findViewById(R.id.button_suchen);
        Button Button_scan = (Button) rootView.findViewById(R.id.button_scan);
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


            }
        });

        return rootView;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//retrieve scan result

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            Toast toast = Toast.makeText(context,
                "SCAN THERE "+scanContent, Toast.LENGTH_SHORT);
            Intent i = new Intent(context, Historie_Activity.class);
            i.putExtra("value_seriennummer", scanContent);
            startActivity(i);
            toast.show();


        }else{
            Toast toast = Toast.makeText(context,
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}