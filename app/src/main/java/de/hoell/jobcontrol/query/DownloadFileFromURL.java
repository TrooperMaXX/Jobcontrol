package de.hoell.jobcontrol.query;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFileFromURL extends AsyncTask<String, String, File> {


    private Context mContext;
    private String mUrl,mOutput;
    private ProgressDialog pDialog;

    public DownloadFileFromURL (Context context,String url,String output){
        mContext = context;
        mUrl=url;
        mOutput=output;
        //mPdialog=pDialog;
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
    protected File doInBackground(String... args) {
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
            File PATHdir =new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Jobcontrol/")));
            PATHdir.mkdirs();
            // Output stream
            OutputStream output = new FileOutputStream(PATHdir+"/"+mOutput);

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
            return new File(PATHdir+"/"+mOutput);
        } catch (Exception e) {

            Log.e("Error: ", e.getMessage());
        return null;
        }


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
    protected void onPostExecute(File file) {
        // dismiss the dialog after the file was downloaded
        Log.e("parameter postexecute", "" + file);

        pDialog.dismiss();
        if (file.getName().equals("artstamm.csv")){
            new DBManager.FillArtDB(mContext, file).execute();
        }else {
            new DBManager.FillGerDB(mContext, file).execute();
        }

//        mPdialog.incrementProgressBy(1);
    }

}