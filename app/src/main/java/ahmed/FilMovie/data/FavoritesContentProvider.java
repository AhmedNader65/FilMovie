package ahmed.FilMovie.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static ahmed.FilMovie.data.MoviesContract.FavMoviesEntry.COLUMN_MOVIE_ID;
import static ahmed.FilMovie.data.MoviesContract.FavMoviesEntry.TABLE_NAME;

/**
 * Created by ahmed on 20/04/17.
 */

public class FavoritesContentProvider extends ContentProvider {
    private MoviesDBHelper dbHelper;

    public static final int CODE_MOVIES = 100;
    public static final int CODE_MOVIES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority,MoviesContract.PATH_FAV_MOVIES,CODE_MOVIES);
        matcher.addURI(authority,MoviesContract.PATH_FAV_MOVIES+"/#",CODE_MOVIES_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MoviesDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor ;
        switch (sUriMatcher.match(uri)){
            case CODE_MOVIES:
                cursor = dbHelper.getReadableDatabase().query(
                        TABLE_NAME,
                        projection,selection,selectionArgs
                        ,null,null,sortOrder);
                break;

            case CODE_MOVIES_WITH_ID:
                String id = uri.getLastPathSegment();
                cursor = dbHelper.getReadableDatabase().query(
                        TABLE_NAME,projection,COLUMN_MOVIE_ID+ "= ? "
                        ,new String[]{id},null,null,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id;
        switch (sUriMatcher.match(uri)){
            case CODE_MOVIES:
                 id= db.insert(TABLE_NAME,null,contentValues);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri "+ uri.toString());
        }
        getContext().getContentResolver().notifyChange(uri,null);
        Uri returnedUri = uri.buildUpon().appendPath(String.valueOf(id)).build();
        return returnedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numRowsDeleted;
        switch (sUriMatcher.match(uri)){
            case CODE_MOVIES:
                numRowsDeleted= db.delete(TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri "+ uri.toString());
        }
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
