package ahmed.FilMovie.fragments;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ahmed.FilMovie.Activities.DetailsActivity;
import ahmed.FilMovie.Activities.SettingsActivity;
import ahmed.FilMovie.R;
import ahmed.FilMovie.adapters.MoviesAdapter;
import ahmed.FilMovie.data.MoviesContract.FavMoviesEntry;
import ahmed.FilMovie.databinding.FragmentMainBinding;
import ahmed.FilMovie.models.Movie;
import ahmed.FilMovie.network.NetworkUtils;
/**
 * Created by ahmed on 25/03/17.
 */

public class MoviesFragment extends Fragment implements NetworkUtils.OnCompleteFetchingData,
        MoviesAdapter.OnClick,View.OnClickListener,SearchView.OnQueryTextListener, SearchView.OnCloseListener{
    FragmentMainBinding binding;
    MoviesAdapter adapter;
    public static boolean needRefresh = false;
    private String path ;
    ArrayList<Movie> mMoviesList = null;
    String searchText="";
    int currentPage = 0, pages = 0;
    String genre = null;
    private SearchView mSearchView;
    private int pos=0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        View rootView = binding.getRoot();

        if(mMoviesList==null)
            mMoviesList = new ArrayList<>();

        adapter = new MoviesAdapter(mMoviesList,this);
        GridLayoutManager layoutManager = new GridLayoutManager(container.getContext(),2);
        binding.moviesRv.setLayoutManager(layoutManager);
        binding.moviesRv.setHasFixedSize(true);
        binding.moviesRv.setAdapter(adapter);

        Bundle bundle = getArguments();
        path = bundle.getString("path");

        // Hide first and previous pages
        binding.firtPage.setVisibility(View.GONE);
        binding.previousPage.setVisibility(View.GONE);
        // Check if this is Favorites Fragment
        if(path.equals("fav")){
            // Get Data from contentProvider
            getDateFromLocalDB();
        }else if(path.equals("genre")) {
           genre = bundle.getString("genre");
           getData(NetworkUtils.getUrl(getContext(), "http://api.themoviedb.org/3/genre", new String[]{genre,"movies"},null,null));
        }else{
            getData(NetworkUtils.getUrl(getContext(), "http://api.themoviedb.org/3/movie", new String[]{path},null,null));
        }
        binding.firtPage.setOnClickListener(this);
        binding.previousPage.setOnClickListener(this);
        binding.nextPage.setOnClickListener(this);
        binding.lastPage.setOnClickListener(this);
        if(savedInstanceState!=null) {
            pos = savedInstanceState.getInt("pos");
        }
//        getData(NetworkUtils.getUrl(getContext(),"http://api.themoviedb.org/3/genre/movie",new String[]{""}));
        return rootView;
    }


    private void getData(String url) {
        NetworkUtils getDataObject = new NetworkUtils(this);
        getDataObject.getDataByUrl(getContext(),url,getString(R.string.get_movies_key));
    }

    @Override
    public void onCompleted(JSONObject result,String key) throws JSONException {
        mMoviesList.clear();
        adapter.clear();
        binding.moviesRv.scrollToPosition(0);
        currentPage = result.getInt("page");
        pages = result.getInt("total_pages");
        JSONArray results = result.getJSONArray("results");
        for(int i = 0 ; i < results.length();i++){
            JSONObject newMovieJson = results.getJSONObject(i);
            int id = newMovieJson.getInt("id");
            boolean isAdult = newMovieJson.getBoolean("adult");
            String backdropPath = newMovieJson.getString("backdrop_path");
            String posterPath = newMovieJson.getString("poster_path");
            String overView = newMovieJson.getString("overview");
            String releaseDate = newMovieJson.getString("release_date");
            String title = newMovieJson.getString("title");
            double voteAvr = newMovieJson.getDouble("vote_average");
            int voteCount = newMovieJson.getInt("vote_count");
            JSONArray genre_ids = newMovieJson.getJSONArray("genre_ids");
            ArrayList<Integer> arrayList = new ArrayList<>();
            for(int j = 0 ; j < genre_ids.length();j++){
                arrayList.add((Integer) genre_ids.get(j));
            }
            Movie newMovie = new Movie(id,title,posterPath,backdropPath,isAdult,overView,releaseDate,voteCount,voteAvr,arrayList);
            mMoviesList.add(newMovie);
        }

        adapter.notifyDataSetChanged();
        if(currentPage==1){
            binding.previousPage.setVisibility(View.GONE);
            binding.firtPage.setVisibility(View.GONE);
        }else{
            binding.previousPage.setVisibility(View.VISIBLE);
            binding.firtPage.setVisibility(View.VISIBLE);
        }
        if(pages<=currentPage){
            binding.nextPage.setVisibility(View.GONE);
            binding.lastPage.setVisibility(View.GONE);

        }else{
            binding.nextPage.setVisibility(View.VISIBLE);
            binding.lastPage.setVisibility(View.VISIBLE);
        }

        binding.moviesRv.smoothScrollToPosition(pos);
    }

    @Override
    public void onMovieClick(Movie movie) {
        startActivity(new Intent(getContext(), DetailsActivity.class).putExtra("movie",movie));
    }

    public void getDateFromLocalDB() {
        Cursor cursor =getContext().getContentResolver().query(FavMoviesEntry.CONTENT_URI
        ,null,null,null,null);
        if(cursor.moveToFirst()){
            do{

                int id = cursor.getInt(cursor.getColumnIndex(FavMoviesEntry.COLUMN_MOVIE_ID));
                boolean isAdult = cursor.getInt(cursor.getColumnIndex(FavMoviesEntry.COLUMN_MOVIE_ADULT)) == 1 ? true:false;
                String backdropPath = cursor.getString(cursor.getColumnIndex(FavMoviesEntry.COLUMN_MOVIE_BACKDROP));
                String posterPath = cursor.getString(cursor.getColumnIndex(FavMoviesEntry.COLUMN_MOVIE_POSTER));
                String overView = cursor.getString(cursor.getColumnIndex(FavMoviesEntry.COLUMN_MOVIE_OVERVIEW));
                String releaseDate = cursor.getString(cursor.getColumnIndex(FavMoviesEntry.COLUMN_MOVIE_RELEASE));
                String title = cursor.getString(cursor.getColumnIndex(FavMoviesEntry.COLUMN_MOVIE_TITLE));
                double voteAvr = cursor.getDouble(cursor.getColumnIndex(FavMoviesEntry.COLUMN_MOVIE_VOTE_AVG));
                int voteCount = cursor.getInt(cursor.getColumnIndex(FavMoviesEntry.COLUMN_MOVIE_VOTE_COUNT));
                Movie newMovie = new Movie(id,title,posterPath,backdropPath,isAdult,overView,releaseDate,voteCount,voteAvr,null);
                mMoviesList.add(newMovie);
            }while (cursor.moveToNext());
        }
        adapter.notifyDataSetChanged();
        cursor.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(needRefresh){
            adapter.clear();
            getDateFromLocalDB();
            needRefresh = false;

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.firtPage :
                if(searchText.length()>0){
                    getData(NetworkUtils.getUrl(getContext(),"https://api.themoviedb.org/3/search",new String[]{"movie"}
                            ,new String[]{"query","page"},new String[]{searchText,"1"}));
                }else if(genre!=null) {
                     getData(NetworkUtils.getUrl(getContext(), "http://api.themoviedb.org/3/genre", new String[]{genre,"movies"},new String[]{"page"}, new String[]{"1"}));
                 }else{
                    if(path.equals("genre")) {
                        getData(NetworkUtils.getUrl(getContext(), "http://api.themoviedb.org/3/genre", new String[]{genre,"movies"},new String[]{"page"}, new String[]{"1"}));
                    }else{
                        getData(NetworkUtils.getUrl(getContext(), "http://api.themoviedb.org/3/movie", new String[]{path},new String[]{"page"}, new String[]{"1"}));
                    }
                }
                break;
            case R.id.nextPage :
                if(searchText.length()>0){
                    getData(NetworkUtils.getUrl(getContext(),"https://api.themoviedb.org/3/search",new String[]{"movie"}
                            ,new String[]{"query","page"},new String[]{searchText, String.valueOf(currentPage + 1)}));
                }else if(genre!=null) {
                    getData(NetworkUtils.getUrl(getContext(), "http://api.themoviedb.org/3/genre", new String[]{genre,"movies"},new String[]{"page"}, new String[]{String.valueOf(currentPage + 1)}));
                }
                else{
                    if(path.equals("genre")) {
                        getData(NetworkUtils.getUrl(getContext(), "http://api.themoviedb.org/3/genre", new String[]{genre,"movies"},new String[]{"page"}, new String[]{String.valueOf(currentPage + 1)}));
                    }else{
                        getData(NetworkUtils.getUrl(getContext(), "http://api.themoviedb.org/3/movie", new String[]{path},new String[]{"page"}, new String[]{String.valueOf(currentPage + 1)}));
                    }
                }
                break;
            case R.id.previousPage :
                if(searchText.length()>0){
                    getData(NetworkUtils.getUrl(getContext(),"https://api.themoviedb.org/3/search",new String[]{"movie"}
                            ,new String[]{"query","page"},new String[]{searchText, String.valueOf(currentPage - 1)}));
                }else if(genre!=null) {
                    getData(NetworkUtils.getUrl(getContext(), "http://api.themoviedb.org/3/genre", new String[]{genre,"movies"},new String[]{"page"}, new String[]{String.valueOf(currentPage - 1)}));
                }
                else{
                    if(path.equals("genre")) {
                        getData(NetworkUtils.getUrl(getContext(), "http://api.themoviedb.org/3/genre", new String[]{genre,"movies"},new String[]{"page"}, new String[]{String.valueOf(currentPage - 1)}));
                    }else{
                        getData(NetworkUtils.getUrl(getContext(), "http://api.themoviedb.org/3/movie", new String[]{path},new String[]{"page"}, new String[]{String.valueOf(currentPage - 1)}));
                    }                }
                break;
            case R.id.lastPage :
                if(searchText.length()>0){
                    getData(NetworkUtils.getUrl(getContext(),"https://api.themoviedb.org/3/search",new String[]{"movie"}
                            ,new String[]{"query","page"},new String[]{searchText, String.valueOf(pages)}));
                }else if(genre!=null) {
                    getData(NetworkUtils.getUrl(getContext(), "http://api.themoviedb.org/3/genre", new String[]{genre,"movies"},new String[]{"page"}, new String[]{String.valueOf(pages)}));
                }
                else{
                    if(path.equals("genre")) {
                        getData(NetworkUtils.getUrl(getContext(), "http://api.themoviedb.org/3/genre", new String[]{genre,"movies"},new String[]{"page"}, new String[]{String.valueOf(pages)}));
                    }else{
                        getData(NetworkUtils.getUrl(getContext(), "http://api.themoviedb.org/3/movie", new String[]{path},new String[]{"page"}, new String[]{String.valueOf(pages)}));
                    }
                }
                break;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
        mSearchView.setQueryHint("Search movie");
        setupSearchView();;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings){

            startActivity(new Intent(getContext(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.v("Query = ","" + query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText.length()<1){
            onClose();
            return false;
        }
        String url = NetworkUtils.getUrl(getContext(),"https://api.themoviedb.org/3/search",new String[]{"movie"}
                ,new String[]{"query"},new String[]{newText});
        getData(url);
        searchText=newText;

        return false;
    }

    @Override
    public boolean onClose() {
        searchText = "";
        if(path.equals("genre")) {
            getData(NetworkUtils.getUrl(getContext(), "http://api.themoviedb.org/3/genre", new String[]{genre,"movies"},null,null));
        }else{
            getData(NetworkUtils.getUrl(getContext(), "http://api.themoviedb.org/3/movie", new String[]{path},null,null));
        }
        return false;
    }

    private void setupSearchView() {

        mSearchView.setIconifiedByDefault(true);

        SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();

            // Try to use the "applications" global search provider
            SearchableInfo info = searchManager.getSearchableInfo(getActivity().getComponentName());
            for (SearchableInfo inf : searchables) {
                if (inf.getSuggestAuthority() != null
                        && inf.getSuggestAuthority().startsWith("applications")) {
                    info = inf;
                }
            }
            mSearchView.setSearchableInfo(info);
        }

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("pos",((LinearLayoutManager)binding.moviesRv.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
        super.onSaveInstanceState(outState);
    }
}