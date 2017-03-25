package ahmed.FilMovie.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ahmed.FilMovie.R;
import ahmed.FilMovie.models.Movie;

/**
 * Created by ahmed on 25/03/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {
    Context context;
    ArrayList<Movie> mMoviesList;
    OnClick onClickItem;
    public interface OnClick{
        void onMovieClick(Movie movie);
    }
    public MoviesAdapter(ArrayList<Movie> movieArrayList,OnClick OnClickObject){
        this.onClickItem = OnClickObject;
        this.mMoviesList = movieArrayList;
    }
    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        int toInflate = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(toInflate,parent,false);
        MoviesViewHolder moviesViewHolder = new MoviesViewHolder(v);
        return moviesViewHolder;
    }

    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {
        holder.bindData(mMoviesList.get(position));
    }

    @Override
    public int getItemCount() {
        return mMoviesList == null? 0:mMoviesList.size();
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView mMoviePosterIV ;
        TextView mMovieNameTV,mMovieRateTV,mMovieAdultTV;
        public MoviesViewHolder(View itemView) {
            super(itemView);
            mMovieAdultTV = (TextView) itemView.findViewById(R.id.movie_adult_tv);
            mMovieNameTV = (TextView) itemView.findViewById(R.id.movie_name_tv);
            mMovieRateTV = (TextView) itemView.findViewById(R.id.movie_rate_tv);
            mMoviePosterIV = (ImageView) itemView.findViewById(R.id.movie_pic_iv);
            itemView.setOnClickListener(this);
        }

        public void bindData(Movie movie){
            Log.v("adapter image url","http://image.tmdb.org/t/p/w185/"+movie.getPoster_path());
            Picasso.with(context).load("http://image.tmdb.org/t/p/w342/"+movie.getPoster_path()).into(mMoviePosterIV);
            mMovieNameTV.setText(movie.getTitle());
            Typeface type = Typeface.createFromAsset(context.getAssets(),"fonts/ColabBol.otf");
            Typeface typeReg = Typeface.createFromAsset(context.getAssets(),"fonts/ColabReg.otf");
            mMovieNameTV.setTypeface(type);
            mMovieRateTV.setText(String.valueOf(movie.getVote_average())+"/10");
            mMovieRateTV.setTypeface(typeReg);
            mMovieAdultTV.setTypeface(typeReg);
            if(movie.isAdult()){
                mMovieAdultTV.setText("+18");
            }else{
                mMovieAdultTV.setText("Safe");
            }
        }

        @Override
        public void onClick(View view) {
            onClickItem.onMovieClick(mMoviesList.get(getAdapterPosition()));
        }
    }
}
