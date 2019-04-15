package hu.selester.android.webstockandroid.Threads;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import hu.selester.android.webstockandroid.Database.SelesterDatabase;
import hu.selester.android.webstockandroid.Database.Tables.LogTable;
import hu.selester.android.webstockandroid.Fragments.MovesSubTableFragment;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.CheckedList;
import hu.selester.android.webstockandroid.Objects.InsertedList;
import hu.selester.android.webstockandroid.Objects.MessageBoxSettingsObject;
import hu.selester.android.webstockandroid.Objects.SessionClass;

public class SaveDataThread extends Thread {

    private List<String[]> data;
    private Context context;
    private int fromNum,toNum;
    private int qLineID, qRefLineID, qHeadID;
    private String tranCode;
    private MovesSubTableFragment frg;
    private SelesterDatabase db;

    public SaveDataThread(Context context, List<String[]> data, MovesSubTableFragment frg, int fromNum, int toNum){
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
        db = SelesterDatabase.getDatabase(context);
        LogTable log = new LogTable(LogTable.LogType_Message,"SaveDataThread","","LOGUSER",null,null);
        String str="";
        for(int i=fromNum; i<toNum; i++ ){
            int x = CheckedList.getParamItem(data.get(i)[0]);
            if(x == 1 || x == 2){
                if( !InsertedList.isInsert( data.get(i)[0] ) ) {
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
                    str = str + "[Line" + data.get(i)[qLineID] + "[comm " + commandString;
                    //log.addNewMessageLine("[Line" + data.get(i)[qLineID] + "[comm " + commandString);
                    CheckedList.setParamItem(data.get(i)[0], 2);
                }
            }
        }
        db.logDao().addLog(log);
        if( !str.equals("") ){
            RequestQueue rq = MySingleton.getInstance(context).getRequestQueue();
            String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_SaveLineData_ByGroup";
            HashMap<String,String> map = new HashMap<>();
            map.put("Terminal",SessionClass.getParam("terminal"));
            map.put("User_id",SessionClass.getParam("userid"));
            map.put("Table_Name",SessionClass.getParam(tranCode+"_Line_ListView_FROM"));
            map.put("PDA_ID","123");
            map.put("Tran_code",tranCode);
            if( tranCode.charAt(0) != '3' && tranCode.charAt(0) != '4' ){
                map.put("Head_ID",data.get(0)[qHeadID]);
            }else{
                map.put("Head_ID","0");
            }
            map.put("cmd",str);
            if(HelperClass.isOnline(context)) {
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
                                        CheckedList.setSetChecked(0);
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
                            db.logDao().addLog(new LogTable(LogTable.LogType_Error, "SaveDataThread", e.getMessage(), "LOGUSER", null, null));
                            e.printStackTrace();

                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        frg.stopProgress();
                        CheckedList.setSetChecked(1);
                        db.logDao().addLog(new LogTable(LogTable.LogType_Error, "SaveDataThread", error.getMessage(), "LOGUSER", null, null));
                        if (error != null) {
                            Toast.makeText(context, "Adatok áttöltése sikertelen, hálózati hiba!", Toast.LENGTH_LONG).show();
                            error.printStackTrace();
                        }
                    }
                });
                jr.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                rq.add(jr);
            }
        }else{
            frg.stopProgress();
            CheckedList.setSetChecked(1);
            Toast.makeText(context, "Nincs mentendő adat!", Toast.LENGTH_LONG).show();
        }
    }
}