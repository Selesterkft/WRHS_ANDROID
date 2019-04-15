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

import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Database.Tables.LogTable;
import hu.selester.android.webstockandroid.Fragments.MovesSubTableFragment;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.ActiveFragment;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.CheckedList;
import hu.selester.android.webstockandroid.Objects.InsertedList;
import hu.selester.android.webstockandroid.Objects.SessionClass;

public class SaveCheckedDataThread extends Thread{

    private Context context;
    private Boolean mainSave;
    private int maxCount = 100;
    private List<String[]> datas;
    private int qLineID, qRefLineID, qHeadID;
    private String tranCode;
    private SelesterDatabase db;

    public SaveCheckedDataThread( Context context) {
        this.context = context;
        this.mainSave = false;
        tranCode    = SessionClass.getParam("tranCode");
        qLineID     = HelperClass.getArrayPosition("Line_ID",       SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qRefLineID  = HelperClass.getArrayPosition("Ref_Line_ID",   SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qHeadID     = HelperClass.getArrayPosition("Head_ID",       SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));

    }

    @Override
    public void run() {
        Log.i("TAG", "SaveCheckedData");
        db = SelesterDatabase.getDatabase(context);
        datas = AllLinesData.getAllDataList();
        String str="";
        String headID = "";
        int count = 0;
        //LogTable log = new LogTable(LogTable.LogType_Message,"SaveCheckedThread","","LOGUSER",null,null);
        for (Map.Entry<String, Integer> entry : CheckedList.getParam().entrySet()) {
            if (entry.getValue() == 1 || entry.getValue() == 2) {

                if(count < maxCount) {
                    try {
                        String[] data = AllLinesData.getParam(entry.getKey());
                        if( !InsertedList.isInsert(data[0]) ) {
                            String commandString = SessionClass.getParam(tranCode + "_Line_Update_String");
                            if( tranCode.charAt(0) != '3' && tranCode.charAt(0) != '4' ) {
                                headID = data[qHeadID];
                            }else{
                                headID = "0";
                            }
                            commandString = commandString.replace("@TERMINAL", SessionClass.getParam("terminal"));
                            commandString = commandString.replace("@LINE_ID", data[qLineID]);
                            commandString = commandString.replace("@TRAN_CODE",tranCode);
                            for (int j = 0; j < data.length; j++) {
                                if (data[j] == "null" || data[j] == null) {
                                    commandString = commandString.replace("'@ITEM" + j + "'", "''");
                                } else {
                                    commandString = commandString.replace("'@ITEM" + j + "'", "'" + data[j] + "'");
                                }
                            }
                            str = str + "[Line" + data[qLineID] + "[comm " + commandString;
                            //log.addNewMessageLine("[Line" + data[4] + "[comm " + commandString);
                            CheckedList.setParamItem(entry.getKey(), 2);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        db.logDao().addLog(new LogTable(LogTable.LogType_Error,"SaveCheckedThread",e.getMessage(),"LOGUSER",null,null));
                    }
                    count++;
                }else{
                    break;
                }
            }
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            //db.logDao().addLog(log);
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
            if(HelperClass.isOnline(context)) {
                JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String rootText = response.getString("WRHS_PDA_SaveLineData_ByGroupResult");
                            JSONObject jsonObject = new JSONObject(rootText);
                            String rtext = jsonObject.getString("ERROR_CODE");
                            if (!rtext.isEmpty()) {
                                if (rtext.equals("-1")) {
                                    CheckedList.setSetChecked(0);
                                    if (mainSave)
                                        Toast.makeText(context, "Adatok áttöltése sikeresen megtörtént!", Toast.LENGTH_LONG).show();
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
                            if (mainSave) Toast.makeText(context, "Adatok áttöltése sikertelen, kérem jelezze a Selesternek!", Toast.LENGTH_LONG).show();
                            db.logDao().addLog(new LogTable(LogTable.LogType_Error,"SaveCheckedThread",e.getMessage(),"LOGUSER",null,null));
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
            }else{
                CheckedList.setSetChecked(1);
            }
        }
    }
}
