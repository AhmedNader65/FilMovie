package ahmed.FilMovie.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ahmed.FilMovie.R;
import ahmed.FilMovie.adapters.ReviewsAdapter;
import ahmed.FilMovie.data.MoviesContract.FavMoviesEntry;
import ahmed.FilMovie.databinding.ActivityDetailsBinding;
import ahmed.FilMovie.fragments.MoviesFragment;
import ahmed.FilMovie.models.Actor;
import ahmed.FilMovie.models.Movie;
import ahmed.FilMovie.models.Review;
import ahmed.FilMovie.network.NetworkUtils;

import static ahmed.FilMovie.models.Movie.Genres;

public class DetailsActivity extends AppCompatActivity implements NetworkUtils.OnCompleteFetchingData, View.OnClickListener {
    ActivityDetailsBinding binding;
    boolean isShow = false;
    Movie movie;
    private boolean isFavorite = false;

    ArrayList<String> trailers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        setSupportActionBar(binding.toolbar);
        Intent intent = getIntent();
        movie = intent.getParcelableExtra("movie");
        //check if is favorite movie or not
        checkFav();
        binding.toolbarLayout.setTitle(movie.getTitle());

        // set smooth scroll for recyclerView
        binding.included.recviewsRv.setNestedScrollingEnabled(false);

        bindDate(movie);
        setTypeFace();
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData(view);
            }
        });
        ;
        binding.fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData(view);
            }
        });

        binding.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    binding.fab2.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                    isShow = true;
                } else if (isShow) {
                    isShow = false;
                    binding.fab2.setVisibility(View.GONE);
                }
            }
        });


        // GET CAST METHOD
        getCast(movie.getId());

        // GET TRAILERS
        getTrailers(movie.getId());

        // GET REVIEWS
        getReviews(movie.getId());
    }

    private void checkFav() {
        Cursor cursor = getContentResolver().query(FavMoviesEntry.CONTENT_URI,new String[]{FavMoviesEntry._ID}, FavMoviesEntry.COLUMN_MOVIE_ID + " = ? "
        ,new String[]{String.valueOf(movie.getId())},null,null);
        if(cursor.getCount()>0) {
            isFavorite = true;
            binding.fab.setImageResource(R.drawable.ic_unfavorite);
            binding.fab2.setImageResource(R.drawable.ic_unfavorite);
        }
    }

    private void saveData(View view) {
        if(isFavorite){
            int num =getContentResolver().delete(FavMoviesEntry.CONTENT_URI,FavMoviesEntry.COLUMN_MOVIE_ID +" = ? ",
                    new String[]{String.valueOf(movie.getId())});
            if(num>0){
                isFavorite = false;
                Snackbar.make(view, "Unsaved ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                binding.fab.setImageResource(R.drawable.ic_favorite);
                binding.fab2.setImageResource(R.drawable.ic_favorite);
                MoviesFragment.needRefresh = true;
            }
        }else {
            if (movie != null) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(FavMoviesEntry.COLUMN_MOVIE_ID, movie.getId());
                contentValues.put(FavMoviesEntry.COLUMN_MOVIE_ADULT, (movie.isAdult()) ? 1 : 0);
                contentValues.put(FavMoviesEntry.COLUMN_MOVIE_BACKDROP, movie.getBackdrop_path());
                contentValues.put(FavMoviesEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
                contentValues.put(FavMoviesEntry.COLUMN_MOVIE_POSTER, movie.getPoster_path());
                contentValues.put(FavMoviesEntry.COLUMN_MOVIE_RELEASE, movie.getRelease_date());
                contentValues.put(FavMoviesEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
                contentValues.put(FavMoviesEntry.COLUMN_MOVIE_VOTE_AVG, movie.getVote_average());
                contentValues.put(FavMoviesEntry.COLUMN_MOVIE_VOTE_COUNT, movie.getVote_count());
                Uri uri = getContentResolver().insert(FavMoviesEntry.CONTENT_URI, contentValues);
                Snackbar.make(view, "Saved ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                isFavorite = true;
                binding.fab.setImageResource(R.drawable.ic_unfavorite);
                binding.fab2.setImageResource(R.drawable.ic_unfavorite);
            }
        }
    }

    private void getTrailers(int id) {
        String url = NetworkUtils.getUrl(this, "http://api.themoviedb.org/3/movie", new String[]{String.valueOf(id), "videos"},null,null);
        NetworkUtils getDataObject = new NetworkUtils(this);
        getDataObject.getDataByUrl(this, url, getString(R.string.get_trailer_key));
    }

    private void getCast(int id) {
        String url = NetworkUtils.getUrl(this, "http://api.themoviedb.org/3/movie", new String[]{String.valueOf(id), "credits"},null,null);
        NetworkUtils getDataObject = new NetworkUtils(this);
        getDataObject.getDataByUrl(this, url, getString(R.string.get_cast_key));
    }

    private void getReviews(int id) {
        String url = NetworkUtils.getUrl(this, "http://api.themoviedb.org/3/movie", new String[]{String.valueOf(id), "reviews"},null,null);
        NetworkUtils getDataObject = new NetworkUtils(this);
        getDataObject.getDataByUrl(this, url, getString(R.string.get_reviews_key));
    }

    private void bindDate(Movie movie) {
        binding.included.countValueTv.setText(String.valueOf(movie.getVote_count()));
        binding.included.voteValueTv.setText(String.valueOf(movie.getVote_average()));
        binding.included.nameValueTv.setText(movie.getTitle());
        binding.included.overviewValueTv.setText(movie.getOverview());
        binding.included.adultValueTv.setText(String.valueOf(movie.isAdult()));
        binding.included.dateValueTv.setText(movie.getRelease_date());
        if(movie.getGenresIds()!=null) {
            binding.included.genresLabelTv.setVisibility(View.VISIBLE);
            binding.included.genresValueTv.setVisibility(View.VISIBLE);
            String genreText = Genres.get(movie.getGenresIds().get(0));
            for (int i = 1; i < movie.getGenresIds().size(); i++) {
                genreText += ", " + Movie.Genres.get(movie.getGenresIds().get(i));
            }
            binding.included.genresValueTv.setText(genreText);
        }
        binding.included.moviePosterIv.setOnClickListener(this);
        Picasso.with(this).load("http://image.tmdb.org/t/p/w342/" + movie.getPoster_path()).into(binding.included.moviePosterIv);
        Target toolbarlayoutTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                binding.toolbarLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Toast.makeText(DetailsActivity.this, "FailedLoading", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        binding.toolbarLayout.setTag(toolbarlayoutTarget);
        Picasso.with(this).load("http://image.tmdb.org/t/p/w342/" + movie.getBackdrop_path()).into((Target)binding.toolbarLayout.getTag());
    }

    private void setTypeFace() {
        // For Headers
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/ColabBol.otf");
        binding.included.dateLabelTv.setTypeface(type);
        binding.included.nameLabelTv.setTypeface(type);
        binding.included.voteLabelTv.setTypeface(type);
        binding.included.overviewLabelTv.setTypeface(type);
        binding.included.adultLabelTv.setTypeface(type);
        binding.included.genresLabelTv.setTypeface(type);
        binding.included.countLabelTv.setTypeface(type);
        binding.included.castLabelTv.setTypeface(type);
        binding.included.videosLabelTv.setTypeface(type);
        binding.included.reviewsLabelTv.setTypeface(type);

        // For items
        Typeface type2 = Typeface.createFromAsset(getAssets(), "fonts/ColabReg.otf");
        binding.included.dateValueTv.setTypeface(type2);
        binding.included.adultValueTv.setTypeface(type2);
        binding.included.nameValueTv.setTypeface(type2);
        binding.included.overviewValueTv.setTypeface(type2);
        binding.included.voteValueTv.setTypeface(type2);
        binding.included.countValueTv.setTypeface(type2);
        binding.included.genresValueTv.setTypeface(type2);
        binding.included.name1.setTypeface(type2);
        binding.included.name2.setTypeface(type2);
        binding.included.name3.setTypeface(type2);
        binding.included.name4.setTypeface(type2);
        binding.included.name5.setTypeface(type2);
        binding.included.characterName1.setTypeface(type2);
        binding.included.characterName2.setTypeface(type2);
        binding.included.characterName3.setTypeface(type2);
        binding.included.characterName4.setTypeface(type2);
        binding.included.characterName5.setTypeface(type2);

    }

    @Override
    public void onCompleted(JSONObject result, String key) throws JSONException {
        if (key.equals(getString(R.string.get_cast_key))) {
            JSONArray cast = result.getJSONArray("cast");
            if (cast.length() > 0) {
                binding.included.castProgressbar.setVisibility(View.GONE);
                binding.included.castLayout.setVisibility(View.VISIBLE);
                int count = 0;
                if(cast.length()>4)
                    count = 4;
                else
                    count = cast.length()-1;
                for (int i = 0; i <= count; i++) {
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
        if (key.equals(getString(R.string.get_trailer_key))) {
            JSONArray videos = result.getJSONArray("results");
            if (videos.length() > 0) {
                trailers = new ArrayList<>();
                for (int i = 0; i < videos.length(); i++) {
                    if (videos.getJSONObject(i).getString("type").equals("Trailer")) {
                        trailers.add(videos.getJSONObject(i).getString("key"));
                        ActivityCompat.invalidateOptionsMenu(this);
                        final ImageView imageView = new ImageView(this);
                        //setting image resource
                        imageView.setImageResource(R.drawable.video_place_holder);
                        // convert px to dp
                        int dps_height = 200;
                        int dps_margin = 8;
                        final float scale = getResources().getDisplayMetrics().density;
                        int pixels_h = (int) (dps_height * scale + 0.5f);
                        int pixels_m = (int) (dps_margin * scale + 0.5f);
                        //setting image position
                        final int width = (int) ((getResources().getDisplayMetrics().widthPixels) / 2.2);
                        FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(width,
                                pixels_h);
                        lp.setMargins(pixels_m, 40, pixels_m, 20);
                        imageView.setLayoutParams(lp);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        Target target = new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                imageView.setBackground(new BitmapDrawable(getResources(), bitmap));
                                imageView.setImageResource(R.drawable.video_shape);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                                Log.d("TAG", "FAILED");
                                imageView.setImageResource(R.drawable.video_failed);
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                Log.d("TAG", "Prepare Load");

                            }
                        };
                        imageView.setTag(R.id.target,target);
                        Picasso.with(this).load("http://img.youtube.com/vi/" + videos.getJSONObject(i).getString("key") + "/0.jpg").into((Target) imageView.getTag(R.id.target));
                        //adding view to layout
                        imageView.setTag(R.id.key,videos.getJSONObject(i).get("key"));
                        imageView.setOnClickListener(this);
                        binding.included.flowLayout.addView(imageView);
                    }
                }
            }
        }
        if(key.equals(getString(R.string.get_reviews_key))){
            JSONArray reviewsArray = result.getJSONArray("results");
            if(reviewsArray.length()>0){
                ArrayList<Review> reviewArrayList = new ArrayList<>();
                for(int i = 0 ; i< reviewsArray.length();i++){
                    Review review = new Review();
                    review.setAuthor(reviewsArray.getJSONObject(i).getString("author"));
                    review.setContent(reviewsArray.getJSONObject(i).getString("content"));
                    reviewArrayList.add(review);
                }
                binding.included.recviewsRv.setLayoutManager(new LinearLayoutManager(DetailsActivity.this));

                binding.included.recviewsRv.setAdapter(new ReviewsAdapter(reviewArrayList));
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getTag(R.id.key)!=null){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String key = (String) view.getTag(R.id.key);
            Uri uri_link = Uri.parse("http://www.youtube.com").buildUpon().appendPath("watch").appendQueryParameter("v",key).build();
            intent.setData(uri_link);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Share menu item clicked */
        int id = item.getItemId();
        if (id == R.id.action_share) {
            if(trailers!=null) {
                Intent shareIntent = createShareForecastIntent();
                startActivity(Intent.createChooser(shareIntent,"Where to share? "));
            }else{
                Toast.makeText(this, "Wait to load trailers", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return true;
    }

    private Intent createShareForecastIntent() {
        if(trailers.size()>0) {
            Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setText("Check out this Trailer!! "+"http://youtu.be/"+trailers.get(0))
                    .getIntent();
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            return shareIntent;
        }else{
            Toast.makeText(this, "No trailers to share!", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
