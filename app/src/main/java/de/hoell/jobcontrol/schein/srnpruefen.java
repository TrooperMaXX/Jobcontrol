package de.hoell.jobcontrol.schein;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
   public EditText editTextAnsprechpartner;
    public EditText mError;
    public TextView Error;
    private boolean ok=true;

    public srnpruefen(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_srnabgleich, container, false);

        final Bundle args=getArguments();
        context = rootView.getContext();
        EditText editTextSerienummer =  (EditText) rootView.findViewById(R.id.Serienummer_Eingabe);
        editTextAnsprechpartner = (EditText) rootView.findViewById(R.id.Ansprechpartner_Eingabe);

        Error =  (TextView) rootView.findViewById(R.id.TextError);

        Error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getErrorDialog();
            }
        });

        TextView textView = (TextView) rootView.findViewById(R.id.textView25);
        TextView textView2 = (TextView) rootView.findViewById(R.id.textView26);


        Log.e("args", "lol " + args);
        final Button Button_abgleichen = (Button) rootView.findViewById(R.id.button_abgleich);
        final Button Button_abgleich_scan = (Button) rootView.findViewById(R.id.button_abgleich_scan);
        Button_abgleich_scan.setEnabled(false);
        Button_abgleichen.setEnabled(false);


        editTextAnsprechpartner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }


            @Override
            public void afterTextChanged(Editable edit) {
                if (edit.length() >= 3&&Error.getText().toString().trim().length()>3) {
                    Button_abgleichen.setEnabled(true);
                    Button_abgleich_scan.setEnabled(true);

                }else{
                        Button_abgleich_scan.setEnabled(false);
                        Button_abgleichen.setEnabled(false);
                    }
            }
        });


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
                    int zanz = result.getInt(result.getColumnIndex(DBManager.COLUMN_ZANZ));
                    int z1 = result.getInt(result.getColumnIndex(DBManager.COLUMN_Z1));
                    int z2 = result.getInt(result.getColumnIndex(DBManager.COLUMN_Z2));
                    Log.e("Result", GerNr +" "+ Ger+" "+ Firma+" "+ Str+" "+ Ort+ " "+ zanz);

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
                    bundle.putInt("zAnz", zanz);
                    bundle.putInt("z1", z1);
                    bundle.putInt("z2", z2);
                    bundle.putInt("Pos", 0);
                    bundle.putInt("TeilePos", 0);

                    if (args!=null) {
                        bundle.putString("AuaNr", args.getString("value_auftragsnr"));
                        bundle.putString("Name", args.getString("value_name"));
                        bundle.putString("TicketID", args.getString("value_id"));
                        bundle.putString("Standort", args.getString("value_standort"));
                        bundle.putString("Error", args.getString("value_error"));
                        bundle.putString("email", args.getString("value_email"));
                    } else {
                        bundle.putString("AuaNr", "");
                        bundle.putString("email", "");
                        bundle.putString("Standort", "");
                        bundle.putString("Error", Error.getText().toString());
                        bundle.putString("Name",  editTextAnsprechpartner.getText().toString());
                    }


                    nextFragment.setArguments(bundle);
                    // Commit the transaction
                    transaction.commit();


                }else {
                    Log.e("ERROR","result is null");
                    Toast.makeText(Jobcontrol.getAppCtx(), "Ungültige Seriennummer Bitte manuellen Schein ausfüllen oder DB updaten?", Toast.LENGTH_SHORT).show();
                }
                result.close();




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
        if(args!=null){
            editTextSerienummer.setText(args.getString("value_seriennummer"));
            Button_abgleich_scan.setVisibility(View.GONE);
            editTextAnsprechpartner.setVisibility(View.GONE);
            Error.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
            Button_abgleichen.setEnabled(true);
        }

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
                        int zanz = result.getInt(result.getColumnIndex(DBManager.COLUMN_ZANZ));
                        int z1 = result.getInt(result.getColumnIndex(DBManager.COLUMN_Z1));
                        int z2 = result.getInt(result.getColumnIndex(DBManager.COLUMN_Z2));
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
                        bundle.putInt("zAnz", zanz);
                        bundle.putInt("z1", z1);
                        bundle.putInt("z2", z2);
                        bundle.putInt("Pos", 0);
                        bundle.putInt("TeilePos", 0);

                        bundle.putString("Name",  editTextAnsprechpartner.getText().toString());
                        bundle.putString("AuaNr", "");
                        bundle.putString("Standort", "");
                        bundle.putString("Error", Error.getText().toString());

                        nextFragment.setArguments(bundle);
                        // Commit the transaction
                        transaction.commit();


                    } else {
                        Log.e("ERROR", "result is null");
                        Toast.makeText(Jobcontrol.getAppCtx(), "Ungültige Seriennummer Bitte manuellen Schein ausfüllen oder DB updaten?", Toast.LENGTH_SHORT).show();
                    }
                    result.close();

                } else {
                    Toast toast = Toast.makeText(context,
                            "No scan data received!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }


    }
    private void getErrorDialog() {
        View mView = View.inflate(context, R.layout.dialog_arbeit, null);
        mError = ((EditText) mView.findViewById(R.id.getarbeit));
        mError.setText(Error.getText().toString());
        mError.setSelection(mError.getText().length());

        final InputMethodManager mInputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.restartInput(mView);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder( context);
        mBuilder.setTitle(getString(R.string.geterror));
        mBuilder.setPositiveButton(getString(R.string.save), new Dialog.OnClickListener() {
            public void onClick(DialogInterface mDialogInterface, int mWhich) {
                String mGetErrorString = mError.getText().toString().trim();

                if (mGetErrorString.length() > 3) {
                    Error.setText(mGetErrorString);
                    Error.setError(null);
                    mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    mDialogInterface.dismiss();
                }else{
                    Error.setError("Bitte Störung eingeben");

                }
            }
        });

        mBuilder.setNegativeButton(getString(R.string.cancel), new Dialog.OnClickListener() {
            public void onClick(DialogInterface mDialogInterface, int mWhich) {

                mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                mDialogInterface.dismiss();

            }
        });

        mBuilder.setView(mView);
        mBuilder.show();

        if (mInputMethodManager != null) {
            mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }


}
