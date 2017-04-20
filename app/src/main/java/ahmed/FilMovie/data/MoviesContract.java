package ahmed.FilMovie.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ahmed on 20/04/17.
 */

public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "ahmed.FilMovie";

    public static final Uri BASE_COTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);


    public static final String PATH_FAV_MOVIES = "favorite_movies";

    public static final class FavMoviesEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_COTENT_URI.buildUpon().appendPath(PATH_FAV_MOVIES).build();

        public static final String TABLE_NAME = "favMovies";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_POSTER = "poster_path";
        public static final String COLUMN_MOVIE_BACKDROP = "backdrop_path";
        public static final String COLUMN_MOVIE_ADULT = "adult";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_RELEASE = "release_date";
        public static final String COLUMN_MOVIE_VOTE_COUNT = "vote_count";
        public static final String COLUMN_MOVIE_VOTE_AVG = "vote_average";

        public static Uri buildMovieWithId(int id){
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(id))
                    .build();
        }
    }
}
