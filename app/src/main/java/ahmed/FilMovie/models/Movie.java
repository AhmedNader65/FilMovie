package ahmed.FilMovie.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ahmed on 25/03/17.
 */

public class Movie implements Parcelable{
    public static HashMap<Integer,String> Genres = new HashMap<>();
    private int id;
    private String title;
    private String poster_path;
    private String backdrop_path;
    private boolean adult;
    private String overview;
    private String release_date;
    private int  vote_count;
    private double  vote_average;
    private ArrayList<Integer> genresIds = new ArrayList<>();
    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        poster_path = in.readString();
        backdrop_path = in.readString();
        adult = in.readByte() != 0;
        overview = in.readString();
        release_date = in.readString();
        vote_count = in.readInt();
        vote_average = in.readDouble();
        genresIds = in.readArrayList(getClass().getClassLoader());

    }
    public Movie(int id , String title,String poster_path,String backdrop_path,boolean adult , String overview,String release_date
    ,int vote_count , double vote_average,ArrayList<Integer> genresIds){
        this.id = id;
        this.title = title;
        this.poster_path = poster_path;
        this.backdrop_path = backdrop_path;
        this.adult = adult;
        this.overview = overview;
        this.release_date = release_date;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
        this.genresIds = genresIds;
    }
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getId() {
        return id;
    }

    public ArrayList<Integer> getGenresIds() {
        return genresIds;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public boolean isAdult() {
        return adult;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public int getVote_count() {
        return vote_count;
    }

    public double getVote_average() {
        return vote_average;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(poster_path);
        parcel.writeString(backdrop_path);
        parcel.writeByte((byte) (adult ? 1 : 0));
        parcel.writeString(overview);
        parcel.writeString(release_date);
        parcel.writeInt(vote_count);
        parcel.writeDouble(vote_average);
        parcel.writeList(genresIds);
    }
}
