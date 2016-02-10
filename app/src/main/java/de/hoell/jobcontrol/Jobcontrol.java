package de.hoell.jobcontrol;

import android.app.Application;
import android.content.Context;
import android.util.Log;



import de.hoell.jobcontrol.query.DownloadFileFromURL;
import de.hoell.jobcontrol.query.MyVolley;
import de.hoell.jobcontrol.session.SessionManager;



/**
 * Created by Hoell on 30.06.2015.
 */
public class Jobcontrol extends Application {

    protected static Context appCtx=null;

    @Override
    public void onCreate(){
        super.onCreate();
        appCtx=getApplicationContext();
       /* Stetho.initializeWithDefaults(this);
        Log.e("Stetho", "done");*/
        init();

    }
    public static Context getAppCtx(){

        return appCtx;
    }
    private void init() {
        MyVolley.init(this);
    }
}

