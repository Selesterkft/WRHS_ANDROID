package hu.selester.android.webstockandroid.Threads;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.selester.android.webstockandroid.Fragments.MovesSubTableFragment;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.CheckedList;
import hu.selester.android.webstockandroid.Objects.InsertedList;
import hu.selester.android.webstockandroid.Objects.NotCloseList;
import hu.selester.android.webstockandroid.Objects.SessionClass;

public class SaveDataThread_All extends Thread {

    private List<String[]> data;
    private Context context;
    private int qLineID, qRefLineID, qHeadID;
    private int fromNum,toNum;
    private MovesSubTableFragment frg;
    private String tranCode;

    public SaveDataThread_All(Context context, List<String[]> data, MovesSubTableFragment frg, int fromNum, int toNum){
        this.data = data;
        this.context = context;
        this.fromNum = fromNum;
        this.toNum = toNum;
        this.frg = frg;
        tranCode    = SessionClass.getParam("tranCode");
        qLineID     = HelperClass.getArrayPosition("Line_ID",       SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qRefLineID  = HelperClass.getArrayPosition("Ref_Line_ID",   SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qHeadID     = HelperClass.getArrayPosition("Head_ID",       SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
    }

    @Override
    public void run() {
        /*
        InsertedList.toStringLog();
        if( InsertedList.getAllEmptyElement().size() > 0 ){
            RequestQueue rq = MySingleton.getInstance(context).getRequestQueue();
            String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_getNewids/" + SessionClass.getParam("terminal") + "/" + InsertedList.getAllEmptyElement().size();
            if (HelperClass.isOnline(context)) {
                JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String rootText = response.getString("WRHS_PDA_getNewidsResult");
                            try {
                                JSONObject jsonObject = new JSONObject(rootText);
                                Toast.makeText(context, jsonObject.getString("ERROR_TEXT"), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                List<String> emptyElement = InsertedList.getAllEmptyElement();
                                JSONArray jsonArray = new JSONArray(rootText);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    InsertedList.setInsertElement( emptyElement.get(i), jsonArray.getJSONObject(i).getString("ID"));
                                }
                                saveItems();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, "Adatok áttöltése sikertelen, kérem jelezze a Selesternek!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null) {
                            Toast.makeText(context, "Adatok áttöltése sikertelen, hálózati hiba!", Toast.LENGTH_LONG).show();
                            error.printStackTrace();
                        }
                    }
                });
                jr.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                rq.add(jr);
            } else {
                Toast.makeText(context, "Nincs hálózat, mentés nem történt meg!", Toast.LENGTH_LONG).show();
            }
        }else{
            saveItems();
        }*/
        Log.i("TAG THREAD","SaveDataThread_All");
        saveItems();
    }

    private String createCommandString(){
        InsertedList.toStringLog();

        String str="";
        int newLineId = 0;
        for(int i=fromNum; i<toNum; i++ ){
            if( !InsertedList.isInsert(data.get(i)[0]) ) {
                String commandString = SessionClass.getParam(tranCode + "_Line_Update_String");
                commandString = commandString.replace("@TERMINAL", SessionClass.getParam("terminal"));
                commandString = commandString.replace("@LINE_ID", data.get(i)[qLineID]);
                commandString = commandString.replace("@TRAN_CODE", tranCode);
                for (int j = 0; j < data.get(i).length; j++) {
                    if (data.get(i)[j] == "null" || data.get(i)[j] == null) {
                        commandString = commandString.replace("'@ITEM" + j + "'", "''");
                    } else {
                        commandString = commandString.replace("'@ITEM" + j + "'", "'" + data.get(i)[j] + "'");
                    }
                }
                str = str + "[Line" + data.get(i)[4] + "[comm " + commandString;
            }
        }
        Log.i("TAG - UPDATE",str);
        return str;
    }

    private void saveItems(){
        String str = createCommandString();
        RequestQueue rq = MySingleton.getInstance(context).getRequestQueue();
        String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_SaveLineData_ByGroup";
        HashMap<String,String> map = new HashMap<>();
        map.put("Terminal",SessionClass.getParam("terminal"));
        map.put("User_id",SessionClass.getParam("userid"));
        map.put("Table_Name",SessionClass.getParam(tranCode + "_Line_ListView_FROM"));
        map.put("PDA_ID","123");
        map.put("Tran_code",tranCode);
        if( tranCode.charAt(0) != '3' && tranCode.charAt(0) != '4' ){
            map.put("Head_ID",data.get(0)[qHeadID]);
        }else{
            map.put("Head_ID","0");
        }
        map.put("cmd",str);

        if(HelperClass.isOnline(context)) {
            Log.i("URL",url);
            JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        frg.stopProgress();
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
            frg.stopProgress();
        }else{
            frg.stopProgress();
            CheckedList.setSetChecked(1);
            Toast.makeText(context, "Nincs hálózat, mentés nem történt meg!", Toast.LENGTH_LONG).show();
        }
    }
}