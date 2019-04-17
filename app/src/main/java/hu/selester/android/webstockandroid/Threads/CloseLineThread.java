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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.selester.android.webstockandroid.Fragments.MovesSubTableFragment;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.AllLinesData;
import hu.selester.android.webstockandroid.Objects.InsertedList;
import hu.selester.android.webstockandroid.Objects.ListSettings;
import hu.selester.android.webstockandroid.Objects.SessionClass;

public class CloseLineThread extends Thread {

    private String tranCode;
    private String tranID;
    private Context context;
    private MovesSubTableFragment f;
    private List<String[]> data;
    private int qBarcode, qLineID, qOrdNum;
    private List<String> barcodeList = new ArrayList<>();
    private List<String> lineIDList = new ArrayList<>();
    private String ord_num = "";

    public CloseLineThread(Context context, String tranCode, String tranID, MovesSubTableFragment f){
        this.context = context;
        this.tranCode = tranCode;
        this.tranID = tranID;
        this.f = f;
        tranCode = SessionClass.getParam("tranCode");
        data = AllLinesData.getAllDataList();
        qBarcode = HelperClass.getArrayPosition("Barcode", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qLineID = HelperClass.getArrayPosition("Line_ID", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
        qOrdNum = HelperClass.getArrayPosition("Ord_Num", SessionClass.getParam(tranCode + "_Line_ListView_SELECT"));
    }

    @Override
    public void run() {
        getBarcodesData();
    }

    private void getBarcodesData(){
        if( SessionClass.getParam("breakBtn").equals("1") || tranCode.charAt(0) == '3' )  {
            Log.i("TAG","0");
            if( qBarcode > -1 ) {
                Log.i("TAG","1");
                int barcodeCount = 0;
                for (int i = 0; i < data.size(); i++) {
                    if (InsertedList.isInsert( data.get(i)[0] ) && data.get(i)[qBarcode].equals("")) {
                        barcodeCount++;
                    }
                }
                Log.i("TAG","" + barcodeCount);
                if (barcodeCount > 0 && tranCode.charAt(0) != '3') {
                    RequestQueue rq = MySingleton.getInstance(context).getRequestQueue();
                    String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_getNewBarcode/" + SessionClass.getParam("terminal") + "/" + barcodeCount;
                    Log.i("URL", url);
                    if (HelperClass.isOnline(context)) {
                        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String rootText = response.getString("WRHS_PDA_getNewBarcodeResult");
                                    try {
                                        JSONObject jsonObject = new JSONObject(rootText);
                                        Toast.makeText(context, jsonObject.getString("ERROR_TEXT"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        JSONArray jsonArray = new JSONArray(rootText);
                                        barcodeList.clear();
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            barcodeList.add(jsonArray.getJSONObject(i).getString("Barcode"));
                                        }
                                    }
                                    if (barcodeList != null && barcodeList.size() > 0) {
                                        getLineIDData();
                                    } else {
                                        Toast.makeText(context, "Nem megfelelő barcode kiosztás, kérem jelezze a Selesternek!", Toast.LENGTH_LONG).show();
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
                } else {
                    getLineIDData();
                }
            }else{
                getLineIDData();
            }
        }else{
            closeLine();
        }
    }

    private void getLineIDData(){
        if( SessionClass.getParam("breakBtn").equals("1")  || tranCode.charAt(0) == '3' ) {
            int lineIDCount = 0;
            for (int i = 0; i < data.size(); i++) {
                if (InsertedList.isInsert( data.get(i)[0]) ){
                    lineIDCount++;
                }
            }
            if( lineIDCount > 0 ) {
                RequestQueue rq = MySingleton.getInstance(context).getRequestQueue();
                String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_getNewids/" + SessionClass.getParam("terminal") + "/" + lineIDCount;
                Log.i("URL", url);
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
                                    JSONArray jsonArray = new JSONArray(rootText);
                                    lineIDList.clear();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        lineIDList.add(jsonArray.getJSONObject(i).getString("ID"));
                                    }
                                }
                                if (lineIDList != null && lineIDList.size() > 0) {
                                    insertData();
                                } else {
                                    Toast.makeText(context, "Nem megfelelő barcode kiosztás, kérem jelezze a Selesternek!", Toast.LENGTH_LONG).show();
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
                closeLine();
            }
        }
    }

    public void insertData(){
        int barcodeCounter = 0;
        int lineIDCounter = 0;
        String str = "";
        for (int i = 0; i < data.size(); i++) {
            if ( InsertedList.isInsert(data.get(i)[0]) ) {
                if( qBarcode > -1 ) {
                    if( data.get(i)[qBarcode].equals("") ) {
                        if (barcodeList.get(barcodeCounter) != null)
                            data.get(i)[qBarcode] = barcodeList.get(barcodeCounter);
                        barcodeCounter++;
                    }
                }
                data.get(i)[qLineID] = lineIDList.get(lineIDCounter);
                lineIDCounter++;
                String commandString = SessionClass.getParam(tranCode + "_Line_Insert_String");
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
            }
        }
        RequestQueue rq = MySingleton.getInstance(context).getRequestQueue();
        String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_SaveLineData_ByGroup";
        Log.i("URL",url);
        HashMap<String,String> map = new HashMap<>();
        map.put("Terminal",SessionClass.getParam("terminal"));
        map.put("User_id",SessionClass.getParam("userid"));
        map.put("Table_Name",SessionClass.getParam(tranCode+"_Line_ListView_FROM"));
        map.put("PDA_ID","123");
        map.put("Tran_code",data.get(0)[2]);
        if( tranCode.charAt(0) == '3' || tranCode.charAt(0) == '4'){
            map.put("Head_ID", "0" );
        }else{
            map.put("Head_ID",data.get(0)[3]);
        }
        map.put("cmd",str);
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
                                Toast.makeText(context, "Adatok áttöltése sikeresen megtörtént!", Toast.LENGTH_LONG).show();
                                closeLine();
                            } else {
                                Toast.makeText(context, "Adatok áttöltése sikertelen, kérem jelezze a Selesternek!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(context, "Adatok áttöltése sikertelen, kérem jelezze a Selesternek!", Toast.LENGTH_LONG).show();
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
        }else{
            Toast.makeText(context, "Nincs hálózat, mentés nem történt meg!", Toast.LENGTH_LONG).show();
        }
    }

    private void closeLine(){
        RequestQueue rq = MySingleton.getInstance(context).getRequestQueue();
        String url = "";
        HashMap<String,String> map = new HashMap<>();
        if( tranCode.charAt(0) == '3' || tranCode.charAt(0) == '4' ){
            url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_XD_CloseLineData";
            map.put("ORD_NUM", data.get(0)[qOrdNum]);
        } else {
            url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_CloseLineData";
            map.put("Table_Name",SessionClass.getParam(tranCode + "_Line_ListView_FROM"));
        }
        map.put("Terminal",SessionClass.getParam("terminal"));
        map.put("User_id",SessionClass.getParam("userid"));
        map.put("PDA_ID","123");
        map.put("Tran_code",tranCode);
        Log.i("URL", url);
        HelperClass.logMapParamteres(map);
        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("TAG",response.toString());
                try {
                    String rootText = "";
                    if( tranCode.charAt(0) == '3' || tranCode.charAt(0) == '4' ) {
                        rootText = response.getString("WRHS_PDA_XD_CloseLineDataResult");
                    }else{
                        rootText = response.getString("WRHS_PDA_CloseLineDataResult");
                    }
                    JSONObject jsonObject = new JSONObject(rootText);
                    String rtext = jsonObject.getString("ERROR_CODE");
                    if(!rtext.isEmpty()){
                        if(rtext.equals("-1")){
                            Toast.makeText(context,"Sorok lezárása sikeres!",Toast.LENGTH_LONG).show();
                            new ChangeStatusThread(context,"PDA_CHECKED", tranCode,tranID,f).start();
                            f.closeFragment();
                        }else{
                            f.errorClose();
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
                        f.errorClose();
                        Toast.makeText(context,"Sorok lezárása sikertelen, kérem jelezze a Selesternek!",Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    f.errorClose();
                    Toast.makeText(context,"Sorok lezárása sikertelen, kérem jelezze a Selesternek!",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null){
                    f.errorClose();
                    Toast.makeText(context,"Sorok lezárása sikertelen, hálózati hiba!",Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        });
        jr.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(jr);
    }
}