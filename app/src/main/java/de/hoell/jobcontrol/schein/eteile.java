package de.hoell.jobcontrol.schein;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import de.hoell.jobcontrol.R;
import de.hoell.jobcontrol.session.SessionManager;

/**
 * A placeholder fragment containing a simple view.
 */
public class eteile extends Fragment {
    private Context context;
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


        EditText editTextTechnikernr =  (EditText) rootView.findViewById(R.id.editTextTeileNr);
        SessionManager session = new SessionManager(context);
        String url = "http://5.158.136.15/job/android/test/file.csv";
        new DownloadFileFromURL(context,url).execute();

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


    private class DownloadFileFromURL extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;

        private Context mContext;
        private String mUrl;

        public DownloadFileFromURL (Context context,String url){
            mContext = context;
            mUrl=url;
        }
        /**
         * Before starting background thread Show Progress Bar Dialog
         * */


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Downloading file. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... args) {
            int count;
            try {
                URL url = new URL(mUrl);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);
                File PATHdir =new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Jobcontrol/" )));
                PATHdir.mkdirs();
                // Output stream
                OutputStream output = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/Jobcontrol/file.csv"));

                byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                        // writing data to file
                        output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            pDialog.dismiss();

        }

    }

}
