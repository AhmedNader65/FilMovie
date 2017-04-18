package ahmed.FilMovie.Activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ahmed.FilMovie.R;
import ahmed.FilMovie.databinding.ActivityDetailsBinding;
import ahmed.FilMovie.models.Actor;
import ahmed.FilMovie.models.Movie;
import ahmed.FilMovie.network.NetworkUtils;

import static ahmed.FilMovie.models.Movie.Genres;

public class DetailsActivity extends AppCompatActivity implements NetworkUtils.OnCompleteFetchingData {
    ActivityDetailsBinding binding;
    boolean isShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        setSupportActionBar(binding.toolbar);
        Intent intent = getIntent();
        Movie movie= intent.getParcelableExtra("movie");
        binding.toolbarLayout.setTitle(movie.getTitle());
        bindDate(movie);
        setTypeFace();
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "In the second phase", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
;
        binding.fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "In the second phase", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        binding.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                    Log.e("scroll range",scrollRange+"");
                    Log.e("vertical offset ",verticalOffset+"");
                }
                if (scrollRange + verticalOffset == 0) {
                    binding.fab2.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                    isShow = true;
                } else if(isShow) {
                    isShow = false;
                    binding.fab2.setVisibility(View.GONE);
                }
            }
        });


        // GET CAST METHOD
        getCast(movie.getId());
    }

    private void getCast(int id) {
        String url = NetworkUtils.getUrl(this, "http://api.themoviedb.org/3/movie", new String[]{String.valueOf(id), "credits"});
        NetworkUtils getDataObject = new NetworkUtils(this);
        getDataObject.getDataByUrl(this, url);
    }

    private void bindDate(Movie movie){
        binding.included.countValueTv.setText(String.valueOf(movie.getVote_count()));
        binding.included.voteValueTv.setText(String.valueOf(movie.getVote_average()));
        binding.included.nameValueTv.setText(movie.getTitle());
        binding.included.overviewValueTv.setText(movie.getOverview());
        binding.included.adultValueTv.setText(String.valueOf(movie.isAdult()));
        binding.included.dateValueTv.setText(movie.getRelease_date());
        String genreText = Genres.get(movie.getGenresIds().get(0));
        for(int i = 1; i< movie.getGenresIds().size();i++){
            genreText +=", " +Movie.Genres.get(movie.getGenresIds().get(i));
        }
        binding.included.genresValueTv.setText(genreText);
        Picasso.with(this).load("http://image.tmdb.org/t/p/w342/"+movie.getPoster_path()).into(binding.included.moviePosterIv);
        Picasso.with(this).load("http://image.tmdb.org/t/p/w342/"+movie.getBackdrop_path()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                binding.toolbarLayout.setBackground(new BitmapDrawable(getResources(),bitmap));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Toast.makeText(DetailsActivity.this, "FailedLoading", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    private void setTypeFace(){

        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/ColabBol.otf");
        binding.included.dateLabelTv.setTypeface(type);
        binding.included.nameLabelTv.setTypeface(type);
        binding.included.voteLabelTv.setTypeface(type);
        binding.included.overviewLabelTv.setTypeface(type);
        binding.included.adultLabelTv.setTypeface(type);
        binding.included.genresLabelTv.setTypeface(type);
        binding.included.countLabelTv.setTypeface(type);
        Typeface type2 = Typeface.createFromAsset(getAssets(),"fonts/ColabReg.otf");
        binding.included.dateValueTv.setTypeface(type2);
        binding.included.adultValueTv.setTypeface(type2);
        binding.included.nameValueTv.setTypeface(type2);
        binding.included.overviewValueTv.setTypeface(type2);
        binding.included.voteValueTv.setTypeface(type2);
        binding.included.countValueTv.setTypeface(type2);
        binding.included.genresValueTv.setTypeface(type2);

    }

    @Override
    public void onCompleted(JSONObject result) throws JSONException {
        JSONArray cast = result.getJSONArray("cast");
        if (cast.length() > 0) {
            binding.included.castProgressbar.setVisibility(View.GONE);
            binding.included.castLayout.setVisibility(View.VISIBLE);
            for (int i = 0; i <= 4; i++) {
                JSONObject actorObj = cast.getJSONObject(i);
                int id = actorObj.getInt("id");
                String name = actorObj.getString("name");
                String characterName = actorObj.getString("character");
                String profPic = actorObj.getString("profile_path");
                Actor newActor = new Actor(id, name, characterName, profPic);
                switch (i) {
                    case 0:
                        binding.included.name1.setText(newActor.getName());
                        binding.included.characterName1.setText(newActor.getCharacterName());
                        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/" + newActor.getPicUrl()).into(binding.included.profileImage1);
                        break;

                    case 1:
                        binding.included.name2.setText(newActor.getName());
                        binding.included.characterName2.setText(newActor.getCharacterName());
                        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/" + newActor.getPicUrl()).into(binding.included.profileImage2);
                        break;

                    case 2:
                        binding.included.name3.setText(newActor.getName());
                        binding.included.characterName3.setText(newActor.getCharacterName());
                        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/" + newActor.getPicUrl()).into(binding.included.profileImage3);
                        break;

                    case 3:
                        binding.included.name4.setText(newActor.getName());
                        binding.included.characterName4.setText(newActor.getCharacterName());
                        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/" + newActor.getPicUrl()).into(binding.included.profileImage4);
                        break;

                    case 4:
                        binding.included.name5.setText(newActor.getName());
                        binding.included.characterName5.setText(newActor.getCharacterName());
                        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/" + newActor.getPicUrl()).into(binding.included.profileImage5);
                        break;

                }
            }
        }
    }
}
