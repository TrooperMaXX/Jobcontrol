        package de.hoell.jobcontrol;

        import android.app.Activity;
        import android.app.Fragment;
        import android.app.FragmentManager;
        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.PackageInfo;
        import android.content.pm.PackageManager.NameNotFoundException;
        import android.content.res.Configuration;
        import android.content.res.TypedArray;
        import android.net.Uri;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.ListView;
        import android.widget.Switch;
        import android.widget.TextView;
        import android.widget.Toast;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.Locale;
        import java.util.concurrent.ExecutionException;
        import java.util.concurrent.TimeUnit;
        import java.util.concurrent.TimeoutException;

        import de.hoell.jobcontrol.adapter.NavDrawerListAdapter;
        import de.hoell.jobcontrol.adapter.SpecialAdapter;
        import de.hoell.jobcontrol.model.NavDrawerItem;
        import de.hoell.jobcontrol.preference.SettingsActivity;
        import de.hoell.jobcontrol.query.DownloadFileFromURL;
        import de.hoell.jobcontrol.query.Functions;
        import de.hoell.jobcontrol.schein.srnpruefen;
        import de.hoell.jobcontrol.session.SessionManager;


        public class
                MainActivity extends Activity implements TicketFragment.OnTicketInteractionListener {
            private DrawerLayout mDrawerLayout;
            private ListView mDrawerList;
            private LinearLayout mDrawer;
            private Switch mSwitch;
            private ActionBarDrawerToggle mDrawerToggle;
            private TextView mIch;
            private ImageView mIchStatus;

            // nav drawer title
            private CharSequence mDrawerTitle;

            // used to store app title
            private CharSequence mTitle;

            // slide menu items
            private String[] navMenuTitles;
            private TypedArray navMenuIcons;

            private ArrayList<NavDrawerItem> navDrawerItems;
            private NavDrawerListAdapter adapter;
            SessionManager session;
            public String versionName;


            private static final String TAG_SUCCESS = "success";
            public JSONArray Technikerliste = null;
            ArrayList<HashMap<String, String>> TheTechniker = new ArrayList<HashMap<String, String>>();
            private ListView mTechList;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                boolean offline =getIntent().getBooleanExtra("offline",false);
                session = new SessionManager(this);
                mTitle = mDrawerTitle = getTitle();

                // load slide menu items
                navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

                // nav drawer icons from resources
                navMenuIcons = getResources()
                        .obtainTypedArray(R.array.nav_drawer_icons);

                mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
                mDrawer = (LinearLayout) findViewById(R.id.layout_slidermenu);
                mTechList = (ListView) findViewById(R.id.techniker_list);
                mSwitch = (Switch) findViewById(R.id.switch_verfuegbar);
                mIch = (TextView) findViewById(R.id.textIch);
                mIchStatus = (ImageView) findViewById(R.id.imageStatusIch);


                navDrawerItems = new ArrayList<NavDrawerItem>();

                // adding nav drawer items to array
                // MyTickets
                //noinspection ResourceType
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
                // Offlinemodus
                //noinspection ResourceType
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
                // NewTickets
                //noinspection ResourceType
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
                //Schein
                //noinspection ResourceType
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));


                PackageInfo pInfo = null;
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
               versionName = pInfo.versionName;

                // Recycle the typed array
                navMenuIcons.recycle();

                mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
                mTechList.setOnItemClickListener(new TechListClickListener());
                mSwitch.setOnClickListener(new SwitchClickListener());

                boolean switchstatus = session.getSwitchstatus();
                mSwitch.setChecked(switchstatus);

                // setting the nav drawer list adapter
                adapter = new NavDrawerListAdapter(Jobcontrol.getAppCtx(),
                        navDrawerItems);
                mDrawerList.setAdapter(adapter);

                // enabling action bar app icon and behaving it as toggle button
                getActionBar().setDisplayHomeAsUpEnabled(true);
                getActionBar().setHomeButtonEnabled(true);

                mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                       // R.drawable.ic_drawer, //nav menu toggle icon
                        R.string.title_activity_slidermenu, // nav drawer open - description for accessibility
                        R.string.title_activity_slidermenu // nav drawer close - description for accessibility
                ) {
                    public void onDrawerClosed(View view) {
                        getActionBar().setTitle(mTitle);
                        // calling onPrepareOptionsMenu() to show action bar icons
                        invalidateOptionsMenu();
                    }

                    public void onDrawerOpened(View drawerView) {
                        getActionBar().setTitle(mDrawerTitle);
                        // calling onPrepareOptionsMenu() to hide action bar icons
                        invalidateOptionsMenu();
                    }
                };
                mDrawerLayout.setDrawerListener(mDrawerToggle);

                if (savedInstanceState == null) {
                    // on first time display view for first nav item
                    if (!offline){
                        displayView(0);
                        //executeTech();
                    }else{
                        displayView(1);
                    }

                }


   /*String url = getResources().getString(R.string.url_artstamm);
          String output = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Jobcontrol/artstamm.csv"));
        File file = new File(output);
                Log.e("nix","da1");
        if(!file.exists()){
            Log.e("nix","da2");
            new DownloadFileFromURL(this,url,output).execute();
        }else {
         DBManager dbManager= new DBManager(context);
                dbManager.onUpgrade(dbManager.getWritableDatabase(), 1, 2);
             dbManager.execute(context,file);

        }
*/
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMAN);
               //session.saveDB("null");
                Calendar cal = Calendar.getInstance();
                Date heute=new Date(123);
                String today = sdf.format(cal.getTime());
                try {

                    heute = sdf.parse(today);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (session.getDB().equals("null")){


                            //makeDbUpdat(MainActivity.this, pDialog);



                    new DownloadFileFromURL(this, "https://hoell.syno-ds.de:55443/job/android/db/artstamm.csv", "artstamm.csv").execute();
                    new DownloadFileFromURL(this, "https://hoell.syno-ds.de:55443/job/android/db/gerstamm.csv", "gerstamm.csv").execute();
                    new DownloadFileFromURL(this, "https://hoell.syno-ds.de:55443/job/android/db/lohnart.csv", "lohnart.csv").execute();


                    cal.add(Calendar.DATE, +7);

                    Log.e("Date+7=", sdf.format(cal.getTime()));
                    session.saveDB(sdf.format(cal.getTime()));
                }else try {
                    if(sdf.parse(session.getDB()).before(heute)){
                        Log.e("loool", "dannach");
                        new DownloadFileFromURL(this, "https://hoell.syno-ds.de:55443/job/android/db/artstamm.csv", "artstamm.csv").execute();
                        new DownloadFileFromURL(this, "https://hoell.syno-ds.de:55443/job/android/db/gerstamm.csv", "gerstamm.csv").execute();
                        new DownloadFileFromURL(this, "https://hoell.syno-ds.de:55443/job/android/db/lohnart.csv", "lohnart.csv").execute();

                        cal.add(Calendar.DATE, +7);

                        Log.e("Date+7=", sdf.format(cal.getTime()));
                        session.saveDB(sdf.format(cal.getTime()));
                    }else{
                        Log.e("Datenbank", "Aktuell");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }





            /**
             * Slide menu item click listener
             * */
            private class SlideMenuClickListener implements
                    ListView.OnItemClickListener {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    // display view for selected nav drawer item
                    displayView(position);
                }
            }
            private class TechListClickListener implements
                    ListView.OnItemClickListener {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    // Call Techniker
                    try {
                        JSONObject c = Technikerliste.getJSONObject(position);

                        String Tel = c.getString("telefon");


                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", Tel, null));
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.main, menu);
                return super.onCreateOptionsMenu(menu);
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                // toggle nav drawer on selecting action bar app icon/title
                if (mDrawerToggle.onOptionsItemSelected(item)) {
                    return true;
                }
                // Handle action bar actions click
                switch (item.getItemId()) {
                    case R.id.action_info:
                        Toast.makeText(Jobcontrol.getAppCtx(), versionName, Toast.LENGTH_SHORT).show();
                        return true;



                    case R.id.action_refresh:
                        Toast.makeText(Jobcontrol.getAppCtx(), "Refresh...", Toast.LENGTH_SHORT).show();


                        Functions Function = new Functions();
                        if( Function.isNetworkOnline(Jobcontrol.getAppCtx())) {
                            displayView(0);
                        }
                        else{
                            Toast.makeText(Jobcontrol.getAppCtx(), "Keine INternet verbindung", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    case R.id.action_settings:

                        Intent intent = new Intent(this, SettingsActivity.class);
                        startActivity(intent);

                        return true;

                    case R.id.action_logout:
                        Toast.makeText(Jobcontrol.getAppCtx(), "Ausloggen...", Toast.LENGTH_SHORT).show();

                        session.logoutUser();

                        Intent i = new Intent(Jobcontrol.getAppCtx(), Start.class);
                        startActivity(i);

                        // closing this screen
                        finish();
                        return true;
                    default:

                        return super.onOptionsItemSelected(item);
                }
            }

            /***
             * Called when invalidateOptionsMenu() is triggered
             */
            @Override
            public boolean onPrepareOptionsMenu(Menu menu) {
                // if nav drawer is opened, hide the action items
                boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

                menu.findItem(R.id.action_refresh).setVisible(!drawerOpen);
                menu.findItem(R.id.action_logout).setVisible(drawerOpen);
                return super.onPrepareOptionsMenu(menu);
            }

            /**
             * Diplaying fragment view for selected nav drawer list item
             * */
            public void displayView(int position) {
                // update the main content by replacing fragments
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = new TicketFragment();
                        executeTech();
                        break;
                    case 1:
                        fragment = new OfflineFragment();
                        break;
                    case 2:
                        fragment = new SrnSuche();
                        break;
                    case 3:
                        fragment = new srnpruefen();
                      /*  Bundle bundle = new Bundle();
                        bundle.putString("value_seriennummer", "0123456879");
                        fragment.setArguments(bundle);*/
                        break;
                    default:
                        break;
                }

                if (fragment != null) {

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, fragment).commit();

                    // update selected item and title, then close the drawer
                    mDrawerList.setItemChecked(position, true);
                    mDrawerList.setSelection(position);
                    setTitle(navMenuTitles[position]);
                    mDrawerLayout.closeDrawer(mDrawer);
                } else {
                    // error in creating fragment
                    Log.e("MainActivity", "Error in creating fragment");
                }
            }

            @Override
            public void setTitle(CharSequence title) {
                mTitle = title;
                getActionBar().setTitle(mTitle);
            }

            /**
             * When using the ActionBarDrawerToggle, you must call it during
             * onPostCreate() and onConfigurationChanged()...
             */

            @Override
            protected void onPostCreate(Bundle savedInstanceState) {
                super.onPostCreate(savedInstanceState);
                // Sync the toggle state after onRestoreInstanceState has occurred.
                mDrawerToggle.syncState();
            }

            @Override
            public void onConfigurationChanged(Configuration newConfig) {
                super.onConfigurationChanged(newConfig);
                // Pass any configuration change to the drawer toggls
                mDrawerToggle.onConfigurationChanged(newConfig);
            }



            public void executeTech(){

                try {
                    Functions Function = new Functions();

                    if( Function.isNetworkOnline(this)) {
                        JSONObject json = new JSONTechniker(this).execute().get(30000, TimeUnit.MILLISECONDS);

                        if (json != null) {  Log.e("JSONTECH",json.toString());
                            try {

                                int success = json.getInt(TAG_SUCCESS);

                                if (success == 1) {
                                    TheTechniker = new ArrayList<HashMap<String, String>>();
                                    mTechList.setAdapter(null);
                                    Technikerliste = json.getJSONArray("techniker");
                                    JSONObject Ich = json.getJSONObject("ich");
                                    int Ich_verfuegbar = Ich.getInt("tech_verfuegbar");
                                    String ICH = Ich.getString("full_name");
                                    mIch.setText(ICH);
                                    int ichimgid;

                                    if(Ich_verfuegbar>0){
                                        ichimgid= this.getResources().getIdentifier("ic_status_green", "mipmap", "de.hoell.jobcontrol");
                                    }else{
                                        ichimgid= this.getResources().getIdentifier("ic_status_red", "mipmap", "de.hoell.jobcontrol");
                                    }
                                    mIchStatus.setImageResource(ichimgid);



                                    for (int i = 0; i < Technikerliste.length(); i++) {
                                        JSONObject c = Technikerliste.getJSONObject(i);

                                        String Techniker = c.getString("full_name");
                                        int verfuegbar = c.getInt("tech_verfuegbar");
                                        String Gebiet = c.getString("tech_gebiet");
                                        String kuerzel = c.getString("user_name");
                                        String Tel = c.getString("telefon");


                                        int imgid;

                                        if(verfuegbar>0){
                                            imgid= this.getResources().getIdentifier("ic_status_green", "mipmap", "de.hoell.jobcontrol");
                                        }else{
                                            imgid= this.getResources().getIdentifier("ic_status_red", "mipmap", "de.hoell.jobcontrol");
                                        }


                                        HashMap<String, String> map = new HashMap<String, String>();
                                        map.put("Techniker", Techniker);

                                        map.put("Gebiet", Gebiet);
                                        map.put("Kuerzel", kuerzel);
                                        map.put("Tel", Tel);

                                        map.put("Verfuegbar_ic", String.valueOf(imgid)); //verfuegbar

                                        TheTechniker.add(map);


                                    }

                                    System.out.println("TEchnikerabfrage" + TheTechniker);

                                } else {


                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            mTechList.setAdapter(new SpecialAdapter(this,TheTechniker,R.layout.row_tech,
                                    new String[] {"Verfuegbar_ic", "Techniker"},
                                    new int[] {R.id.Verfuegbar_img,R.id.TECHNIKER}));
                        }
                    }
                    else{

                        Toast.makeText(this, "Keine Internet verbindung", Toast.LENGTH_LONG).show();



                    }

                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onTicketInteraction(String id) {

            }


            public class JSONTechniker extends AsyncTask<String, String, JSONObject> {

                private Context mContext;


                public JSONTechniker (Context context){
                    mContext = context;
                }




                @Override
                protected JSONObject doInBackground(String... args) {


                    String user;



                    Functions Function = new Functions();
                    SessionManager session = new SessionManager(mContext);
                    user = session.getUser();

                    JSONObject json = Function.Techniker(user);

                    return json;

                }



            }


            private class SwitchClickListener implements View.OnClickListener {

                @Override
                public void onClick(View v) {
                    Functions Function =new Functions();
                    if( Function.isNetworkOnline(MainActivity.this)){
                       int switch_st;


                    if (mSwitch.isChecked())
                    {
                        session.saveSwitchstatus(true);
                        switch_st=1;
                    }
                    else
                    {
                        session.saveSwitchstatus(false);
                        switch_st=0;
                    }
                        try {
                            new JSONSaveSwitch(switch_st).execute().get(30000, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException | ExecutionException | TimeoutException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        Toast.makeText(Jobcontrol.getAppCtx(), "Keine INternet verbindung", Toast.LENGTH_SHORT).show();}


                }
            }



            private class JSONSaveSwitch extends AsyncTask<Integer, Integer, JSONObject> {
                private int mStatus;
                public JSONSaveSwitch(int status) {
                    mStatus=status;
                }

                @Override
                protected JSONObject doInBackground(Integer... params) {
                    Functions Function = new Functions();
                    SessionManager session = new SessionManager(MainActivity.this);
                     String user = session.getUser();
                    JSONObject json_saveswitch = Function.SaveSwitch(mStatus,user);

                    return json_saveswitch;
                }
            }
        }
