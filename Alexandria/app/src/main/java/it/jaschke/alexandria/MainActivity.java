package it.jaschke.alexandria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import it.jaschke.alexandria.api.Callback;


public class MainActivity extends ActionBarActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks, Callback,
        BookDetail.BookDetailCallback{

    private static final String DETAIL_FRAGMENT = "DETAIL_BOOK_F";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String L_FRAG_TAG = "l_frag";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment navigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence title;
    public static boolean IS_TABLET = false;
    private BroadcastReceiver messageReciever;

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IS_TABLET = isTablet();
        if(IS_TABLET){
            setContentView(R.layout.activity_main_tablet);
        }else {
            setContentView(R.layout.activity_main);
        }

        messageReciever = new MessageReciever();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReciever,filter);

        navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        title = getTitle();

        // Set up the drawer.
        navigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Add list fragment only once, as the base fragment.
        if(!IS_TABLET) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new ListOfBooks())
                    .addToBackStack(L_FRAG_TAG)
                    .commit();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment nextFragment;

        // Clear backstack to leave list fragment as base.
        clearFragmentBackstack(fragmentManager);
        
        switch (position){
            case 1:
                nextFragment = new AddBook();
                break;
            case 2:
                nextFragment = new About();
                break;
            default:
                nextFragment = null;
        }
        
        if(nextFragment != null) {
            int id = R.id.container;

            if(IS_TABLET){
                id = R.id.right_container;
            }

            // Do not add to back stack to prevent confusion.
            FragmentTransaction ft = fragmentManager.beginTransaction();

            ft.replace(id, nextFragment);
            if(!IS_TABLET) {
                ft.addToBackStack(null);
            }
            ft.commit();
        }
    }

    private void clearFragmentBackstack(FragmentManager fm) {
        int delta = 1;
        int count = fm.getBackStackEntryCount();
        if(IS_TABLET) {
            delta = 0;
        }
        for(int i=0 ; i < count - delta ; i++ ) {
            fm.popBackStack();
        }
        
    }

    public void setTitle(int titleId) {
        title = getString(titleId);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReciever);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String ean) {
        int id = R.id.container;

        if(IS_TABLET){
            id = R.id.right_container;
        }

        Bundle args = new Bundle();
        args.putString(BookDetail.EAN_KEY, ean);
        args.putBoolean(BookDetail.TWO_PANE_MODE, IS_TABLET);

        BookDetail fragment = BookDetail.newInstance(this);
        fragment.setArguments(args);

        FragmentManager fm = getSupportFragmentManager();
        clearFragmentBackstack(fm);
        // Stack get replaced with invalid view when changing between items in list. Did not add
        // to back stack.
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(id, fragment);
        if(!IS_TABLET) {
            ft.addToBackStack(null);
        }
        ft.commit();

    }

    @Override
    public void onBookDeleted() {
        // Change to list of books.
        onNavigationDrawerItemSelected(0);
        // Remove fragment if available
        if(findViewById(R.id.right_container)!=null) {
            Fragment dFrag = getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT);
            getSupportFragmentManager().beginTransaction().remove(dFrag).commit();
        }
    }

    private class MessageReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(MESSAGE_KEY)!=null){
                Toast.makeText(MainActivity.this, intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 1){
            finish();
        }
        super.onBackPressed();
    }


}