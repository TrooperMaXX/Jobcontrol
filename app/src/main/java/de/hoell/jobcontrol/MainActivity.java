        package de.hoell.jobcontrol;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.Fragment;
        import android.app.FragmentManager;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.pm.PackageInfo;
        import android.content.res.Configuration;
        import android.content.res.TypedArray;
        import android.os.Bundle;
        import android.support.v4.app.ActionBarDrawerToggle;
        import android.support.v4.widget.DrawerLayout;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.LinearLayout;
        import android.widget.ListView;
        import android.widget.NumberPicker;
        import android.widget.Toast;
        import android.content.pm.PackageManager.NameNotFoundException;

        import java.util.ArrayList;

        import de.hoell.jobcontrol.adapter.NavDrawerListAdapter;
        import de.hoell.jobcontrol.model.NavDrawerItem;
        import de.hoell.jobcontrol.query.Functions;
        import de.hoell.jobcontrol.session.SessionManager;


        public class
                MainActivity extends Activity implements TicketFragment.OnTicketInteractionListener {
            private DrawerLayout mDrawerLayout;
            private ListView mDrawerList;
            private LinearLayout mDrawer;
            private ActionBarDrawerToggle mDrawerToggle;

            public static Context context = null;
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

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                session = new SessionManager(this);
                mTitle = mDrawerTitle = getTitle();
                 context=this;
                // load slide menu items
                navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

                // nav drawer icons from resources
                navMenuIcons = getResources()
                        .obtainTypedArray(R.array.nav_drawer_icons);

                mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
                mDrawer = (LinearLayout) findViewById(R.id.layout_slidermenu);

                navDrawerItems = new ArrayList<NavDrawerItem>();

                // adding nav drawer items to array
                // MyTickets
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
                // Offlinemodus
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
                // NewTickets
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));


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

                // setting the nav drawer list adapter
                adapter = new NavDrawerListAdapter(getApplicationContext(),
                        navDrawerItems);
                mDrawerList.setAdapter(adapter);

                // enabling action bar app icon and behaving it as toggle button
                getActionBar().setDisplayHomeAsUpEnabled(true);
                getActionBar().setHomeButtonEnabled(true);

                mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                        R.drawable.ic_drawer, //nav menu toggle icon
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
                    displayView(0);
                }
            }

            @Override
            public void onTicketInteraction(String id) {

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

            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.main, menu);
                return true;
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
                        Toast.makeText(getApplicationContext(), versionName, Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.action_zeit:



                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        final NumberPicker np = new NumberPicker(MainActivity.this);

                        alert.setTitle("Select the value: ");
                        alert.setView(np);

                        Integer[] nums = new Integer[100];
                        for(int i=1; i<nums.length; i++)
                        {
                            nums[i] = i;


                        }

                        np.setMinValue(1);
                        np.setMaxValue(nums.length - 1);
                        np.setWrapSelectorWheel(true);
                        ///np.setDisplayedValues(nums);
                        np.setValue(15);

                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Do something with value!
                                int value = np.getValue();
                                session.saveZeit(value);
                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Cancel.
                            }
                        });

                        alert.show();








                        return true;


                    case R.id.action_refresh:
                        Toast.makeText(getApplicationContext(), "Refresh...", Toast.LENGTH_SHORT).show();


                        Functions Function = new Functions();
                        if( Function.isNetworkOnline(MainActivity.this)) {
                            displayView(0);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Keine INternet verbindung", Toast.LENGTH_LONG).show();
                        }
                        return true;

                    case R.id.action_logout:
                        Toast.makeText(getApplicationContext(), "Ausloggen...", Toast.LENGTH_SHORT).show();

                        session.logoutUser();

                        Intent i = new Intent(getApplicationContext(), Start.class);
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
                        break;
                    case 1:
                        fragment = new OfflineFragment();
                        break;
                    case 2:
                        fragment = new SrnSuche();
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

        }