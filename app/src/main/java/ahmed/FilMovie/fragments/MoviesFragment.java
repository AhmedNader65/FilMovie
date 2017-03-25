package ahmed.FilMovie.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ahmed.FilMovie.Activities.DetailsActivity;
import ahmed.FilMovie.R;
import ahmed.FilMovie.adapters.MoviesAdapter;
import ahmed.FilMovie.databinding.FragmentMainBinding;
import ahmed.FilMovie.models.Movie;
import ahmed.FilMovie.network.NetworkUtils;

/**
 * Created by ahmed on 25/03/17.
 */

public class MoviesFragment extends Fragment implements NetworkUtils.OnCompleteFetchingData ,MoviesAdapter.OnClick{
    FragmentMainBinding binding;
    MoviesAdapter adapter;
    private String path ;
    ArrayList<Movie> mMoviesList = null;
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
        getData(NetworkUtils.getUrl(getContext(),"http://api.themoviedb.org/3/movie",path));
        getData(NetworkUtils.getUrl(getContext(),"http://api.themoviedb.org/3/genre/movie",""));
        return rootView;
    }


    private void getData(String url) {
        NetworkUtils getDataObject = new NetworkUtils(this);
        getDataObject.getDataByUrl(getContext(),url);
    }

    @Override
    public void onCompleted(JSONObject result) throws JSONException {
        Log.v("MoviesFragment",result.toString());
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
    }

    @Override
    public void onMovieClick(Movie movie) {
        startActivity(new Intent(getContext(), DetailsActivity.class).putExtra("movie",movie));
    }
}
