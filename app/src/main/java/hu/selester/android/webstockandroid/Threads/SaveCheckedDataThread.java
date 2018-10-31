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
import java.util.Map;

import hu.selester.android.webstockandroid.Fragments.MovesSubTableFragment;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.ActiveFragment;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.CheckedList;
import hu.selester.android.webstockandroid.Objects.SessionClass;

public class SaveCheckedDataThread extends Thread{

    private Context context;
    private Boolean mainSave;
    private int maxCount = 100;
    private List<String[]> datas;

    public SaveCheckedDataThread( Context context) {
        this.context = context;
        this.mainSave = false;
    }

    @Override
    public void run() {

        datas = AllLinesData.getAllDataList();
        String str="";

        Log.i("SaveCheckedData", "START");
        String tranCode = "";
        String headID = "";
        int count = 0;
        for (Map.Entry<String, Integer> entry : CheckedList.getParam().entrySet()) {
            if (entry.getValue() == 1 || entry.getValue() == 2) {
                if(count < maxCount) {
                    try {
                        String[] data = AllLinesData.getParam(entry.getKey());
                        String commandString = SessionClass.getParam(data[2] + "_Line_Update_String");
                        if (tranCode.isEmpty()) tranCode = data[2];
                        if (headID.isEmpty()) headID = data[3];
                        commandString = commandString.replace("@TERMINAL", SessionClass.getParam("terminal"));
                        commandString = commandString.replace("@LINE_ID", data[4]);
                        commandString = commandString.replace("@TRAN_CODE", data[2]);
                        for (int j = 0; j < data.length; j++) {
                            if (data[j] == "null" || data[j] == null) {
                                commandString = commandString.replace("'@ITEM" + j + "'", "''");
                            } else {
                                commandString = commandString.replace("'@ITEM" + j + "'", "'" + data[j] + "'");
                            }
                        }
                        str = str + "[Line" + data[4] + "[comm " + commandString;
                        Log.i("SaveCheckedData", str);
                        CheckedList.setParamItem(entry.getKey(), 2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    count++;
                }else{
                    break;
                }
            }
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        if( !str.isEmpty() ) {

            RequestQueue rq = MySingleton.getInstance(context).getRequestQueue();
            String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_SaveLineData_ByGroup";
            HashMap<String, String> map = new HashMap<>();
            map.put("Terminal", SessionClass.getParam("terminal"));
            map.put("User_id", SessionClass.getParam("userid"));
            map.put("Table_Name",SessionClass.getParam(tranCode+"_Line_ListView_FROM"));
            map.put("PDA_ID", "123");
            map.put("Tran_code",tranCode);
            map.put("Head_ID",headID);
            map.put("cmd", str);
            Log.i("SaveCheckedData",new JSONObject(map).toString());
            if(HelperClass.isOnline(context)) {
                JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("SaveCheckedData", response.toString());
                            String rootText = response.getString("WRHS_PDA_SaveLineData_ByGroupResult");
                            JSONObject jsonObject = new JSONObject(rootText);
                            String rtext = jsonObject.getString("ERROR_CODE");
                            if (!rtext.isEmpty()) {
                                if (rtext.equals("-1")) {
                                    CheckedList.setSetChecked(0);
                                    if (mainSave)
                                        Toast.makeText(context, "Adatok áttöltése sikeresen megtörtént!", Toast.LENGTH_LONG).show();
                                    Log.i("SaveCheckedData", "Áttöltés sikeres!");
                                } else {
                                    CheckedList.setSetChecked(1);
                                    if (mainSave)
                                        Toast.makeText(context, "Adatok áttöltése sikertelen, kérem jelezze a Selesternek!", Toast.LENGTH_LONG).show();
                                }

                            } else {
                                CheckedList.setSetChecked(1);
                                if (mainSave)
                                    Toast.makeText(context, "Adatok áttöltése sikertelen, kérem jelezze a Selesternek!", Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            CheckedList.setSetChecked(1);
                            if (mainSave)
                                Toast.makeText(context, "Adatok áttöltése sikertelen, kérem jelezze a Selesternek!", Toast.LENGTH_LONG).show();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CheckedList.setSetChecked(1);
                        if (mainSave)
                            Toast.makeText(context, "Adatok áttöltése sikertelen, kérem jelezze a Selesternek!", Toast.LENGTH_LONG).show();
                        if (error != null) {
                            error.printStackTrace();
                        }
                    }
                });
                jr.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                rq.add(jr);
            }
        }
    }
}
