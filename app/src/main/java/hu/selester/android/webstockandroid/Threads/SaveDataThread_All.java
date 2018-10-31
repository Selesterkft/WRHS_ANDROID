package hu.selester.android.webstockandroid.Threads;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import hu.selester.android.webstockandroid.Fragments.MovesSubTableFragment;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.CheckedList;
import hu.selester.android.webstockandroid.Objects.SessionClass;

public class SaveDataThread_All extends Thread {

    private List<String[]> data;
    private Context context;
    private int fromNum,toNum;
    private MovesSubTableFragment frg;

    public SaveDataThread_All(Context context, List<String[]> data, MovesSubTableFragment frg, int fromNum, int toNum){
        this.data = data;
        this.context = context;
        this.fromNum = fromNum;
        this.toNum = toNum;
        this.frg = frg;
    }

    @Override
    public void run() {
        CheckedList.toLogString();
        String str="";
        Log.i("SAVE DATA", "START : "+fromNum+" - "+toNum+" - "+CheckedList.getParam().size());
        for(int i=fromNum; i<toNum; i++ ){
                String commandString = SessionClass.getParam(data.get(i)[2] + "_Line_Update_String");

                commandString = commandString.replace("@TERMINAL",SessionClass.getParam("terminal"));
                commandString = commandString.replace("@LINE_ID", data.get(i)[4]);
                commandString = commandString.replace("@TRAN_CODE",data.get(i)[2]);
                for(int j=0;j<data.get(i).length; j++) {
                    if(data.get(i)[j]=="null" || data.get(i)[j]==null) {
                        commandString = commandString.replace("'@ITEM" + j+"'", "''");
                    }else{
                        commandString = commandString.replace("'@ITEM" + j+"'", "'"+data.get(i)[j]+"'");
                    }
                }
                str = str + "[Line"+data.get(i)[4]+"[comm " + commandString;
        }
        Log.i("INSERT",str);
        RequestQueue rq = MySingleton.getInstance(context).getRequestQueue();
        String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_SaveLineData_ByGroup";
        HashMap<String,String> map = new HashMap<>();
        map.put("Terminal",SessionClass.getParam("terminal"));
        map.put("User_id",SessionClass.getParam("userid"));
        map.put("Table_Name",SessionClass.getParam(data.get(0)[2]+"_Line_ListView_FROM"));
        map.put("PDA_ID","123");
        map.put("Tran_code",data.get(0)[2]);
        map.put("Head_ID",data.get(0)[3]);
        map.put("cmd",str);
        if(HelperClass.isOnline(context)) {
            JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        frg.stopProgress();
                        Log.i("REQUEST TAG", response.toString());
                        String rootText = response.getString("WRHS_PDA_SaveLineData_ByGroupResult");
                        JSONObject jsonObject = new JSONObject(rootText);
                        String rtext = jsonObject.getString("ERROR_CODE");
                        if (!rtext.isEmpty()) {
                            if (rtext.equals("-1")) {
                                if (frg.uploadpbar.getMax() == frg.uploadpbar.getProgress()) {
                                    Toast.makeText(context, "Adatok áttöltése sikeresen megtörtént!", Toast.LENGTH_LONG).show();
                                    CheckedList.clearAllData();
                                }
                            } else {
                                CheckedList.setSetChecked(1);
                                frg.stopProgress();
                                Toast.makeText(context, "Adatok áttöltése sikertelen, kérem jelezze a Selesternek!", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            frg.stopProgress();
                            CheckedList.setSetChecked(1);
                            Toast.makeText(context, "Adatok áttöltése sikertelen, kérem jelezze a Selesternek!", Toast.LENGTH_LONG).show();
                        }


                    } catch (JSONException e) {
                        frg.stopProgress();
                        CheckedList.setSetChecked(1);
                        Toast.makeText(context, "Adatok áttöltése sikertelen, kérem jelezze a Selesternek!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    frg.stopProgress();
                    CheckedList.setSetChecked(1);
                    if (error != null) {
                        Toast.makeText(context, "Adatok áttöltése sikertelen, hálózati hiba!", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }
            });
            jr.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rq.add(jr);
        }else{
            frg.stopProgress();
            CheckedList.setSetChecked(1);
            Toast.makeText(context, "Nincs hálózat, mentés nem történt meg!", Toast.LENGTH_LONG).show();
        }
    }
}