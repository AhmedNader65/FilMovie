package ahmed.FilMovie.network;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import ahmed.FilMovie.R;


/**
 * Created by ahmed on 25/03/17.
 */

public class NetworkUtils {
    private OnCompleteFetchingData onCompleteFetchingData;
    public interface OnCompleteFetchingData{
        void onCompleted(JSONObject result) throws JSONException;
    }
    public NetworkUtils(OnCompleteFetchingData onComplete){
        onCompleteFetchingData = onComplete;
    }
    public String getDataByUrl(Context context,String url ){

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            onCompleteFetchingData.onCompleted(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
        return null;
    }

    public static String getUrl(Context context,String defultUrl,String path) {
        Uri uri = Uri.parse(defultUrl)
                .buildUpon()
                .appendPath(path)
                .appendQueryParameter("api_key",context.getString(R.string.api_key)).build();
        String url = uri.toString();
        return url;
    }
}
