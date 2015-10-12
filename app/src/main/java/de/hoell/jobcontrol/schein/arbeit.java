package de.hoell.jobcontrol.schein;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import de.hoell.jobcontrol.R;
import de.hoell.jobcontrol.session.SessionManager;

/**
 * A placeholder fragment containing a simple view.
 */
public class arbeit extends Fragment {
    private Context context;
    public arbeit() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.fab);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);


        final View rootView = localInflater.inflate(R.layout.fragment_arbeit, container, false);


        context = rootView.getContext();
        Bundle args=getArguments();
        /** AW Picker **/
        NumberPicker numberPickerAw = (NumberPicker) rootView.findViewById(R.id.numberPickerAw);
        numberPickerAw.setMaxValue(100);
        numberPickerAw.setMinValue(0);
        numberPickerAw.setWrapSelectorWheel(false);
        /** Weg Picker **/
        NumberPicker numberPickerWeg = (NumberPicker) rootView.findViewById(R.id.numberPickerWeg);
        numberPickerWeg.setMaxValue(100);
        numberPickerWeg.setMinValue(0);
        numberPickerWeg.setWrapSelectorWheel(false);

        EditText editTextTechnikernr =  (EditText) rootView.findViewById(R.id.editTextTechniker);
        SessionManager session = new SessionManager(context);

        editTextTechnikernr.setText(String.valueOf(session.getTechNum()));
        String mSeriennummer =args.getString("Srn");
        final   int pos =args.getInt("Pos");
        Log.e("args", mSeriennummer + "lol " + args);
        final  Bundle next_args=args;

        FloatingActionButton fab_next = (FloatingActionButton) rootView.findViewById(R.id.fab_next);
        fab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle next= addValues(next_args,rootView,String.valueOf(pos));
                Log.e("final next", ""+next );

                eteile nextFragment = new eteile();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.fragment_container, nextFragment);
                transaction.addToBackStack(null);

                nextFragment.setArguments(next);
                // Commit the transaction
                transaction.commit();

                Toast.makeText(context, "next", Toast.LENGTH_SHORT).show();



            }
        });
        FloatingActionButton fab_add = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle next= addValues(next_args,rootView,String.valueOf(pos));

                arbeit nextFragment = new arbeit();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.fragment_container, nextFragment);
                transaction.addToBackStack(null);
                next.putInt("Pos", pos + 1);
                nextFragment.setArguments(next);
                // Commit the transaction
                transaction.commit();
                Toast.makeText(context, "add", Toast.LENGTH_SHORT).show();


            }
        });



       /** Button_abgleichen.setOnClickListener(new View.OnClickListener() {
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
        });**/

       /** Button_abgleich_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                Intent qrDroid = new Intent( "la.droid.qr.scan" );
                qrDroid.putExtra("la.droid.qr.complete", true);
                try {
                    startActivityForResult(qrDroid, 0);
                    //ja=true;
                } catch (ActivityNotFoundException activity) {
                    //  ja=false;
                    qrDroidRequired(getActivity());
                }
            }
        });**/


        return rootView;
    }

    private Bundle addValues(Bundle next_args, View rootView,String Position) {
        Bundle next= new Bundle(next_args);
        DatePicker Datum = (DatePicker) rootView.findViewById(R.id.datePickerArbeit);
        NumberPicker Aw = (NumberPicker) rootView.findViewById(R.id.numberPickerAw);
        NumberPicker Weg = (NumberPicker) rootView.findViewById(R.id.numberPickerWeg);
        EditText TechnikerNr =  (EditText) rootView.findViewById(R.id.editTextTechniker);
        EditText Arbeit =  (EditText) rootView.findViewById(R.id.editTextArbeit);

        String Date = String.valueOf(Datum.getYear())+"-"+String.valueOf(Datum.getMonth()+1)+"-"+String.valueOf(Datum.getDayOfMonth());
        Log.e("DATUM!!!", Date);
        String AW =String.valueOf(Aw.getValue());
        String WEG =String.valueOf(Weg.getValue());
        String TechNr = String.valueOf(TechnikerNr.getText());
        String ARBEIT =String.valueOf(Arbeit.getText());

        next.putString("AW"+Position,AW);
        next.putString("WEG"+Position,WEG);
        next.putString("TechNr"+Position,TechNr);
        next.putString("Arbeit"+Position,ARBEIT);
        next.putString("Datum"+Position, Date);
        Log.e("NextBundle",""+next);
        return next;
    }

}
