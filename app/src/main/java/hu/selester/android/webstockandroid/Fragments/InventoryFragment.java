package hu.selester.android.webstockandroid.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.collections.map.HashedMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import hu.selester.android.webstockandroid.Database.Tables.SessionTemp;
import hu.selester.android.webstockandroid.Helper.HelperClass;
import hu.selester.android.webstockandroid.Helper.KeyboardUtils;
import hu.selester.android.webstockandroid.Helper.MySingleton;
import hu.selester.android.webstockandroid.Objects.SessionClass;
import hu.selester.android.webstockandroid.Objects.WHItem;
import hu.selester.android.webstockandroid.R;

public class InventoryFragment extends Fragment {

    private Map<Integer, String> dataList;
    private TextView whIDTV, renterTV, virtwhTV;
    private List<WHItem> whList;
    private String[] WHTexts, virtWHTexts, renterTexts;
    private Integer[] WHIds, virtWHIds, renterIds;
    private int selectWHID, selectvirtWHID, selectRenterID;
    private EditText placeIdET, palET, itemIdET, countET, lotET, expET;
    private boolean WHListLoaded = false;
    private String isBar;
    private Button nextBtn1,nextBtn2;
    private LinearLayout group1, group2;
    private String[] params;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_inventory,container,false);
        loadWHData();
        loadGetParams();
        loadRenterData();
        selectWHID = 0;
        params = new String[5];
        whIDTV = rootView.findViewById(R.id.inventory_text1);
        placeIdET = rootView.findViewById(R.id.inventory_text2);
        palET = rootView.findViewById(R.id.inventory_text3);
        renterTV = rootView.findViewById(R.id.inventory_text4);
        itemIdET = rootView.findViewById(R.id.inventory_text5);
        countET = rootView.findViewById(R.id.inventory_text6);
        lotET = rootView.findViewById(R.id.inventory_text7);
        expET = rootView.findViewById(R.id.inventory_text8);
        virtwhTV = rootView.findViewById(R.id.inventory_text9);
        nextBtn1 = rootView.findViewById(R.id.inventory_text1_btn);
        nextBtn2 = rootView.findViewById(R.id.inventory_text2_btn);
        nextBtn2 = rootView.findViewById(R.id.inventory_text2_btn);
        group1 = rootView.findViewById(R.id.inventory_group1);
        group2 = rootView.findViewById(R.id.inventory_group2);
        group1.setVisibility(View.GONE);
        group2.setVisibility(View.GONE);

        whIDTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWHList();
            }
        });
        ImageButton flushBtn = rootView.findViewById(R.id.checkplace_flushBtn);
        flushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(getActivity());
                getFragmentManager().popBackStack();
            }
        });

        placeIdET.addTextChangedListener(placeIdTW);
        itemIdET.addTextChangedListener(itemIdTW);

        virtwhTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadVirtWHList();
            }
        });

        renterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRenterList();
            }
        });

        nextBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData1();
            }
        });
        nextBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData2();
            }
        });
        return rootView;
    }

    TextWatcher placeIdTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                isBar = HelperClass.isBarcode(placeIdET.getText().toString());
                if (isBar != null) {
                    placeIdET.removeTextChangedListener(placeIdTW);
                    placeIdET.setText(isBar);
                    placeIdET.addTextChangedListener(placeIdTW);
                    loadData1();
                }
            }catch (Exception e){
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    TextWatcher itemIdTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                isBar = HelperClass.isBarcode(itemIdET.getText().toString());
                if (isBar != null) {
                    itemIdET.removeTextChangedListener(itemIdTW);
                    itemIdET.setText(isBar);
                    itemIdET.addTextChangedListener(itemIdTW);
                    loadData2();
                }
            }catch (Exception e){
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void createSelectItemDialog(String title, String[] datas){
        AlertDialog.Builder b = new AlertDialog.Builder(getContext());
        b.setTitle(title);
        b.setItems(datas, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectWHID = WHIds[which];
                whIDTV.setText(WHTexts[which]);
                dialog.dismiss();
            }
        });
        b.show();
    }

    private void createVirtSelectItemDialog(String title, String[] datas){
        AlertDialog.Builder b = new AlertDialog.Builder(getContext());
        b.setTitle(title);
        b.setItems(datas, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectvirtWHID = virtWHIds[which];
                virtwhTV.setText(virtWHTexts[which]);
                dialog.dismiss();
            }
        });
        b.show();
    }

    private void createRenterSelectItemDialog(String title, String[] datas){
        AlertDialog.Builder b = new AlertDialog.Builder(getContext());
        b.setTitle(title);
        b.setItems(datas, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectRenterID = renterIds[which];
                renterTV.setText(renterTexts[which]);
                dialog.dismiss();
            }
        });
        b.show();
    }

    private void loadWHList(){
        if(WHListLoaded){
            createSelectItemDialog("Válasszon raktárat", WHTexts);
        }else{
            Toast.makeText(getContext(),"A raktár törzs még nem töltődött le, kérem várjon pár másodpercet!",Toast.LENGTH_LONG).show();
        }
    }

    private void loadVirtWHList(){
        if(WHListLoaded){
            if(virtWHTexts != null && virtWHTexts.length > 0) {
                createVirtSelectItemDialog("Válasszon virt. raktárat", virtWHTexts);
            }else{
                Toast.makeText(getContext(),"A virtuális raktár törzs üres!",Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getContext(),"A virtuális raktár törzs még nem töltődött le, kérem várjon pár másodpercet!",Toast.LENGTH_LONG).show();
        }
    }

    private void loadRenterList(){
        if(renterTexts != null && renterTexts.length > 0) {
            createRenterSelectItemDialog("Válasszon bérlőt", renterTexts);
        }else{
            Toast.makeText(getContext(),"A bérlő törzs törzs üres!",Toast.LENGTH_LONG).show();
        }
    }

    private void loadWHData(){
        if(HelperClass.isOnline(getContext())) {
            WHListLoaded = false;
            String url = SessionClass.getParam("WSUrl") + "/whs/0";
            Log.i("URL", url);
            RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
            JSONObject jsonObject = null;
            JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String rootText = response.getString("WHSResult");
                        JSONArray jsonArray = new JSONArray(rootText);
                        int maxCount = jsonArray.length();
                        if (jsonArray.getJSONObject(0).getString("KundeName1").isEmpty()) {
                            maxCount = jsonArray.length() - 1;
                        }
                        WHTexts = new String[maxCount];
                        WHIds = new Integer[maxCount];
                        int c = 0;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (!jsonArray.getJSONObject(i).getString("KundeName1").isEmpty()) {
                                JSONObject jsonObj = jsonArray.getJSONObject(i);
                                WHIds[c] = jsonObj.getInt("ID");
                                WHTexts[c] = jsonObj.getString("KundeName1");
                                c++;
                            }
                        }
                        WHListLoaded = true;
                        loadVirtWHData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Hiba a raktár törzs betöltésekor!", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error != null) {
                        error.printStackTrace();
                    }
                    Toast.makeText(getContext(), "Hiba a raktár törzs betöltésekor!", Toast.LENGTH_LONG).show();
                }
            });
            rq.add(jr);
        }else{
            Toast.makeText(getContext(), "Nincs hálózat az eszközön!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadVirtWHData(){
        if(HelperClass.isOnline(getContext())) {
            String url = SessionClass.getParam("WSUrl") + "/whs/1";
            Log.i("URL", url);
            RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
            JSONObject jsonObject = null;
            JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("TAG",response.toString());
                    try {
                        String rootText = response.getString("WHSResult");
                        JSONArray jsonArray = new JSONArray(rootText);
                        int maxCount = jsonArray.length();
                        if (jsonArray.getJSONObject(0).getString("KundeName1").isEmpty()) {
                            maxCount = jsonArray.length() - 1;
                        }
                        virtWHTexts = new String[maxCount];
                        virtWHIds = new Integer[maxCount];
                        int c = 0;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (!jsonArray.getJSONObject(i).getString("KundeName1").isEmpty()) {
                                JSONObject jsonObj = jsonArray.getJSONObject(i);
                                virtWHIds[c] = jsonObj.getInt("ID");
                                virtWHTexts[c] = jsonObj.getString("KundeName1");
                                c++;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Hiba a virtuális raktár törzs betöltésekor!", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error != null) {
                        error.printStackTrace();
                    }
                    Toast.makeText(getContext(), "Hiba a virtuális raktár törzs betöltésekor!", Toast.LENGTH_LONG).show();
                }
            });
            rq.add(jr);
        }else{
            Toast.makeText(getContext(), "Nincs hálózat az eszközön!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadRenterData(){
        if(HelperClass.isOnline(getContext())) {
            String url = SessionClass.getParam("WSUrl") + "/WRHS_RENTER";
            Log.i("URL", url);
            RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
            JSONObject jsonObject = null;
            JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("TAG",response.toString());
                    try {
                        String rootText = response.getString("WRHS_RENTERResult");
                        JSONArray jsonArray = new JSONArray(rootText);
                        int maxCount = jsonArray.length();
                        if (jsonArray.getJSONObject(0).getString("CompanyName1").isEmpty()) {
                            maxCount = jsonArray.length() - 1;
                        }
                        renterTexts = new String[maxCount];
                        renterIds = new Integer[maxCount];
                        int c = 0;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (!jsonArray.getJSONObject(i).getString("CompanyName1").isEmpty()) {
                                JSONObject jsonObj = jsonArray.getJSONObject(i);
                                renterIds[c] = jsonObj.getInt("CompanyID");
                                renterTexts[c] = jsonObj.getString("CompanyName1");
                                c++;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Hiba a bérlő törzs betöltésekor!", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error != null) {
                        error.printStackTrace();
                    }
                    Toast.makeText(getContext(), "Hiba a bérlő törzs betöltésekor!", Toast.LENGTH_LONG).show();
                }
            });
            rq.add(jr);
        }else{
            Toast.makeText(getContext(), "Nincs hálózat az eszközön!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadData1(){
        group1.setVisibility(View.GONE);
        group2.setVisibility(View.GONE);
        loadLocsControl();
        Toast.makeText(getContext(),"LOAD DATA 1",Toast.LENGTH_LONG).show();
        KeyboardUtils.hideKeyboard(getActivity());
    }

    private void loadData2(){
        group2.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(),"LOAD DATA 2",Toast.LENGTH_LONG).show();
        KeyboardUtils.hideKeyboard(getActivity());
    }

    private void loadGetParams(){
        if (HelperClass.isOnline(getContext())) {
            WHListLoaded = false;
            String terminal = SessionClass.getParam("terminal");
            String url = SessionClass.getParam("WSUrl") + "/WRHS_PDA_getParams/"+terminal;
            Log.i("URL", url);
            RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
            JSONObject jsonObject = null;
            JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        //String rootText = response.getString("WRHS_PDA_getParamsResult");
                        String rootText = "{\"ERROR_CODE\": 0,\"ERROR_TEXT\": \"\",\"Controls\": \"TextBox_PalettenNr,ComboBox_Einlagerer,TextBox_ProdFeld1,TextBox_MHD,ComboBox_VirtualLager\",\"PARAMS\": \"1,1,1,1,1\"}";

                        JSONObject jsonObject = new JSONObject(rootText);
                        if (jsonObject.getInt("ERROR_CODE") == 0 ){
                            params = jsonObject.getString("PARAMS").split(",");
                            if(params[0].equals("0")){ rootView.findViewById(R.id.inventory_panel3).setVisibility(View.GONE); }
                            if(params[1].equals("0")){ rootView.findViewById(R.id.inventory_panel4).setVisibility(View.GONE); }
                            if(params[2].equals("0")){ rootView.findViewById(R.id.inventory_panel3).setVisibility(View.GONE); }
                            if(params[3].equals("0")){ rootView.findViewById(R.id.inventory_panel3).setVisibility(View.GONE); }
                            if(params[4].equals("0")){ rootView.findViewById(R.id.inventory_panel9).setVisibility(View.GONE); }
                        }else{
                            Toast.makeText(getContext(), jsonObject.getString("ERROR_TEXT"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Hiba a paraméterek betöltésekor!", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error != null) {
                        error.printStackTrace();
                    }
                    Toast.makeText(getContext(), "Hiba a paraméterek betöltésekor!", Toast.LENGTH_LONG).show();
                }
            });
            rq.add(jr);
        } else {
            Toast.makeText(getContext(), "Nincs hálózat az eszközön!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadLocsControl(){
        //http://185.187.72.228:8090/Service1.svc/Locs_Control_LocName/A011/82
        String terminal = SessionClass.getParam("terminal");
        String url = SessionClass.getParam("WSUrl") + "/Locs_Control_LocName/"+placeIdET.getText().toString()+"/"+selectWHID;
        Log.i("URL", url);
        RequestQueue rq = MySingleton.getInstance(getContext()).getRequestQueue();
        JSONObject jsonObject = null;
        JsonRequest<JSONObject> jr = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String rootText = response.getString("Locs_Control_LocNameResult");
                    JSONObject jsonObject = new JSONObject(rootText);
                    if (jsonObject.getString("ERROR_TEXT").isEmpty() ){
                        Log.i("TAG",jsonObject.getString("OUT_WrhsLocks_ID"));
                        group1.setVisibility(View.VISIBLE);
                        group2.setVisibility(View.GONE);
                    }else{
                        Toast.makeText(getContext(), jsonObject.getString("ERROR_TEXT"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Hiba a betöltésekor!", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    error.printStackTrace();
                }
                Toast.makeText(getContext(), "Hiba a paraméterek betöltésekor!", Toast.LENGTH_LONG).show();
            }
        });
        rq.add(jr);
    }

}
