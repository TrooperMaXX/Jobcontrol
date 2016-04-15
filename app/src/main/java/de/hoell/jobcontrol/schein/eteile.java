package de.hoell.jobcontrol.schein;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import de.hoell.jobcontrol.R;
import de.hoell.jobcontrol.query.DBManager;

/**
 * A placeholder fragment containing a simple view.
 */
public class eteile extends Fragment {
    private Context context;
    EditText editTextTeileNr,editTextBezeichnung;
    CheckBox Kleinteile,Entsorgung;
       int teilepos;
    public eteile() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.fab);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);


        final View rootView = localInflater.inflate(R.layout.fragment_eteile, container, false);



        context = rootView.getContext();
        Bundle args=getArguments();
       teilepos =args.getInt("TeilePos");
        final  Bundle next_args=args;

        if(teilepos==0){
            Kleinteile = (CheckBox) rootView.findViewById(R.id.checkBoxKleinteile);
            Entsorgung = (CheckBox) rootView.findViewById(R.id.checkBoxEntsorgung);

            Kleinteile.setVisibility(View.VISIBLE);
            Entsorgung.setVisibility(View.VISIBLE);
        }


        /** AW Picker **/
        final NumberPicker numberPickerAnz = (NumberPicker) rootView.findViewById(R.id.numberPickerAnzahl);
        numberPickerAnz.setMaxValue(100);
        numberPickerAnz.setMinValue(0);
        numberPickerAnz.setWrapSelectorWheel(false);


        editTextTeileNr =  (EditText) rootView.findViewById(R.id.editTextTeileNr);
        editTextBezeichnung =  (EditText) rootView.findViewById(R.id.editTextBezeichnung);

        editTextTeileNr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }


            @Override
            public void afterTextChanged(Editable edit) {
                if (edit.length() >= 3) {

                    // Business logic for search here
                    DBManager dbManager = new DBManager(context);
                    SQLiteDatabase db = dbManager.getReadableDatabase();
                    String query = "SELECT " + DBManager.COLUMN_BESCHREIBUNG +
                            " FROM " +
                            DBManager.TABLE_ARTSTAMM +
                            " WHERE " + DBManager.COLUMN_EAN + " LIKE '" + edit.toString() + "%' ;";
                    Cursor result = db.rawQuery(query, null);

                    Log.e("querryyyyy:::", query);
                    int count = result.getCount();
                    String values[] = new String[count + 1];
                    int i = 0;

                    while (result.moveToNext()) {
                        values[i] = result.getString(result.getColumnIndex(DBManager.COLUMN_BESCHREIBUNG));
                        System.out.println("values[" + i + "]: " + values[i]);
                        i++;
                    }
                    result.close();
                    System.out.println("länge " + values.length);
                    if (values.length > 0) {
                        editTextBezeichnung.setText(values[0]);
                    }

                    numberPickerAnz.setValue(1);
                }else{
                    numberPickerAnz.setValue(0);
                }
            }
        });





        Button Button_scan = (Button) rootView.findViewById(R.id.button_bscan);

        Button_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                IntentIntegrator scanIntegrator = new IntentIntegrator(eteile.this);
                scanIntegrator.initiateScan();


            }
        });

        FloatingActionButton fab_next = (FloatingActionButton) rootView.findViewById(R.id.fab_next);
        fab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle next = addValues(next_args, rootView, String.valueOf(teilepos));
                Log.e("final next", "" + next);
                if(next.getBoolean("Kleinteile")){
                    teilepos++;
                    next.putString("Anz" + teilepos, "1");
                    next.putString("TeileNr" + teilepos, "919001100");
                    next.putString("ArtNr" + teilepos, "919001100");
                    next.putString("Bez" + teilepos, "Kleinteile/Reinigungsmateriel");
                    next.putInt("TeilePos", teilepos);
                }
                if(next.getBoolean("Entsorgung")){
                    teilepos++;
                    next.putString("Anz" + teilepos, "1");
                    next.putString("TeileNr" + teilepos, "919001201");
                    next.putString("ArtNr" + teilepos, "919001201");
                    next.putString("Bez" + teilepos, "Entsorgung");
                    next.putInt("TeilePos", teilepos);
                }
                vde nextFragment = new vde();

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
        FloatingActionButton fab_add = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle next = addValues(next_args, rootView, String.valueOf(teilepos));

                eteile nextFragment = new eteile();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.frame_container, nextFragment);
                transaction.addToBackStack(null);
                next.putInt("TeilePos", teilepos + 1);
                nextFragment.setArguments(next);
                // Commit the transaction
                transaction.commit();
                Toast.makeText(context, "add", Toast.LENGTH_SHORT).show();


            }
        });





        return rootView;
    }

    private Bundle addValues(Bundle next_args, View rootView,String Position) {
        Bundle next= new Bundle(next_args);

        NumberPicker numberPickerAnz = (NumberPicker) rootView.findViewById(R.id.numberPickerAnzahl);


        String Anzahl =String.valueOf(numberPickerAnz.getValue());

        String TeileNr = String.valueOf(editTextTeileNr.getText());
        String Bezeichnung =String.valueOf(editTextBezeichnung.getText());

        if (Bezeichnung.trim().length()>2){

        DBManager dbManager = new DBManager(context);
        SQLiteDatabase db = dbManager.getReadableDatabase();
        String query = "SELECT " + DBManager.COLUMN_EAN +","+DBManager.COLUMN_ARTNR +
                " FROM " +
                DBManager.TABLE_ARTSTAMM +
                " WHERE " + DBManager.COLUMN_BESCHREIBUNG + " LIKE '"+Bezeichnung+"';";
        Cursor result = db.rawQuery(query, null);

        Log.e("querryyyyy:::", query);
        int count = result.getCount();
        String values[] = new String[count + 1];
        String artnr[] = new String[count + 1];
        int i = 0;

        while (result.moveToNext()) {
            values[i] = result.getString(result.getColumnIndex(DBManager.COLUMN_EAN));
            artnr[i] = result.getString(result.getColumnIndex(DBManager.COLUMN_ARTNR));
            System.out.println("values[" + i + "]: " + values[i]);
            System.out.println("artnr[" + i + "]: " + values[i]);
            i++;
        }
            result.close();
        System.out.println("länge " + values.length);
        if (values.length > 0) {
            next.putString("TeileNr" + Position, values[0]);
            next.putString("ArtNr" + Position, artnr[0]);
        }
        next.putString("Bez" + Position, Bezeichnung);

        next.putString("Anz" + Position, Anzahl);

        }
        if (teilepos==0) {
            next.putBoolean("Kleinteile", Kleinteile.isChecked());
            next.putBoolean("Entsorgung",Entsorgung.isChecked());
        }


        Log.e("NextBundle", "" + next);
        return next;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//retrieve scan result

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        Log.e("scanningResult",""+scanningResult);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            if (scanContent != null){

                editTextTeileNr.setText(scanContent);
                Toast toast = Toast.makeText(context,
                        "SCAN THERE "+scanContent, Toast.LENGTH_SHORT);

                toast.show();
            }

        }else{
            Toast toast = Toast.makeText(context,
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }


    }






}
