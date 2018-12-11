
package hu.selester.android.webstockandroid.Threads;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Fragments.MovesSubTableFragment;
import hu.selester.android.webstockandroid.Fragments.MovesSubViewPager;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.SessionClass;

public class DeleteTempThread extends Thread{

    private String status;
    private String tranCode;
    private Context context;
    private String tranID;
    private MovesSubTableFragment f;

    public DeleteTempThread(Context context, String tranCode, MovesSubTableFragment f){
        this.context = context;
        this.tranCode = tranCode;
        this.f = f;
    }

    @Override
    public void run() {
        RequestQueue rq = MySingleton.getInstance(context).getRequestQueue();
        String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_DeleteTempData";
        HashMap<String,String> map = new HashMap<>();
        map.put("Terminal",SessionClass.getParam("terminal"));
        map.put("User_id",SessionClass.getParam("userid"));
        map.put("PDA_ID","123");
        map.put("Tran_code",tranCode);
        map.put("Table_Name",SessionClass.getParam(tranCode + "_Line_ListView_FROM"));
        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    SelesterDatabase db = SelesterDatabase.getDatabase(context);
                    String rootText=response.getString("WRHS_PDA_DeleteTempDataResult");
                    JSONObject jsonObject = new JSONObject(rootText);
                    String rtext = jsonObject.getString("ERROR_CODE");
                    if(!rtext.isEmpty()){
                        if(rtext.equals("-1")){
                            Toast.makeText(context,"Ideiglenes adatok törlése sikeres!",Toast.LENGTH_LONG).show();
                            f.closeFragment();
                        }else{
                            Toast.makeText(context,"Ideiglenes adatok törlése sikertelen, kérem jelezze a Selesternek!",Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(context,"Ideiglenes adatok törlése sikertelen, kérem jelezze a Selesternek!",Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    Toast.makeText(context,"Ideiglenes adatok törlése sikertelen, kérem jelezze a Selesternek!",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null){
                    Toast.makeText(context,"Ideiglenes adatok törlése sikertelen, hálózati hiba!",Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        });
        rq.add(jr);
    }
}
