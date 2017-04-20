package ahmed.FilMovie.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ahmed.FilMovie.R;
import ahmed.FilMovie.fragments.MoviesFragment;
import ahmed.FilMovie.models.Movie;
import ahmed.FilMovie.network.NetworkUtils;
import ahmed.FilMovie.services.NotificationService;

public class MainActivity extends AppCompatActivity implements NetworkUtils.OnCompleteFetchingData
        , NavigationView.OnNavigationItemSelectedListener {

    int checkedDrawarId = 0;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    private static boolean sInitialized;
    NavigationView navigationView;
    /**
     * The {@link ViewPager} that will host the section contents.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColorPrimary));
        getData(NetworkUtils.getUrl(this,"http://api.themoviedb.org/3/genre/movie",new String[]{"list"},null,null));
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
//        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//
//        // Set up the ViewPager with the sections adapter.
//        mViewPager = (ViewPager) findViewById(R.id.container);
//        mViewPager.setAdapter(mSectionsPagerAdapter);
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setTabTextColors(getResources().getColor(R.color.textnonSelectedTab),getResources().getColor(R.color.textSelectedTab));
//        tabLayout.setupWithViewPager(mViewPager);

        //DRAWER
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.onFire);
        if(savedInstanceState!=null){
            navigationView.setCheckedItem(savedInstanceState.getInt("selected"));
        }else {
            Bundle bundle = new Bundle();
            bundle.putString("path", "popular");
            MoviesFragment moviesFragment = new MoviesFragment();
            moviesFragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, moviesFragment).commit();
        }
        if (!sInitialized){
            sInitialized = true;
            Intent intentToSyncImmediately = new Intent(this, NotificationService.class);
            startService(intentToSyncImmediately);
        }
    }



    @Override
    public void onCompleted(JSONObject result,String key) throws JSONException {
        JSONObject root = result;
        JSONArray genresJsonArray = root.getJSONArray("genres");
        for(int i = 0 ; i < genresJsonArray.length();i++){
            JSONObject genre = genresJsonArray.getJSONObject(i);
            Movie.Genres.put(genre.getInt("id"),genre.getString("name"));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        checkedDrawarId = item.getItemId();
        MoviesFragment moviesFragment =new MoviesFragment();
        if (id == R.id.onFire) {
            Bundle bundle = new Bundle();
            bundle.putString("path","popular");
            moviesFragment.setArguments(bundle);
        } else if (id == R.id.mostPopular) {
            Bundle bundle = new Bundle();
            bundle.putString("path","top_rated");
            moviesFragment.setArguments(bundle);
        } else if (id == R.id.fav) {
            Bundle bundle = new Bundle();
            bundle.putString("path","fav");
            moviesFragment.setArguments(bundle);

        } else if(id == R.id.Adventure){
            Bundle bundle = new Bundle();
            bundle.putString("path","genre");
            bundle.putString("genre", String.valueOf(getKeyFromValue("Adventure")));
            moviesFragment.setArguments(bundle);
        }else if(id == R.id.Animation){
            Bundle bundle = new Bundle();
            bundle.putString("path","genre");
            bundle.putString("genre", String.valueOf(getKeyFromValue("Animation")));
            moviesFragment.setArguments(bundle);
        }else if(id == R.id.Comedy){
            Bundle bundle = new Bundle();
            bundle.putString("path","genre");
            bundle.putString("genre", String.valueOf(getKeyFromValue("Comedy")));
            moviesFragment.setArguments(bundle);
        }else if(id == R.id.Drama){
            Bundle bundle = new Bundle();
            bundle.putString("path","genre");
            bundle.putString("genre", String.valueOf(getKeyFromValue("Drama")));
            moviesFragment.setArguments(bundle);
        }else if(id == R.id.History){
            Bundle bundle = new Bundle();
            bundle.putString("path","genre");
            bundle.putString("genre", String.valueOf(getKeyFromValue("History")));
            moviesFragment.setArguments(bundle);
        }else if(id == R.id.Horror){
            Bundle bundle = new Bundle();
            bundle.putString("path","genre");
            bundle.putString("genre", String.valueOf(getKeyFromValue("Horror")));
            moviesFragment.setArguments(bundle);
        }else if(id == R.id.Romance){
            Bundle bundle = new Bundle();
            bundle.putString("path","genre");
            bundle.putString("genre", String.valueOf(getKeyFromValue("Romance")));
            moviesFragment.setArguments(bundle);
        }else if(id == R.id.ScienceFiction){
            Bundle bundle = new Bundle();
            bundle.putString("path","genre");
            bundle.putString("genre", String.valueOf(getKeyFromValue("Science Fiction")));
            moviesFragment.setArguments(bundle);
        }else if(id == R.id.TVMovie){
            Bundle bundle = new Bundle();
            bundle.putString("path","genre");
            bundle.putString("genre", String.valueOf(getKeyFromValue("TV Movie")));
            moviesFragment.setArguments(bundle);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, moviesFragment).commit();

        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        // Close the navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public static int getKeyFromValue(String value) {
        for (int o : Movie.Genres.keySet()) {
            if (Movie.Genres.get(o).equals(value)) {
                return o;
            }
        }
        return 0;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Bundle bundle = new Bundle();
            if(position == 0){
                bundle.putString("path","popular");
            }else{
                bundle.putString("path","top_rated");
            }
            MoviesFragment moviesFragment =new MoviesFragment();
            moviesFragment.setArguments(bundle);
            return moviesFragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "On fire";
                case 1:
                    return "Most rated";

            }
            return null;
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void getData(String url) {
        NetworkUtils getDataObject = new NetworkUtils(this);
        getDataObject.getDataByUrl(this,url,getString(R.string.get_genres_key));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("selected",checkedDrawarId);
        super.onSaveInstanceState(outState);
    }
}
