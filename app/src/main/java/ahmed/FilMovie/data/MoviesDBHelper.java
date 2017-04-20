package ahmed.FilMovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ahmed.FilMovie.data.MoviesContract.FavMoviesEntry;
/**
 * Created by ahmed on 20/04/17.
 */

public class MoviesDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "FilMovies.db";
    public static final int DATABASE_VERSION = 1;
    public MoviesDBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE  = "CREATE TABLE "+ FavMoviesEntry.TABLE_NAME +"( "+
                FavMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FavMoviesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, "
                + FavMoviesEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, "
                + FavMoviesEntry.COLUMN_MOVIE_RELEASE + " TEXT NOT NULL, "
                + FavMoviesEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, "
                + FavMoviesEntry.COLUMN_MOVIE_BACKDROP + " TEXT NOT NULL, "
                + FavMoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + FavMoviesEntry.COLUMN_MOVIE_ADULT + " INTEGER NOT NULL, "
                + FavMoviesEntry.COLUMN_MOVIE_VOTE_AVG + " REAL NOT NULL, "
                + FavMoviesEntry.COLUMN_MOVIE_VOTE_COUNT + " REAL NOT NULL,"
        +"UNIQUE (" + FavMoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ FavMoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
