package de.hoell.jobcontrol.schein;

import android.app.Fragment;
import android.app.FragmentTransaction;
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

import de.hoell.jobcontrol.Jobcontrol;
import de.hoell.jobcontrol.R;

/**
 * Created by Hoell on 10.09.2015.
 */
public class srnabgleich extends Fragment {
    private Context context;
    private String mSeriennummer;
    public srnabgleich(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_srnabgleich, container, false);

        Bundle args=getArguments();
        context = rootView.getContext();
        mSeriennummer =args.getString("value_seriennummer");
        Log.e("args",mSeriennummer + "lol "+args);
        Button Button_abgleichen = (Button) rootView.findViewById(R.id.button_abgleich);
        Button Button_abgleich_scan = (Button) rootView.findViewById(R.id.button_abgleich_scan);




        Button_abgleichen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Log.e("button_abgleich","clicked");
                EditText editTextSerienummer =  (EditText) rootView.findViewById(R.id.Serienummer_Eingabe);

                String Seriennummer_eingabe = editTextSerienummer.getText().toString().trim();


                if (mSeriennummer != null) {
                    if (mSeriennummer.equals(Seriennummer_eingabe)){

                        arbeit nextFragment = new arbeit();

                        FragmentTransaction transaction = getFragmentManager().beginTransaction();

                        // Replace whatever is in the fragment_container view with this fragment,
                        // and add the transaction to the back stack so the user can navigate back
                        transaction.replace(R.id.fragment_container, nextFragment);
                        transaction.addToBackStack(null);
                        Bundle bundle = new Bundle();
                        bundle.putString("Srn", mSeriennummer);
                        bundle.putInt("Pos",0);
                        nextFragment.setArguments(bundle);
                         // Commit the transaction
                        transaction.commit();

                    }
                    else{
                        Toast.makeText(Jobcontrol.getAppCtx(), "Ungültige Seriennummer NEUE Maschine?", Toast.LENGTH_SHORT).show();
                    }
                }else
                {
                    Toast.makeText(Jobcontrol.getAppCtx(), "mSeriennummer null!!!!!!!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Button_abgleich_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                IntentIntegrator scanIntegrator = new IntentIntegrator(srnabgleich.this);
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

        if (intent!= null){

            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            Log.e("scanningResult",""+scanningResult);
            if (scanningResult != null) {
                String scanContent = scanningResult.getContents();
                String scanFormat = scanningResult.getFormatName();
                if (scanContent != null && scanFormat.equals("QR_CODE")){

                    if (mSeriennummer.equals(scanContent)) {
                        Toast toast = Toast.makeText(context,
                                "Gerät stimmt überein" + scanContent, Toast.LENGTH_SHORT);
                        toast.show();
                        arbeit nextFragment = new arbeit();

                        FragmentTransaction transaction = getFragmentManager().beginTransaction();

                        // Replace whatever is in the fragment_container view with this fragment,
                        // and add the transaction to the back stack so the user can navigate back
                        transaction.replace(R.id.fragment_container, nextFragment);
                        transaction.addToBackStack(null);
                        Bundle bundle = new Bundle();
                        bundle.putString("Srn", mSeriennummer);
                        bundle.putInt("Pos", 0);
                        nextFragment.setArguments(bundle);
                        // Commit the transaction
                        transaction.commit();
                    }

                }else{
                   Toast toast = Toast.makeText(context,
                            "Gerät stimmt nicht überein"+scanContent, Toast.LENGTH_SHORT);
                    toast.show();
                }




            }else{
                Toast toast = Toast.makeText(context,
                        "No scan data received!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }




}
