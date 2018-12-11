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

import org.apache.commons.collections.map.HashedMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Fragments.CheckPlaceFragment;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.SessionClass;

public class CheckPlaceSaveThread extends Thread {

    private Context context;
    private int whID;
    private String placeID, taskID;

    public CheckPlaceSaveThread(Context context, int whID, String placeID, String taskID) {
        this.context = context;
        this.whID = whID;
        this.placeID = placeID;
        this.taskID = taskID;
    }

    @Override
    public void run() {

        SelesterDatabase db = SelesterDatabase.getDatabase(context);
        List<String[]> dataList = AllLinesData.getAllDataList();
        String str = "[";
        String ean;
        for(int i = 0; i < dataList.size(); i++ ){

            try{
                int c = 0;
                if(dataList.get(i)[3].isEmpty()){
                    c = Integer.parseInt(dataList.get(i)[4]);
                }else{
                    c = Integer.parseInt(dataList.get(i)[4]) - Integer.parseInt(dataList.get(i)[3]);
                }
                String[] x = db.productDataDAO().getProdBarcode(dataList.get(i)[0]);
                if( x == null || c < 0 ){
                    ean = "";
                }else{
                    ean = x[0];
                }
                if( c!=0 ) str +="{\"terminal\":\""+SessionClass.getParam("terminal")+"\",\"taskid\":\""+taskID+"\",\"whid\":\""+whID+"\",\"placeid\":\""+placeID+"\",\"itemno\":\""+dataList.get(i)[0]+"\", \"ean\":\""+ean+"\", \"count\":\""+c+"\"},";


            }catch (Exception e){
                e.printStackTrace();
            }
        }
        str = str.substring(0,str.length()-1);
        str += "]";

        RequestQueue rq = MySingleton.getInstance(context).getRequestQueue();
        String url = SessionClass.getParam("WSUrl") + "/randominventory_save";
        JSONObject job = new JSONObject();
        try {
            job.put("DATA", str);
        }catch (Exception e){
            e.printStackTrace();
        }
        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.POST, url, job , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String rootText=response.getString("randominventory_saveResult");
                    JSONObject jsonObject = new JSONObject(rootText);
                    String rtext = jsonObject.getString("ERROR_CODE");
                    if(!rtext.isEmpty()){
                        if(rtext.equals("-1")){
                            Toast.makeText(context,"Mentés sikeresen megtörtént!",Toast.LENGTH_LONG).show();
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
