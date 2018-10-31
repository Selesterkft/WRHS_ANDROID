package hu.selester.android.webstockandroid.Threads;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Database.Tables.ProductData;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.SessionClass;

public class LoadEANDatasThread extends Thread {

    private Context context;
    private SelesterDatabase db;

    public LoadEANDatasThread(Context context) {
        this.context = context;
        db = SelesterDatabase.getDatabase(context);
    }

    @Override
    public void run() {
        Long lastTID = db.productDataDAO().getLastTransactId();
        if(lastTID == null){ lastTID = 0L; }
        Log.i("TAG","LAST TRANSACT ID: "+lastTID);
        String url=SessionClass.getParam("WSUrl")+"/GET_items_width_eans/"+lastTID;
        RequestQueue rq = MySingleton.getInstance(context).getRequestQueue();
        JSONObject jsonObject=null;
        Log.i("TAG",url);
        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String rootText=response.getString("GET_items_width_eansResult");
                    JSONArray jsonArray = new JSONArray(rootText);
                    List<ProductData> list = new ArrayList<>();
                    for(int i=0;i<jsonArray.length();i++) {
                        ProductData pd = new ProductData(
                                jsonArray.getJSONObject(i).getInt("itemId"),
                                jsonArray.getJSONObject(i).getString("ITEM"),
                                "",
                                jsonArray.getJSONObject(i).getString("EAN_Code"),
                                jsonArray.getJSONObject(i).getLong("TransactID")
                        );
                        list.add(pd);
                        //Log.i("TAG",jsonArray.getJSONObject(i).getString("ITEM")+" - "+jsonArray.getJSONObject(i).getString("EAN_Code"));
                    }
                    db.productDataDAO().setProductData(list);
                    Log.i("TAG", "EAN CODES LOADING COUNT: "+list.size());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null){
                    error.printStackTrace();
                }
            }
        });
        jr.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(jr);
    }
}
