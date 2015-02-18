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

import de.hoell.jobcontrol.historie.Historie_Activity;

public class SrnSuche extends Fragment {
    Context context;
    public SrnSuche(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       final View rootView = inflater.inflate(R.layout.fragment_srnsuche, container, false);

        Button Button_such = (Button) rootView.findViewById(R.id.button_suchen);
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

        return rootView;
    }
}