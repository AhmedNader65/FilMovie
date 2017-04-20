package ahmed.FilMovie.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import ahmed.FilMovie.R;
import ahmed.FilMovie.databinding.ReviewsItemBinding;
import ahmed.FilMovie.models.Review;

/**
 * Created by ahmed on 20/04/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsHolder> {
    Context context;
    ArrayList<Review> reviewArrayList;
    public ReviewsAdapter(ArrayList<Review> reviews){
        Log.e("revieee",reviews.size()+"");
        reviewArrayList = reviews;
    }
    @Override
    public ReviewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("revieee","hiii 22");
        context = parent.getContext();
        int toInflate = R.layout.reviews_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        ReviewsItemBinding reviewsItemBinding = ReviewsItemBinding.inflate(inflater,parent,false);
//        View view = inflater.inflate(toInflate,parent,false);
        ReviewsHolder holder = new ReviewsHolder(reviewsItemBinding);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReviewsHolder holder, int position) {
        Log.e("revieee","hiii");
        holder.bind(reviewArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return reviewArrayList.size();
    }

    public class ReviewsHolder extends RecyclerView.ViewHolder {
//        TextView author_tv , content_tv;
        ReviewsItemBinding itemBinding;
        public ReviewsHolder(ReviewsItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public void bind(Review review){
            Log.e("revieee",review.getAuthor());
            itemBinding.author.setText(review.getAuthor());
            itemBinding.content.setText(review.getContent());
            // For Headers
            Typeface type = Typeface.createFromAsset(context.getAssets(), "fonts/ColabBol.otf");
            itemBinding.author.setTypeface(type);

            // For items
            Typeface type2 = Typeface.createFromAsset(context.getAssets(), "fonts/ColabReg.otf");
            itemBinding.content.setTypeface(type2);
        }
    }
}
