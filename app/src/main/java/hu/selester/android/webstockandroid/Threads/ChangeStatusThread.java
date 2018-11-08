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

import hu.selester.android.webstockandroid.Fragments.LoginFragment;
import hu.selester.android.webstockandroid.Fragments.MovesSubTableFragment;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.SessionClass;

public class ChangeStatusThread extends Thread{
    private String status;
    private String tranCode;
    private Context context;
    private String tranID;
    private MovesSubTableFragment f;

    public ChangeStatusThread(Context context,  String status, String tranCode, String tranID, MovesSubTableFragment f){
        this.tranID = tranID;
        this.context = context;
        this.status = status;
        this.tranCode = tranCode;
        this.f = f;
    }

    @Override
    public void run() {
        RequestQueue rq = MySingleton.getInstance(context).getRequestQueue();
        String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_setStatus";
        HashMap<String,String> map = new HashMap<>();
        map.put("Terminal",SessionClass.getParam("terminal"));
        map.put("User_id",SessionClass.getParam("userid"));
        map.put("PDA_ID","123");
        map.put("Tran_code",tranCode);
        map.put("where","ID=" + tranID);
        map.put("NewStatus",status);
        Log.i("STATUS URL",url);
        Log.i("Terminal",SessionClass.getParam("terminal"));
        Log.i("User_id",SessionClass.getParam("userid"));
        Log.i("PDA_ID","123");
        Log.i("Tran_code",tranCode);
        Log.i("where","ID=" + tranID);
        Log.i("NewStatus",status);


        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("STATUS REQUEST TAG",response.toString());
                    String rootText=response.getString("WRHS_PDA_setStatusResult");
                    JSONObject jsonObject = new JSONObject(rootText);
                    String rtext = jsonObject.getString("ERROR_CODE");
                    if(!rtext.isEmpty()){
                        if(rtext.equals("-1")){
                            //Toast.makeText(context,"Státusz váltása sikeres: "+status+"!",Toast.LENGTH_LONG).show();
                            if(status.equals("PDA")){
                                new DeleteTempThread(context,tranCode,f).start();
                            }
                        }else{

                            Toast.makeText(context,"Státusz váltása sikertelen, kérem jelezze a Selesternek!",Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(context,"Státusz váltása sikertelen, kérem jelezze a Selesternek!",Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    Toast.makeText(context,"Státusz váltása sikertelen, kérem jelezze a Selesternek!",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null){
                    Toast.makeText(context,"Státusz váltása sikertelen, hálózati hiba!",Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        });
        rq.add(jr);
    }
}