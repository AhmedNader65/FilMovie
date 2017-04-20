package ahmed.FilMovie.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import ahmed.FilMovie.R;

/**
 * Created by ahmed on 20/04/17.
 */

public class PicassoTarget implements Target {
    ImageView imageView;
    Context context;
    public PicassoTarget(Context context,ImageView imageView){
        this.imageView = imageView;
        this.context = context;
    }
    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

        imageView.setBackground(new BitmapDrawable(context.getResources(), bitmap));
        imageView.setImageResource(R.drawable.video_shape);
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

        Log.d("TAG", "FAILED");
        imageView.setImageResource(R.drawable.video_failed);
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
