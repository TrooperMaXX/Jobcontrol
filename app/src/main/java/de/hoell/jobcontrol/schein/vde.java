package de.hoell.jobcontrol.schein;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.hoell.jobcontrol.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class vde extends Fragment {
    private Context context;
    EditText RPE,RISO,ILEAK;
    CheckBox VDE;
    private List<EditText> editTextList = new ArrayList<EditText>();
    public vde() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.fab);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);


        final View rootView = localInflater.inflate(R.layout.fragment_vde, container, false);



        context = rootView.getContext();
        final Bundle args=getArguments();
        VDE = (CheckBox) rootView.findViewById(R.id.checkBoxVDE);
        RPE = (EditText) rootView.findViewById(R.id.editTextRPE);
        editTextList.add(RPE);
        RISO = (EditText) rootView.findViewById(R.id.editTextRISO);
        editTextList.add(RISO);
        ILEAK = (EditText) rootView.findViewById(R.id.editTextILEAK);
        editTextList.add(ILEAK);

        FloatingActionButton fab_next = (FloatingActionButton) rootView.findViewById(R.id.fab_next);

        fab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               boolean ok=true;


                if (!VDE.isChecked()) {
                    args.putBoolean("VDE", VDE.isChecked());
                }else if(!RPE.getText().toString().isEmpty()&&!RISO.getText().toString().isEmpty()&&!ILEAK.getText().toString().isEmpty()){

                    args.putBoolean("VDE", VDE.isChecked());
                    args.putString("RPE", RPE.getText().toString());
                    args.putString("RISO",RISO.getText().toString());
                    args.putString("ILEAK", ILEAK.getText().toString());

                    int teilepos=args.getInt("TeilePos");
                    teilepos++;
                    args.putString("Anz" + teilepos, "1");
                    args.putString("TeileNr" + teilepos, "912562212");
                    args.putString("ArtNr" + teilepos, "912562212");
                    args.putString("Bez" + teilepos, "VDE geprüft");
                    args.putInt("TeilePos", teilepos);
                }else{
                    for(EditText edit : editTextList){
                        if(TextUtils.isEmpty(edit.getText())){
                            // EditText was empty
                            // Do something fancy
                            edit.setError("bitte ausfüllen!");
                            ok=false;
                        }
                    }
                }

                if(ok){
                Fragment nextFragment;
                if(args.getInt("zAnz")>0) {
                     nextFragment = new zaehler();
                }else{
                     nextFragment = new pruefen();
                }
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.frame_container, nextFragment);
                transaction.addToBackStack(null);
                Log.e("args",args.toString());
                nextFragment.setArguments(args);
                // Commit the transaction
                transaction.commit();

                Toast.makeText(context, "next", Toast.LENGTH_SHORT).show();
                }
            }


        });


        return rootView;
    }








}
