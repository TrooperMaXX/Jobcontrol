package de.hoell.jobcontrol.schein;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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
    EditText editTextTeileNr;
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
        /** AW Picker **/
        NumberPicker numberPickerAw = (NumberPicker) rootView.findViewById(R.id.numberPickerAnzahl);
        numberPickerAw.setMaxValue(100);
        numberPickerAw.setMinValue(0);
        numberPickerAw.setWrapSelectorWheel(false);


        editTextTeileNr =  (EditText) rootView.findViewById(R.id.editTextTeileNr);
        EditText editTextBezeichnung =  (EditText) rootView.findViewById(R.id.editTextBezeichnung);

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
                    DBManager dbManager= new DBManager(context);
                    SQLiteDatabase db=dbManager.getReadableDatabase();
                    Cursor result = db.rawQuery("SELECT "+DBManager.COLUMN_BESCHREIBUNG+
                                                " FROM "+
                                                DBManager.TABLE_NAME+
                                                " WHERE " + DBManager.COLUMN_EAN + " = '%" + edit.toString() + "%' ;", null);
                    Log.e("querryyyyy:::","SELECT "+DBManager.COLUMN_BESCHREIBUNG+
                            " FROM "+
                            DBManager.TABLE_NAME+
                            " WHERE " + DBManager.COLUMN_EAN + " = '%" + edit.toString() + "%'"+
                            "LIMIT 5 ;" );
                    int count = result.getCount();
                    String values[] = new String[count+1];
                    int i = 0;
                    while(result.moveToNext())
                    {
                        values[i]= result.getString(result.getColumnIndex(DBManager.COLUMN_BESCHREIBUNG));
                        i++;
                    }
                    System.out.println("mal sehn obs geht: "+values[0]);


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

        //TODO: download und fill der db iorgend wo anders hin machen
        /** String url = getResources().getString(R.string.url_artstamm);
        String output = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Jobcontrol/file.csv"));
        File file = new File(output);

        if(!file.exists()){
            new DownloadFileFromURL(context,url,output).execute();
        }else {
         DBManager dbManager= new DBManager(context);
                dbManager.onUpgrade(dbManager.getWritableDatabase(), 1, 2);
             dbManager.execute(context,file);

        }**/



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

        next.putString("AW" + Position, AW);
        next.putString("WEG" + Position, WEG);
        next.putString("TechNr"+Position,TechNr);
        next.putString("Arbeit"+Position,ARBEIT);
        next.putString("Datum" + Position, Date);
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
