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
        void onCompleted(JSONObject result,String key) throws JSONException;
    }
    public NetworkUtils(OnCompleteFetchingData onComplete){
        onCompleteFetchingData = onComplete;
    }
    public String getDataByUrl(Context context, String url , final String key){

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            onCompleteFetchingData.onCompleted(response,key);
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

    public static String getUrl(Context context,String defaultUrl,String[] path,String[] queriesKey,String[] queriesValue) {
        Uri.Builder uriBuilder = Uri.parse(defaultUrl)
                .buildUpon();

        for(int i = 0 ; i < path.length;i++)
            uriBuilder.appendPath(path[i]);
        uriBuilder.appendQueryParameter("api_key",context.getString(R.string.api_key));
        if(queriesKey!=null){
            for(int i = 0 ; i < queriesKey.length;i++)
                uriBuilder.appendQueryParameter(queriesKey[i],queriesValue[i]);
        }
        Uri uri = uriBuilder.build();
        String url = uri.toString();
        return url;
    }
}
