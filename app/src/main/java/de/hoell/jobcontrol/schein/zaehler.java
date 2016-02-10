package de.hoell.jobcontrol.schein;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.hoell.jobcontrol.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class zaehler extends Fragment {
    private Context context;
    EditText editTextFarbe,editTextSw;
    TextView textViewGesamt;
    public zaehler() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.fab);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);


        final View rootView = localInflater.inflate(R.layout.fragment_zaehler, container, false);



        context = rootView.getContext();

        final  Bundle next_args= getArguments();




        editTextFarbe =  (EditText) rootView.findViewById(R.id.editTextFarb);
        editTextSw =  (EditText) rootView.findViewById(R.id.editTextSw);
        textViewGesamt = (TextView) rootView.findViewById(R.id.textViewGesamt);

        editTextFarbe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }


            @Override
            public void afterTextChanged(Editable edit) {
                if (editTextFarbe.length()>0&&editTextSw.length()>0) {
                    int gesamt= Integer.parseInt(editTextFarbe.getText().toString())+Integer.parseInt(editTextSw.getText().toString());
                    Log.e("gesamt",""+gesamt);
                    textViewGesamt.setText("Gesamt: "+ gesamt);
                }
            }
        });
        editTextSw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }


            @Override
            public void afterTextChanged(Editable edit) {
                if (editTextFarbe.length()>0&&editTextSw.length()>0){

                int gesamt= Integer.parseInt(editTextFarbe.getText().toString())+Integer.parseInt(editTextSw.getText().toString());
                    Log.e("gesamt",""+gesamt);
                textViewGesamt.setText("Gesamt: " + gesamt);
            }
            }
        });


        FloatingActionButton fab_next = (FloatingActionButton) rootView.findViewById(R.id.fab_next);
            fab_next.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){

                Bundle next = addValues(next_args);
                Log.e("final next", "" + next);

                pruefen nextFragment = new pruefen();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.frame_container, nextFragment);
                transaction.addToBackStack(null);

                nextFragment.setArguments(next);
                // Commit the transaction
                transaction.commit();

                Toast.makeText(context, "next", Toast.LENGTH_SHORT).show();


            }
        });




        return rootView;
    }

    private Bundle addValues(Bundle next_args) {
        Bundle next= new Bundle(next_args);



        String Farb = String.valueOf(editTextFarbe.getText());
        String Sw =String.valueOf(editTextSw.getText());
        String Ges =String.valueOf(textViewGesamt.getText());

        next.putString("Farb", Farb);
        next.putString("Ges", Ges);
        next.putString("Sw", Sw);


        Log.e("NextBundle", "" + next);
        return next;
    }






}
