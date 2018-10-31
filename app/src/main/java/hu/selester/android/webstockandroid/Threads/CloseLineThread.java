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

import hu.selester.android.webstockandroid.Fragments.MovesSubTableFragment;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.SessionClass;

public class CloseLineThread extends Thread {

    private String tranCode;
    private String tranID;
    private Context context;
    private MovesSubTableFragment f;


    public CloseLineThread(Context context, String tranCode, String tranID, MovesSubTableFragment f){
        this.context = context;
        this.tranCode = tranCode;
        this.tranID = tranID;
        this.f = f;
    }

    @Override
    public void run() {
        RequestQueue rq = MySingleton.getInstance(context).getRequestQueue();
        String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_CloseLineData";
        HashMap<String,String> map = new HashMap<>();
        map.put("Terminal",SessionClass.getParam("terminal"));
        map.put("User_id",SessionClass.getParam("userid"));
        map.put("Table_Name",SessionClass.getParam(tranCode + "_Line_ListView_FROM"));
        map.put("PDA_ID","123");
        map.put("Tran_code",tranCode);

        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("REQUEST TAG",response.toString());
                    String rootText=response.getString("WRHS_PDA_CloseLineDataResult");
                    JSONObject jsonObject = new JSONObject(rootText);
                    String rtext = jsonObject.getString("ERROR_CODE");
                    if(!rtext.isEmpty()){
                        if(rtext.equals("-1")){
                            Toast.makeText(context,"Sorok lezárása sikeres!",Toast.LENGTH_LONG).show();
                            new ChangeStatusThread(context,"PDA_CHECKED", tranCode,tranID,f).start();
                            f.closeFragment();
                        }else{
                            try {
                                String rtext2 = jsonObject.getString("ERROR_TEXT");
                                if (rtext2!=null && !rtext2.equals("")){
                                    Toast.makeText(context, rtext2, Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(context, "Sorok lezárása sikertelen, kérem jelezze a Selesternek!", Toast.LENGTH_LONG).show();
                                }
                            }catch(Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Sorok lezárása sikertelen, kérem jelezze a Selesternek!", Toast.LENGTH_LONG).show();
                            }
                        }

                    }else{
                        Toast.makeText(context,"Sorok lezárása sikertelen, kérem jelezze a Selesternek!",Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    Toast.makeText(context,"Sorok lezárása sikertelen, kérem jelezze a Selesternek!",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null){
                    Toast.makeText(context,"Sorok lezárása sikertelen, hálózati hiba!",Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        });
        rq.add(jr);

    }
}
